package com.hutuneko.psi_ex.system.capability;

import com.hutuneko.psi_ex.PsiEX;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.jetbrains.annotations.NotNull;

public class PsionProvider implements ICapabilityProvider, ICapabilitySerializable<CompoundTag> {
    public static final Capability<IPsionData> CAP =
            CapabilityManager.get(new CapabilityToken<>(){});
    private final IPsionData inst = new PsionData();

    @Override public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, Direction side){
        return cap == CAP ? LazyOptional.of(() -> inst).cast() : LazyOptional.empty();
    }
    @Override public CompoundTag serializeNBT(){ CompoundTag t=new CompoundTag(); inst.save(t); return t; }
    @Override public void deserializeNBT(CompoundTag nbt){ inst.load(nbt); }
}
