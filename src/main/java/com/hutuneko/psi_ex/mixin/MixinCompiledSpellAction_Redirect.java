package com.hutuneko.psi_ex.mixin;

import com.hutuneko.psi_ex.Config;
import com.hutuneko.psi_ex.api.SpellTriggerContext;
import com.hutuneko.psi_ex.compat.PsiEXRegistry;
import com.hutuneko.psi_ex.api.piece.PieceTrickExclusive;
import com.hutuneko.psi_ex.system.PieceConditionRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
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
import vazkii.psi.api.spell.SpellRuntimeException;

@Mixin(CompiledSpell.Action.class)
public abstract class MixinCompiledSpellAction_Redirect {
    @Shadow @Final public SpellPiece piece;

    @Inject(method = "execute", at = @At("HEAD"), remap = false, cancellable = true)
    private void gate$redirectExecute(IPlayerData data, SpellContext ctx, CallbackInfo ci) throws SpellRuntimeException {
        SpellTriggerContext.set(ctx);
        if (ctx.caster.hasEffect(PsiEXRegistry.CASTJAMMING.get())){
            throw new SpellRuntimeException("psi_ex.spellerror.castjamming");
        }
        if (this.piece instanceof PieceTrickExclusive isTrick){
            if (!isTrick.isCast(ctx.caster, ctx)) throw new SpellRuntimeException("psi_ex.spellerror.exclusive_failed");
        }
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
                    if (msg instanceof MutableComponent com) {
                        msg = com.append(Component.translatable("message.psi_ex.requirement_suffix"));
                    }
                    if (msg != null && ctx.caster != null && !ctx.caster.level().isClientSide) {
                        throw new SpellRuntimeException(msg.getString());
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
