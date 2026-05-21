package io.github.hutuneko.psi_ex.block;

import io.github.hutuneko.psi_ex.compat.PsiEXRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.common.block.tile.TileProgrammer;

import java.util.Objects;

public class MultiPageTileProgrammer extends TileProgrammer {
    private static final String TAG_PAGES = "pages";
    private static final String TAG_CURRENT_PAGE = "currentpage";

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
    public @NotNull BlockEntityType<?> getType() {
        return PsiEXRegistry.MULTI_PROGRAMMER != null ? PsiEXRegistry.MULTI_PROGRAMMER.get() : super.getType();
    }
    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        
        this.currentPage = tag.getInt(TAG_CURRENT_PAGE);

        if (currentPage < 0 || currentPage >= pageSpells.length) {
            currentPage = 0;
        }

        if (tag.contains(TAG_PAGES, 9)) { 
            ListTag pagesTag = tag.getList(TAG_PAGES, 10);

            for (int i = 0; i < Math.min(pageSpells.length, pagesTag.size()); i++) {
                CompoundTag spellTag = pagesTag.getCompound(i);

                Spell loaded = Spell.createFromNBT(spellTag);
                pageSpells[i] = Objects.requireNonNullElseGet(loaded, Spell::new);
            }
        }
        this.spell = pageSpells[currentPage];
        
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        if (currentPage >= 0 && currentPage < pageSpells.length && this.spell != null) {
            pageSpells[currentPage] = this.spell;
        }
        ListTag pagesTag = new ListTag();
        for (Spell pageSpell : pageSpells) {
            CompoundTag spellTag = new CompoundTag();
            if (pageSpell != null) {
                pageSpell.writeToNBT(spellTag);
            }
            pagesTag.add(spellTag);
        }
        tag.put(TAG_PAGES, pagesTag);
        tag.putInt(TAG_CURRENT_PAGE, this.currentPage);

        super.saveAdditional(tag);
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
        if (page >= 0 && page < pageSpells.length && spell != null) {
            pageSpells[page] = spell;
            if (page == currentPage) {
                this.spell = spell;
            }
            setChanged();
            
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
    @Override
    public @NotNull CompoundTag getUpdateTag() {
        
        CompoundTag tag = super.getUpdateTag();
        saveAdditional(tag);
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        
        super.handleUpdateTag(tag);
        load(tag); 
    }
}
