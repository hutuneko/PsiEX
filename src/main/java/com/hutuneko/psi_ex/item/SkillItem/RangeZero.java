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
        super(pProperties,ZERO_MODIFIER_UUID);
        setAttribute(PsiEXAttributes.PSI_SPELL_RANGE.get(),-1);
    }
}
