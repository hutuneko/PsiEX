package com.hutuneko.psi_ex.item;

import com.hutuneko.psi_ex.system.capability.SocketableProvider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

public class PsiCuriosbullet extends CuriosItem {

    public static final String NBT_SOCKETS  = "psi_sockets";
    public static final String NBT_SELECTED = "psi_selected";

    public PsiCuriosbullet(Properties props) {
        super(props);
    }

    @Override
    public @Nullable ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        return new SocketableProvider(stack);
    }

    @Override
    public boolean shouldOverrideMultiplayerNbt() {
        return super.shouldOverrideMultiplayerNbt();
    }
}
