package com.hutuneko.psi_ex.system;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import vazkii.psi.api.spell.ISpellAcceptor;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.common.item.ItemSpellDrive;

import javax.annotation.Nullable;
import java.util.ArrayList;

public record MySpellAcceptor(ItemStack stack) implements ISpellAcceptor {

    @Override
    public boolean containsSpell() {
        return ItemSpellDrive.getSpell(stack) != null;
    }

    @Nullable
    @Override
    public Spell getSpell() {
        return ItemSpellDrive.getSpell(stack);
    }

    @Override
    public void setSpell(Player player, Spell spell) {
        ItemSpellDrive.setSpell(stack, spell);
    }

    @Override
    public boolean castableFromSocket() {
        return true;
    }

    @Override
    public ArrayList<Entity> castSpell(SpellContext ctx) {
        ctx.cspell.safeExecute(ctx);
        return null;
    }

    @Override
    public double getCostModifier() {
        return 1.0f;
    }
}

