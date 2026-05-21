package io.github.hutuneko.psi_ex.compat.ars;

import io.github.hutuneko.psi_ex.PsiEX;
import io.github.hutuneko.psi_ex.spell.trick.PieceTrick_ArsScrollCast;
import moffy.addonapi.AddonModule;
import net.minecraft.resources.ResourceLocation;
import vazkii.psi.api.PsiAPI;

public class ArsCompatModule implements AddonModule {
    public ArsCompatModule() {
        PsiAPI.registerSpellPieceAndTexture(new ResourceLocation(PsiEX.MOD_ID, "piecetrick_arsscrollcast"), PieceTrick_ArsScrollCast.class);
    }
}

