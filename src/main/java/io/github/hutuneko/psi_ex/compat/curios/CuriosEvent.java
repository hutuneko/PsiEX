package io.github.hutuneko.psi_ex.compat.curios;

import io.github.hutuneko.psi_ex.PsiEX;
import io.github.hutuneko.psi_ex.api.KeyBindings;
import io.github.hutuneko.psi_ex.api.PsiEXAPI;
import io.github.hutuneko.psi_ex.compat.PsiEXRegistry;
import io.github.hutuneko.psi_ex.item.GeneralPurposeTypeCAD;
import io.github.hutuneko.psi_ex.item.SkillItem.Orochimaru;
import io.github.hutuneko.psi_ex.system.CuriosUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CuriosEvent {

    private static final Map<UUID,Entity> target = new HashMap<>();
    @SubscribeEvent
    public static void onPlayerTickE(TickEvent.PlayerTickEvent e) {
        if (!(e.player instanceof ServerPlayer player)) return;

        if (player.getEffect(PsiEXRegistry.ECLAIREFFECT.get()) == null) return;

        Entity entity = target.get(player.getUUID());

        if (entity == null || !entity.isAlive()) {
            Entity newTarget = PsiEXAPI.raycastEntity(player, 32,60,true);
            target.remove(player.getUUID());
            target.put(player.getUUID(), newTarget);

            if (newTarget != null) {
                Vec3 eyePos = player.getEyePosition(1.0F);
                Vec3 entityPos = newTarget.position().add(0, newTarget.getBbHeight() / 2, 0);
                Vec3 toEntity = entityPos.subtract(eyePos);
                double distance = toEntity.length();

                Vec3 teleportPos = eyePos.add(toEntity.normalize().scale(Math.max(0.1, distance - 1)));
                float[] rot = PsiEXAPI.lookAtRotation(player, newTarget);

                player.teleportTo(
                        player.serverLevel(),
                        teleportPos.x,
                        teleportPos.y,
                        teleportPos.z,
                        rot[0],
                        rot[1]
                );
            }
        }
    }

    public static void onPlayerTickO(TickEvent.PlayerTickEvent event) {
        
        if (!(event.player instanceof ServerPlayer player)) return;
        
        if (!(player.getMainHandItem().getItem() instanceof Orochimaru)) {
            return;
        }
        if (!player.isUsingItem()) {
            return;
        }
        double speed = player.getDeltaMovement().length() * 20;

        if (speed < 10.0) {
            return;
        }

        Vec3 center = player.position();
        AABB area = new AABB(center, center).inflate(2.0);

        List<LivingEntity> targets = player.level().getEntitiesOfClass(
                LivingEntity.class, area,
                e -> e.isAlive() && e != player
        );

        if (targets.isEmpty()) {
            return;
        }

        LivingEntity nearest = PsiEXAPI.findNearest(targets, center);
        if (nearest == null) {
            return;
        }

        
        if (player.distanceToSqr(nearest) > 25.0) { 
            return;
        }

        
        performAttack(player, nearest, speed);

    }

    private static void performAttack(ServerPlayer player, LivingEntity target, double speed) {
        ServerLevel level = player.serverLevel();
        DamageSource source = level.damageSources().playerAttack(player);

        float baseDamage = 4.0f;

        float speedBonus = (float) (speed * 0.5);

        float damage = baseDamage + speedBonus;

        PsiEX.LOGGER.info("[CuriosEvent] Attack! Speed: {}, Damage: {}",
                String.format("%.2f", speed), damage);

        player.attack(target);

        
        target.hurt(source, damage);

        
        player.setDeltaMovement(Vec3.ZERO);
        player.hurtMarked = true;
    }

    }