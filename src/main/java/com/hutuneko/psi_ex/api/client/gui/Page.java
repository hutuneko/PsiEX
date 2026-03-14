package com.hutuneko.psi_ex.api.client.gui;

import net.minecraft.client.gui.GuiGraphics;

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