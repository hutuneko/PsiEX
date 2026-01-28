package com.hutuneko.psi_ex.system;

import com.hutuneko.psi_ex.PsiEX;
import com.hutuneko.psi_ex.system.attribute.C2SSetAttribute;
import com.hutuneko.psi_ex.system.attribute.S2COpenEditor;
import com.hutuneko.psi_ex.system.attribute.S2CUpdateAttribute;
import com.hutuneko.psi_ex.system.capability.SyncPsionS2C;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public final class Net {

    private static final String PROTOCOL = "1";
    public static SimpleChannel CHANNEL;
    private static int id = 0;

    @SubscribeEvent
    public static void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            CHANNEL = NetworkRegistry.newSimpleChannel(
                    new ResourceLocation(PsiEX.MOD_ID, "main"),
                    () -> PROTOCOL, PROTOCOL::equals, PROTOCOL::equals
            );

            id = 0;

            // --- Client 向け（サーバ→クライアント）
            CHANNEL.messageBuilder(SyncPsionS2C.class, id++, NetworkDirection.PLAY_TO_CLIENT)
                    .encoder(SyncPsionS2C::encode)
                    .decoder(SyncPsionS2C::decode)
                    .consumerMainThread(SyncPsionS2C::handle)
                    .add();

            // --- Server 向け（クライアント→サーバ）
            CHANNEL.messageBuilder(C2SSetAttribute.class, id++, NetworkDirection.PLAY_TO_SERVER)
                    .encoder(C2SSetAttribute::encode)
                    .decoder(C2SSetAttribute::decode)
                    .consumerMainThread(C2SSetAttribute::handle)
                    .add();

            // --- Client 向け（サーバ→クライアント）
            CHANNEL.messageBuilder(S2COpenEditor.class, id++, NetworkDirection.PLAY_TO_CLIENT)
                    .encoder(S2COpenEditor::encode)
                    .decoder(S2COpenEditor::decode)
                    .consumerMainThread(S2COpenEditor::handle)
                    .add();
            CHANNEL.messageBuilder(S2CUpdateAttribute.class, id++, NetworkDirection.PLAY_TO_CLIENT)
                    .encoder(S2CUpdateAttribute::encode)
                    .decoder(S2CUpdateAttribute::decode)
                    .consumerMainThread(S2CUpdateAttribute::handle)
                    .add();
        });
    }
}

