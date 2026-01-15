package com.hutuneko.psi_ex;

import com.hutuneko.psi_ex.compat.CompatModule;
import moffy.addonapi.AddonModuleRegistry;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class Config {
    public static ForgeConfigSpec COMMON_SPEC;
    public static Common COMMON;

    private static ForgeConfigSpec.Builder BUILDER;

    public static void config(FMLJavaModLoadingContext context) {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        BUILDER = builder;

        COMMON = new Common(builder);

        AddonModuleRegistry.INSTANCE.LoadModule(new CompatModule(context), BUILDER);

        COMMON_SPEC = BUILDER.build();
    }

    public static class Common {
        public final ForgeConfigSpec.BooleanValue spellgeat;
        Common(ForgeConfigSpec.Builder builder) {
            builder.push("features");

            spellgeat = builder
                    .define("spellgeat", true);
            builder.pop();
        }
    }
}