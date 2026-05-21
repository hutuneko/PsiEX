package io.github.hutuneko.psi_ex.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class PsiAirEntity extends ThrowableItemProjectile {
    // 同期データ（ダメージ値）
    private static final net.minecraft.network.syncher.EntityDataAccessor<Float> DATA_DAMAGE =
            net.minecraft.network.syncher.SynchedEntityData.defineId(PsiAirEntity.class, net.minecraft.network.syncher.EntityDataSerializers.FLOAT);

    public PsiAirEntity(EntityType<? extends PsiAirEntity> type, net.minecraft.world.level.Level level) {
        super(type, level);
    }

    public PsiAirEntity(EntityType<? extends PsiAirEntity> type, net.minecraft.world.level.Level level, LivingEntity owner) {
        super(type, owner, level);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_DAMAGE, 4.0f); // 既定ダメージ
    }

    public void setDamage(float damage) { this.entityData.set(DATA_DAMAGE, damage); }
    public float getDamage() { return this.entityData.get(DATA_DAMAGE); }

    /** 好きな方向・速度で撃つヘルパー */
    public void launchFrom(LivingEntity shooter, Vec3 dir, float speed, float inaccuracy) {
        this.setOwner(shooter);
        this.shoot(dir.x, dir.y, dir.z, speed, inaccuracy);
    }

    /** 命中時のエンティティダメージ */
    @Override
    protected void onHitEntity(@NotNull EntityHitResult hit) {
        super.onHitEntity(hit);
        var target = hit.getEntity();
        var owner = this.getOwner();
        target.hurt(this.damageSources().thrown(this, owner == null ? this : owner), getDamage());
        this.discard();
    }

    /** ブロックに当たったら消える（必要なら貫通などに変えてOK） */
    @Override
    protected void onHitBlock(@NotNull BlockHitResult hit) {
        super.onHitBlock(hit);
        this.discard();
    }

    @Override
    protected float getGravity() {
        return 0.0F;
    }
    
    @Override
    protected @NotNull Item getDefaultItem() {
        return Items.ARROW; // 見た目用。自作アイテムに差し替え可
    }

    // 保存/読込（ダメージ値を保持したい場合）
    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putFloat("Damage", getDamage());
    }
    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("Damage")) setDamage(tag.getFloat("Damage"));
    }
}
