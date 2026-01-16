package com.hutuneko.psi_ex;

import com.hutuneko.psi_ex.compat.PsiEXRegistry;
import com.hutuneko.psi_ex.system.attribute.PsiEXAttributes;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

@Mod(PsiEX.MOD_ID)
public class PsiEX {
    public static final String MOD_ID = "psi_ex";
    public static final Logger LOGGER = LogUtils.getLogger();
    public PsiEX() {
        FMLJavaModLoadingContext context = FMLJavaModLoadingContext.get();
        Config.config(context);
        IEventBus modBus = context.getModEventBus();
        PsiEXRegistry.ITEMS.register(modBus);
        PsiEXRegistry.TYPES.register(modBus);
        PsiEXRegistry.BLOCKS.register(modBus);
        PsiEXRegistry.SERIALIZERS.register(modBus);
        PsiEXRegistry.BLOCK_ENTITIES.register(modBus);
        PsiEXRegistry.ENTITIES.register(modBus);
        PsiEXRegistry.TABS.register(modBus);
        PsiEXAttributes.register(modBus);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.COMMON_SPEC);
    }

    public static List<String> listAllAttributeNames() {
        List<String> names = new ArrayList<>();
        for (ResourceLocation rl : ForgeRegistries.ATTRIBUTES.getKeys()) {
            names.add(rl.toString());
        }
        return names;
    }
}
