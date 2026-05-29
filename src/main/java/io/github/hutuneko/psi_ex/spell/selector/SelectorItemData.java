package io.github.hutuneko.psi_ex.spell.selector;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellRuntimeException;
import vazkii.psi.api.spell.EnumPieceType;
import vazkii.psi.api.spell.piece.PieceSelector;

import java.util.List;

public class SelectorItemData extends PieceSelector {

    public SelectorItemData(Spell spell) {
        super(spell);
    }

    @Override
    public EnumPieceType getPieceType() {
        return EnumPieceType.SELECTOR;
    }

    @Override
    public Class<CompoundTag> getEvaluationType() {
        return CompoundTag.class;
    }

    @Override
    public CompoundTag execute(SpellContext context) throws SpellRuntimeException {
        Player player = context.caster;
        ItemStack held = player.getMainHandItem();
        if (held.isEmpty()) {
            throw new SpellRuntimeException("メインハンドにアイテムを持ってください");
        }

        List<ItemStack> inv = player.getInventory().items;
        int idx = inv.indexOf(held);
        if (idx < 0) {
            throw new SpellRuntimeException("インベントリに手持ちアイテムが見つかりません");
        }
        int right = idx + 1;
        if (right >= inv.size()) {
            throw new SpellRuntimeException("右隣にアイテムがありません");
        }

        ItemStack neighbor = inv.get(right);
        if (neighbor.isEmpty()) {
            throw new SpellRuntimeException("右隣のアイテムが空です");
        }
        return neighbor.getOrCreateTag();
    }

}
