package com.hutuneko.psi_ex.net;

import com.hutuneko.psi_ex.PsiEX;
import com.hutuneko.psi_ex.compat.PsiEXRegistry;
import com.hutuneko.psi_ex.item.GeneralPurposeTypeCAD;
import com.hutuneko.psi_ex.system.CuriosUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record SpellCastPacket(int index) {

    public static void encode(SpellCastPacket msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.index);
    }

    public static SpellCastPacket decode(FriendlyByteBuf buf) {
        return new SpellCastPacket(buf.readInt());
    }

    public static void handle(SpellCastPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;

            var slot = CuriosUtil.findFirstByItem(player, PsiEXRegistry.GPTCAD.get());
            if (slot.isEmpty() || !(slot.get().stack().getItem() instanceof GeneralPurposeTypeCAD)) return;
            GeneralPurposeTypeCAD.spellCast(msg.index, player, slot.get().stack());
        });
        ctx.get().setPacketHandled(true);
    }
}