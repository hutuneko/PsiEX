package com.hutuneko.psi_ex.datagen;

// 例: src/main/java/.../datagen/ModItemModelProvider.java

import com.hutuneko.psi_ex.compat.PsiEXRegistry;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;
// ... (他のimport)

public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(PackOutput output, String modid, ExistingFileHelper existingFileHelper) {
        super(output, modid, existingFileHelper);
    }
    @Override
    protected void registerModels() {

        for (RegistryObject<Item> regObj : PsiEXRegistry.ITEMS.getEntries()) {
            if (!(regObj == PsiEXRegistry.CAST_SCROLL)){
                basicItem(regObj.get());
            }
        }
    }
}
