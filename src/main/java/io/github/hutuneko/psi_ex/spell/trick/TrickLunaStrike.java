package io.github.hutuneko.psi_ex.spell.trick;

import io.github.hutuneko.psi_ex.system.capability.PsionProvider;
import io.github.hutuneko.psi_ex.system.capability.PsionSync;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import vazkii.psi.api.spell.*;
import vazkii.psi.api.spell.param.ParamEntity;
import vazkii.psi.api.spell.param.ParamNumber;
import vazkii.psi.api.spell.piece.PieceTrick;

public class TrickLunaStrike extends PieceTrick {
    private ParamEntity targetParam;
    private ParamNumber damageParam;
    public TrickLunaStrike(Spell spell) {
        super(spell);
    }

    @Override
    public void initParams() {
        addParam(targetParam = new ParamEntity(SpellParam.GENERIC_NAME_TARGET,SpellParam.RED,false,false
        ));
        addParam(damageParam = new ParamNumber("damage",SpellParam.BLUE,false,false
        ));
    }

    @Override
    public void addToMetadata(SpellMetadata meta) throws SpellCompilationException {
        super.addToMetadata(meta);
        Double d = this.getParamEvaluation(damageParam);
        meta.addStat(EnumSpellStat.POTENCY, 20);
        meta.addStat(EnumSpellStat.COST,   500*d.intValue());
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
        Player player = context.caster;
        Level world = player.level();
        if (world.isClientSide) return null;
        Number n = getParamValue(context, damageParam);
        double d = n.doubleValue();
        Entity t = getParamValue(context, targetParam);
        var cap = t.getCapability(PsionProvider.CAP);
        if (!cap.isPresent()){
            throw new SpellRuntimeException(SpellRuntimeException.NULL_TARGET);
        }
        cap.ifPresent(now -> now.hurt(d));
        if (t instanceof ServerPlayer target){
            PsionSync.toSelf(target);
        }
        return null;
    }
}
