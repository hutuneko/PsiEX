package com.hutuneko.psi_ex.system;

import com.hutuneko.psi_ex.compat.cc.PsiGlobalAPI;
import dan200.computercraft.api.ComputerCraftAPI;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public class CCCevent {
    public static void onCommonSetup(FMLCommonSetupEvent event) {
        ComputerCraftAPI.registerAPIFactory(PsiGlobalAPI::new);
    }
}
