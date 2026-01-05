package com.hutuneko.psi_ex;

import com.hutuneko.psi_ex.compat.CompatModule;
import moffy.addonapi.AddonModuleRegistry;
import net.minecraftforge.common.ForgeConfigSpec;

public class Config {
    public static final ForgeConfigSpec COMMON_SPEC;
    public static final Common COMMON;

    private static final ForgeConfigSpec.Builder BUILDER;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        BUILDER = builder;

        COMMON = new Common(builder);

        AddonModuleRegistry.INSTANCE.LoadModule(new CompatModule(), BUILDER);

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