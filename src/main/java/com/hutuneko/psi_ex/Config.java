package com.hutuneko.psi_ex;

import com.hutuneko.psi_ex.compat.CompatModule;
import moffy.addonapi.AddonModuleRegistry;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class Config {
    public static ForgeConfigSpec SERVER_SPEC;
    public static ForgeConfigSpec COMMON_SPEC;
    public static ForgeConfigSpec.BooleanValue spellgeat;
    public static void config(FMLJavaModLoadingContext context) {
        ForgeConfigSpec.Builder server = new ForgeConfigSpec.Builder();
        ForgeConfigSpec.Builder common = new ForgeConfigSpec.Builder();

        server.push("features");

        spellgeat = server
                .define("spellgeat", true);
        server.pop();

        AddonModuleRegistry.INSTANCE.LoadModule(new CompatModule(context), common);

        SERVER_SPEC = server.build();
        COMMON_SPEC = common.build();
    }
}