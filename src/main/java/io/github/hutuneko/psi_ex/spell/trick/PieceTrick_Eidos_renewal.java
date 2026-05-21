package io.github.hutuneko.psi_ex.spell.trick;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import vazkii.psi.api.spell.*;
import vazkii.psi.api.spell.piece.PieceTrick;
import vazkii.psi.api.spell.param.ParamEntity;
import vazkii.psi.api.spell.param.ParamNumber;
import vazkii.psi.api.spell.SpellContext;

import java.util.Objects;

public class PieceTrick_Eidos_renewal extends PieceTrick {
    private ParamEntity targetParam;
    private ParamNumber         valueParam;

    public PieceTrick_Eidos_renewal(Spell spell) {
        super(spell);
    }

    @Override
    public void initParams() {

        addParam(targetParam = new ParamEntity(SpellParam.GENERIC_NAME_TARGET,SpellParam.GREEN,false,false// エンティティ描画用レンダラー
        ));
        addParam(valueParam = new ParamNumber(SpellParam.GENERIC_NAME_NUMBER,SpellParam.BLUE,true,true
        ));
    }


    @Override
    public void addToMetadata(SpellMetadata meta) {
        try {
            super.addToMetadata(meta);
        } catch (SpellCompilationException e) {
            throw new RuntimeException(e);
        }
        try {
            meta.addStat(EnumSpellStat.POTENCY, 50);
        } catch (SpellCompilationException e) {
            throw new RuntimeException(e);
        }
        try {
            meta.addStat(EnumSpellStat.COST, 100);
        } catch (SpellCompilationException e) {
            throw new RuntimeException(e);
        }
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
        double hpVal = raw.doubleValue();

        if (!context.isInRadius(e)) {
            throw new SpellRuntimeException(SpellRuntimeException.OUTSIDE_RADIUS);
        }
        if (!(e instanceof LivingEntity living)) {
            throw new SpellRuntimeException(SpellRuntimeException.NULL_TARGET);
        }
        Objects.requireNonNull(living.getAttribute(Attributes.MAX_HEALTH)).setBaseValue(hpVal);
        if (living.getHealth() > hpVal) {
            living.setHealth((float) hpVal);
        }
        return null;
    }

}
