package io.github.hutuneko.psi_ex.spell.operator;

import io.github.hutuneko.psi_ex.system.capability.PlayerDataProvider;
import net.minecraft.world.entity.player.Player;
import vazkii.psi.api.internal.Vector3;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellParam;
import vazkii.psi.api.spell.SpellRuntimeException;
import vazkii.psi.api.spell.param.ParamNumber;
import vazkii.psi.api.spell.piece.PieceOperator;

public class PieceOperator_getSeve_Vector3 extends PieceOperator {
    private ParamNumber SeveParam;
    private Vector3 v;
    private Vector3 h;
    public PieceOperator_getSeve_Vector3(Spell spell) { super(spell); }

    @Override
    public void initParams() {
        SeveParam = new ParamNumber(SpellParam.GENERIC_NAME_NUMBER, SpellParam.GREEN, false, false);
        addParam(SeveParam);
    }

    @Override
    public Class<?> getEvaluationType() { return Vector3.class; }

    public Object execute(SpellContext ctx) throws SpellRuntimeException {
        Player p = ctx.caster;
        if (!p.level().isClientSide()) {
            Number n = getParamValue(ctx, SeveParam);
            int s = n.intValue();
            p.getCapability(PlayerDataProvider.CAP).ifPresent(data ->
                    v = data.getValue("vector3" + s));
            p.getCapability(PlayerDataProvider.CAP).ifPresent(data ->
                    h = data.getValue("vector3" + s + "_vec"));
        }
        return v;
    }
}
