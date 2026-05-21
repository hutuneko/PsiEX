package io.github.hutuneko.psi_ex.system.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import vazkii.psi.api.PsiAPI;
import vazkii.psi.api.cad.ISocketable;

public class SocketableProvider implements ICapabilityProvider, ICapabilitySerializable<CompoundTag> {

    private final ItemStack stack;
    private final LazyOptional<ISocketable> socketable;

    public SocketableProvider(ItemStack stack) {
        this.stack = stack;
        this.socketable = LazyOptional.of(() -> new StackSocketable(stack));
    }

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable net.minecraft.core.Direction side) {
        if (cap == PsiAPI.SOCKETABLE_CAPABILITY) {
            return socketable.cast();
        }
        return LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        return stack.getOrCreateTag().copy();
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        if (nbt != null) stack.getOrCreateTag().merge(nbt);
    }
}
