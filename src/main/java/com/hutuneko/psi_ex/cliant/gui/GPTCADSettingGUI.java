package com.hutuneko.psi_ex.cliant.gui;

import com.hutuneko.psi_ex.cliant.menu.GPTCADSettingMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.client.renderer.RenderType;

public class GPTCADSettingGUI extends AbstractContainerScreen<GPTCADSettingMenu> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation("modid", "textures/gui/my_gui.png");

    public GPTCADSettingGUI(GPTCADSettingMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        // 背景を描画
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        graphics.fill(RenderType.gui(), x, y, 0, 0, imageWidth, imageHeight);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);
    }
}
