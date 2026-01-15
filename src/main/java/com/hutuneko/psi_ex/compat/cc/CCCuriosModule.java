package com.hutuneko.psi_ex.compat.cc;

import dan200.computercraft.api.ComputerCraftAPI;
import moffy.addonapi.AddonModule;
import net.minecraftforge.common.MinecraftForge;

public class CCCuriosModule implements AddonModule {
    public CCCuriosModule(){
        MinecraftForge.EVENT_BUS.addListener(CCCevent::onCommonSetup);
        ComputerCraftAPI.registerAPIFactory(PsiGlobalAPI::new);
    }
}

