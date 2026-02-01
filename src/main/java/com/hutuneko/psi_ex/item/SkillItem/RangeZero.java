package com.hutuneko.psi_ex.item.SkillItem;

import com.hutuneko.psi_ex.system.attribute.PsiEXAttributes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;

import java.util.UUID;

public class RangeZero extends SkillItem{
    private static final UUID ZERO_MODIFIER_UUID = UUID.fromString("739d4822-4a09-4d92-984b-013149495755");
    public RangeZero(Properties pProperties) {
        super(pProperties);
    }
    @Override
    public void onEquip(SlotContext slotContext, ItemStack prevStack, ItemStack stack) {
        LivingEntity entity = slotContext.entity();
        AttributeInstance inst = entity.getAttribute(PsiEXAttributes.PSI_SPELL_RANGE.get());
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

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        LivingEntity entity = slotContext.entity();
        AttributeInstance inst = entity.getAttribute(PsiEXAttributes.PSI_SPELL_RANGE.get());
        if (inst != null) {
            inst.removeModifier(ZERO_MODIFIER_UUID);
        }
    }
}
