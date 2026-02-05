package com.hutuneko.psi_ex.system.attribute;

import com.hutuneko.psi_ex.net.Net;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public record C2SSetAttribute(ResourceLocation attributeId, double baseValue) {
    public static void encode(C2SSetAttribute msg, FriendlyByteBuf buf) {
        buf.writeResourceLocation(msg.attributeId);
        buf.writeDouble(msg.baseValue);
    }
    public static C2SSetAttribute decode(FriendlyByteBuf buf) {
        return new C2SSetAttribute(buf.readResourceLocation(), buf.readDouble());
    }
    public static void handle(C2SSetAttribute msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer sp = ctx.get().getSender();
            if (sp == null) return;

            if (!AllowedAttributes.isAllowed(msg.attributeId)) return;

            Attribute attr = ForgeRegistries.ATTRIBUTES.getValue(msg.attributeId);
            if (attr == null) return;
            AttributeInstance inst = sp.getAttribute(attr);
            if (inst == null) return;

            double value = msg.baseValue;

            inst.setBaseValue(value);

            // MAX_HEALTH など特殊属性は副作用調整
            if (attr.equals(Attributes.MAX_HEALTH)) {
                sp.setHealth(Math.min(sp.getHealth(), (float) sp.getMaxHealth()));
            }

            Net.CHANNEL.send(PacketDistributor.PLAYER.with(() -> sp),
                    new S2CUpdateAttribute(msg.attributeId.toLanguageKey(), value));
        });
        ctx.get().setPacketHandled(true);
    }
}
