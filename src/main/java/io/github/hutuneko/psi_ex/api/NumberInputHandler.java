package io.github.hutuneko.psi_ex.api;

import io.github.hutuneko.psi_ex.item.GeneralPurposeTypeCAD;
import io.github.hutuneko.psi_ex.net.C2SCADInput;
import io.github.hutuneko.psi_ex.net.Net;
import io.github.hutuneko.psi_ex.net.SpellCastPacket;
import io.github.hutuneko.psi_ex.system.CuriosUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.TickEvent;

@OnlyIn(Dist.CLIENT)
public class NumberInputHandler {
    private static final int[] NUMBER_KEYS = {
            48, 49, 50, 51, 52, 53, 54, 55, 56, 57  
    };
    private static final int[] NUMPAD_KEYS = {
            320, 321, 322, 323, 324, 325, 326, 327, 328, 329  
    };

    
    private static int firstDigit = -1;
    private static long lastInputTime = 0;
    private static final long INPUT_TIMEOUT = 1000; 
    private static final long KEY_COOLDOWN = 150;   
    private static int lastKeyCode = -1;
    private static long lastKeyTime = 0;

    
    private static int wheelValue = 0;
    private static boolean wheelModeActive = false;
    private static final int WHEEL_MIN = 0;
    private static final int WHEEL_MAX = 99;

    
    public static void onKeyInput(InputEvent.Key event) {
        if (event.getAction() != 1) return;

        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null) return;
        if (mc.screen != null) return;

        int key = event.getKey();
        long currentTime = System.currentTimeMillis();

        
        if (key == lastKeyCode && (currentTime - lastKeyTime) < KEY_COOLDOWN) {
            return;
        }

        if (!hasCADEquipped(player)) return;

        
        if (KeyBindings.CAST_KEY.matches(key, event.getScanCode())) {
            sendWheelValue(player);
            return;
        }

        
        int number = getNumberFromKey(key);
        if (number == -1) return;

        lastKeyCode = key;
        lastKeyTime = currentTime;
        onNumberPressed(number, player);
    }

    
    public static void onMouseScroll(InputEvent.MouseScrollingEvent event) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null) return;
        if (mc.screen != null) return;
        if (!hasCADEquipped(player)) return;

        boolean altDown = KeyBindings.ALT_KEY.isDown();
        if (!altDown) return;

        event.setCanceled(true);

        double scrollDelta = event.getScrollDelta();
        int delta = (int) Math.signum(scrollDelta);

        if (KeyBindings.SFT_KEY.isDown()) {
            wheelValue += delta * 10;
        } else {
            wheelValue += delta;
        }

        wheelValue = Math.max(WHEEL_MIN, Math.min(WHEEL_MAX, wheelValue));
        wheelModeActive = true;

        String displayMsg = (wheelValue < 10) ? "0" + wheelValue : String.valueOf(wheelValue);
        player.displayClientMessage(Component.literal("[" + displayMsg + "]"), true);
    }

    
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null) return;
        if (mc.screen != null) return;
        if (!hasCADEquipped(player)) return;

        
        while (KeyBindings.CAST_KEY.consumeClick()) {
            sendWheelValue(player);
        }
    }

    
    private static void sendWheelValue(LocalPlayer player) {
        if (!wheelModeActive) return;

        int index = wheelValue;
        String displayMsg = (index < 10) ? "0" + index : String.valueOf(index);
        player.displayClientMessage(Component.literal("Cast: " + displayMsg), true);

        Net.CHANNEL.sendToServer(new SpellCastPacket(index));
    }

    
    private static boolean hasCADEquipped(LocalPlayer player) {
        var slot = CuriosUtil.findFirst(player, itemStack -> itemStack.getItem() instanceof GeneralPurposeTypeCAD);
        return slot.isPresent() && slot.get().stack().getItem() instanceof GeneralPurposeTypeCAD;
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

    private static void onNumberPressed(int number, LocalPlayer player) {
        long currentTime = System.currentTimeMillis();

        if (currentTime - lastInputTime > INPUT_TIMEOUT) {
            firstDigit = -1;
        }
        lastInputTime = currentTime;

        if (firstDigit == -1) {
            firstDigit = number;
            player.displayClientMessage(Component.literal(firstDigit + "_"), true);
        } else {
            int wheelValue = firstDigit * 10 + number;
            player.displayClientMessage(Component.translatable("psi_ex.cad-value",wheelValue), true);
            Net.CHANNEL.sendToServer(new C2SCADInput(wheelValue));
        }
    }
}