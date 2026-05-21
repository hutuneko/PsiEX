package io.github.hutuneko.psi_ex.system.attribute;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;

public final class AttributeResolver {
    public static @Nullable Attribute resolve(ResourceLocation key) {
        if (key == null) return null;

        // Forge のレジストリを使うのが簡単
        Attribute attr = ForgeRegistries.ATTRIBUTES.getValue(key);
        if (attr != null) return attr;

        // 念のためバニラの登録からも拾う
        return BuiltInRegistries.ATTRIBUTE.get(key);
    }
}
