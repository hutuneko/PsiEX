package io.github.hutuneko.psi_ex.system;

import net.minecraft.network.chat.Component;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellPiece;

public interface PieceCondition {
    boolean test(SpellContext ctx, SpellPiece piece);
    default Component failMessage() { return null; }
}
