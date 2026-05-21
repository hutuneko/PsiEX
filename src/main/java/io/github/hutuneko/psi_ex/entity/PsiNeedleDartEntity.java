package io.github.hutuneko.psi_ex.entity;

import io.github.hutuneko.psi_ex.api.PsiEXAPI;
import io.github.hutuneko.psi_ex.compat.PsiEXRegistry;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.NotNull;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.common.item.ItemSpellDrive;

import java.util.Objects;

public class PsiNeedleDartEntity extends ThrowableItemProjectile {
    public PsiNeedleDartEntity(EntityType<? extends PsiNeedleDartEntity> type, Level level) {
        super(type, level);
    }

    public PsiNeedleDartEntity(EntityType<? extends PsiNeedleDartEntity> type, Level level, LivingEntity thrower) {
        super(type, thrower, level);
    }

    @Override
    protected @NotNull Item getDefaultItem() {
        return PsiEXRegistry.PSI_NEEDLE_DART.get();
    }
    public ItemStack getNeedleStack() {
        return this.getItem();
    }
    @Override
    protected void onHitEntity(@NotNull EntityHitResult result) {
        super.onHitEntity(result);
        Entity target = result.getEntity();
        target.hurt(damageSources().thrown(this, this.getOwner()), 1.0F);
        Spell spell = ItemSpellDrive.getSpell(getNeedleStack());

        if (!level().isClientSide) {
            if (!(spell == null)) {
                PsiEXAPI.runPsiAt(Objects.requireNonNull(this.getOwner()).position(),
                        spell,
                        this.level(),
                        result.getEntity(),true );
            }
            this.discard();
        }
    }

    @Override
    protected void onHitBlock(@NotNull BlockHitResult result) {
        super.onHitBlock(result);
        if (!level().isClientSide) {
            this.discard();
        }
    }
}
