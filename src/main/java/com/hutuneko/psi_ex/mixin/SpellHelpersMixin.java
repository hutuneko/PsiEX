package com.hutuneko.psi_ex.mixin;

import com.hutuneko.psi_ex.system.attribute.PsiEXAttributes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellHelpers;
import vazkii.psi.api.spell.SpellParam;
import vazkii.psi.api.spell.SpellPiece;

import java.util.Objects;

@Mixin(SpellHelpers.class)
public class SpellHelpersMixin {
    @ModifyVariable(
            method = "rangeLimitParam",
            at = @At("HEAD"),
            ordinal = 0,
            argsOnly = true,
            remap = false
    )
    private static double rangeLimitParam(double max, SpellPiece piece, SpellContext context, SpellParam<Number> param){
        return Objects.requireNonNull(context.caster.getAttribute(PsiEXAttributes.PSI_SPELL_RANGE.get())).getValue();
    }
}
