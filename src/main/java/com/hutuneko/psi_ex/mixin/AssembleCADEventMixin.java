package com.hutuneko.psi_ex.mixin;

import com.hutuneko.psi_ex.api.CadBehavior;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vazkii.psi.api.cad.AssembleCADEvent;

@Mixin(value = AssembleCADEvent.class)
public abstract class AssembleCADEventMixin {

    @Shadow(remap = false) private ItemStack cad;

    /**
     * setCad メソッドの先頭に処理を注入します。
     * PsiEX の CAD フラグが立っている場合、本来の instanceof チェックが走る前に
     * 自前で値をセットして、メソッド全体をキャンセル（早期終了）させます。
     */
    @Inject(method = "setCad", at = @At("HEAD"), cancellable = true,remap = false)
    private void onSetCad(ItemStack newCad, CallbackInfo ci) {
        if (newCad != null && !newCad.isEmpty() && CadBehavior.isCAD(newCad)) {
            this.cad = newCad;
            ci.cancel();
        }
    }
}