package com.hutuneko.psi_ex.mixin;

import com.hutuneko.psi_ex.Config;
import com.hutuneko.psi_ex.api.SpellTriggerContext;
import com.hutuneko.psi_ex.system.PieceConditionRegistry;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vazkii.psi.api.internal.IPlayerData;
import vazkii.psi.api.spell.CompiledSpell;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellPiece;
@Mixin(CompiledSpell.Action.class)
public abstract class MixinCompiledSpellAction_Redirect {
    @Shadow @Final public SpellPiece piece;

    @Inject(method = "execute", at = @At("HEAD"), remap = false, cancellable = true)
    private void gate$redirectExecute(IPlayerData data, SpellContext ctx, CallbackInfo ci) {
        SpellTriggerContext.set(ctx);

        var id = ((AccessorSpellPiece) this.piece).getRegistryKey();
        var cond = PieceConditionRegistry.get(id).orElse(null);
        if (Config.COMMON.spellgeat.get()) {
            if (cond != null) {
                boolean ok;
                try {
                    ok = cond.test(ctx, piece);
                } catch (Throwable t) {
                    ok = false;
                }
                if (!ok) {
                    var msg = cond.failMessage();
                    if (msg != null && ctx != null && ctx.caster != null && !ctx.caster.level().isClientSide) {
                        ctx.caster.sendSystemMessage(msg);
                    }

                    SpellTriggerContext.remove();
                    ci.cancel();
                }
            }
        }
    }

    @Inject(method = "execute", at = @At("RETURN"), remap = false)
    private void psiEX$afterExecute(IPlayerData data, SpellContext context, CallbackInfo ci) {
        SpellTriggerContext.remove();
    }
}
