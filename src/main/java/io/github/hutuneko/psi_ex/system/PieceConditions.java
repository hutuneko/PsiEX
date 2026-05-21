package io.github.hutuneko.psi_ex.system;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellPiece;

import java.util.ArrayList;
import java.util.List;

/** 条件を合成するための補助クラス */
public final class PieceConditions {

    private PieceConditions() {}

    public static PieceCondition and(PieceCondition a, PieceCondition b) {
        if (a == null || b == null) {
            return (ctx, piece) -> false;
        }
        return (ctx, piece) -> a.test(ctx, piece) && b.test(ctx, piece);
    }

    public static PieceCondition or(PieceCondition a, PieceCondition b) {
        // nullチェック：両方nullならfalse、片方nullならもう片方を評価
        if (a == null && b == null) {
            return (ctx, piece) -> false;
        }
        if (a == null) {
            return b;
        }
        if (b == null) {
            return a;
        }
        return (ctx, piece) -> a.test(ctx, piece) || b.test(ctx, piece);
    }

    public static PieceCondition not(PieceCondition a) {
        if (a == null) {
            return (ctx, piece) -> true;
        }
        return (ctx, piece) -> !a.test(ctx, piece);
    }

    /** すべて満たす（空なら true） */
    public static PieceCondition all(List<? extends PieceCondition> list) {
        return new PieceCondition() {
            @Override
            public boolean test(SpellContext ctx, SpellPiece piece) {
                if (list == null || list.isEmpty()) return true;
                for (PieceCondition c : list) if (!c.test(ctx, piece)) return false;
                return true;
            }

            @Override
            public Component failMessage() {
                if (list == null || list.isEmpty()) return null;

                MutableComponent combined = Component.empty();
                List<Component> parts = new ArrayList<>();

                for (PieceCondition c : list) {
                    Component msg = c.failMessage();
                    if (msg != null) parts.add(msg);
                }

                if (parts.isEmpty()) return null;

                for (int i = 0; i < parts.size(); i++) {
                    combined.append(parts.get(i));
                    if (i < parts.size() - 1) {
                        combined.append(Component.translatable("message.psi_ex.requirement_separator"));
                    }
                }

                return combined;
            }
        };
    }

    /** いずれか満たす（空なら false） */
    public static PieceCondition any(List<? extends PieceCondition> list) {
        return (ctx, piece) -> {
            if (list == null || list.isEmpty()) return false;
            for (PieceCondition c : list) if (c.test(ctx, piece)) return true;
            return false;
        };
    }

    /**
     * 失敗時メッセージを後付け（ベースをそのままラップ）
     * null メッセージならベースをそのまま返す
     */
    public static PieceCondition withMessage(PieceCondition base, Component failMessage) {
        if (failMessage == null) return base;
        return new PieceCondition() {
            @Override public boolean test(SpellContext ctx, SpellPiece piece) { return base.test(ctx, piece); }
            @Override public Component failMessage() { return failMessage; }
        };
    }
}
