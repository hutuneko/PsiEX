package io.github.hutuneko.psi_ex.mixin;

import io.github.hutuneko.psi_ex.api.CadBehavior;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vazkii.psi.api.PsiAPI;
import vazkii.psi.api.cad.ICAD;
import vazkii.psi.common.core.handler.PlayerDataHandler;

@Mixin(PlayerDataHandler.EventHandler.class)
public class PlayerDataHandlerMixin {
    @Inject(at = @At("HEAD"),method = "onPlayerTick",remap = false)
    private static void psi_ex_1_20_1$onPlayerTick(LivingEvent.LivingTickEvent event, CallbackInfo ci){
        if (event.getEntity() instanceof Player player && !event.getEntity().isSpectator()) {
            ItemStack cadStack = PsiAPI.getPlayerCAD(player);
            if (!cadStack.isEmpty() && CadBehavior.isCAD(cadStack) && PsiAPI.canCADBeUpdated(player)) {
                ((ICAD) cadStack.getItem()).incrementTime(cadStack);
            }
        }
    }
}
