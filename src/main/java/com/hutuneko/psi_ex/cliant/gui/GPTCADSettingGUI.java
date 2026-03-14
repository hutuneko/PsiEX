package com.hutuneko.psi_ex.cliant.gui;

import com.hutuneko.psi_ex.api.client.gui.NumberedPageScreen;
import com.hutuneko.psi_ex.block.MultiPageTileProgrammer;
import net.minecraft.network.chat.Component;

public class GPTCADSettingGUI extends NumberedPageScreen {
    private final MultiPageTileProgrammer programmer;
    protected GPTCADSettingGUI(Component title, MultiPageTileProgrammer programmer, int page) {
        super(title);
        this.programmer = programmer;
        for (int i = 0; i < 99; i++) {
            addPage(new GuiProgrammerPage(programmer, i));
        }
        setPage(page);
    }
}
