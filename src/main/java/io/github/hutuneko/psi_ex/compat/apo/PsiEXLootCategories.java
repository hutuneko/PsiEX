package io.github.hutuneko.psi_ex.compat.apo;

import io.github.hutuneko.psi_ex.PsiEX;
import io.github.hutuneko.psi_ex.api.CadBehavior;
import dev.shadowsoffire.apotheosis.adventure.loot.LootCategory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModList;
import vazkii.psi.common.item.ItemCAD;
import vazkii.psi.common.item.base.ModItems;

import java.util.function.Predicate;

public class PsiEXLootCategories {
    public static LootCategory CAD;
    public static void init(){
        Predicate<ItemStack> item = stack -> stack.getItem() instanceof ItemCAD || CadBehavior.isCAD(stack);
        CAD = LootCategory.register(null, new ResourceLocation(PsiEX.MOD_ID,"cad").toString(), item, new EquipmentSlot[]{EquipmentSlot.MAINHAND,EquipmentSlot.OFFHAND});
    }
}
