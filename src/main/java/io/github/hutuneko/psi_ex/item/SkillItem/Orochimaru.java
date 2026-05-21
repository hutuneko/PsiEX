package io.github.hutuneko.psi_ex.item.SkillItem;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import vazkii.psi.common.item.tool.ItemPsimetalSword;

public class Orochimaru extends ItemPsimetalSword {
    public Orochimaru(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level pLevel, Player pPlayer, @NotNull InteractionHand pUsedHand) {
        pPlayer.startUsingItem(pUsedHand);

        return InteractionResultHolder.pass(pPlayer.getItemInHand(pUsedHand));
    }
    @Override
    public int getUseDuration(@NotNull ItemStack stack) {
        return 72000;
    }
}
