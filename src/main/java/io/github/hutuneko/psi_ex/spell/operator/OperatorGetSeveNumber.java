package io.github.hutuneko.psi_ex.spell.operator;

import io.github.hutuneko.psi_ex.system.capability.PlayerDataProvider;
import net.minecraft.world.entity.player.Player;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellParam;
import vazkii.psi.api.spell.SpellRuntimeException;
import vazkii.psi.api.spell.param.ParamNumber;
import vazkii.psi.api.spell.piece.PieceOperator;

public class OperatorGetSeveNumber extends PieceOperator {
    private ParamNumber SeveParam;
    private Number v;
    public OperatorGetSeveNumber(Spell spell) { super(spell); }

    @Override
    public void initParams() {
        SeveParam = new ParamNumber(SpellParam.GENERIC_NAME_NUMBER, SpellParam.GREEN, false, false);
        addParam(SeveParam);
    }

    @Override
    public Class<?> getEvaluationType() { return Number.class; }

    public Object execute(SpellContext ctx) throws SpellRuntimeException {
        Player p = ctx.caster;
        Number n = getParamValue(ctx, SeveParam);
        int s = n.intValue();
        p.getCapability(PlayerDataProvider.CAP).ifPresent(data ->
                v = data.getD("number" + s));
        return v;
    }
}
