package com.hutuneko.psi_ex.mixin;

import com.hutuneko.psi_ex.system.attribute.PsiEXAttributes;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellHelpers;
import vazkii.psi.api.spell.SpellParam;
import vazkii.psi.api.spell.SpellPiece;

@Mixin(SpellHelpers.class)
public class SpellHelpersMixin {
    @ModifyVariable(
            method = "rangeLimitParam",
            at = @At("HEAD"),
            argsOnly = true,
            remap = false,
            name = "arg3")
    private static double rangeLimitParam(double max, SpellPiece piece, SpellContext context, SpellParam<Number> param){
        AttributeInstance instance = context.caster.getAttribute(PsiEXAttributes.PSI_SPELL_RANGE.get());
        return instance != null ? instance.getValue() : 32;
    }
}
