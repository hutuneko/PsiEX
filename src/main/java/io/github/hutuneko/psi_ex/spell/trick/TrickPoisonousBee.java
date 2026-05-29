package io.github.hutuneko.psi_ex.spell.trick;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import vazkii.psi.api.spell.*;
import vazkii.psi.api.spell.param.ParamEntity;
import vazkii.psi.api.spell.param.ParamNumber;
import vazkii.psi.api.spell.piece.PieceTrick;

public class TrickPoisonousBee extends PieceTrick {
    private ParamEntity targetParam;
    private ParamNumber valueParam;

    public TrickPoisonousBee(Spell spell) {
        super(spell);
    }

    @Override
    public void initParams() {

        addParam(targetParam = new ParamEntity(SpellParam.GENERIC_NAME_TARGET,SpellParam.GREEN,false,false
        ));
        addParam(valueParam = new ParamNumber(SpellParam.GENERIC_NAME_NUMBER,SpellParam.BLUE,false,false
        ));
    }


    @Override
    public void addToMetadata(SpellMetadata meta) throws SpellCompilationException {
        super.addToMetadata(meta);
        Double powerVal = this.<Double>getParamEvaluation(valueParam);
        if(powerVal == null) {
            powerVal = 1.0;
        }
        meta.addStat(EnumSpellStat.POTENCY, 50);
        meta.addStat(EnumSpellStat.COST, (int) (50 * Math.abs(powerVal)));
    }

    @Override
    public EnumPieceType getPieceType() {
        return EnumPieceType.TRICK;
    }

    @Override
    public Class<?> getEvaluationType() {
        return Void.class;
    }

    @Override
    public Object execute(SpellContext context) throws SpellRuntimeException {
        Entity e = this.getParamValue(context, targetParam);

        Number raw = this.getParamValue(context, valueParam);
        float hpVal = raw.floatValue();
        if (!(e instanceof LivingEntity living)) {
            throw new SpellRuntimeException(SpellRuntimeException.NULL_TARGET);
        }
        float max = living.getMaxHealth();
        float current = living.getHealth();
        float hp = (max - current) * hpVal;
        if (hp >= current){
            living.setHealth(0);
        }else {
            e.hurt(e.level().damageSources().
                    thrown(e, e), hp);
        }
        return null;
    }

}
