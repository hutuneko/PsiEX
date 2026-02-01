package com.hutuneko.psi_ex.item.SkillItem;

import com.hutuneko.psi_ex.compat.PsiEXRegistry;
import com.hutuneko.psi_ex.item.CuriosItem;
import com.hutuneko.psi_ex.system.attribute.PsiEXAttributes;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.SlotContext;

import java.util.*;

public class SkillItem extends CuriosItem {
    private final UUID ZERO_MODIFIER_UUID;
    public SkillItem(Properties pProperties, UUID uuid) {
        super(pProperties);
        ZERO_MODIFIER_UUID = uuid;
    }
    private static final Map<Attribute,Integer> atMap = new LinkedHashMap<>();
    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, List<Component> tooltip, @NotNull TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.psi_ex.skillitem.desc").withStyle(ChatFormatting.GRAY));
    }
    public static void setAttribute(Attribute attribute,int set){
        atMap.put(attribute,set);
    }
    @Override
    public void onEquip(SlotContext slotContext, ItemStack prevStack, ItemStack stack) {
        LivingEntity entity = slotContext.entity();
        for (int i = 0;i < atMap.size();i++){
            Attribute attribute = new ArrayList<>(atMap.keySet()).get(i);
            int in = new ArrayList<>(atMap.values()).get(i);
            AttributeInstance inst = entity.getAttribute(attribute);
            if (inst != null) {
                AttributeModifier modifier = new AttributeModifier(
                        ZERO_MODIFIER_UUID,
                        "Temporary attribute zeroing",
                        in,
                        AttributeModifier.Operation.MULTIPLY_TOTAL
                );
                inst.addTransientModifier(modifier);
            }
        }
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        LivingEntity entity = slotContext.entity();
        for (RegistryObject<Attribute> attr : PsiEXRegistry.ATTRIBUTES.getEntries()) {
            Attribute targetAttr = attr.get();
            AttributeInstance inst = entity.getAttribute(targetAttr);
            if (inst != null) {
                inst.removeModifier(ZERO_MODIFIER_UUID);
            }
        }
    }
}
