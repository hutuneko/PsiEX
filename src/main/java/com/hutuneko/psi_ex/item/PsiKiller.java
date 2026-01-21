package com.hutuneko.psi_ex.item;

import com.hutuneko.psi_ex.compat.PsiEXRegistry;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import org.jetbrains.annotations.NotNull;

public class PsiKiller extends SwordItem {
    public PsiKiller(Tier pTier, int pAttackDamageModifier, float pAttackSpeedModifier, Properties pProperties) {
        super(pTier, pAttackDamageModifier, pAttackSpeedModifier, pProperties);
    }
    @Override
    public boolean hurtEnemy(@NotNull ItemStack stack, @NotNull LivingEntity target, LivingEntity attacker) {
        if (!attacker.level().isClientSide) {
            target.addEffect(new MobEffectInstance(PsiEXRegistry.CASTJAMMING.get(), 100, 0));
        }
        return super.hurtEnemy(stack, target, attacker);
    }
}
