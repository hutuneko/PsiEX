package io.github.hutuneko.psi_ex.spell.trick;

import io.github.hutuneko.psi_ex.api.spellparam.ParamCompoundTag;
import net.minecraft.nbt.CompoundTag;
import vazkii.psi.api.spell.*;
import vazkii.psi.api.spell.piece.PieceTrick;

public class TrickExecuteSpell extends PieceTrick {

    private ParamCompoundTag spellParam;

    public TrickExecuteSpell(Spell spell) {
        super(spell);
    }
    @Override
    public void initParams() {
        spellParam = new ParamCompoundTag("spell");
        addParam(spellParam);
    }
    public void addToMetadata(SpellMetadata meta) throws SpellCompilationException {
        super.addToMetadata(meta);
        meta.addStat(EnumSpellStat.POTENCY, 5000);
        meta.addStat(EnumSpellStat.COST, 500);
    }
    @Override
    public Object execute(SpellContext ctx) throws SpellRuntimeException {
        CompoundTag v = (CompoundTag) this.getParamValue(ctx, spellParam);
        Spell spell = new Spell();
        spell.readFromNBT(v);
        SpellContext sc = new SpellContext().setSpell(spell).setPlayer(ctx.caster);
        CompiledSpell s = sc.cspell;
        s.safeExecute(sc);

        return null;
    }
}
