package com.hutuneko.psi_ex.entity;

import com.hutuneko.psi_ex.api.PsiEXAPI;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.*;
import org.jetbrains.annotations.NotNull;

public class Railgun extends Projectile {
    private static final double SPEED = 5.0;
    private int life = 100;

    public Railgun(EntityType<? extends Railgun> type, Level level) {
        super(type, level);
        this.noCulling = true;
    }

    public Railgun(EntityType<? extends Railgun> type, LivingEntity shooter, Level level) {
        super(type, level);
        this.setOwner(shooter);
        this.setPos(shooter.getX(), shooter.getEyeY() - 0.1, shooter.getZ());
    }

    @Override
    public void tick() {
        super.tick();
        Vec3 motion = this.getDeltaMovement();
        Vec3 pos = this.position();
        PsiEXAPI.applyExplosionEffect(level(),pos,5);
        Vec3 nextPos = pos.add(motion);
        PsiEXAPI.applyExplosionEffect(level(),nextPos,5);
        HitResult hitResult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);

        if (hitResult.getType() != HitResult.Type.MISS) {
            this.onHit(hitResult);
        }

        this.setPos(nextPos.x, nextPos.y, nextPos.z);

        if (level().isClientSide) {
            // 周囲の空気が帯電
            for (int i = 0; i < 3; i++) {
                Vec3 offset = new Vec3(
                        random.nextGaussian() * 0.5,
                        random.nextGaussian() * 0.5,
                        random.nextGaussian() * 0.5
                );
                Vec3 sparkPos = pos.add(offset);

                // 弾に向かうスパーク
                Vec3 toBullet = pos.subtract(sparkPos).normalize().scale(0.2);
                level().addParticle(ParticleTypes.ELECTRIC_SPARK,
                        sparkPos.x, sparkPos.y, sparkPos.z,
                        toBullet.x, toBullet.y, toBullet.z);
            }

            // プラズマのコア（明るい中心部）
            level().addParticle(ParticleTypes.END_ROD, pos.x, pos.y, pos.z, 0, 0, 0);
        }

        if (!this.level().isClientSide) {
            this.life--;
            if (this.life <= 0) {
                this.discard();
            }
        }
    }

    @Override
    protected void onHitEntity(@NotNull EntityHitResult result) {
        super.onHitEntity(result);
        Entity target = result.getEntity();
        target.hurt(this.damageSources().thrown(this, this.getOwner()), 10.0f);
    }

    @Override
    protected void onHitBlock(@NotNull BlockHitResult result) {
        super.onHitBlock(result);
        this.discard();
    }

    @Override
    protected void defineSynchedData() {
    }

    @Override
    public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this);
    }
}