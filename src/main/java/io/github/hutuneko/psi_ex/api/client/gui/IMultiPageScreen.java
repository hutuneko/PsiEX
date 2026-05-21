package io.github.hutuneko.psi_ex.api.client.gui;

import net.minecraft.client.gui.GuiGraphics;
import java.util.List;

public interface IMultiPageScreen {
    // 状態管理用の抽象メソッド（実装クラスでフィールドを定義する）
    List<Page> getPages();
    int getCurrentPageIndex();
    void setCurrentPageIndex(int index);
    void addPage(Page page);
    // 共通ロジック
    default void initPages(int leftPos, int topPos, int width, int height) {
        for (Page page : getPages()) {
            page.init(null, leftPos, topPos, width, height); // parentが必要なら適宜調整
        }
        updateCurrentPage();
    }

    default void updateCurrentPage() {
        if (!getPages().isEmpty() && getCurrentPageIndex() >= 0 && getCurrentPageIndex() < getPages().size()) {
            getPages().get(getCurrentPageIndex()).onPageSelected();
        }
    }

    default void setPage(int index) {
        if (index >= 0 && index < getPages().size()) {
            getPages().get(getCurrentPageIndex()).onPageDeselected();
            setCurrentPageIndex(index);
            updateCurrentPage();
        }
    }

    default void renderCurrentPage(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        if (!getPages().isEmpty()) {
            getPages().get(getCurrentPageIndex()).render(graphics, mouseX, mouseY, partialTick);
        }
    }
    
}