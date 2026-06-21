package io.github.hutuneko.psi_ex.system.capability;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raider;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;

@Mod.EventBusSubscriber
public class PsionProvider implements ICapabilityProvider, ICapabilitySerializable<CompoundTag> {
    public static final Capability<IPsionData> CAP =
            CapabilityManager.get(new CapabilityToken<>(){});
    private final IPsionData inst = new PsionData();

    @Override public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, Direction side){
        return cap == CAP ? LazyOptional.of(() -> inst).cast() : LazyOptional.empty();
    }
    @Override public CompoundTag serializeNBT(){ CompoundTag t=new CompoundTag(); inst.save(t); return t; }
    @Override public void deserializeNBT(CompoundTag nbt){ inst.load(nbt); }
    @SubscribeEvent
    public static void tick(LivingEvent.LivingTickEvent livingEvent){
        if (is(livingEvent.getEntity())){
            livingEvent.getEntity().getCapability(CAP).ifPresent(data -> data.tickRegain(livingEvent.getEntity()));
        }
    }
    public static boolean is(Entity e){
        return e instanceof Raider ||e instanceof AbstractVillager || e instanceof Player;
    }
}
