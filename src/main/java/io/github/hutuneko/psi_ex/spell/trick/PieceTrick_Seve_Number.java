package io.github.hutuneko.psi_ex.spell.trick;

import io.github.hutuneko.psi_ex.system.capability.PlayerDataProvider;
import net.minecraft.world.entity.player.Player;
import vazkii.psi.api.spell.*;
import vazkii.psi.api.spell.param.ParamNumber;
import vazkii.psi.api.spell.piece.PieceTrick;

public class PieceTrick_Seve_Number extends PieceTrick {
    private ParamNumber seveParam;
    private ParamNumber sParam;

    public PieceTrick_Seve_Number(Spell spell) {
        super(spell);
    }

    @Override
    public void initParams() {
        addParam(seveParam = new ParamNumber(SpellParam.GENERIC_NAME_NUMBER1,SpellParam.GREEN,false,false
        ));
        addParam(sParam = new ParamNumber(SpellParam.GENERIC_NAME_NUMBER2,SpellParam.GREEN,false,false
        ));
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
        Number n = getParamValue(context, sParam);
        double ns = getParamValue(context, seveParam).doubleValue();
        int s = n.intValue();
        Player player = context.caster;
        player.getCapability(PlayerDataProvider.CAP).ifPresent(
                data -> data.setDouble("number" + s, ns));
        return null;
    }
}
