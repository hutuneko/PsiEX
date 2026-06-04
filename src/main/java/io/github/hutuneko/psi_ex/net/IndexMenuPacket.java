package io.github.hutuneko.psi_ex.net;

import io.github.hutuneko.psi_ex.api.menu.IndexMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record IndexMenuPacket(Action action, int data, String text, ItemStack stack) {
    public enum Action {
        SET_SELECTED,
        SET_DESCRIPTION,
        REGISTER_BULLET,
        WRITE_SPELL
    }

    // インデックスのみのパケット
    public IndexMenuPacket(Action action, int data) {
        this(action, data, "", ItemStack.EMPTY);
    }

    // テキスト付きパケット
    public IndexMenuPacket(Action action, int data, String text) {
        this(action, data, text, ItemStack.EMPTY);
    }

    // アイテム付きパケット
    public IndexMenuPacket(Action action, int data, ItemStack stack) {
        this(action, data, "", stack);
    }

    public IndexMenuPacket(Action action, int data, String text, ItemStack stack) {
        this.action = action;
        this.data = data;
        this.text = text != null ? text : "";
        this.stack = stack != null ? stack : ItemStack.EMPTY;
    }

    public static void encode(IndexMenuPacket msg, FriendlyByteBuf buf) {
        buf.writeEnum(msg.action);
        buf.writeInt(msg.data);
        buf.writeUtf(msg.text);
        buf.writeItem(msg.stack);
    }

    public static IndexMenuPacket decode(FriendlyByteBuf buf) {
        return new IndexMenuPacket(
                buf.readEnum(Action.class),
                buf.readInt(),
                buf.readUtf(),
                buf.readItem()
        );
    }

    public static void handle(IndexMenuPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null && player.containerMenu instanceof IndexMenu menu) {
                menu.handlePacket(msg);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}