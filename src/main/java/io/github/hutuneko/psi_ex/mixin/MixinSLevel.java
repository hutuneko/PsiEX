package io.github.hutuneko.psi_ex.mixin;

import io.github.hutuneko.psi_ex.api.SpellTriggerContext;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vazkii.psi.api.spell.SpellContext;


@Mixin(ServerLevel.class)
public class MixinSLevel {
    @Inject(method = "addFreshEntity", at = @At("HEAD"))
    private void psiEX$markLightning(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if (SpellTriggerContext.isCasting()) {
            entity.getPersistentData().putBoolean("psiex_magic", true);

            SpellContext context = SpellTriggerContext.getCurrent();
            if (context.caster != null) {
                entity.getPersistentData().putUUID("psiex_caster", context.caster.getUUID());
            }
        }
    }
}