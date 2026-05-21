package io.github.hutuneko.psi_ex.api;

import io.github.hutuneko.psi_ex.compat.PsiEXRegistry;
import io.github.hutuneko.psi_ex.item.GeneralPurposeTypeCAD;
import io.github.hutuneko.psi_ex.net.Net;
import io.github.hutuneko.psi_ex.net.SpellCastPacket;
import io.github.hutuneko.psi_ex.system.CuriosUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent;

@OnlyIn(Dist.CLIENT)
public class NumberInputHandler {
    private static final int[] NUMBER_KEYS = {
            48, 49, 50, 51, 52, 53, 54, 55, 56, 57  // 0-9
    };
    private static final int[] NUMPAD_KEYS = {
            320, 321, 322, 323, 324, 325, 326, 327, 328, 329  // NUMPAD 0-9
    };

    // 2桁入力用バッファ
    private static int firstDigit = -1;
    private static long lastInputTime = 0;
    private static final long INPUT_TIMEOUT = 1000; // 1秒
    private static final long KEY_COOLDOWN = 150;   // 同じキー連打防止用クールダウン(ms)
    private static int lastKeyCode = -1;
    private static long lastKeyTime = 0;

    public static void onKeyInput(InputEvent.Key event) {
        // Action 1 (PRESS) のみ処理
        if (event.getAction() != 1) return;

        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;

        if (player == null) return;
        if (mc.screen != null) return;

        int key = event.getKey();
        long currentTime = System.currentTimeMillis();

        // 同じキーの連打防止（チャタリング対策）
        if (key == lastKeyCode && (currentTime - lastKeyTime) < KEY_COOLDOWN) {
            
            return;
        }

        var slot = CuriosUtil.findFirstByItem(player, PsiEXRegistry.GPTCAD.get());
        if (slot.isEmpty() || !(slot.get().stack().getItem() instanceof GeneralPurposeTypeCAD)) return;

        int number = getNumberFromKey(key);
        if (number == -1) return;

        // キー記録を更新
        lastKeyCode = key;
        lastKeyTime = currentTime;

        onNumberPressed(number, player, slot.get().stack());
    }

    private static int getNumberFromKey(int keyCode) {
        for (int i = 0; i < NUMBER_KEYS.length; i++) {
            if (NUMBER_KEYS[i] == keyCode) return i;
        }
        for (int i = 0; i < NUMPAD_KEYS.length; i++) {
            if (NUMPAD_KEYS[i] == keyCode) return i;
        }
        return -1;
    }

    private static void onNumberPressed(int number, LocalPlayer player, ItemStack cad) {
        long currentTime = System.currentTimeMillis();

        // タイムアウトチェック
        if (currentTime - lastInputTime > INPUT_TIMEOUT) {
            
            firstDigit = -1;
        }
        lastInputTime = currentTime;

        if (firstDigit == -1) {
            // 1桁目入力
            firstDigit = number;
            String msg = firstDigit + "_";
            
            player.displayClientMessage(Component.literal(msg), true);
        } else {
            // 2桁目入力
            int index = firstDigit * 10 + number;

            // 00 の場合は特別表示（見えるように）
            String displayMsg = (index < 10) ? "0" + index : String.valueOf(index);
            

            player.displayClientMessage(Component.literal(displayMsg), true);
            firstDigit = -1;

            // サーバーに送信
            Net.CHANNEL.sendToServer(new SpellCastPacket(index));
        }
    }

    public static void resetInput() {
        firstDigit = -1;
    }

    public static String getInputStatus() {
        if (firstDigit == -1) return "";
        return firstDigit + "_";
    }
}