package io.github.hutuneko.psi_ex.net;

import io.github.hutuneko.psi_ex.PsiEX;
import io.github.hutuneko.psi_ex.api.CadBehavior;
import io.github.hutuneko.psi_ex.item.GeneralPurposeTypeCAD;
import io.github.hutuneko.psi_ex.system.CuriosUtil;
import moffy.addonapi.AddonAPI;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import vazkii.psi.api.cad.ICAD;
import vazkii.psi.common.item.ItemCAD;

import java.util.function.Supplier;

public record C2SShutdown(boolean isshutdown) {
    public static void encode(C2SShutdown msg, FriendlyByteBuf buf) {
        buf.writeBoolean(msg.isshutdown);
    }
    public static C2SShutdown decode(FriendlyByteBuf buf) {
        return new C2SShutdown(buf.readBoolean());
    }
    public static void handle(C2SShutdown msg, Supplier<NetworkEvent.Context> ctx){
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null) {
                ItemStack stack = player.getMainHandItem();
                if (!(stack.getItem() instanceof ItemCAD || CadBehavior.isCAD(stack)) && AddonAPI.isModuleAvailable(new ResourceLocation(PsiEX.MOD_ID,"curioscompat"))){
                    var slotResultOptional = CuriosUtil.findFirst(player, itemStack -> itemStack.getItem() instanceof GeneralPurposeTypeCAD);
                    if (slotResultOptional.isPresent()){
                        stack = slotResultOptional.get().stack();
                    }
                }
                if (!(stack.getItem() instanceof ItemCAD || CadBehavior.isCAD(stack)))return;
                var tag = stack.getOrCreateTag();
                tag.putBoolean("psi_ex.isshutdown",msg.isshutdown);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
