package com.hutuneko.psi_ex.spell.trick.skill;

import com.hutuneko.psi_ex.api.piece.PieceSkillTrick;
import com.hutuneko.psi_ex.compat.PsiEXRegistry;
import net.minecraft.world.effect.MobEffectInstance;
import vazkii.psi.api.spell.*;
import vazkii.psi.api.spell.param.ParamNumber;

public class PieceTrick_Eclair extends PieceSkillTrick {
    ParamNumber timeParam;
    public PieceTrick_Eclair(Spell spell) {
        super(spell,PsiEXRegistry.ECLAIR.get());
    }

    @Override
    public void initParams() {
        addParam(timeParam = new ParamNumber(SpellParam.GENERIC_NAME_TIME, SpellParam.BLUE, false, true));
    }
    @Override
    public void addToMetadata(SpellMetadata meta) throws SpellCompilationException {
        super.addToMetadata(meta);
        meta.addStat(EnumSpellStat.POTENCY, 50);
        meta.addStat(EnumSpellStat.COST, 500);
    }
    @Override
    public Object execute(SpellContext context) throws SpellRuntimeException {
        Number n = getParamValue(context, timeParam);
        context.caster.addEffect(new MobEffectInstance(PsiEXRegistry.ECLAIREFFECT.get(), n.intValue(), 0));
        return null;
    }
}
