package io.github.hutuneko.psi_ex.api.client.gui;

import net.minecraft.client.gui.GuiGraphics;

public interface Page {
    void pageInit(MultiPageScreen parent, int x, int y, int width, int height);
    void pageRender(GuiGraphics graphics, int mouseX, int mouseY, float partialTick);
    void pageTick();

    boolean pageMouseClicked(double mouseX, double mouseY, int button);
    boolean pageMouseReleased(double mouseX, double mouseY, int button);
    boolean pageMouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY);
    boolean pageMouseScrolled(double mouseX, double mouseY, double delta);
    void pageMouseMoved(double mouseX, double mouseY);
    boolean pageKeyReleased(int keyCode, int scanCode, int modifiers);
    boolean pageCharTyped(char codePoint, int modifiers);

    void onPageSelected();
    void onPageDeselected();
}