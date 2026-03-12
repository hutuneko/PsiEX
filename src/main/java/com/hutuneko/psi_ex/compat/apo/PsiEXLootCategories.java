package com.hutuneko.psi_ex.compat.apo;

import com.hutuneko.psi_ex.PsiEX;
import com.hutuneko.psi_ex.api.CadBehavior;
import com.hutuneko.psi_ex.compat.tic.TiCCompatModule;
import dev.shadowsoffire.apotheosis.adventure.loot.LootCategory;
import moffy.addonapi.AddonModuleRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import vazkii.psi.common.item.base.ModItems;

import java.util.function.Predicate;

public class PsiEXLootCategories {
    public static LootCategory CAD;
    public static void init(){
        Predicate<ItemStack> item;
        if (AddonModuleRegistry.INSTANCE.getLoadedModules().containsKey(new ResourceLocation(PsiEX.MOD_ID,"ticcompat"))){
            item = stack -> stack.is(ModItems.cad) || stack.is(TiCCompatModule.TICCAD.asItem()) || CadBehavior.isCAD(stack);
        }else{
            item = stack -> stack.is(ModItems.cad) || CadBehavior.isCAD(stack);
        }
        CAD = LootCategory.register(null, new ResourceLocation(PsiEX.MOD_ID,"cad").toString(), item, new EquipmentSlot[]{EquipmentSlot.MAINHAND,EquipmentSlot.OFFHAND});
    }
}
