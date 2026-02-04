package com.hutuneko.psi_ex.mixin.curios;

import com.hutuneko.psi_ex.item.SkillItem.Phantom;
import com.hutuneko.psi_ex.item.SkillItem.RangeZero;
import com.hutuneko.psi_ex.item.SkillItem.SkillItem;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellParam;
import vazkii.psi.api.spell.SpellPiece;
import vazkii.psi.api.spell.SpellRuntimeException;

@Mixin(SpellPiece.class)
public abstract class SpellPieceMixin {

    @Inject(
            method = "getParamValue",
            at = @At("RETURN"),
            cancellable = true,
            remap = false
    )
    private <T> void checkParamValueReturn(SpellContext context, SpellParam<T> param,
                                           CallbackInfoReturnable<T> cir) throws SpellRuntimeException {

        T value = cir.getReturnValue();

        if (value instanceof ServerPlayer targetPlayer) {
            if (context.caster == targetPlayer) return;

            if (!(context.caster instanceof ServerPlayer casterPlayer)) return;

            if (targetPlayer.getUUID().equals(casterPlayer.getUUID())) return;

            SlotResult slotResult = CuriosApi.getCuriosHelper()
                    .findFirstCurio(targetPlayer, stack -> stack.getItem() instanceof SkillItem)
                    .orElse(null);

            if (slotResult != null) {
                ItemStack stack = slotResult.stack();
                if (stack.getItem() instanceof RangeZero || stack.getItem() instanceof Phantom) {
                    cir.setReturnValue(null);
                    throw new SpellRuntimeException(SpellRuntimeException.NULL_TARGET);
                }
            }
        }
    }
}
