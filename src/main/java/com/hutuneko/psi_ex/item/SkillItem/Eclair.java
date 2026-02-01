package com.hutuneko.psi_ex.item.SkillItem;

import com.hutuneko.psi_ex.compat.PsiEXRegistry;
import com.hutuneko.psi_ex.system.attribute.PsiEXAttributes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.RegistryObject;
import top.theillusivec4.curios.api.SlotContext;

import java.util.UUID;

public class Eclair extends SkillItem{
    private static final UUID ZERO_MODIFIER_UUID = UUID.fromString("259d4822-4a09-4d92-984b-013149495755");
    public Eclair(Properties pProperties) {
        super(pProperties);
    }
    @Override
    public void onEquip(SlotContext slotContext, ItemStack prevStack, ItemStack stack) {
        LivingEntity entity = slotContext.entity();
        AttributeInstance range = entity.getAttribute(PsiEXAttributes.PSI_SPELL_RANGE.get());
        AttributeInstance move = entity.getAttribute(PsiEXAttributes.PSI_MOVEMENT_POINT.get());
        if (range != null) {
            AttributeModifier modifier = new AttributeModifier(
                    ZERO_MODIFIER_UUID,
                    "Temporary attribute zeroing",
                    -1.0,
                    AttributeModifier.Operation.MULTIPLY_TOTAL
            );
            range.addTransientModifier(modifier);
        }
        if (move != null) {
            AttributeModifier modifier = new AttributeModifier(
                    ZERO_MODIFIER_UUID,
                    "Temporary attribute zeroing",
                    1000,
                    AttributeModifier.Operation.MULTIPLY_TOTAL
            );
            move.addTransientModifier(modifier);
        }
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        LivingEntity entity = slotContext.entity();
        for (RegistryObject<Attribute> attr : PsiEXRegistry.ATTRIBUTES.getEntries()) {
            Attribute targetAttr = attr.get();
            AttributeInstance inst = entity.getAttribute(targetAttr);
            if (inst != null) {
                    inst.removeModifier(ZERO_MODIFIER_UUID);
            }
        }
    }
}
