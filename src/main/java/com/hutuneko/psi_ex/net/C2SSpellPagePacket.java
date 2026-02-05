package com.hutuneko.psi_ex.net;

import com.hutuneko.psi_ex.PsiEX;
import com.hutuneko.psi_ex.block.MultiPageTileProgrammer;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;
import vazkii.psi.api.spell.Spell;

import java.util.function.Supplier;

public record C2SSpellPagePacket(BlockPos pos, int page, CompoundTag spellTag) {
    public static void encode(C2SSpellPagePacket msg, FriendlyByteBuf buf) {
        buf.writeBlockPos(msg.pos);
        buf.writeInt(msg.page);
        buf.writeNbt(msg.spellTag);
    }
    public static C2SSpellPagePacket decode(FriendlyByteBuf buf) {
        return new C2SSpellPagePacket(buf.readBlockPos(), buf.readInt(),buf.readNbt());
    }
    public static void handle(C2SSpellPagePacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null) {
                Level level = player.level();
                if (level.getBlockEntity(msg.pos) instanceof MultiPageTileProgrammer tile) {
                    Spell spell = Spell.createFromNBT(msg.spellTag);
                    if (spell != null) {
                        tile.setPageSpell(msg.page, spell);

                        // 強制的にセーブマークと同期
                        tile.setChanged();
                        level.sendBlockUpdated(msg.pos, tile.getBlockState(), tile.getBlockState(), 2);

                        PsiEX.LOGGER.error("Server saved page {} at {}", msg.page, msg.pos);
                    }
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }

}
