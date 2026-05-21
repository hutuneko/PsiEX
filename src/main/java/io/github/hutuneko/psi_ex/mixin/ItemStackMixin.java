package io.github.hutuneko.psi_ex.mixin;

import io.github.hutuneko.psi_ex.api.CadBehavior;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public class ItemStackMixin {
    @Inject(method = "use",at = @At("RETURN"), cancellable = true)
    private void icaduse(Level pLevel, Player pPlayer, InteractionHand pUsedHand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir){
        InteractionResultHolder<ItemStack> holder = CadBehavior.getCADBehavior().use(pLevel,pPlayer,pUsedHand);
        if (holder != null){
            if (!pPlayer.getItemInHand(pUsedHand).getItem().use(pLevel, pPlayer, pUsedHand).equals(InteractionResultHolder.pass(pPlayer.getItemInHand(pUsedHand)))){
                cir.setReturnValue(holder);
            }
        }
    }
}
