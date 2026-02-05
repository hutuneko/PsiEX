package com.hutuneko.psi_ex.block;

import com.hutuneko.psi_ex.PsiEX;
import com.hutuneko.psi_ex.compat.PsiEXRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.common.block.tile.TileProgrammer;

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

        PsiEX.LOGGER.error("===== load CALLED =====");
        PsiEX.LOGGER.error("Tag contents: {}", tag.getAllKeys());

        this.currentPage = tag.getInt(TAG_CURRENT_PAGE);
        PsiEX.LOGGER.error("Loaded currentPage from NBT: {}", currentPage);

        if (currentPage < 0 || currentPage >= pageSpells.length) {
            currentPage = 0;
        }

        if (tag.contains(TAG_PAGES, 9)) { 
            ListTag pagesTag = tag.getList(TAG_PAGES, 10);
            PsiEX.LOGGER.error("Found pages list with {} entries", pagesTag.size());

            for (int i = 0; i < Math.min(pageSpells.length, pagesTag.size()); i++) {
                CompoundTag spellTag = pagesTag.getCompound(i);
                PsiEX.LOGGER.error("Loading page {} from NBT, tag keys: {}", i, spellTag.getAllKeys());

                Spell loaded = Spell.createFromNBT(spellTag);
                if (loaded != null) {
                    PsiEX.LOGGER.error("Loaded spell {}: name={}, valid={}",
                            i, loaded.name, loaded.grid != null);
                    pageSpells[i] = loaded;
                } else {
                    PsiEX.LOGGER.error("Failed to load spell {}, creating new", i);
                    pageSpells[i] = new Spell();
                }
            }
        } else {
            PsiEX.LOGGER.error("No 'pages' tag found in NBT!");
        }

        this.spell = pageSpells[currentPage];
        PsiEX.LOGGER.error("Final spell set to page {}: {}", currentPage, this.spell.name);
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        PsiEX.LOGGER.error("saveAdditional called on class: {}", this.getClass().getName());
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

        PsiEX.LOGGER.error("saveAdditional: Saved {} pages at {}", pagesTag.size(), worldPosition);

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
            PsiEX.LOGGER.error("setPageSpell called for page {}, setChanged() invoked", page);
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
