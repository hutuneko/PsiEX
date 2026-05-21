package io.github.hutuneko.psi_ex.compat.apo.affix;

import io.github.hutuneko.psi_ex.api.CadBehavior;
import io.github.hutuneko.psi_ex.compat.apo.ApoCompatModule;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.shadowsoffire.apotheosis.adventure.affix.Affix;
import dev.shadowsoffire.apotheosis.adventure.affix.AffixHelper;
import dev.shadowsoffire.apotheosis.adventure.affix.AffixInstance;
import dev.shadowsoffire.apotheosis.adventure.affix.AffixType;
import dev.shadowsoffire.apotheosis.adventure.loot.LootCategory;
import dev.shadowsoffire.apotheosis.adventure.loot.LootRarity;
import dev.shadowsoffire.apotheosis.adventure.socket.gem.bonus.GemBonus;
import dev.shadowsoffire.placebo.util.StepFunction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import vazkii.psi.api.PsiAPI;
import vazkii.psi.api.spell.SpellCastEvent;
import vazkii.psi.common.item.ItemCAD;

import java.util.Map;

public class PsiReplayAffix extends Affix {
    public static final Codec<PsiReplayAffix> CODEC =
            RecordCodecBuilder.create(inst -> inst
                    .group(
                            GemBonus.VALUES_CODEC.fieldOf("values").forGetter(a -> a.values))
                    .apply(inst, PsiReplayAffix::new));
    private final Map<LootRarity, StepFunction> values;
    public PsiReplayAffix(Map<LootRarity, StepFunction> values) {
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
            String exclusionKey = ApoCompatModule.PSI_RECOVERY.toString();
            String exclusionKey2 = ApoCompatModule.PSI_REDUCTION.toString();
            return !(affixesTag.contains(exclusionKey) || affixesTag.contains(exclusionKey2));
        }

        return true;
    }
    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onSpellCast(SpellCastEvent event){
        Player player = event.context.caster;
        var affixs = AffixHelper.getAffixes(PsiAPI.getPlayerCAD(player));
        if (affixs.containsKey(ApoCompatModule.PSI_REPLAY)){
            AffixInstance instance = affixs.get(ApoCompatModule.PSI_REPLAY);
            if (player.getRandom().nextFloat() < instance.level()/100f){
                if (player instanceof ServerPlayer sp){
                    sp.sendSystemMessage(Component.translatable("message.psi_ex.spellreplay"));
                    event.context.cspell.safeExecute(event.context);
                }
            }
        }
    }
}
