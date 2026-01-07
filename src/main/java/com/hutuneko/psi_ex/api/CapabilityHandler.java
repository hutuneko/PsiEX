package com.hutuneko.psi_ex.api; // パッケージ名は適宜合わせてください

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import vazkii.psi.common.core.handler.capability.CADData;

@Mod.EventBusSubscriber
public class CapabilityHandler {
    private static final ResourceLocation CAD_DATA_ID = new ResourceLocation("psi", "cad_data");
    @SubscribeEvent
    public static void attachItemCapabilities(AttachCapabilitiesEvent<ItemStack> event) {
        ItemStack stack = event.getObject();

        // ここで「このアイテムがCADとして振る舞うべきか」を判定
        if (CadBehavior.isCAD(stack)) {
            // Psi標準のCADData (ICapabilityProvider) をアタッチする
            CADData data = new CADData(stack);
            event.addCapability(CAD_DATA_ID, data);
        }
    }
}