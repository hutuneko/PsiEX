package com.hutuneko.psi_ex.api.piece;

import com.hutuneko.psi_ex.system.CuriosUtil;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.SlotResult;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellContext;

public abstract class PieceSkillTrick extends PieceTrickExclusive {
    Item skillItem;
    public PieceSkillTrick(Spell spell, @NotNull Item item) {
        super(spell);
        skillItem = item;
    }

    @Override
    public boolean isCast(Player caster, SpellContext ctx) {
        return getskillItem(caster);
    }

    private boolean getskillItem(Player player){
        SlotResult slotResult = CuriosUtil.findFirstByItem(player, skillItem).orElse(null);
        return slotResult != null;
    }
}
