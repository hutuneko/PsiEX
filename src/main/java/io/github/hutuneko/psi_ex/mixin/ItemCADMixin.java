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
import vazkii.psi.common.item.ItemCAD;

@Mixin(ItemCAD.class)
public class ItemCADMixin {
    @Inject(method = "use",at = @At("HEAD"), cancellable = true)
    private void use(Level pLevel, Player pPlayer, InteractionHand pUsedHand,
                     CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir) {
        ItemStack stack = pPlayer.getItemInHand(pUsedHand);
        if (stack.getTag() == null) {
            cir.setReturnValue(InteractionResultHolder.pass(stack));
            return;
        }
        if (!stack.getTag().contains("psi_ex.isshutdown")) {
            stack.getTag().putBoolean("psi_ex.isshutdown", false);
        }
        if (stack.getTag().getBoolean("psi_ex.isshutdown")) {
            cir.setReturnValue(InteractionResultHolder.pass(stack));;
        }
    }
}
