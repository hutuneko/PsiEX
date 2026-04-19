package com.hutuneko.psi_ex.api;

import com.google.common.collect.Multimap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import vazkii.psi.api.cad.CADStatEvent;
import vazkii.psi.api.cad.EnumCADStat;
import vazkii.psi.common.item.ItemCAD;
import vazkii.psi.common.item.base.ModItems;

import javax.annotation.Nonnull;

public class CadBehavior {
    private ItemCAD cad;
    public static CadBehavior getCADBehavior() {
        CadBehavior cadBehavior = new CadBehavior();
        if (ModItems.cad instanceof ItemCAD itemCAD){
            cadBehavior.cad = itemCAD;
        }
        return cadBehavior;
    }
    public ItemCAD cad(){
        return cad;
    }
    public static boolean isCAD(ItemStack stack){
        CompoundTag nbt = stack.getTag();
        if (nbt != null) {
            return nbt.getBoolean("psiex_iscad");
        }
        return false;
    }
    public int getStatValue(ItemStack stack, EnumCADStat stat) {

        if (isCAD(stack)) {
            int durability = stack.getMaxDamage();
            double attackDamage = 4 + getAttributeValue(stack, Attributes.ATTACK_DAMAGE);
            double attackSpeed = 4.0 + getAttributeValue(stack, Attributes.ATTACK_SPEED);

            int baseStatValue = 0;

            // --- ステータスの割り当て ---

            // 1. 耐久力ベース
            if (stat == EnumCADStat.OVERFLOW) {
                // Psi容量: 耐久力の半分
                baseStatValue = durability / 2;

            } else if (stat == EnumCADStat.SOCKETS) {
                // ソケット数: 耐久力に応じて増える
                baseStatValue = Math.min(12, durability / 100 + 1);

            } else if (stat == EnumCADStat.SAVED_VECTORS) {
                // 保存ベクトル数
                baseStatValue = durability / 20;

                // 2. 攻撃速度ベース (効率、複雑性など処理速度に関わるもの)
            } else if (stat == EnumCADStat.EFFICIENCY) {
                // 効率: 攻撃速度が速いほど高い
                baseStatValue = (int) (attackSpeed * 20);

            } else if (stat == EnumCADStat.COMPLEXITY) {
                // 複雑性: 攻撃速度が高いほど処理能力が高いとみなす
                baseStatValue = (int) (attackSpeed * 15);

            } else if (stat == EnumCADStat.PROJECTION) {
                // 投影精度
                baseStatValue = (int) (attackSpeed * 10);

                // 3. 攻撃力ベース (威力、帯域などパワーに関わるもの)
            } else if (stat == EnumCADStat.POTENCY) {
                // 威力: 攻撃力が高いほど強い
                baseStatValue = (int) (attackDamage * 30);

            } else if (stat == EnumCADStat.BANDWIDTH) {
                // 帯域幅
                baseStatValue = (int) (attackDamage * 20);
            }

            // イベントを発火して結果を返す
            CADStatEvent event = new CADStatEvent(stat, stack, ItemStack.EMPTY, baseStatValue);
            MinecraftForge.EVENT_BUS.post(event);

            return event.getStatValue();
        }

        // CADでない場合は -1 (対象外) を返す
        return -1;
    }

    private double getAttributeValue(ItemStack stack, Attribute attribute) {
        double total = 0;
        // メインハンドに装備した時の属性を取得
        Multimap<Attribute, AttributeModifier> modifiers =
                stack.getAttributeModifiers(EquipmentSlot.MAINHAND);

        if (modifiers.containsKey(attribute)) {
            for (AttributeModifier modifier : modifiers.get(attribute)) {
                if (modifier != null) {
                    total += modifier.getAmount();
                }
            }
        }
        return total;
    }
    public InteractionResultHolder<ItemStack> use(Level world, Player player, @Nonnull InteractionHand hand){
        if (isCAD(player.getItemInHand(hand))){
            return cad.use(world, player, hand);
        }
        return null;
    }
}
