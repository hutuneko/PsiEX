package io.github.hutuneko.psi_ex.system.capability;

import io.github.hutuneko.psi_ex.net.Net;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.PacketDistributor;

// PsionSync.java
public final class PsionSync {
    public static void toSelf(ServerPlayer player) {
        player.getCapability(PsionProvider.CAP).ifPresent(cap -> Net.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player),
                new SyncPsionS2C(cap.getPsion())));
    }
    public static void toTracking(Entity who) { // ほかのプレイヤーにも見せたい場合
        who.getCapability(PsionProvider.CAP).ifPresent(cap -> Net.CHANNEL.send(PacketDistributor.TRACKING_ENTITY.with(() -> who),
                new SyncPsionS2C(cap.getPsion())));
    }
}
