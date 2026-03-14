package com.hutuneko.psi_ex.api.client.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import java.util.ArrayList;
import java.util.List;

public class NumberedPageScreen extends Screen implements IMultiPageScreen {
    private final List<Page> pages = new ArrayList<>();
    private int currentPageIndex = 0;

    protected int imageWidth = 174;
    protected int imageHeight = 184;
    protected int leftPos, topPos;

    // スクロール用
    private double scrollAmount = 0;
    private int maxScroll = 0;
    private final int BTN_SIZE = 25;
    private final int SPACING = 4;

    public NumberedPageScreen(Component title) {
        super(title);
    }

    @Override
    protected void init() {
        this.leftPos = (this.width - this.imageWidth) / 2;
        this.topPos = (this.height - this.imageHeight) / 2;

        this.initPages(leftPos, topPos, imageWidth, imageHeight);
        this.rebuildButtonGrid();
    }

    private void rebuildButtonGrid() {
        this.clearWidgets();

        int xStart = this.leftPos;
        int yStart = this.topPos - 50; // 配置開始位置
        int availableWidth = this.imageWidth;

        int buttonsPerRow = Math.max(1, availableWidth / (BTN_SIZE + SPACING));

        for (int i = 0; i < pages.size(); i++) {
            int index = i;
            int row = i / buttonsPerRow;
            int col = i % buttonsPerRow;

            int x = xStart + (col * (BTN_SIZE + SPACING));
            // スクロール量を考慮した座標（render内でScissor制御が必要）
            int y = yStart + (row * (BTN_SIZE + SPACING)) - (int)scrollAmount;

            this.addRenderableWidget(Button.builder(
                            Component.literal(String.valueOf(i + 1)),
                            btn -> this.setPage(index))
                    .bounds(x, y, BTN_SIZE, BTN_SIZE)
                    .build());
        }

        int totalRows = (int) Math.ceil((double) pages.size() / buttonsPerRow);
        this.maxScroll = Math.max(0, (totalRows * (BTN_SIZE + SPACING)) - 40); // 40は表示高さ
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        // ボタンエリアの上でホイールを回した時にスクロール
        if (mouseY < this.topPos && mouseY > this.topPos - 50) {
            this.scrollAmount = Math.max(0, Math.min(maxScroll, scrollAmount - delta * 10));
            this.rebuildButtonGrid(); // 座標再計算
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics);

        // ページ内容の描画
        this.renderCurrentPage(graphics, mouseX, mouseY, partialTick);

        // GUIの横幅に合わせたボタン描画（Scissorで範囲外を隠す）
        // ※ yStartから一定範囲のみ表示
        graphics.enableScissor(leftPos, topPos - 50, leftPos + imageWidth, topPos - 5);
        super.render(graphics, mouseX, mouseY, partialTick);
        graphics.disableScissor();
    }
    @Override
    public void addPage(Page page) {
        this.pages.add(page);
    }
    // --- IMultiPageScreenの実装 ---
    @Override public List<Page> getPages() { return pages; }
    @Override public int getCurrentPageIndex() { return currentPageIndex; }
    @Override public void setCurrentPageIndex(int index) { this.currentPageIndex = index; }
}