package io.github.hutuneko.psi_ex.mixin;

import io.github.hutuneko.psi_ex.api.CadBehavior;
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

    @Inject(method = "setCad", at = @At("HEAD"), cancellable = true,remap = false)
    private void psi_ex_1_20_1$onSetCad(ItemStack newCad, CallbackInfo ci) {
        if (newCad != null && !newCad.isEmpty() && CadBehavior.isCAD(newCad)) {
            this.cad = newCad;
            ci.cancel();
        }
    }
}