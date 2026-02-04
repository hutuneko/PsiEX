package com.hutuneko.psi_ex.api.client.gui;

import com.hutuneko.psi_ex.block.MultiPageTileProgrammer;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MultiProgrammerScreen extends MultiPageScreen {

    private final MultiPageTileProgrammer programmer;

    protected MultiProgrammerScreen(Component title, MultiPageTileProgrammer programmer, int page) {
        super(title);
        this.programmer = programmer;
        int i = 0;
        while (i < 5) {
            addPage(new GuiProgrammerPage(programmer, i));
            i++;
        }
        setPage(page);
    }

    public static void openGUI(MultiPageTileProgrammer programmer, int page) {
        Minecraft.getInstance().setScreen(new MultiProgrammerScreen(
                Component.translatable("psi_ex.gui.multiprogrammer"), programmer, page));
    }

    @Override
    protected void nextPage() {
        // 現在のページの変更を保存
        saveCurrentPage();

        super.nextPage();
        programmer.setCurrentPage(currentPageIndex,true);

        // 新しいページのデータをロード
        loadCurrentPage();
    }

    @Override
    protected void prevPage() {
        saveCurrentPage();

        super.prevPage();
        programmer.setCurrentPage(currentPageIndex,true);

        loadCurrentPage();
    }

    @Override
    protected void setPage(int page) {
        if (currentPageIndex >= 0 && currentPageIndex < pages.size()) {
            saveCurrentPage();
        }

        super.setPage(page);
        programmer.setCurrentPage(page,true);

        loadCurrentPage();
    }

    private void saveCurrentPage() {
        Page currentPage = pages.get(currentPageIndex);
        if (currentPage instanceof GuiProgrammerPage programmerPage) {
            programmerPage.onPageDeselected();
        }
    }

    private void loadCurrentPage() {
        Page currentPage = pages.get(currentPageIndex);
        if (currentPage instanceof GuiProgrammerPage programmerPage) {
            programmerPage.onPageSelected();
        }
    }

    @Override
    public void onClose() {
        saveCurrentPage();
        super.onClose();
    }
}