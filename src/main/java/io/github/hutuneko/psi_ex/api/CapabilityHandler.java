package io.github.hutuneko.psi_ex.api;

import net.minecraft.nbt.CompoundTag;
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

        if (CadBehavior.isCAD(stack)) {
            CompoundTag nbt = stack.getTag();
            CADData data = new CADData(stack);
            if (nbt != null && nbt.contains("Parent", 10)) {
                data.deserializeNBT(nbt.getCompound("Parent"));
            }
            event.addCapability(CAD_DATA_ID, data);
        }
    }
}