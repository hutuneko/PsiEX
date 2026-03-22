package com.hutuneko.psi_ex.block;

import com.hutuneko.psi_ex.cliant.menu.GPTCADSettingMenu;
import com.hutuneko.psi_ex.compat.PsiEXRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GPTCADSettingTile extends BlockEntity implements MenuProvider {
    // 100スロットのインベントリ
    private final ItemStackHandler itemHandler = new ItemStackHandler(100) {
        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            // ここに許可するアイテムの条件を書く (例: 鉄インゴットのみ)
            return stack.is(Items.IRON_INGOT);
        }

        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };

    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();

    public GPTCADSettingTile(BlockPos pos, BlockState state) {
        super(PsiEXRegistry.GPTCADSETTINGTILE.get(), pos, state);
    }

    // Menuからインベントリにアクセスするために使用
    public IItemHandler getItemHandler() {
        return itemHandler;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return lazyItemHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        lazyItemHandler = LazyOptional.of(() -> itemHandler);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyItemHandler.invalidate();
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.psi_ex.gptcadsetting");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new GPTCADSettingMenu(pContainerId,pPlayerInventory,this);
    }
}
