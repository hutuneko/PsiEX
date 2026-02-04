package com.hutuneko.psi_ex.item.SkillItem;

import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;

import java.util.UUID;

public class Phantom extends SkillItem{
    public Phantom(Properties pProperties) {
        super(pProperties, UUID.fromString("f3d69fb4-df6e-f139-acf3-59bfe19306c4"));
    }

    @Override
    protected void initializeAttributes() {}

    @Override
    public void onEquip(SlotContext slotContext, ItemStack prevStack, ItemStack stack) {
        slotContext.entity().setInvisible(true);
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        slotContext.entity().setInvisible(false);
    }
}
