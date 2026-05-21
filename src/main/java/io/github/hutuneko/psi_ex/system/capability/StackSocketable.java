package io.github.hutuneko.psi_ex.system.capability;

import io.github.hutuneko.psi_ex.item.PsiCuriosbullet;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import vazkii.psi.api.cad.ISocketable;
import vazkii.psi.api.spell.ISpellAcceptor;


public class StackSocketable implements ISocketable {
    private final ItemStack stack;

    private int maxSlots = 2;

    public StackSocketable(ItemStack stack) {
        this.stack = stack;
        normalize(); // NBTの整形
    }

    /* ================= ISocketable ================= */

    public int getSlots() { return maxSlots; }

    @Override public boolean isSocketSlotAvailable(int slot) {
        return slot >= 0 && slot < getSlots();
    }

    @Override
    public ItemStack getBulletInSocket(int slot) {
        ListTag list = getList();
        if (slot < 0 || slot >= list.size()) return ItemStack.EMPTY;
        return ItemStack.of(list.getCompound(slot));
    }

    @Override
    public void setBulletInSocket(int slot, ItemStack bullet) {
        if (slot < 0 || slot >= getSlots()) return;
        if (!isAcceptableBullet(bullet)) {
            bullet = ItemStack.EMPTY;
        }
        ListTag list = getList();
        ensureSize(list, getSlots());
        list.set(slot, bullet.save(new CompoundTag()));
        putList(list);
        setSelectedSlot(getSelectedSlot());
    }

    @Override
    public int getSelectedSlot() {
        return Mth.clamp(stack.getOrCreateTag().getInt(PsiCuriosbullet.NBT_SELECTED), 0, Math.max(0, getSlots() - 1));
    }

    @Override
    public void setSelectedSlot(int slot) {
        stack.getOrCreateTag().putInt(PsiCuriosbullet.NBT_SELECTED, Mth.clamp(slot, 0, Math.max(0, getSlots() - 1)));
    }

    public Component getSocketableName() {
        return stack.getHoverName();
    }

    /* ================= helpers ================= */

    private ListTag getList() {
        return stack.getOrCreateTag().getList(PsiCuriosbullet.NBT_SOCKETS, Tag.TAG_COMPOUND);
    }

    private void putList(ListTag list) {
        stack.getOrCreateTag().put(PsiCuriosbullet.NBT_SOCKETS, list);
    }

    private static void ensureSize(ListTag list, int size) {
        while (list.size() < size) list.add(new CompoundTag());
        while (list.size() > size) list.remove(list.size() - 1);
    }

    private void normalize() {
        ListTag list = getList();
        ensureSize(list, getSlots());
        putList(list);
        setSelectedSlot(getSelectedSlot());
    }

    private static boolean isAcceptableBullet(@Nullable ItemStack stack) {
        if (stack == null || stack.isEmpty()) return true;
        ISpellAcceptor acc = ISpellAcceptor.acceptor(stack);
        return acc != null;
    }

    public void setMaxSlots(int value) {
        this.maxSlots = Math.max(0, value);
        normalize();
    }
}
