package io.github.hutuneko.psi_ex.api;

import io.github.hutuneko.psi_ex.PsiEX;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * SpellIndexの自動保存管理
 */
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE,modid = PsiEX.MOD_ID)
public class SpellIndexManager {

    private static int tickCounter = 0;

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            tickCounter++;
            // 10秒ごとに自動保存（20tick/sec × 10 = 200tick）
            if (tickCounter >= 200) {
                tickCounter = 0;
                SpellIndex.getInstance().save();
            }
        }
    }

    @SubscribeEvent
    public static void onServerStopping(ServerStoppingEvent event) {
        SpellIndex.getInstance().save();
    }

    public static void forceSave() {
        SpellIndex.getInstance().save();
    }
}