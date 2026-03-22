package com.hutuneko.psi_ex;

import com.hutuneko.psi_ex.compat.CompatModule;
import moffy.addonapi.AddonModuleRegistry;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class Config {
    public static ForgeConfigSpec SERVER_SPEC;
    public static Server SERVER;

    private static ForgeConfigSpec.Builder BUILDER;

    public static void config(FMLJavaModLoadingContext context) {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        BUILDER = builder;
        var spec = builder.configure(Server::new);
        SERVER_SPEC = spec.getRight();
        SERVER = spec.getLeft();

        AddonModuleRegistry.INSTANCE.LoadModule(new CompatModule(context), BUILDER);

        SERVER_SPEC = BUILDER.build();
    }

    public static class Server {
        public final ForgeConfigSpec.BooleanValue spellgeat;
        Server(ForgeConfigSpec.Builder builder) {
            builder.push("features");

            spellgeat = builder
                    .define("spellgeat", true);
            builder.pop();
        }
    }
}