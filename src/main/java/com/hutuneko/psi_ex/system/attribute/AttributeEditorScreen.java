package com.hutuneko.psi_ex.system.attribute;

import com.hutuneko.psi_ex.system.Net;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * スライダーGUI版：
 * - 各属性はスライダーで編集
 * - 合計が MAX_TOTAL を超えないよう変更側のみクランプ
 * - 値確定時（ドラッグ終了 or +/- キー操作確定）にサーバーへ送信
 */
public class AttributeEditorScreen extends Screen {

    /** 合計の上限（必要に応じて変更） */
    private static final double MAX_TOTAL = 1000;

    /** 個々の下限・上限（全項目共通。個別に変えたければ Row にフィールドを足してください） */
    private static final double MIN_PER = 0.0;
    private static final double MAX_PER = 1000;

    private static class Row {
        final ResourceLocation id;
        final Component label;
        double current; // 実値（MIN_PER..MAX_PER）
        Slider slider;
        EditBox editBox;
        Row(ResourceLocation id, double current) {
            this.id = id;
            this.label = AllowedAttributes.getAttributeName(id);
            this.current = current;
        }
    }

    private final List<Row> rows = new ArrayList<>();

    /** S2C で初期値を受け取り、この画面を開く想定 */
    public AttributeEditorScreen(Map<ResourceLocation, Double> values) {
        super(Component.literal("Attribute Editor"));
        values.forEach((id, v) -> rows.add(new Row(id, clamp(v, MIN_PER, MAX_PER))));
    }

    @Override
    protected void init() {
        int y = 34;
        int sliderX = this.width / 2 - 30;
        int sliderW = 210;

        int editBoxX = this.width / 2 + 190;
        int editBoxW = 70;

        for (Row r : rows) {
            double norm = denormalize(r.current);
            r.slider = new Slider(sliderX, y - 4, sliderW, 20, r, norm);
            addRenderableWidget(r.slider);

            r.editBox = new EditBox(
                    this.font, editBoxX, y - 4, editBoxW, 20, Component.empty()
            );
            r.editBox.setMaxLength(10);
            r.editBox.setValue(String.format(Locale.ROOT, "%.2f", r.current));
            r.editBox.setFilter(s -> s.matches("^-?\\d*(\\.\\d*)?$"));
            r.editBox.setResponder(this::onEditBoxChange);

            r.editBox.setResponder(this::onEditBoxChange);

            addRenderableWidget(r.editBox);

            y += 24;
        }

        addRenderableWidget(Button.builder(Component.literal("Close"), b -> onClose())
                .bounds(this.width / 2, this.height - 28, 80, 20).build());
    }
    @Override
    public void render(@NotNull GuiGraphics g, int mx, int my, float pt) {
        renderBackground(g);
        g.drawCenteredString(this.font, this.title, this.width / 2, 10, 0xFFFFFF);

        // 合計表示
        int left = this.width / 2 - 160;
        int sumY = 18;
        g.drawString(this.font, Component.literal(
                "Total: " + String.format(Locale.ROOT, "%.2f / %.2f", total(), MAX_TOTAL)
        ), left, sumY, 0xFFD080, false);

        int y = 36;
        for (Row r : rows) {
            g.drawString(this.font, r.label, left, y, 0xAAAAAA, false);
            y += 24;
        }

        super.render(g, mx, my, pt);
    }

    @Override
    public boolean isPauseScreen() { return false; }

    /* ------------- 内部クラス：スライダー ------------- */

    private class Slider extends AbstractSliderButton {
        final Row row;

        /**
         * @param value 正規化済み [0..1]
         */
        Slider(int x, int y, int w, int h, Row row, double value) {
            super(x, y, w, h, Component.empty(), value);
            this.row = row;
            updateFromNormalized(value); // 表示文言更新
        }

        @Override
        protected void updateMessage() {
            setMessage(Component.empty());
        }

        @Override
        protected void applyValue() {
            // ユーザ操作で値が変わった時に呼ばれる
            // 1) 正規化→実値へ
            double proposed = normalize(this.value);

            // 2) 合計チェック（このスライダーだけをクランプ）
            double others = totalExcept(row);
            double allowed = clamp(MAX_TOTAL - others, MIN_PER, MAX_PER);
            double clamped = clamp(proposed, MIN_PER, allowed);

            // 3) 実値・表示の反映
            row.current = clamped;
            double backNorm = denormalize(clamped);
            if (Math.abs(backNorm - this.value) > 1e-9) {
                // 表示側を合わせる（はみ出しクランプした場合）
                this.value = backNorm;
            }
        }

        @Override
        public void onRelease(double mouseX, double mouseY) {
            super.onRelease(mouseX, mouseY);
            // 値確定 → サーバーへ送信
            Net.CHANNEL.sendToServer(new C2SSetAttribute(row.id, row.current));
        }

        /** 正規化→実値 */
        private double normalize(double v01) {
            return MIN_PER + v01 * (MAX_PER - MIN_PER);
        }
        /** 実値→正規化 */
        private double denormalize(double actual) {
            return (actual - MIN_PER) / (MAX_PER - MIN_PER);
        }
        /** 表示同期用（初期化時） */
        private void updateFromNormalized(double v01) {
            this.value = clamp(v01, 0, 1);
            this.row.current = normalize(this.value);
            updateMessage();
        }
        public void setNormalizedValue(double v01) {
            // protected の 'value' にアクセスできるのは Slider クラス内のみ
            this.value = clamp(v01, 0, 1);

            // 値が変更されたら、実値への変換とクランプ処理を行う
            // EditBox側で既にクランプと合計チェックは済んでいるので、
            // ここでは単に実値と表示を同期させる目的で applyValue() を呼び出します。
            applyValue();
            updateMessage();
        }
    }

    /* ------------- 合計・補助 ------------- */

    private double total() {
        double t = 0;
        for (Row r : rows) t += r.current;
        return t;
    }

    private double totalExcept(Row except) {
        double t = 0;
        for (Row r : rows) if (r != except) t += r.current;
        return t;
    }

    private static double denormalize(double actual) {
        return (actual - MIN_PER) / (MAX_PER - MIN_PER);
    }

    private static double clamp(double v, double min, double max) {
        return Math.max(min, Math.min(max, v));
    }

    public void onServerConfirmed(ResourceLocation id, double value) {
        for (Row r : rows) {
            if (r.id.equals(id)) {
                r.current = value;
                if (r.slider != null) {
                    r.slider.updateFromNormalized((value - MIN_PER) / (MAX_PER - MIN_PER));
                }
            }
        }
    }
    @Override
    public void removed() {
        // まとめて確定送信（バッチにできるなら1パケットにするのがベスト）
        rows.forEach(r -> Net.CHANNEL.sendToServer(new C2SSetAttribute(r.id, r.current)));
        super.removed();
    }
    private void onEditBoxChange(String text) {
        // 現在フォーカスされている EditBox を特定する
        Row targetRow = null;
        for (Row r : rows) {
            if (r.editBox != null && r.editBox.isFocused()) {
                targetRow = r;
                break;
            }
        }

        if (targetRow == null || text.isEmpty() || text.equals("-") || text.equals(".")) {
            // 無効な入力の場合は処理しない
            return;
        }

        try {
            double proposedValue = Double.parseDouble(text);

            // 1. 合計上限チェックとクランプ
            double others = totalExcept(targetRow);
            // 許容される最大値 (MAX_TOTAL - 他の属性値)
            double allowed = clamp(MAX_TOTAL - others, MIN_PER, MAX_PER);
            // 個別上限と合計上限でクランプ
            double clampedValue = clamp(proposedValue, MIN_PER, allowed);

            // 2. 実値とスライダーの更新
            targetRow.current = clampedValue;
            double backNorm = denormalize(clampedValue);
            targetRow.slider.setNormalizedValue(backNorm);
            targetRow.slider.updateMessage(); // スライダーの表示を更新 (ここは空なので実質不要)

            // 3. EditBoxの表示をクランプ後の値に更新
            // ※クランプされた場合、入力値と表示値を一致させる
            if (clampedValue != proposedValue) {
                targetRow.editBox.setValue(String.format(Locale.ROOT, "%.2f", clampedValue));
            }

            // 4. 即時送信（EditBoxの場合はエンターキーやフォーカス喪失で確定送信の方が一般的ですが、ここでは即時送信の例）
            // Net.CHANNEL.sendToServer(new C2SSetAttribute(targetRow.id, targetRow.current));

        } catch (NumberFormatException ignored) {
            // 数字として解析できない場合は無視
        }
    }
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (super.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }

        // ENTERキーで確定とみなし、サーバーへ送信
        if (keyCode == 257 || keyCode == 335) { // 257: ENTER, 335: NUMPAD_ENTER
            for (Row r : rows) {
                if (r.editBox != null && r.editBox.isFocused()) {
                    Net.CHANNEL.sendToServer(new C2SSetAttribute(r.id, r.current));
                    r.editBox.setFocused(false); // フォーカスを外す
                    return true;
                }
            }
        }
        return false;
    }
}
