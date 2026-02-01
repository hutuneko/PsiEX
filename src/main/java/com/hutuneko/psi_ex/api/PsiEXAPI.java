package com.hutuneko.psi_ex.api;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.*;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.locating.IModFile;
import org.jetbrains.annotations.Nullable;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellPiece;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;

public class PsiEXAPI {
    public static void runPsiAt(Vec3 pos, Spell spell, Level level, @Nullable Entity e,boolean l) {
        SpellContext ctx = new SpellContext();
        if (l) {
            if (!(e instanceof LivingEntity livingEntity)) return;
            ctx.attackedEntity = livingEntity;
        }
        if (spell != null) {
            FakePlayer caster = FakePlayerFactory.getMinecraft((ServerLevel) level);
            double ox = pos.x, oy = pos.y, oz = pos.z;
            caster.setPos(ox, oy, oz);
            ctx.setPlayer(caster).setSpell(spell);
            ctx.cspell.safeExecute(ctx);
        }
    }
    public static void applyExplosionEffect(Level level, Vec3 center, double maxRadius,float speed) {
        AABB area = new AABB(
                center.x - maxRadius, center.y - maxRadius, center.z - maxRadius,
                center.x + maxRadius, center.y + maxRadius, center.z + maxRadius
        );

        List<LivingEntity> targets = level.getEntitiesOfClass(LivingEntity.class, area);

        for (LivingEntity target : targets) {
            double distance = target.distanceToSqr(center.x, center.y, center.z);
            if (distance > maxRadius * maxRadius) continue;

            // 距離に応じてダメージ減衰（線形補間）
            float damageRatio = 1.0f - (float)(Math.sqrt(distance) / maxRadius);
            float damage = 20.0f * damageRatio * speed;

            // ノックバック（中心から外へ）
            Vec3 knockback = target.position().subtract(center).normalize().scale(damageRatio * 2.0);
            target.push(knockback.x, knockback.y + 0.5, knockback.z);

            target.hurt(level.damageSources().explosion(null, null), damage);
        }
    }
    public static <T> void ifNotNull(T obj, Consumer<T> action) {
        if (obj != null) action.accept(obj);
    }
    public static Entity raycastEntity(Entity caster, double range, double angleDegrees, boolean checkLineOfSight) {
        Vec3 eyePos = caster.getEyePosition(1.0F);
        Vec3 lookVec = caster.getLookAngle();
        double minCos = Math.cos(Math.toRadians(angleDegrees));

        AABB searchBox = caster.getBoundingBox().inflate(range);
        List<Entity> entities = caster.level().getEntities(caster, searchBox,
                e -> e.isAlive() && e.isPickable() && e != caster);

        return entities.stream()
                .filter(e -> {
                    Vec3 targetPos = e.position().add(0, e.getBbHeight() / 2, 0);
                    Vec3 toTarget = targetPos.subtract(eyePos);
                    double distance = toTarget.length();

                    if (distance > range) return false;
                    if (lookVec.dot(toTarget.normalize()) < minCos) return false;

                    if (checkLineOfSight) {
                        Vec3 endPos = eyePos.add(toTarget.normalize().scale(distance));
                        BlockHitResult blockHit = caster.level().clip(new ClipContext(
                                eyePos,
                                endPos,
                                ClipContext.Block.COLLIDER,
                                ClipContext.Fluid.NONE,
                                caster
                        ));

                        if (blockHit.getType() != HitResult.Type.MISS) {
                            double blockDist = blockHit.getLocation().distanceTo(eyePos);
                            return !(blockDist < distance - 0.5);
                        }
                    }

                    return true;
                })
                .min(Comparator.comparingDouble(e -> e.distanceToSqr(caster)))
                .orElse(null);
    }
    public static float[] lookAtRotation(Entity from, Entity to) {
        Vec3 fromPos = from.getEyePosition(1.0F);
        Vec3 toPos = to.position().add(0, to.getBbHeight() / 2, 0);
        Vec3 delta = toPos.subtract(fromPos);

        double horizontalDist = Math.sqrt(delta.x * delta.x + delta.z * delta.z);

        float yRot = (float) Math.toDegrees(Math.atan2(delta.z, delta.x)) - 90;
        yRot = normalizeYaw(yRot);

        float xRot = (float) Math.toDegrees(-Math.atan2(delta.y, horizontalDist));
        xRot = Math.max(-90, Math.min(90, xRot));

        return new float[]{yRot, xRot};
    }

    public static float normalizeYaw(float yaw) {
        while (yaw < -180) yaw += 360;
        while (yaw >= 180) yaw -= 360;
        return yaw;
    }
    public static List<Class<? extends SpellPiece>> findSpellPieces(String modId, String packageName) {
        List<Class<? extends SpellPiece>> classes = new ArrayList<>();

        var modFileInfo = ModList.get().getModFileById(modId);

        if (modFileInfo != null) {
            IModFile modFile = modFileInfo.getFile();

            modFile.getScanResult().getClasses().forEach(classInfo -> {
                String className = classInfo.clazz().getClassName();

                if (className.startsWith(packageName)) {
                    try {
                        Class<?> clazz = Class.forName(className);
                        if (SpellPiece.class.isAssignableFrom(clazz)) {
                            classes.add((Class<? extends SpellPiece>) clazz);
                        }
                    } catch (ClassNotFoundException ignored) {
                    }
                }
            });
        }

        return classes;
    }
}
