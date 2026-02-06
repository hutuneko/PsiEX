package com.hutuneko.psi_ex.api.client.gui;

import com.hutuneko.psi_ex.PsiEX;
import com.hutuneko.psi_ex.block.MultiPageTileProgrammer;
import com.hutuneko.psi_ex.net.C2SProgrammerPagePacket;
import com.hutuneko.psi_ex.net.C2SSpellPagePacket;
import com.hutuneko.psi_ex.net.Net;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import vazkii.psi.api.spell.Spell;

@OnlyIn(Dist.CLIENT)
public class MultiProgrammerScreen extends MultiPageScreen {

    private final MultiPageTileProgrammer programmer;

    protected MultiProgrammerScreen(Component title, MultiPageTileProgrammer programmer, int page) {
        super(title);
        this.programmer = programmer;
        for (int i = 0; i < 5; i++) {
            addPage(new GuiProgrammerPage(programmer, i));
        }
        setPage(page);
    }

    @OnlyIn(Dist.CLIENT)
    public static void openGUI(MultiPageTileProgrammer programmer, int page) {
        Minecraft.getInstance().setScreen(new MultiProgrammerScreen(
                Component.translatable("psi_ex.gui.multiprogrammer"), programmer, page));
    }

    @Override
    protected void nextPage() {
        saveAndSyncCurrentPage();

        super.nextPage();

        programmer.setCurrentPage(currentPageIndex, false);
        Net.CHANNEL.sendToServer(new C2SProgrammerPagePacket(
                programmer.getBlockPos(), currentPageIndex));
        loadCurrentPage();
    }

    @Override
    protected void prevPage() {
        saveAndSyncCurrentPage();

        super.prevPage();

        programmer.setCurrentPage(currentPageIndex, false);
        Net.CHANNEL.sendToServer(new C2SProgrammerPagePacket(
                programmer.getBlockPos(), currentPageIndex));
        loadCurrentPage();
    }

    @Override
    protected void setPage(int page) {
        if (currentPageIndex >= 0 && currentPageIndex < pages.size()) {
            saveAndSyncCurrentPage();
        }

        super.setPage(page);

        programmer.setCurrentPage(page, false);
        Net.CHANNEL.sendToServer(new C2SProgrammerPagePacket(
                programmer.getBlockPos(), page));
        loadCurrentPage();
    }

    private void saveAndSyncCurrentPage() {
        Page currentPageObj = pages.get(currentPageIndex);
        if (currentPageObj instanceof GuiProgrammerPage programmerPage) {
            programmerPage.onPageDeselected();
            Spell spell = programmerPage.getSpell();
            programmer.setPageSpell(currentPageIndex, spell);
            CompoundTag spellTag = new CompoundTag();
            spell.writeToNBT(spellTag);
            Net.CHANNEL.sendToServer(new C2SSpellPagePacket(
                    programmer.getBlockPos(),
                    currentPageIndex,
                    spellTag
            ));
        }
    }

    private void loadCurrentPage() {
        Page currentPageObj = pages.get(currentPageIndex);
        if (currentPageObj instanceof GuiProgrammerPage programmerPage) {
            programmerPage.onPageSelected();
        }
    }

    @Override
    public void onClose() {
        saveAndSyncCurrentPage();
        super.onClose();
    }
}