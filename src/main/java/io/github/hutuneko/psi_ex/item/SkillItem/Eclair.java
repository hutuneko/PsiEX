package io.github.hutuneko.psi_ex.item.SkillItem;

import io.github.hutuneko.psi_ex.system.attribute.PsiEXAttributes;

import java.util.UUID;

public class Eclair extends SkillItem{
    private static final UUID ZERO_MODIFIER_UUID = UUID.fromString("259d4822-4a09-4d92-984b-013149495755");
    public Eclair(Properties pProperties) {
        super(pProperties,ZERO_MODIFIER_UUID);
    }

    @Override
    protected void initializeAttributes() {
        setAttributeTotal(PsiEXAttributes.PSI_SPELL_RANGE.get(),-1);
        setAttributeTotal(PsiEXAttributes.PSI_MOVEMENT_POINT.get(),1000);
    }
}
