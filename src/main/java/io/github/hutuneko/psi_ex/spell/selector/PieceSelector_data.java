package io.github.hutuneko.psi_ex.spell.selector;

import io.github.hutuneko.psi_ex.compat.PsiEXRegistry;
import io.github.hutuneko.psi_ex.item.ItemStorage;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellRuntimeException;
import vazkii.psi.api.spell.piece.PieceSelector;

public class PieceSelector_data extends PieceSelector {


    public PieceSelector_data(Spell spell) {
        super(spell);
    }

    @Override
    public Class<?> getEvaluationType() {
        return Double.class;
    }

    @Override
    public ListTag execute(SpellContext context) throws SpellRuntimeException {
        if (context.caster == null) {
            throw new SpellRuntimeException("Must be a player");
        }
        Player player = context.caster;

        ItemStack stack = player.getMainHandItem();
        if (stack.isEmpty() || stack.getItem() != PsiEXRegistry.STORAGE.get()) {
            throw new SpellRuntimeException("No storage item in hand");
        }

        return ItemStorage.getStoredBlocks(stack);
    }

}



