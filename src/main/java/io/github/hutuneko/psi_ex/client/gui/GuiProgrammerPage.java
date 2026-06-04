package io.github.hutuneko.psi_ex.client.gui;

import io.github.hutuneko.psi_ex.api.client.gui.MultiPageScreen;
import io.github.hutuneko.psi_ex.api.client.gui.Page;
import io.github.hutuneko.psi_ex.block.MultiPageTileProgrammer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.client.gui.GuiProgrammer;

public class GuiProgrammerPage extends GuiProgrammer implements Page {

    private boolean isActive = false;
    private boolean initialized = false;
    private final MultiPageTileProgrammer multiProgrammer;
    private final int pageIndex;

    public GuiProgrammerPage(MultiPageTileProgrammer programmer, int page) {
        super(programmer, null);
        this.multiProgrammer = programmer;
        this.pageIndex = page;
        this.minecraft = Minecraft.getInstance();
        this.font = this.minecraft.font;

        Spell pageSpell = programmer.getPageSpell(page);
        if (pageSpell != null) {
            this.spell = pageSpell.copy();
        } else {
            this.spell = new Spell();
        }
    }

    @Override
    public void pageInit(MultiPageScreen parent, int x, int y, int width, int height) {
        this.minecraft = Minecraft.getInstance();
        this.font = this.minecraft.font;
        this.width = parent.width;
        this.height = parent.height;

        this.left = x;
        this.top = y;
        this.xSize = 174;
        this.ySize = 184;
        this.padLeft = 7;
        this.padTop = 7;

        this.gridLeft = this.left + this.padLeft;
        this.gridTop = this.top + this.padTop;
        this.cursorX = -1;
        this.cursorY = -1;

        if (this.spellNameField != null) {
            this.spellNameField.setValue(this.spell.name);
        }

        super.init();

        this.left = x;
        this.top = y;
        this.gridLeft = this.left + this.padLeft;
        this.gridTop = this.top + this.padTop;

        // spellNameField の値を現在のスペル名に設定
        if (this.spellNameField != null) {
            this.spellNameField.setValue(this.spell.name);
        }

        initialized = true;
    }

    @Override
    public void pageRender(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        if (!isActive) return;
        super.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public void onPageSelected() {
        this.isActive = true;
        // ページが選択された時、このページのスペルを programmer に設定
        refreshSpellFromPage();
    }

    @Override
    public void onPageDeselected() {
        this.isActive = false;
        // ページが非選択になった時、現在のスペルを保存
        if (initialized) {
            saveSpellToPage();
            onSpellChanged(false);
        }
    }

    /**
     * TileEntity からこのページのスペルを再読み込み
     */
    private void refreshSpellFromPage() {
        Spell pageSpell = multiProgrammer.getPageSpell(pageIndex);
        if (pageSpell != null) {
            this.spell = pageSpell.copy();
        } else {
            this.spell = new Spell();
        }

        if (this.spellNameField != null) {
            this.spellNameField.setValue(this.spell.name);
        }

        this.compileResult = (new vazkii.psi.common.spell.SpellCompiler()).compile(this.spell);

        selectedX = -1;
        selectedY = -1;
    }
    private void saveSpellToPage() {
        if (this.spell != null) {
            multiProgrammer.setPageSpell(pageIndex, this.spell.copy());
        }
    }
    @Override
    public void pageTick() {
        if (!isActive) return;
        super.tick();
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        if (!isActive) return;
        super.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean pageMouseClicked(double mouseX, double mouseY, int button) {
        if (!isActive) return false;
        this.programmer.spell = this.spell;
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean pageMouseReleased(double mouseX, double mouseY, int button) {
        if (!isActive) return false;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean pageMouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (!isActive) return false;
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean pageMouseScrolled(double mouseX, double mouseY, double delta) {
        if (!isActive) return false;
        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    @Override
    public void pageMouseMoved(double mouseX, double mouseY) {
        if (!isActive) return;
        super.mouseMoved(mouseX, mouseY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (!isActive) return false;
        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    @Override
    public void mouseMoved(double xPos, double mouseY) {
        if (!isActive) return;
        super.mouseMoved(xPos, mouseY);
    }

    @Override
    public boolean pageKeyReleased(int keyCode, int scanCode, int modifiers) {
        if (!isActive) return false;

        return super.keyPressed(keyCode,scanCode,modifiers);
    }

    @Override
    public boolean pageCharTyped(char codePoint, int modifiers) {
        if (!isActive) return false;

        boolean result = super.charTyped(codePoint, modifiers);

        if (result && this.spell != null) {
            saveSpellToPage();
        }

        return result;
    }

    @Override
    public void onSpellChanged(boolean nameOnly) {
        saveSpellToPage();

        multiProgrammer.setCurrentPage(pageIndex,false);

        this.programmer.spell = this.spell;

        super.onSpellChanged(nameOnly);
    }
    public Spell getSpell() {
        return this.spell != null ? this.spell.copy() : new Spell();
    }
    // GuiProgrammerのprotectedメンバにアクセスするためのアクセッサ
    public int getLeft() { return left; }
    public int getTop() { return top; }
    public boolean isActive() { return isActive; }
}