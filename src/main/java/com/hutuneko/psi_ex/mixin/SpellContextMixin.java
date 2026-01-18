package com.hutuneko.psi_ex.mixin;

import com.hutuneko.psi_ex.system.attribute.PsiEXAttributes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vazkii.psi.api.internal.MathHelper;
import vazkii.psi.api.spell.SpellContext;

import java.util.Objects;

@Mixin(SpellContext.class)
public class SpellContextMixin {
    @Shadow
    public Entity focalPoint;

    @Shadow
    public Player caster;

    @Inject(at = @At("HEAD"), method = "isInRadius(DDD)Z", cancellable = true,remap = false)
    public void psi_ex_1_20_1$isInRadius(double x, double y, double z, CallbackInfoReturnable<Boolean> cir){
        boolean isR = MathHelper.pointDistanceSpace(x,y,z,focalPoint.getX(), focalPoint.getY(), focalPoint.getZ()) <= Objects.requireNonNull(caster.getAttribute(PsiEXAttributes.PSI_SPELL_RANGE.get())).getValue();
        cir.setReturnValue(isR);
    }
}
