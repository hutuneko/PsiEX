package io.github.hutuneko.psi_ex.mixin;

import io.github.hutuneko.psi_ex.PsiEX;
import io.github.hutuneko.psi_ex.api.CadBehavior;
import io.github.hutuneko.psi_ex.compat.PsiEXRegistry;
import io.github.hutuneko.psi_ex.item.GeneralPurposeTypeCAD;
import io.github.hutuneko.psi_ex.system.CuriosUtil;
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

        // Curios から検索
        ItemStack curiosCad = ItemStack.EMPTY;
        if (AddonAPI.isModuleAvailable(new ResourceLocation(PsiEX.MOD_ID, "curioscompat"))) {
            var result = CuriosUtil.findFirst(player, stack ->
                    stack.getItem() instanceof GeneralPurposeTypeCAD
            );
            if (result.isPresent()) {
                ItemStack stack = result.get().stack();
                if (isPoweredOn(stack)) {
                    curiosCad = stack;
                }
            }
        }

        // インベントリから検索
        ItemStack invCad = ItemStack.EMPTY;
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (!stack.isEmpty() && (stack.getItem() instanceof ItemCAD || CadBehavior.isCAD(stack))) {
                if (isPoweredOn(stack)) {
                    // 2つ目見つかった時点で確定でEMPTY
                    if (!invCad.isEmpty()) {
                        cir.setReturnValue(ItemStack.EMPTY);
                        return;
                    }
                    invCad = stack;
                }
            }
        }

        // 両方に存在する場合はEMPTY
        if (!curiosCad.isEmpty() && !invCad.isEmpty()) {
            cir.setReturnValue(ItemStack.EMPTY);
            return;
        }

        // どちらか一方にちょうど1つある場合のみ返す
        ItemStack result = !curiosCad.isEmpty() ? curiosCad : invCad;
        cir.setReturnValue(result);
    }

    private static boolean isPoweredOn(ItemStack stack) {
        if (stack.isEmpty()) return false;
        if (stack.getTag() == null) return true;
        return !stack.getTag().getBoolean("psi_ex.isshutdown");
    }

    @Inject(method = "getPlayerCADSlot", at = @At("HEAD"), cancellable = true)
    private static void onGetPlayerCADSlot(Player player, CallbackInfoReturnable<Integer> cir) {
        if (player == null) {
            cir.setReturnValue(-1);
            return;
        }
        if (AddonAPI.isModuleAvailable(new ResourceLocation(PsiEX.MOD_ID, "curioscompat"))) {
            var result = CuriosUtil.findFirst(player, stack ->
                    stack.getItem() instanceof GeneralPurposeTypeCAD
            );
            if (result.isPresent()) {
                ItemStack stack = result.get().stack();
                if (isPoweredOn(stack)) {
                    cir.setReturnValue(-1);
                    return;
                }
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