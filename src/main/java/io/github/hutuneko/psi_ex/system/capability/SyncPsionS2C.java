package io.github.hutuneko.psi_ex.system.capability;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

// SyncPsionS2C.java
public record SyncPsionS2C(double psion) {

    public static void encode(SyncPsionS2C msg, FriendlyByteBuf buf) {
        buf.writeDouble(msg.psion());
    }
    public static SyncPsionS2C decode(FriendlyByteBuf buf) {
        return new SyncPsionS2C(buf.readDouble());
    }
    public static void handle(SyncPsionS2C msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Minecraft mc = Minecraft.getInstance();
            LocalPlayer clientPlayer = mc.player;
            if (clientPlayer != null) {
                clientPlayer.getCapability(PsionProvider.CAP).ifPresent(cap -> cap.setPsion(msg.psion()));
            }
        });
        ctx.get().setPacketHandled(true);
    }
}

