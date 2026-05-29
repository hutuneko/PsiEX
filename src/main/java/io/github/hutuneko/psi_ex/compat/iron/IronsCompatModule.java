package io.github.hutuneko.psi_ex.compat.iron;

import io.github.hutuneko.psi_ex.PsiEX;
import io.github.hutuneko.psi_ex.compat.PsiEXRegistry;
import io.github.hutuneko.psi_ex.item.PsiSpellBook;
import io.github.hutuneko.psi_ex.spell.trick.TrickCastScroll;
import moffy.addonapi.AddonModule;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import vazkii.psi.api.PsiAPI;

public class IronsCompatModule implements AddonModule {
    public IronsCompatModule() {
        PsiAPI.registerSpellPieceAndTexture(new ResourceLocation(PsiEX.MOD_ID, "piecetrick_castscroll"), TrickCastScroll.class);
        PsiEXRegistry.PSI_SPELLBOOK = PsiEXRegistry.ITEMS.register("psi_spellbook", () -> new PsiSpellBook(12));
        MinecraftForge.EVENT_BUS.addListener(IronsEvent::onSpellDamage);
        MinecraftForge.EVENT_BUS.addListener(IronsEvent::onServerTick);
    }
}

