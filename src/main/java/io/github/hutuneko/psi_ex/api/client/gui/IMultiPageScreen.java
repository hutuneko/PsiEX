package io.github.hutuneko.psi_ex.api.client.gui;

import java.util.List;

public interface IMultiPageScreen {
    // 状態管理用の抽象メソッド（実装クラスでフィールドを定義する）
    List<Page> getPages();
    int getCurrentPageIndex();
    void setCurrentPageIndex(int index);
    void addPage(Page page);

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
}