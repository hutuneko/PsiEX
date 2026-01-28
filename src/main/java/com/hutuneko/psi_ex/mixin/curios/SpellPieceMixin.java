package com.hutuneko.psi_ex.mixin.curios;

import com.hutuneko.psi_ex.compat.PsiEXRegistry;
import com.hutuneko.psi_ex.item.RangeZero;
import com.hutuneko.psi_ex.system.CuriosUtil;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.theillusivec4.curios.api.SlotResult;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellParam;
import vazkii.psi.api.spell.SpellPiece;
import vazkii.psi.api.spell.SpellRuntimeException;

@Mixin(SpellPiece.class)
public abstract class SpellPieceMixin {
    @Shadow
    public abstract Object getRawParamValue(SpellContext context, SpellParam<?> param);

    @Inject(method = "getParamValue",at = @At("HEAD"),remap = false)
    private <T> void getParamValue(SpellContext context, SpellParam<T> param, CallbackInfoReturnable<T> cir) throws SpellRuntimeException {
        T returnValue = (T) getRawParamValue(context, param);
        if (returnValue instanceof ServerPlayer serverPlayer){
            if (serverPlayer.getUUID() != context.caster.getUUID()){
                SlotResult slotResult = CuriosUtil.findFirstByItem(serverPlayer, PsiEXRegistry.RANGEZERO.get()).orElse(null);
                if (slotResult != null) {
                    ItemStack stack = slotResult.stack();
                    if (stack.getItem() instanceof RangeZero) {
                        throw new SpellRuntimeException(SpellRuntimeException.NULL_TARGET);
                    }
                }
            }
        }
    }
}
