package io.github.hutuneko.psi_ex.net;

import io.github.hutuneko.psi_ex.compat.PsiEXRegistry;
import io.github.hutuneko.psi_ex.item.GeneralPurposeTypeCAD;
import io.github.hutuneko.psi_ex.system.CuriosUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
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

            var slot = CuriosUtil.findFirst(player, itemStack -> itemStack.getItem() instanceof GeneralPurposeTypeCAD);
            if (slot.isEmpty()) return;
            ItemStack stack = slot.get().stack();
            if (stack.getTag() == null) return;
            if (!stack.getTag().contains("psi_ex.isshutdown")) {
                stack.getTag().putBoolean("psi_ex.isshutdown",false);
            }
            if (stack.getTag().getBoolean("psi_ex.isshutdown"))return;
            GeneralPurposeTypeCAD.spellCast(msg.index, player, slot.get().stack());
        });
        ctx.get().setPacketHandled(true);
    }
}