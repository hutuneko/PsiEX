package io.github.hutuneko.psi_ex.api;

import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

/**
 * TransientCraftingContainer を参考にした簡易コンテナ
 */
public class SimpleSpellContainer implements Container {
    private final NonNullList<ItemStack> items;

    @Nullable
    private final AbstractContainerMenu menu;

    public SimpleSpellContainer(int size, @Nullable AbstractContainerMenu menu) {
        this.items = NonNullList.withSize(size, ItemStack.EMPTY);
        this.menu = menu;
    }
    public SimpleSpellContainer(int size) {
        this.items = NonNullList.withSize(size, ItemStack.EMPTY);
        menu = null;
    }

    @Override
    public int getContainerSize() {
        return this.items.size();
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack itemstack : this.items) {
            if (!itemstack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public @NotNull ItemStack getItem(int pSlot) {
        return pSlot >= this.getContainerSize() ? ItemStack.EMPTY : this.items.get(pSlot);
    }

    @Override
    public @NotNull ItemStack removeItemNoUpdate(int pSlot) {
        return ContainerHelper.takeItem(this.items, pSlot);
    }

    @Override
    public @NotNull ItemStack removeItem(int pSlot, int pAmount) {
        ItemStack itemstack = ContainerHelper.removeItem(this.items, pSlot, pAmount);
        this.setChanged();
        return itemstack;
    }

    @Override
    public void setItem(int pSlot, @NotNull ItemStack pStack) {
        this.items.set(pSlot, pStack);
        this.setChanged();
    }

    @Override
    public void setChanged() {
        if (this.menu != null) {
            this.menu.slotsChanged(this);
        }
    }

    @Override
    public boolean stillValid(@NotNull Player pPlayer) {
        return true;
    }

    @Override
    public void clearContent() {
        this.items.clear();
    }
}