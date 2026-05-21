package io.github.hutuneko.psi_ex.compat.cc;

import dan200.computercraft.api.ComputerCraftAPI;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public class CCCevent {
    public static void onCommonSetup(FMLCommonSetupEvent event) {
        ComputerCraftAPI.registerAPIFactory(PsiGlobalAPI::new);
    }
}
