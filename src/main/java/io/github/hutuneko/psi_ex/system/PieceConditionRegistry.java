package io.github.hutuneko.psi_ex.system;

import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class PieceConditionRegistry {

    private static final Map<ResourceLocation, PieceCondition> MAP = new HashMap<>();

    private PieceConditionRegistry() {}

    /** 置き換え登録（同一IDがあれば上書き） */
    public static void register(ResourceLocation pieceId, PieceCondition cond) {
        if (pieceId == null || cond == null) return;
        MAP.put(pieceId, cond);
    }

    /** 取得 */
    public static Optional<PieceCondition> get(ResourceLocation pieceId) {
        return Optional.ofNullable(MAP.get(pieceId));
    }

    /** クリア（データパック /reload 時に使う） */
    public static void clear() {
        MAP.clear();
    }

    /** デバッグ等で中身を読みたいとき用（読み取り専用） */
    public static Map<ResourceLocation, PieceCondition> snapshot() {
        return Map.copyOf(MAP);
    }
}
