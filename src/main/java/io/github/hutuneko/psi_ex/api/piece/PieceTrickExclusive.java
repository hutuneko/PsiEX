package io.github.hutuneko.psi_ex.api.piece;

import net.minecraft.world.entity.player.Player;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.piece.PieceTrick;

public abstract class PieceTrickExclusive extends PieceTrick {
    public PieceTrickExclusive(Spell spell) {
        super(spell);
    }
    public abstract boolean isCast(Player caster, SpellContext ctx);
}
