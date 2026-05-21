package io.github.hutuneko.psi_ex.net;

import io.github.hutuneko.psi_ex.block.MultiPageTileProgrammer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record C2SProgrammerPagePacket(BlockPos pos, int page) {
    public static void encode(C2SProgrammerPagePacket msg, FriendlyByteBuf buf) {
        buf.writeBlockPos(msg.pos);
        buf.writeInt(msg.page);
    }
    public static C2SProgrammerPagePacket decode(FriendlyByteBuf buf) {
        return new C2SProgrammerPagePacket(buf.readBlockPos(), buf.readInt());
    }
    public static void handle(C2SProgrammerPagePacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null) {
                Level level = player.level();
                if (level.getBlockEntity(msg.pos) instanceof MultiPageTileProgrammer tile) {
                    tile.setCurrentPage(msg.page, false);
                    tile.setChanged();
                    level.sendBlockUpdated(msg.pos, tile.getBlockState(), tile.getBlockState(), 2);
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }

}
