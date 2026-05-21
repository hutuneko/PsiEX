package io.github.hutuneko.psi_ex.item.SkillItem;

import io.github.hutuneko.psi_ex.system.attribute.PsiEXAttributes;

import java.util.UUID;

public class RangeZero extends SkillItem{
    private static final UUID ZERO_MODIFIER_UUID = UUID.fromString("739d4822-4a09-4d92-984b-013149495755");
    public RangeZero(Properties pProperties) {
        super(pProperties,ZERO_MODIFIER_UUID);
    }

    @Override
    protected void initializeAttributes() {
        setAttributeTotal(PsiEXAttributes.PSI_SPELL_RANGE.get(),-1);
    }
}
