package io.github.hutuneko.psi_ex.block;

import io.github.hutuneko.psi_ex.api.UUIDDataHandler;
import io.github.hutuneko.psi_ex.cliant.menu.GPTCADSettingMenu;
import io.github.hutuneko.psi_ex.compat.PsiEXRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import vazkii.psi.common.item.ItemSpellBullet;

import java.util.UUID;

public class GPTCADSettingTile extends BlockEntity implements MenuProvider {
    public static final String DATA_ID = "psiex_gptdata";
    private static final String NBT_UUID = "tile_uuid";
    private static final String NBT_ITEMS = "items";

    private final ItemStackHandler itemHandler = new ItemStackHandler(100) {
        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return stack.is(itemHolder -> itemHolder.get() instanceof ItemSpellBullet);
        }

        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            // サーバーサイドのみ保存
            if (level != null && !level.isClientSide) {
                saveToFile();
            }
        }

        @Override
        public int getSlotLimit(int slot) {
            return 1;
        }

        @Override
        @NotNull
        public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
            if (!getStackInSlot(slot).isEmpty()) {
                return stack;
            }
            if (stack.getCount() > 1) {
                ItemStack single = stack.copy();
                single.setCount(1);

                if (!simulate) {
                    setStackInSlot(slot, single);
                }

                ItemStack remainder = stack.copy();
                remainder.shrink(1);
                return remainder;
            }
            return super.insertItem(slot, stack, simulate);
        }

        @Override
        @NotNull
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            return super.extractItem(slot, 1, simulate);
        }
    };

    private UUID id;
    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();
    private boolean loadedFromFile = false; // ロード済みフラグ

    public GPTCADSettingTile(BlockPos pos, BlockState state) {
        super(PsiEXRegistry.GPTCADSETTINGTILE.get(), pos, state);
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);

        // UUIDの読み込み（ない場合は新規生成）
        if (tag.hasUUID(NBT_UUID)) {
            this.id = tag.getUUID(NBT_UUID);
        } else {
            this.id = UUID.randomUUID();
        }
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);

        if (this.id == null) {
            this.id = UUID.randomUUID();
        }
        tag.putUUID(NBT_UUID, this.id);

        // アイテムデータをNBTにも保存（バックアップとして）
        tag.put(NBT_ITEMS, itemHandler.serializeNBT());

        // ファイルにも保存
        if (level != null && !level.isClientSide) {
            saveToFile();
        }
    }

    @Override
    public void onLoad() {
        super.onLoad();
        lazyItemHandler = LazyOptional.of(() -> itemHandler);

        // levelが確実に存在するタイミングでファイルからロード
        if (!loadedFromFile && level != null && !level.isClientSide) {
            loadFromFile();
            loadedFromFile = true;
        }
    }

    private void saveToFile() {
        if (this.id == null || level == null || level.isClientSide) {
            return;
        }

        CompoundTag data = new CompoundTag();
        data.putLong("pos", this.worldPosition.asLong());
        data.put(NBT_ITEMS, itemHandler.serializeNBT());

        UUIDDataHandler.saveCompoundTag(this.id, data, DATA_ID);
    }

    private void loadFromFile() {
        if (this.id == null || level == null || level.isClientSide) {
            return;
        }

        CompoundTag data = UUIDDataHandler.loadCompoundTag(this.id, DATA_ID);

        if (!data.isEmpty() && data.contains(NBT_ITEMS)) {
            itemHandler.deserializeNBT(data.getCompound(NBT_ITEMS));
        }
    }

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
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyItemHandler.invalidate();
    }

    @Override
    public void setRemoved() {
        // 削除前に保存
        if (level != null && !level.isClientSide) {
            saveToFile();
        }
        super.setRemoved();
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("block.psi_ex.gptcadsetting");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int pContainerId, @NotNull Inventory pPlayerInventory, @NotNull Player pPlayer) {
        return new GPTCADSettingMenu(pContainerId, pPlayerInventory, this);
    }

    public UUID getID() {
        if (this.id == null) {
            this.id = UUID.randomUUID();
        }
        return this.id;
    }
}