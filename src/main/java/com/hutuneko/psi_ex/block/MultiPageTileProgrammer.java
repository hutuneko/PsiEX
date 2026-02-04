package com.hutuneko.psi_ex.block;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.common.block.tile.TileProgrammer;

public class MultiPageTileProgrammer extends TileProgrammer {

    // 複数のスペルを保持（ページごと）
    private final Spell[] pageSpells = new Spell[5];
    private int currentPage = 0;

    public MultiPageTileProgrammer(BlockPos pos, BlockState state) {
        super(pos, state);

        for (int i = 0; i < pageSpells.length; i++) {
            pageSpells[i] = new Spell();
        }

        if (this.spell == null) {
            this.spell = pageSpells[0];
        }
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);

        this.currentPage = tag.getInt("currentpage");

        for (int i = 0; i < pageSpells.length; i++) {
            if (tag.contains("spell" + i)) {
                pageSpells[i] = Spell.createFromNBT(tag.getCompound("spell" + i));
            }
        }

        if (currentPage >= 0 && currentPage < pageSpells.length) {
            this.spell = pageSpells[currentPage];
        }
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        if (currentPage >= 0 && currentPage < pageSpells.length) {
            pageSpells[currentPage] = this.spell;
        }

        super.saveAdditional(tag);

        // 他のページも保存
        tag.putInt("currentpage", this.currentPage);
        for (int i = 0; i < pageSpells.length; i++) {
            if (pageSpells[i] != null && i != currentPage) {  // currentPage 以外
                CompoundTag pageSpellTag = new CompoundTag();
                pageSpells[i].writeToNBT(pageSpellTag);
                tag.put("spell" + i, pageSpellTag);
            }
        }
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public Spell getPageSpell(int page) {
        if (page >= 0 && page < pageSpells.length) {
            return pageSpells[page];
        }
        return this.spell;
    }

    public void setPageSpell(int page, Spell spell) {
        if (page >= 0 && page < pageSpells.length) {
            pageSpells[page] = spell;
            if (page == currentPage) {
                this.spell = spell;
            }
        }
    }

    public void setCurrentPage(int page, boolean updateSpell ) {
        this.currentPage = page;
        if (page >= 0 && page < pageSpells.length) {
            if (updateSpell){
                this.spell = pageSpells[page];
            }
            setChanged();
            if (level != null) {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }
    }
}
