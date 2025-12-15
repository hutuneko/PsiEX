package com.hutuneko.psi_ex.compat.mek;

import com.hutuneko.psi_ex.PsiEX;
import mekanism.common.registration.impl.GasDeferredRegister;
import mekanism.common.registration.impl.GasRegistryObject;
import moffy.addonapi.AddonModule;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class MekanismCompatModule extends AddonModule {


    public MekanismCompatModule() {
        GasRegistryObject PSI_GAS = null;

        final GasDeferredRegister GASES =
                new GasDeferredRegister(PsiEX.MOD_ID);
        GASES.register(FMLJavaModLoadingContext.get().getModEventBus());


        PSI_GAS = GASES.register("psi_gas",0x8A2BE2);
    }

}

