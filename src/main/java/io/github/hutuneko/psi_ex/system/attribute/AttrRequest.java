package io.github.hutuneko.psi_ex.system.attribute;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.LinkedHashMap;
import java.util.Map;

public final class AttrRequest {
    public static void requestOpen() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        Map<ResourceLocation, Double> map = new LinkedHashMap<>();
        for (Attribute attr : ForgeRegistries.ATTRIBUTES.getValues()) {
            AttributeInstance inst = mc.player.getAttribute(attr);
            if (inst == null) continue;
            ResourceLocation id = ForgeRegistries.ATTRIBUTES.getKey(attr);
            if (id != null && AllowedAttributes.isAllowed(id)) {
                map.put(id, inst.getBaseValue());
            }
        }
        mc.setScreen(new AttributeEditorScreen(map));

    }
}
