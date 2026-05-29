package io.github.hutuneko.psi_ex.net;

import io.github.hutuneko.psi_ex.system.capability.PlayerDataProvider;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record C2SCADInput(int i) {
    public static void encode(C2SCADInput msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.i);
    }
    public static C2SCADInput decode(FriendlyByteBuf buf) {
        return new C2SCADInput(buf.readInt());
    }
    public static void handle(C2SCADInput msg, Supplier<NetworkEvent.Context> ctx){
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null) {
                player.getCapability(PlayerDataProvider.CAP).ifPresent(
                        data -> data.setDouble("psi_ex:cad_data", msg.i));
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
