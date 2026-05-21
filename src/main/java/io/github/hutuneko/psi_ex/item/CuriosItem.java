package io.github.hutuneko.psi_ex.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

public class CuriosItem extends Item implements ICurioItem {
    public CuriosItem(Properties pProperties) {
        super(pProperties.stacksTo(1));
    }
    @Override
    public boolean canEquipFromUse(SlotContext context, ItemStack stack) {
        return true;
    }
}
