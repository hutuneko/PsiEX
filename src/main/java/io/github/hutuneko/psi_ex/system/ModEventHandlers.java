package io.github.hutuneko.psi_ex.system;

import io.github.hutuneko.psi_ex.PsiEX;
import io.github.hutuneko.psi_ex.system.attribute.PsiEXAttributes;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

// ModEventHandlers.java
@Mod.EventBusSubscriber(modid = PsiEX.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEventHandlers {
    @SubscribeEvent
    public static void onEntityAttributeMod(EntityAttributeModificationEvent event) {
        event.add(EntityType.PLAYER, PsiEXAttributes.PSI_SPELL_RANGE.get());
        event.add(EntityType.PLAYER, PsiEXAttributes.PSI_PSION_POINT.get());
        event.add(EntityType.PLAYER, PsiEXAttributes.PSI_ACCELERATION_POINT.get());
        event.add(EntityType.PLAYER, PsiEXAttributes.PSI_CONVERGENCE_POINT.get());
        event.add(EntityType.PLAYER, PsiEXAttributes.PSI_ABSORPTION_POINT.get());
        event.add(EntityType.PLAYER, PsiEXAttributes.PSI_MOVEMENT_POINT.get());
        event.add(EntityType.PLAYER, PsiEXAttributes.PSI_DIVERGENCE_POINT.get());
        event.add(EntityType.PLAYER, PsiEXAttributes.PSI_EMISSION_POINT.get());
        event.add(EntityType.PLAYER, PsiEXAttributes.PSI_WEIGHTING_POINT.get());
        event.add(EntityType.PLAYER, PsiEXAttributes.PSI_VIBRATION_POINT.get());
        event.add(EntityType.PLAYER, PsiEXAttributes.PSI_ANCIENTRITES_POINT.get());
    }

}
