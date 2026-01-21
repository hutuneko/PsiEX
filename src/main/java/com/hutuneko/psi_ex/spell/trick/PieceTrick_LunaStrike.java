package com.hutuneko.psi_ex.spell.trick;

import com.hutuneko.psi_ex.system.capability.PsionProvider;
import com.hutuneko.psi_ex.system.capability.PsionSync;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import vazkii.psi.api.spell.*;
import vazkii.psi.api.spell.param.ParamEntity;
import vazkii.psi.api.spell.param.ParamNumber;
import vazkii.psi.api.spell.piece.PieceTrick;

public class PieceTrick_LunaStrike extends PieceTrick {
    private ParamEntity targetParam;
    private ParamNumber damageParam;
    private double k;
    private boolean l;
    public PieceTrick_LunaStrike(Spell spell) {
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
        meta.addStat(EnumSpellStat.POTENCY, 20);
        meta.addStat(EnumSpellStat.COST,   50);
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
        if (!(t instanceof ServerPlayer target)){
            throw new SpellRuntimeException(SpellRuntimeException.IMMUNE_TARGET);
        }
        target.getCapability(PsionProvider.CAP).ifPresent(now -> {
            now.hurt(d);            // 減算
            l = now.isPsion();      // 直後の判定（同じインスタンス）
            k = now.getPsion();});
        PsionSync.toSelf(target);
        System.out.println(k);
        System.out.println(l);
        return null;
    }
}
