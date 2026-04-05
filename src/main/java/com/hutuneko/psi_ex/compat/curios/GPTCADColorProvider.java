package com.hutuneko.psi_ex.compat.curios;

import com.hutuneko.psi_ex.compat.PsiEXRegistry;
import com.hutuneko.psi_ex.item.ItemGPTCADAssembly;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;
import vazkii.psi.api.cad.ICAD;

public class GPTCADColorProvider implements ItemColor {

    @Override
    public int getColor(@NotNull ItemStack stack, int tintIndex) {
        if (tintIndex == 1) {
            if (stack.getItem() instanceof ICAD cad){
                return cad.getSpellColor(stack);
            }
        }else if(tintIndex == 2){
            if (stack.hasTag()) {
                CompoundTag rootTag = stack.getTag();

                if (rootTag != null && rootTag.contains("componentASSEMBLY", Tag.TAG_COMPOUND)) {
                    CompoundTag assemblyTag = rootTag.getCompound("componentASSEMBLY");

                    String assemblyId = assemblyTag.getString("id");
                    for (RegistryObject<Item> regObj : PsiEXRegistry.ITEMS.getEntries()){
                        if (regObj.getId() != null && regObj.getId().equals(new ResourceLocation(assemblyId))) {
                            if (regObj.get() instanceof ItemGPTCADAssembly assembly){
                                return assembly.getColor();
                            }
                        }
                    }
                }
            }
        }
        return 0xFFFFFF;
    }
}