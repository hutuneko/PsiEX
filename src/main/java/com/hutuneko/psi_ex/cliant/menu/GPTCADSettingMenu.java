package com.hutuneko.psi_ex.cliant.menu;

import com.hutuneko.psi_ex.block.GPTCADSettingTile;
import com.hutuneko.psi_ex.compat.PsiEXRegistry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class GPTCADSettingMenu extends AbstractContainerMenu {
    private final GPTCADSettingTile blockEntity;

    public GPTCADSettingMenu(int id, Inventory inv, FriendlyByteBuf extraData) {
        this(id, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()));
    }

    public GPTCADSettingMenu(int id, Inventory inv, BlockEntity entity) {
        super(PsiEXRegistry.GPTCAD_SETTING_MENU.get(), id);
        this.blockEntity = (GPTCADSettingTile) entity;

        IItemHandler handler = this.blockEntity.getItemHandler();

        // 100スロットの配置 (10列 x 10行)
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                this.addSlot(new SlotItemHandler(handler, j + i * 10, 8 + j * 18, 18 + i * 18));
            }
        }

        // プレイヤーインベントリ (位置は調整が必要)
        layoutPlayerInventorySlots(inv, 8, 202);
    }

    private void layoutPlayerInventorySlots(Inventory inv, int x, int y) {
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(inv, j + i * 9 + 9, x + j * 18, y + i * 18));
            }
        }
        for (int k = 0; k < 9; ++k) {
            this.addSlot(new Slot(inv, k, x + k * 18, y + 58));
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(ContainerLevelAccess.create(blockEntity.getLevel(), blockEntity.getBlockPos()), player, PsiEXRegistry.GPTCADSETTINGBLOCK.get());
    }

    // シフトクリック時のアイテム移動ロジック (簡略版)
    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (index < 100) {
                if (!this.moveItemStackTo(itemstack1, 100, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (!this.moveItemStackTo(itemstack1, 0, 100, false)) {
                    return ItemStack.EMPTY;
                }
            }
            if (itemstack1.isEmpty()) slot.set(ItemStack.EMPTY);
            else slot.setChanged();
        }
        return itemstack;
    }
}