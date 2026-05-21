// AttributeValueCondition.java
package io.github.hutuneko.psi_ex.system.attribute;

import io.github.hutuneko.psi_ex.system.PieceCondition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.registries.ForgeRegistries;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellPiece;
import net.minecraft.network.chat.Component;

public final class AttributeValueCondition implements PieceCondition {
    public enum Mode { BASE, TOTAL } // BASE=BaseValue, TOTAL=getValue()（modifiers込み）

    private final ResourceLocation attrId;
    private final double threshold;
    private final Mode mode;
    private final boolean atLeast; // true: >=, false: <=
    private final Component message; // 任意：失敗時メッセージ

    public AttributeValueCondition(ResourceLocation attrId, double threshold, Mode mode, boolean atLeast, Component failMsg) {
        this.attrId = attrId;
        this.threshold = threshold;
        this.mode = mode;
        this.atLeast = atLeast;
        this.message = failMsg;
    }

    @Override
    public boolean test(SpellContext ctx, SpellPiece piece) {
        if (ctx == null || ctx.caster == null) return false;
        Player p = ctx.caster;

        Attribute attr = ForgeRegistries.ATTRIBUTES.getValue(attrId);
        if (attr == null) return false; // 未登録

        AttributeInstance inst = p.getAttribute(attr);
        if (inst == null) return false; // プレイヤーに付与されていない

        double value = (mode == Mode.BASE) ? inst.getBaseValue() : inst.getValue(); // TOTALはModifier込み
        return atLeast ? (value >= threshold) : (value <= threshold);
    }

    @Override
    public Component failMessage() {
        if (this.message != null) return this.message;
        String translationKey = "attribute.name." + attrId.getNamespace() + "." + attrId.getPath();
        Component attrName = Component.translatable(translationKey);

        return Component.translatable("message.psi_ex.attribute_requirement", attrName, threshold);
    }
}
