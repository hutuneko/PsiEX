package io.github.hutuneko.psi_ex.mixin.curios;

import io.github.hutuneko.psi_ex.item.SkillItem.Phantom;
import io.github.hutuneko.psi_ex.item.SkillItem.RangeZero;
import io.github.hutuneko.psi_ex.system.CuriosUtil;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellParam;
import vazkii.psi.api.spell.SpellPiece;
import vazkii.psi.api.spell.SpellRuntimeException;

@Mixin(value = {SpellPiece.class},remap = false)
public abstract class SpellPieceMixin {

    @Inject(
            method = "getParamValue",
            at = @At("RETURN"),
            cancellable = true
    )
    private <T> void checkParamValueReturn(SpellContext context, SpellParam<T> param,
                                           CallbackInfoReturnable<T> cir) throws SpellRuntimeException {

        T value = cir.getReturnValue();

        if (value instanceof ServerPlayer targetPlayer) {
            if (context.caster == targetPlayer) return;

            if (!(context.caster instanceof ServerPlayer casterPlayer)) return;

            if (targetPlayer.getUUID().equals(casterPlayer.getUUID())) return;

            ItemStack stack = CuriosUtil.test(targetPlayer);
            if (stack != null&&(stack.getItem() instanceof RangeZero || stack.getItem() instanceof Phantom)) {
                cir.setReturnValue(null);
                throw new SpellRuntimeException(SpellRuntimeException.NULL_TARGET);
            }
        }
    }
}
