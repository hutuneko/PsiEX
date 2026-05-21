package io.github.hutuneko.psi_ex.system.attribute;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.network.NetworkEvent;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

public record S2COpenEditor(Map<ResourceLocation, Double> values) {
    public static void encode(S2COpenEditor msg, FriendlyByteBuf buf) {
        buf.writeVarInt(msg.values.size());
        msg.values.forEach((k, v) -> { buf.writeResourceLocation(k); buf.writeDouble(v); });
    }
    public static S2COpenEditor decode(FriendlyByteBuf buf) {
        int n = buf.readVarInt();
        Map<ResourceLocation, Double> m = new LinkedHashMap<>();
        for (int i = 0; i < n; i++) {
            ResourceLocation id = buf.readResourceLocation();
            double v = buf.readDouble();
            if (AllowedAttributes.isAllowed(id)) {
                m.put(id, v);
            }
        }
        return new S2COpenEditor(m);

    }
    public static void handle(S2COpenEditor msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (FMLEnvironment.dist == Dist.CLIENT) {
                clienthandle(msg);
            }
        });
        ctx.get().setPacketHandled(true);
    }
    @OnlyIn(Dist.CLIENT)
    public static void clienthandle(S2COpenEditor msg) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        mc.setScreen(new AttributeEditorScreen(msg.values));
    }
}
