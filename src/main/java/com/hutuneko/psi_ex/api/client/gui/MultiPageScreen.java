package com.hutuneko.psi_ex.api.client.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class MultiPageScreen extends Screen {
    protected int leftPos, topPos;
    protected int imageWidth = 174;
    protected int imageHeight = 184;

    protected final List<Page> pages = new ArrayList<>();
    protected int currentPageIndex = 0;

    protected MultiPageScreen(Component title) {
        super(title);
    }

    public interface Page {
        void init(MultiPageScreen parent, int x, int y, int width, int height);
        void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick);
        void tick();

        boolean mouseClicked(double mouseX, double mouseY, int button);
        boolean mouseReleased(double mouseX, double mouseY, int button);
        boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY);
        boolean mouseScrolled(double mouseX, double mouseY, double delta);
        void mouseMoved(double mouseX, double mouseY);
        boolean keyReleased(int keyCode, int scanCode, int modifiers);
        boolean charTyped(char codePoint, int modifiers);

        void onPageSelected();
        void onPageDeselected();
    }

    @Override
    protected void init() {
        super.init();
        this.leftPos = (this.width - this.imageWidth) / 2;
        this.topPos = (this.height - this.imageHeight) / 2;

        // ページ切り替えボタン
        this.addRenderableWidget(Button.builder(Component.literal("◀"), btn -> prevPage())
                .bounds(this.leftPos - 30, this.topPos + 100, 25, 20).build());
        this.addRenderableWidget(Button.builder(Component.literal("▶"), btn -> nextPage())
                .bounds(this.leftPos + this.imageWidth + 5, this.topPos + 100, 25, 20).build());

        // 全ページを初期化
        for (Page page : pages) {
            page.init(this, leftPos, topPos, imageWidth, imageHeight);
        }

        initCurrentPage();
    }

    protected void initCurrentPage() {
        if (!pages.isEmpty() && currentPageIndex >= 0 && currentPageIndex < pages.size()) {
            Page page = pages.get(currentPageIndex);
            page.onPageSelected();
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (!pages.isEmpty()) {
            pages.get(currentPageIndex).tick();
        }
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics);

        if (!pages.isEmpty()) {
            pages.get(currentPageIndex).render(graphics, mouseX, mouseY, partialTick);
        }

        // ページインジケーター
        String pageText = (currentPageIndex + 1) + " / " + pages.size();
        graphics.drawCenteredString(this.font, pageText,
                this.leftPos + this.imageWidth / 2, this.topPos - 15, 0xFFFFFF);

        super.render(graphics, mouseX, mouseY, partialTick);
    }

    // === イベントフォワーディング ===

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        super.mouseMoved(mouseX, mouseY);
        if (!pages.isEmpty()) {
            pages.get(currentPageIndex).mouseMoved(mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!pages.isEmpty()) {
            if (pages.get(currentPageIndex).mouseClicked(mouseX, mouseY, button)) {
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (!pages.isEmpty()) {
            if (pages.get(currentPageIndex).mouseReleased(mouseX, mouseY, button)) {
                return true;
            }
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (!pages.isEmpty()) {
            if (pages.get(currentPageIndex).mouseDragged(mouseX, mouseY, button, dragX, dragY)) {
                return true;
            }
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (!pages.isEmpty()) {
            if (pages.get(currentPageIndex).mouseScrolled(mouseX, mouseY, delta)) {
                return true;
            }
        }
        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        if (!pages.isEmpty()) {
            if (pages.get(currentPageIndex).keyReleased(keyCode, scanCode, modifiers)) {
                return true;
            }
        }
        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (!pages.isEmpty()) {
            if (pages.get(currentPageIndex).charTyped(codePoint, modifiers)) {
                return true;
            }
        }
        return super.charTyped(codePoint, modifiers);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return super.shouldCloseOnEsc();
    }

    // === ページ管理 ===

    protected void addPage(Page page) {
        this.pages.add(page);
    }

    protected void nextPage() {
        if (currentPageIndex < pages.size() - 1) {
            pages.get(currentPageIndex).onPageDeselected();
            currentPageIndex++;
            initCurrentPage();
        }
    }

    protected void prevPage() {
        if (currentPageIndex > 0) {
            pages.get(currentPageIndex).onPageDeselected();
            currentPageIndex--;
            initCurrentPage();
        }
    }
    protected void setPage(int page){
        if (page <= pages.size()){
            pages.get(currentPageIndex).onPageDeselected();
            currentPageIndex = page;
            initCurrentPage();
        }
    }
    private final List<AbstractWidget> pageWidgets = new ArrayList<>();

    public void clearPageWidgets() {
        this.renderables.removeAll(pageWidgets);
        this.children().removeAll(pageWidgets);
        pageWidgets.clear();
    }

    public <T extends AbstractWidget> T addPageWidget(T widget) {
        this.addRenderableWidget(widget);
        pageWidgets.add(widget);
        return widget;
    }
}