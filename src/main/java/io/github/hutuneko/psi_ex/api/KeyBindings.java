package io.github.hutuneko.psi_ex.api;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import org.lwjgl.glfw.GLFW;

public class KeyBindings {

    // キーマッピングの定義（Lazy初期化推奨）
    public static final KeyMapping CAST_KEY = new KeyMapping(
            "key.psi_ex.cast",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_V,
            "key.categories.psi_ex"
    );

    public static final KeyMapping SFT_KEY = new KeyMapping(
            "key.psi_ex.sft",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_LEFT_SHIFT,
            "key.categories.psi_ex"
    );
    public static final KeyMapping ALT_KEY = new KeyMapping(
            "key.psi_ex.alt",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_LEFT_ALT,
            "key.categories.psi_ex"
    );
    public static final KeyMapping SHUTDOWN_KEY = new KeyMapping(
            "key.psi_ex.shutdown",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_L,
            "key.categories.psi_ex"
    );
    public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(CAST_KEY);
        event.register(SFT_KEY);
        event.register(ALT_KEY);
        event.register(SHUTDOWN_KEY);
    }
}