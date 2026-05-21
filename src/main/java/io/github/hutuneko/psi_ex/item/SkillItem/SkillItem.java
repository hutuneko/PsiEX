package io.github.hutuneko.psi_ex.item.SkillItem;

import io.github.hutuneko.psi_ex.item.CuriosItem;
import io.github.hutuneko.psi_ex.system.CuriosUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.SlotContext;

import java.util.*;

public abstract class SkillItem extends CuriosItem {
    private final UUID modifierUuid;
    private final Map<Attribute, Integer> totalModifiers = new LinkedHashMap<>();
    private final Map<Attribute, Integer> additionModifiers = new LinkedHashMap<>();

    public SkillItem(Properties props, UUID uuid) {
        super(props);
        this.modifierUuid = uuid;
        initializeAttributes();
    }
    protected abstract void initializeAttributes();

    // 子クラス用の setter（親クラス内から呼ばれる）
    protected final void setAttributeTotal(Attribute attribute, int set) {
        totalModifiers.put(attribute, set);
    }

    protected final void setAttributeAddition(Attribute attribute, int add) {
        additionModifiers.put(attribute, add);
    }
    @Override
    public void onEquip(SlotContext slotContext, ItemStack prevStack, ItemStack stack) {
        LivingEntity entity = slotContext.entity();
        applyModifiers(entity, totalModifiers, AttributeModifier.Operation.MULTIPLY_TOTAL);
        applyModifiers(entity, additionModifiers, AttributeModifier.Operation.ADDITION);
    }

    private void applyModifiers(LivingEntity entity, Map<Attribute, Integer> modifiers,
                                AttributeModifier.Operation op) {
        for (Map.Entry<Attribute, Integer> entry : modifiers.entrySet()) {
            AttributeInstance inst = entity.getAttribute(entry.getKey());
            if (inst == null) continue;

            inst.addTransientModifier(new AttributeModifier(
                    modifierUuid,
                    "SkillItem modifier",
                    entry.getValue(),
                    op
            ));
        }
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        removeModifiers(slotContext.entity(), totalModifiers.keySet());
        removeModifiers(slotContext.entity(), additionModifiers.keySet());
    }

    private void removeModifiers(LivingEntity entity, Set<Attribute> attributes) {
        for (Attribute attr : attributes) {
            AttributeInstance inst = entity.getAttribute(attr);
            if (inst != null) {
                inst.removeModifier(modifierUuid);
            }
        }
    }

    @Override
    public boolean canEquip(SlotContext slotContext, ItemStack stack) {
        if (!(slotContext.entity() instanceof Player player)) return false;
        return CuriosUtil.findFirstByItem(player, this).isEmpty();
    }
    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, List<Component> tooltip, @NotNull TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.psi_ex.skillitem.desc").withStyle(ChatFormatting.GRAY));
    }
}