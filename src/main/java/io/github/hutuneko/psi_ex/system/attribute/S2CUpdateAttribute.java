package io.github.hutuneko.psi_ex.system.attribute;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record S2CUpdateAttribute(String key, double value) {
    public static void encode(S2CUpdateAttribute m, FriendlyByteBuf buf){
        buf.writeUtf(m.key());
        buf.writeDouble(m.value());
    }
    public static S2CUpdateAttribute decode(FriendlyByteBuf buf){
        return new S2CUpdateAttribute(buf.readUtf(), buf.readDouble());
    }
    public static void handle(S2CUpdateAttribute m, Supplier<NetworkEvent.Context> ctx){
        ctx.get().enqueueWork(() -> {
            if (FMLEnvironment.dist == Dist.CLIENT) {
                clienthandle(m);
            }
        });
        ctx.get().setPacketHandled(true);
    }
    @OnlyIn(Dist.CLIENT)
    public static void clienthandle(S2CUpdateAttribute m) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.screen instanceof AttributeEditorScreen scr) {
            scr.onServerConfirmed(ResourceLocation.parse(m.key()), m.value()); // ← 画面にメソッド用意
        }
    }
}
