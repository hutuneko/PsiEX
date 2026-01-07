package com.hutuneko.psi_ex.mixin;

import com.hutuneko.psi_ex.api.CadBehavior;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vazkii.psi.api.PsiAPI;
import vazkii.psi.api.cad.ICAD;

@Mixin(value = PsiAPI.class, remap = false)
public class PsiAPIMixin {

    /**
     * getPlayerCAD 内のループで各スロットをチェックする際、
     * instanceof ICAD の判定結果を無視して、フラグがない場合は「次へ」飛ばすように制御します。
     * * ここでは単純に「ループ内の stackAt 判定」を上書きするのではなく、
     * メソッド全体に対して Inject し、自前でループを回して「正しいCAD」が見つかったらそれを返すようにします。
     */
    @Inject(method = "getPlayerCAD", at = @At("HEAD"), cancellable = true)
    private static void onGetPlayerCAD(Player player, CallbackInfoReturnable<ItemStack> cir) {
        if (player == null) {
            cir.setReturnValue(ItemStack.EMPTY);
            return;
        }

        ItemStack foundCad = ItemStack.EMPTY;
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stackAt = player.getInventory().getItem(i);

            // ここで独自の isCAD チェックを挟む
            if (!stackAt.isEmpty() && CadBehavior.isCAD(stackAt)) {
                if (stackAt.getItem() instanceof ICAD) {
                    if (!foundCad.isEmpty()) {
                        cir.setReturnValue(ItemStack.EMPTY);
                        return;
                    }
                    foundCad = stackAt;
                }
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

        int slot = -1;
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stackAt = player.getInventory().getItem(i);

            if (!stackAt.isEmpty() && CadBehavior.isCAD(stackAt)) {
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