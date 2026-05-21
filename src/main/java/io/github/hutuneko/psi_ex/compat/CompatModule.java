package io.github.hutuneko.psi_ex.compat;


import io.github.hutuneko.psi_ex.PsiEX;
import io.github.hutuneko.psi_ex.compat.apo.ApoCompatModule;
import io.github.hutuneko.psi_ex.compat.ars.ArsCompatModule;
import io.github.hutuneko.psi_ex.compat.botania.BotaniaCompatModule;
import io.github.hutuneko.psi_ex.compat.cc.CCCuriosModule;
import io.github.hutuneko.psi_ex.compat.curios.CuriosCompatModule;
import io.github.hutuneko.psi_ex.compat.iron.IronsCompatModule;
import io.github.hutuneko.psi_ex.compat.mek.MekanismCompatModule;
import io.github.hutuneko.psi_ex.compat.tic.TiCCompatModule;
import moffy.addonapi.AddonModuleProvider;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class CompatModule extends AddonModuleProvider {
    public CompatModule(FMLJavaModLoadingContext context) {
        super(context);
    }

    @Override
    public void registerRawModules() {
        addRawModule("defaultcompatmodule",
        "Default Compat",
                DefaultCompatModule.class,
                new String[] { "psi", PsiEX.MOD_ID});
        addRawModule("botaniacompat",
                "Botania Compat",
                BotaniaCompatModule.class,
                new String[] { "psi", "botania" });
        addRawModule("arscompat",
                "Ars Nouveau Compat",
                ArsCompatModule.class,
                new String[] { "psi", "ars_nouveau" });
        addRawModule("ironscompat",
                "Iron's Compat",
                IronsCompatModule.class,
                new String[] { "psi", "irons_spellbooks" });
        addRawModule("mekcompat",
                "Mekanism Compat",
                MekanismCompatModule.class,
                new String[] { "psi", "mekanism" });
        addRawModule("curioscompat",
                "Curios Compat",
                CuriosCompatModule.class,
                new String[] { "psi", "curios" });
        addRawModule("ccccompat",
                "CCC Compat",
                CCCuriosModule.class,
                new String[] { "psi", "curios", "computercraft" });
        addRawModule("ticcompat",
                "TiC Compat",
                TiCCompatModule.class,
                new String[] { "psi", "tconstruct" });
        addRawModule("apocompat",
                "Apo Compat",
                ApoCompatModule.class,
                new String[] { "psi", "apotheosis" });
    }
    @Override
    public String getModId() {
        return PsiEX.MOD_ID;
    }
}
