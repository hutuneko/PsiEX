package com.hutuneko.psi_ex.item;

import com.hutuneko.psi_ex.system.capability.SocketableProvider;
import com.hutuneko.psi_ex.system.capability.StackSocketable;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.magic.SpellSelectionManager;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.item.SpellBook;
import io.redspace.ironsspellbooks.player.ClientMagicData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import vazkii.psi.api.PsiAPI;

import javax.annotation.Nullable;

public class PsiSpellBook extends SpellBook {
    private final int maxStackSize;
    public static int getIndex(ItemStack stack, Player player){
        int slot = -1;
        if (stack.getItem() instanceof SpellBook book) {
            slot = MagicData.getPlayerMagicData(player).getSyncedData().getSpellSelection().index;
        }
        return slot;
    }
    public PsiSpellBook(int maxStackSize) {
        super(maxStackSize,new Item.Properties().stacksTo(maxStackSize));
        this.maxStackSize = maxStackSize;
    }

    @Override
    public @Nullable ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        var provider = new SocketableProvider(stack);
        provider.getCapability(PsiAPI.SOCKETABLE_CAPABILITY).ifPresent(cap -> {
            if (cap instanceof StackSocketable s) {
                s.setMaxSlots(maxStackSize);
            }
        });
        return provider;
    }
}
