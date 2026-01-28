package com.hutuneko.psi_ex.compat.apo.affix;

import com.hutuneko.psi_ex.api.CadBehavior;
import com.hutuneko.psi_ex.api.SpellTriggerContext;
import com.hutuneko.psi_ex.compat.apo.ApoCompatModule;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.shadowsoffire.apotheosis.adventure.affix.*;
import dev.shadowsoffire.apotheosis.adventure.loot.LootCategory;
import dev.shadowsoffire.apotheosis.adventure.loot.LootRarity;
import dev.shadowsoffire.apotheosis.adventure.socket.gem.bonus.GemBonus;
import dev.shadowsoffire.placebo.util.StepFunction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import vazkii.psi.api.PsiAPI;
import vazkii.psi.common.core.handler.PlayerDataHandler;
import vazkii.psi.common.item.ItemCAD;

import java.util.Map;

public class PsiRecoveryAffix extends Affix {
    public static final Codec<PsiRecoveryAffix> CODEC =
            RecordCodecBuilder.create(inst -> inst
                    .group(
                            GemBonus.VALUES_CODEC.fieldOf("values").forGetter(a -> a.values))
                    .apply(inst, PsiRecoveryAffix::new));
    private final Map<LootRarity, StepFunction> values;
    public PsiRecoveryAffix(Map<LootRarity, StepFunction> values) {
        super(AffixType.ABILITY);
        this.values = values;
    }

    @Override
    public Codec<? extends Affix> getCodec() {
        return CODEC;
    }
    @Override
    public boolean canApplyTo(ItemStack itemStack, LootCategory lootCategory, LootRarity lootRarity) {
        if (!((itemStack.getItem() instanceof ItemCAD) || CadBehavior.isCAD(itemStack))) {
            return false;
        }

        CompoundTag tag = itemStack.getTag();
        if (tag != null && tag.contains("affixes", Tag.TAG_COMPOUND)) {
            CompoundTag affixesTag = tag.getCompound("affixes");
            String exclusionKey = ApoCompatModule.PSI_REDUCTION.toString();
            String exclusionKey2 = ApoCompatModule.PSI_REPLAY.toString();
            return !(affixesTag.contains(exclusionKey) || affixesTag.contains(exclusionKey2));
        }

        return true;
    }
    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onLivingHurt(LivingHurtEvent event){
        DamageSource source = event.getSource();
        Entity direct = source.getDirectEntity();
        Entity attacker = source.getEntity();
        boolean isPsiMagic = false;
        if (SpellTriggerContext.isCasting()) {
            isPsiMagic = true;
        }
        else if (direct != null && direct.getPersistentData().contains("psiex_magic")) {
            isPsiMagic = true;
        }
        else if (attacker != null && attacker.getPersistentData().contains("psiex_magic")) {
            isPsiMagic = true;
        }
        if (isPsiMagic) {
            Player player = null;
            if (attacker instanceof Player p) {
                player = p;
            } else if (direct != null && direct.getPersistentData().hasUUID("psiex_caster")) {
                player = event.getEntity().level().getPlayerByUUID(direct.getPersistentData().getUUID("psiex_caster"));
            } else if (attacker != null && attacker.getPersistentData().hasUUID("psiex_caster")) {
                player = event.getEntity().level().getPlayerByUUID(attacker.getPersistentData().getUUID("psiex_caster"));
            } else if (SpellTriggerContext.isCasting()) {
                player = SpellTriggerContext.getCurrent().caster;
            }

            if (player != null) {
                var affixs = AffixHelper.getAffixes(PsiAPI.getPlayerCAD(player));
                if (affixs.containsKey(ApoCompatModule.PSI_RECOVERY)) {
                    if (!player.level().isClientSide) {

                        PlayerDataHandler.PlayerData data = PlayerDataHandler.get(player);
                        if (data.overflowed) {
                            data.overflowed = false;
                        }
                        if (!player.level().isClientSide) {
                            AffixInstance instance = affixs.get(ApoCompatModule.PSI_RECOVERY);
                            data.deductPsi(-(int)(event.getAmount() * instance.level() * 50), 0, true, false);
                        }
                        event.setAmount(0);
                    }
                }
            }
        }
    }
}
