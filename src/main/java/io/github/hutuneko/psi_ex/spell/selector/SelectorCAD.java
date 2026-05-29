package io.github.hutuneko.psi_ex.spell.selector;

import io.github.hutuneko.psi_ex.system.capability.PlayerDataProvider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import vazkii.psi.api.spell.EnumPieceType;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellRuntimeException;
import vazkii.psi.api.spell.piece.PieceSelector;

public class SelectorCAD extends PieceSelector {
    public SelectorCAD(Spell spell) {
        super(spell);
    }

    @Override
    public EnumPieceType getPieceType() {
        return EnumPieceType.SELECTOR;
    }

    @Override
    public Class<Integer> getEvaluationType() {
        return Integer.class;
    }

    @Override
    public Object execute(SpellContext context) throws SpellRuntimeException {
        if (context.caster.level().isClientSide())throw new SpellRuntimeException("psi_ex.spellerror.cad-no-value");
        Player player = context.caster;
        final double[] i = new double[1];
        player.getCapability(PlayerDataProvider.CAP).ifPresent(data ->
                i[0] = data.getD("psi_ex:cad_data"));
        return i[0];
    }
}
