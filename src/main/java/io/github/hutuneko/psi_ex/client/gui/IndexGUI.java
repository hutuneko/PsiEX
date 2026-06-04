package io.github.hutuneko.psi_ex.client.gui;

import io.github.hutuneko.psi_ex.PsiEX;
import io.github.hutuneko.psi_ex.api.menu.IndexMenu;
import io.github.hutuneko.psi_ex.net.IndexMenuPacket;
import io.github.hutuneko.psi_ex.net.Net;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * AbstractContainerScreen を継承した標準的なGUI実装
 * CraftingMenu / CraftingTableBlock の構造に準拠
 */
@OnlyIn(Dist.CLIENT)
public class IndexGUI extends AbstractContainerScreen<IndexMenu> {

    private static final ResourceLocation GUI_TEXTURE =
            new ResourceLocation(PsiEX.MOD_ID, "textures/gui/indexgui.png");

    // ===== GUIテクスチャサイズ =====
    private static final int GUI_WIDTH = 191;
    private static final int GUI_HEIGHT = 182;
    private static final int TEXTURE_WIDTH = 191;
    private static final int TEXTURE_HEIGHT = 182;

    // ===== スペルリストエリア =====
    private static final int LIST_X = 8;
    private static final int LIST_Y = 8;
    private static final int LIST_W = 152;
    private static final int LIST_ROW_HEIGHT = 18;
    private static final int LIST_VISIBLE_ROWS = 3;
    private static final int LIST_H = LIST_VISIBLE_ROWS * LIST_ROW_HEIGHT;

    // ===== スクロールバー =====
    private static final int SCROLLBAR_X = 162;
    private static final int SCROLLBAR_Y = 8;
    private static final int SCROLLBAR_WIDTH = 6;
    private static final int SCROLLBAR_HEIGHT = LIST_H;

    // ===== 詳細パネル =====
    private static final int DETAIL_X = 8;
    private static final int DETAIL_Y = 62;
    private static final int DETAIL_W = 152;
    private static final int DETAIL_H = 16;

    // ===== Description入力欄 =====
    private static final int DESC_X = 8;
    private static final int DESC_Y = 74;
    private static final int DESC_W = 152;
    private static final int DESC_H = 12;

    private int scrollOffset = 0;
    private boolean isScrolling = false;
    private EditBox descriptionBox;
    private int lastEditedIndex = -1;

    public IndexGUI(IndexMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        this.imageWidth = GUI_WIDTH;
        this.imageHeight = GUI_HEIGHT;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    protected void init() {
        super.init();
        this.titleLabelX = 8;
        this.titleLabelY = -10;

        // Description入力欄の初期化
        descriptionBox = new EditBox(this.font, this.leftPos + DESC_X, this.topPos + DESC_Y, DESC_W, DESC_H, Component.empty());
        descriptionBox.setMaxLength(128);
        descriptionBox.setResponder(this::onDescriptionChanged);
        this.addRenderableWidget(descriptionBox);
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        // 選択が変わったらdescriptionを更新
        int selected = this.menu.getSelectedSpellIndex();
        if (selected != lastEditedIndex && selected >= 0) {
            lastEditedIndex = selected;
            String desc = this.menu.getSpellDescription(selected);
            descriptionBox.setValue(desc != null ? desc : "");
        } else if (selected < 0) {
            lastEditedIndex = -1;
            descriptionBox.setValue("");
            descriptionBox.setEditable(false);
        } else {
            descriptionBox.setEditable(true);
        }
    }

    private void onDescriptionChanged(String text) {
        int selected = this.menu.getSelectedSpellIndex();
        if (selected >= 0 && selected == lastEditedIndex) {
            // サーバーに送信
            if (!this.minecraft.hasSingleplayerServer()) {
                Net.CHANNEL.sendToServer(new IndexMenuPacket(IndexMenuPacket.Action.SET_DESCRIPTION, selected, text));
            }
            this.menu.setSpellDescription(selected, text);
        }
    }

    // ===== レンダリング =====
    @Override
    public void render(@NotNull GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        this.renderBackground(pGuiGraphics);
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        this.renderTooltip(pGuiGraphics, pMouseX, pMouseY);
    }

    @Override
    protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        int leftPos = this.leftPos;
        int topPos = this.topPos;

        // GUI背景テクスチャ
        pGuiGraphics.blit(GUI_TEXTURE, leftPos, topPos, 0, 0,
                this.imageWidth, this.imageHeight, TEXTURE_WIDTH, TEXTURE_HEIGHT);

        // スペルリスト
        renderSpellList(pGuiGraphics, leftPos, topPos, pMouseX, pMouseY);

        // スクロールバー
        renderScrollbar(pGuiGraphics, leftPos, topPos, pMouseX, pMouseY);

        // 詳細パネル
        renderDetailPanel(pGuiGraphics, leftPos, topPos);
    }

    @Override
    protected void renderLabels(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY) {
        pGuiGraphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 0xFFFFFFFF, false);
        pGuiGraphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 0x404040, false);
    }

    private void renderSpellList(GuiGraphics pGuiGraphics, int leftPos, int topPos, int mouseX, int mouseY) {
        int listStartX = leftPos + LIST_X;
        int listStartY = topPos + LIST_Y;
        int selected = this.menu.getSelectedSpellIndex();
        List<String> spellNames = this.menu.getSpellNames();

        for (int i = 0; i < LIST_VISIBLE_ROWS; i++) {
            int itemIndex = scrollOffset + i;
            if (itemIndex >= spellNames.size()) break;

            int slotY = listStartY + (i * LIST_ROW_HEIGHT);
            boolean isSelected = (itemIndex == selected);
            boolean isHovered = mouseX >= listStartX && mouseX <= listStartX + LIST_W &&
                    mouseY >= slotY && mouseY <= slotY + LIST_ROW_HEIGHT;

            renderWideSlot(pGuiGraphics, listStartX, slotY, LIST_W, LIST_ROW_HEIGHT,
                    spellNames.get(itemIndex), isSelected, isHovered);
        }
    }

    private void renderWideSlot(GuiGraphics pGuiGraphics, int x, int y, int width, int height,
                                String name, boolean isSelected, boolean isHovered) {
        int bgColor = isSelected ? 0xFF6E6E6E : (isHovered ? 0xFF505050 : 0xFF3A3A3A);
        pGuiGraphics.fill(x, y, x + width, y + height, bgColor);

        int lightColor = isSelected ? 0xFFFFFFFF : 0xFFAAAAAA;
        pGuiGraphics.fill(x, y, x + width, y + 1, lightColor);
        pGuiGraphics.fill(x, y, x + 1, y + height, lightColor);
        int darkColor = isSelected ? 0xFF000000 : 0xFF373737;
        pGuiGraphics.fill(x, y + height - 1, x + width, y + height, darkColor);
        pGuiGraphics.fill(x + width - 1, y, x + width, y + height, darkColor);

        pGuiGraphics.fill(x + 2, y + 2, x + 16, y + 16, 0xFF222222);

        int nameColor = isSelected ? 0xFFFFFF00 : 0xFFFFFFFF;
        pGuiGraphics.drawString(this.font, name, x + 18, y + 5, nameColor);
    }

    private void renderScrollbar(GuiGraphics pGuiGraphics, int leftPos, int topPos, int mouseX, int mouseY) {
        int trackX = leftPos + SCROLLBAR_X;
        int trackY = topPos + SCROLLBAR_Y;

        pGuiGraphics.fill(trackX, trackY, trackX + SCROLLBAR_WIDTH, trackY + SCROLLBAR_HEIGHT, 0xFF000000);

        int totalItems = this.menu.getSpellCount();
        int maxScroll = Math.max(0, totalItems - LIST_VISIBLE_ROWS);
        if (maxScroll == 0) return;

        float scrollPercent = (float) scrollOffset / maxScroll;
        int thumbHeight = Math.max(10, SCROLLBAR_HEIGHT * LIST_VISIBLE_ROWS / Math.max(totalItems, LIST_VISIBLE_ROWS));
        int thumbY = trackY + (int) ((SCROLLBAR_HEIGHT - thumbHeight) * scrollPercent);

        boolean hovered = mouseX >= trackX && mouseX <= trackX + SCROLLBAR_WIDTH &&
                mouseY >= thumbY && mouseY <= thumbY + thumbHeight;
        int thumbColor = hovered ? 0xFFAAAAAA : 0xFF888888;

        pGuiGraphics.fill(trackX, thumbY, trackX + SCROLLBAR_WIDTH, thumbY + thumbHeight, thumbColor);
    }

    private void renderDetailPanel(GuiGraphics pGuiGraphics, int leftPos, int topPos) {
        int selected = this.menu.getSelectedSpellIndex();
        if (selected >= 0 && selected < this.menu.getSpellCount()) {
            String selectedName = this.menu.getSpellName(selected);
            pGuiGraphics.drawString(this.font, selectedName, leftPos + DETAIL_X + 4, topPos + DETAIL_Y, 0xFF000000);
        }
    }

    // ===== マウス入力 =====
    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        // EditBoxがフォーカスを持っている場合は先に処理
        if (descriptionBox.isMouseOver(pMouseX, pMouseY)) {
            return descriptionBox.mouseClicked(pMouseX, pMouseY, pButton);
        }

        // スペルリストのクリック
        int listStartX = this.leftPos + LIST_X;
        int listStartY = this.topPos + LIST_Y;

        for (int i = 0; i < LIST_VISIBLE_ROWS; i++) {
            int itemIndex = scrollOffset + i;
            if (itemIndex >= this.menu.getSpellCount()) break;

            int slotY = listStartY + (i * LIST_ROW_HEIGHT);
            if (pMouseX >= listStartX && pMouseX <= listStartX + LIST_W &&
                    pMouseY >= slotY && pMouseY <= slotY + LIST_ROW_HEIGHT) {
                // サーバーに選択変更を通知
                if (!this.minecraft.hasSingleplayerServer()) {
                    Net.CHANNEL.sendToServer(new IndexMenuPacket(IndexMenuPacket.Action.SET_SELECTED, itemIndex));
                }
                this.menu.setSelectedSpellIndex(itemIndex);
                return true;
            }
        }

        // スクロールバーのクリック
        int trackX = this.leftPos + SCROLLBAR_X;
        int trackY = this.topPos + SCROLLBAR_Y;
        if (pMouseX >= trackX && pMouseX <= trackX + SCROLLBAR_WIDTH &&
                pMouseY >= trackY && pMouseY <= trackY + SCROLLBAR_HEIGHT) {
            isScrolling = true;
            return true;
        }

        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    @Override
    public boolean mouseReleased(double pMouseX, double pMouseY, int pButton) {
        isScrolling = false;
        return super.mouseReleased(pMouseX, pMouseY, pButton);
    }

    @Override
    public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
        if (isScrolling) {
            int trackY = this.topPos + SCROLLBAR_Y;
            int totalItems = this.menu.getSpellCount();
            int maxScroll = Math.max(0, totalItems - LIST_VISIBLE_ROWS);

            if (maxScroll > 0) {
                float percent = (float) (pMouseY - trackY) / SCROLLBAR_HEIGHT;
                percent = Math.max(0, Math.min(1, percent));
                scrollOffset = (int) (percent * maxScroll);
            }
            return true;
        }
        return super.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
    }

    @Override
    public boolean mouseScrolled(double pMouseX, double pMouseY, double pDelta) {
        int totalItems = this.menu.getSpellCount();
        int maxScroll = Math.max(0, totalItems - LIST_VISIBLE_ROWS);

        if (pDelta > 0) {
            scrollOffset = Math.max(0, scrollOffset - 1);
        } else {
            scrollOffset = Math.min(maxScroll, scrollOffset + 1);
        }
        return true;
    }

    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        // EditBoxがフォーカスを持っている場合はそちらに渡す
        if (descriptionBox.isFocused()) {
            if (pKeyCode == 256) { // ESC
                descriptionBox.setFocused(false);
                return true;
            }
            return descriptionBox.keyPressed(pKeyCode, pScanCode, pModifiers);
        }
        return super.keyPressed(pKeyCode, pScanCode, pModifiers);
    }

    @Override
    public boolean charTyped(char pCodePoint, int pModifiers) {
        if (descriptionBox.isFocused()) {
            return descriptionBox.charTyped(pCodePoint, pModifiers);
        }
        return super.charTyped(pCodePoint, pModifiers);
    }
}