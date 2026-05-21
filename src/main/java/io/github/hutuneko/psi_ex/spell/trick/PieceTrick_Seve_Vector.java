package io.github.hutuneko.psi_ex.spell.trick;

import io.github.hutuneko.psi_ex.system.capability.PlayerDataProvider;
import net.minecraft.world.entity.player.Player;
import vazkii.psi.api.internal.Vector3;
import vazkii.psi.api.spell.*;
import vazkii.psi.api.spell.param.ParamNumber;
import vazkii.psi.api.spell.param.ParamVector;
import vazkii.psi.api.spell.piece.PieceTrick;

public class PieceTrick_Seve_Vector extends PieceTrick {
    private ParamVector seveParam;
    private ParamNumber sParam;
    private Vector3 v;
    public PieceTrick_Seve_Vector(Spell spell) {
        super(spell);
    }

    @Override
    public void initParams() {
        addParam(seveParam = new ParamVector(SpellParam.GENERIC_NAME_VECTOR,SpellParam.GREEN,false,false
        ));
        addParam(sParam = new ParamNumber(SpellParam.GENERIC_NAME_NUMBER,SpellParam.GREEN,false,false
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
        Player player = context.caster;
        if (!player.level().isClientSide) {
            Number n = getParamValue(context, sParam);
            Vector3 vector3 = getParamValue(context, seveParam);
            int s = n.intValue();
            player.getCapability(PlayerDataProvider.CAP).ifPresent(
                    data -> data.setVector("vector3" + s, vector3)
            );
            player.getCapability(PlayerDataProvider.CAP).ifPresent(
                    data ->
                            v = data.getValue("vector3" + s)
            );
            System.out.println(v);
        }
        return null;
    }
}
