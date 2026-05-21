package io.github.hutuneko.psi_ex.effect;

import io.github.hutuneko.psi_ex.compat.PsiEXRegistry;
import io.github.hutuneko.psi_ex.system.attribute.PsiEXAttributes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;


public class CastJammingEffect extends MobEffect {
    private static final UUID ZERO_MODIFIER_UUID = UUID.fromString("739d4822-4a09-4d92-984b-013149495721");

    public CastJammingEffect(MobEffectCategory pCategory, int pColor) {
        super(pCategory, pColor);
    }

    @Override
    public void addAttributeModifiers(@NotNull LivingEntity entity, @NotNull AttributeMap attributeMap, int amplifier) {
        super.addAttributeModifiers(entity,attributeMap,amplifier);
        if (entity instanceof ServerPlayer){
            for (RegistryObject<Attribute> attr : PsiEXRegistry.ATTRIBUTES.getEntries()) {
                Attribute targetAttr = attr.get();
                if (targetAttr != PsiEXAttributes.PSI_SPELL_RANGE.get()){
                    AttributeInstance inst = entity.getAttribute(targetAttr);
                    if (inst != null) {
                        AttributeModifier modifier = new AttributeModifier(
                                ZERO_MODIFIER_UUID,
                                "Temporary attribute zeroing",
                                -1.0,
                                AttributeModifier.Operation.MULTIPLY_TOTAL
                        );
                        inst.addTransientModifier(modifier);
                    }
                }
            }
        }
    }

    @Override
    public void removeAttributeModifiers(@NotNull LivingEntity entity, @NotNull AttributeMap attributeMap, int amplifier) {
        super.removeAttributeModifiers(entity, attributeMap, amplifier);
        if (entity instanceof ServerPlayer){
            for (RegistryObject<Attribute> attr : PsiEXRegistry.ATTRIBUTES.getEntries()) {
                Attribute targetAttr = attr.get();
                if (targetAttr != PsiEXAttributes.PSI_SPELL_RANGE.get()){
                    AttributeInstance inst = entity.getAttribute(targetAttr);
                    if (inst != null) {
                        inst.removeModifier(ZERO_MODIFIER_UUID);
                    }
                }
            }
        }
    }
}
