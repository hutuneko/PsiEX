package io.github.hutuneko.psi_ex.net;

import io.github.hutuneko.psi_ex.api.client.ClientRenderer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent;

import java.util.List;
import java.util.function.Supplier;

public record SniperTargetPacket(List<Integer> entityIds) {

    public static void encode(SniperTargetPacket msg, FriendlyByteBuf buf) {
        buf.writeCollection(msg.entityIds, FriendlyByteBuf::writeInt);
    }

    public static SniperTargetPacket decode(FriendlyByteBuf buf) {
        return new SniperTargetPacket(buf.readList(FriendlyByteBuf::readInt));
    }

    public static void handle(SniperTargetPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            clienthandle(msg);
        });
        ctx.get().setPacketHandled(true);
    }
    @OnlyIn(Dist.CLIENT)
    public static void clienthandle(SniperTargetPacket msg) {
        ClientRenderer.updateTargets(msg.entityIds);
    }
}
