package io.github.hutuneko.psi_ex.api;

import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Collection;

public class CopyPlayerInventory {
    public static void copyFeke(ServerPlayer src, ServerPlayer dst){
        copyInventory(src, dst);
        copyAttributes(src,dst,true);
    }
    public static void copyInventory(ServerPlayer src, ServerPlayer dst) {
        Inventory a = src.getInventory();
        Inventory b = dst.getInventory();
        ListTag tag = new ListTag();
        a.save(tag);
        b.load(tag);
    }
    public static void copyAttributes(LivingEntity source, LivingEntity target, boolean copyModifiers) {
        for (Attribute attribute : ForgeRegistries.ATTRIBUTES) {

            AttributeInstance sourceInst = source.getAttribute(attribute);
            AttributeInstance targetInst = target.getAttribute(attribute);

            if (sourceInst != null && targetInst != null) {

                targetInst.setBaseValue(sourceInst.getBaseValue());

                if (copyModifiers) {
                    targetInst.getModifiers().stream().toList().forEach(targetInst::removeModifier);

                    Collection<AttributeModifier> sourceModifiers = sourceInst.getModifiers();
                    for (AttributeModifier mod : sourceModifiers) {
                        if (!targetInst.hasModifier(mod)) {
                            targetInst.addTransientModifier(mod);
                        }
                    }
                }
            }
        }
        target.setHealth(Math.min(target.getMaxHealth(), source.getHealth()));
    }
}
