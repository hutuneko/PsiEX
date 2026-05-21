package io.github.hutuneko.psi_ex.entity;

import io.github.hutuneko.psi_ex.api.PsiEXAPI;
import io.github.hutuneko.psi_ex.compat.PsiEXRegistry;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.common.item.ItemSpellDrive;

public class PsiArrowEntity extends AbstractArrow {
    private ItemStack stack = ItemStack.EMPTY;
    private boolean triggered = false;
    public void setStack(ItemStack s) { this.stack = s.copy(); }
    public ItemStack getStack() { return stack; }
    public PsiArrowEntity(EntityType<? extends PsiArrowEntity> type, Level level) {
        super(type, level);
        this.pickup = Pickup.ALLOWED;
    }
    public PsiArrowEntity(Level level, LivingEntity shooter ,ItemStack stack) {
        super(PsiEXRegistry.PSI_ARROW_ENTITY.get(), shooter, level);
        this.pickup = Pickup.ALLOWED;
    }
    @Override
    protected void onHitEntity(@NotNull EntityHitResult hit) {
        super.onHitEntity(hit);
        if (!level().isClientSide && !triggered) {
            Vec3 p = hit.getLocation();
            Entity e = hit.getEntity();
            Spell spell = ItemSpellDrive.getSpell(stack);
            PsiEXAPI.runPsiAt(p, spell, this.level(), e, true);
            triggered = true;
        }
    }
    @Override
    protected void onHitBlock(@NotNull BlockHitResult hit){
        if (!level().isClientSide && !triggered) {
            Vec3 p = hit.getLocation();
            Spell spell = ItemSpellDrive.getSpell(stack);
            PsiEXAPI.runPsiAt(p, spell, this.level(), null, false);
            triggered = true;
        }
    }

    @Override
    protected @NotNull ItemStack getPickupItem() {
        return new ItemStack(PsiEXRegistry.PSI_ARROW.get());
    }
}
