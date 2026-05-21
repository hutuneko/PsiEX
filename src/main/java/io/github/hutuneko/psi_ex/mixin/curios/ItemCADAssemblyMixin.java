package io.github.hutuneko.psi_ex.mixin.curios;

import io.github.hutuneko.psi_ex.item.GeneralPurposeTypeCAD;
import io.github.hutuneko.psi_ex.item.ItemGPTCADAssembly;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vazkii.psi.common.item.ItemCAD;

import java.util.List;

@Mixin(ItemCAD.class)
public class ItemCADAssemblyMixin {

    @Inject(
            method = "makeCADWithAssembly(Lnet/minecraft/world/item/ItemStack;Ljava/util/List;)Lnet/minecraft/world/item/ItemStack;",
            at = @At("HEAD"),
            remap = false,
            cancellable = true
    )
    private static void psiex$makeCADWithAssembly(
            ItemStack assembly,
            List<ItemStack> components,
            CallbackInfoReturnable<ItemStack> cir) {
        if (assembly.getItem() instanceof ItemGPTCADAssembly){
            cir.setReturnValue(GeneralPurposeTypeCAD.makeCADWithAssemblyCustom(assembly, components));
        }
    }
}