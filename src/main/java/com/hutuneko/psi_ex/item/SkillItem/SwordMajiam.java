package com.hutuneko.psi_ex.item.SkillItem;

import com.hutuneko.psi_ex.system.attribute.PsiEXAttributes;

import java.util.UUID;

public class SwordMajiam extends SkillItem{
    public SwordMajiam(Properties props) {
        super(props, UUID.fromString("9be0df19-bc32-4df5-0633-9fc2551f033d"));
    }

    @Override
    protected void initializeAttributes() {
        setAttributeTotal(PsiEXAttributes.PSI_SPELL_RANGE.get(),-1);
        setAttributeAddition(PsiEXAttributes.PSI_ACCELERATION_POINT.get(),500);
        setAttributeAddition(PsiEXAttributes.PSI_MOVEMENT_POINT.get(),500);
        setAttributeAddition(PsiEXAttributes.PSI_WEIGHTING_POINT.get(),500);
    }
}
