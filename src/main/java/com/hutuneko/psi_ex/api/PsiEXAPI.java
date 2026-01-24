package com.hutuneko.psi_ex.api;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;
import org.jetbrains.annotations.Nullable;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellContext;

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
    public static void applyExplosionEffect(Level level, Vec3 center, double maxRadius) {
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
            float damage = 20.0f * damageRatio; // 最大20ダメージ

            // ノックバック（中心から外へ）
            Vec3 knockback = target.position().subtract(center).normalize().scale(damageRatio * 2.0);
            target.push(knockback.x, knockback.y + 0.5, knockback.z);

            target.hurt(level.damageSources().explosion(null, null), damage);
        }
    }
    public static <T> void ifNotNull(T obj, Consumer<T> action) {
        if (obj != null) action.accept(obj);
    }
}
