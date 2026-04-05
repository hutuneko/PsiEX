package com.hutuneko.psi_ex.mixin;

import com.hutuneko.psi_ex.PsiEX;
import com.hutuneko.psi_ex.api.CadBehavior;
import com.hutuneko.psi_ex.compat.PsiEXRegistry;
import com.hutuneko.psi_ex.system.CuriosUtil;
import moffy.addonapi.AddonAPI;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vazkii.psi.api.PsiAPI;
import vazkii.psi.api.cad.ICAD;
import vazkii.psi.common.item.ItemCAD;

@Mixin(value = PsiAPI.class, remap = false)
public class PsiAPIMixin {

    @Inject(method = "getPlayerCAD", at = @At("HEAD"), cancellable = true)
    private static void onGetPlayerCAD(Player player, CallbackInfoReturnable<ItemStack> cir) {
        if (player == null) {
            cir.setReturnValue(ItemStack.EMPTY);
            return;
        }
        ItemStack foundCad = ItemStack.EMPTY;
        if (AddonAPI.isModuleAvailable(new ResourceLocation(PsiEX.MOD_ID,"curioscompat"))){
            foundCad = CuriosUtil.findFirstStrict(player, PsiEXRegistry.GPTCAD.get());
        }

        for(int i = 0; i < player.getInventory().getContainerSize(); ++i) {
            ItemStack stackAt = player.getInventory().getItem(i);
            if (!stackAt.isEmpty() && (stackAt.getItem() instanceof ItemCAD || CadBehavior.isCAD(stackAt))) {
                if (!foundCad.isEmpty()) {
                    cir.setReturnValue(ItemStack.EMPTY);
                    return;
                }

                foundCad = stackAt;
            }
        }
        cir.setReturnValue(foundCad);
    }

    @Inject(method = "getPlayerCADSlot", at = @At("HEAD"), cancellable = true)
    private static void onGetPlayerCADSlot(Player player, CallbackInfoReturnable<Integer> cir) {
        if (player == null) {
            cir.setReturnValue(-1);
            return;
        }
        if (AddonAPI.isModuleAvailable(new ResourceLocation(PsiEX.MOD_ID,"curioscompat"))){
             ItemStack stack = CuriosUtil.findFirstStrict(player, PsiEXRegistry.GPTCAD.get());
             if (stack != ItemStack.EMPTY) {
                 cir.setReturnValue(-1);
                 return;
             }
        }
        int slot = -1;
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stackAt = player.getInventory().getItem(i);

            if (!stackAt.isEmpty() && (stackAt.getItem() instanceof ItemCAD || CadBehavior.isCAD(stackAt))) {
                if (stackAt.getItem() instanceof ICAD) {
                    if (slot != -1) {
                        cir.setReturnValue(-1);
                        return;
                    }
                    slot = i;
                }
            }
        }
        cir.setReturnValue(slot);
    }
}