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
//    @Inject(at = @At("HEAD"),method = "rangeLimitParam", remap = false)
//    private static void rangeLimitParam(SpellPiece piece, SpellContext context, SpellParam<Number> param, double max, CallbackInfoReturnable<Double> cir){
//        max = Objects.requireNonNull(context.caster.getAttribute(PsiEXAttributes.PSI_SPELL_RANGE.get())).getValue();
//    }
    @ModifyVariable(
            method = "rangeLimitParam", // 対象のメソッド名
            at = @At("HEAD"),    // メソッドの開始時点
            ordinal = 0,         // double型の「0番目」の引数（max）を指定
            argsOnly = true,      // ローカル変数ではなく引数のみを対象にする
            remap = false
    )
    private static double rangeLimitParam(double max, SpellPiece piece, SpellContext context, SpellParam<Number> param){
        return Objects.requireNonNull(context.caster.getAttribute(PsiEXAttributes.PSI_SPELL_RANGE.get())).getValue();
    }
}
