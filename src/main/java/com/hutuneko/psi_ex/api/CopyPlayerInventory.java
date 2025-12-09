package com.hutuneko.psi_ex.api;

import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;

public class CopyPlayerInventory {
    public static void copyInventory(ServerPlayer src, ServerPlayer dst) {
        Inventory a = src.getInventory();
        Inventory b = dst.getInventory();
        ListTag tag = new ListTag();
        a.save(tag);
        b.load(tag);
//        // いったん空に
//        b.clearContent();
//
//        // メイン（36）
//        for (int i = 0; i < a.items.size(); i++) {
//            ItemStack s = a.items.get(i);
//            b.items.set(i, s.isEmpty() ? ItemStack.EMPTY : s.copy());
//        }
//        // 防具（4）
//        for (int i = 0; i < a.armor.size(); i++) {
//            ItemStack s = a.armor.get(i);
//            b.armor.set(i, s.isEmpty() ? ItemStack.EMPTY : s.copy());
//        }
//        // オフハンド（1）
//        for (int i = 0; i < a.offhand.size(); i++) {
//            ItemStack s = a.offhand.get(i);
//            b.offhand.set(i, s.isEmpty() ? ItemStack.EMPTY : s.copy());
//        }
//
//        // 選択中スロットも同期
//        b.selected = a.selected;
//
//        var se = src.getEnderChestInventory();
//        var de = dst.getEnderChestInventory();
//        for (int i = 0; i < de.getContainerSize(); i++) {
//            ItemStack s = se.getItem(i);
//            de.setItem(i, s.isEmpty() ? ItemStack.EMPTY : s.copy());
//        }
    }
}
