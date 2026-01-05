package com.hutuneko.psi_ex.compat.tic;

import com.hutuneko.psi_ex.api.SpellTriggerContext;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityStruckByLightningEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import vazkii.psi.api.PsiAPI;

public class TiCEvent {
    private static boolean isApplyingTiC = false;
    public static void onDamage(LivingHurtEvent event) {

        if (isApplyingTiC) return;
        DamageSource source = event.getSource();
        Entity direct = source.getDirectEntity();
        Entity attacker = source.getEntity(); // 犯人（プレイヤーなど）
        boolean isPsiMagic = false;
        // 1. ThreadLocalチェック
        if (SpellTriggerContext.isCasting()) {
            isPsiMagic = true;
        }
        // 2. 直接の実体（雷など）のNBTチェック
        else if (direct != null && direct.getPersistentData().contains("psiex_magic")) {
            isPsiMagic = true;
        }
        // 3. 犯人エンティティそのものに印がついているケース（一部のMod魔法対策）
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
                applyTiCDamage(event, player, event.getEntity());
            }
        }
    }
    public static void onLightningStrike(EntityStruckByLightningEvent event) {
        LightningBolt bolt = event.getLightning();
        Entity target = event.getEntity();

        if (bolt.getPersistentData().contains("psiex_magic")) {
            Player player = bolt.getCause();

            if (player != null && target instanceof LivingEntity livingTarget) {
                player.sendSystemMessage(Component.literal("§6[PsiEX]§r 雷撃の魔法を検知しました"));
                isApplyingTiC = true;
                ticDamage(player, livingTarget);
                isApplyingTiC = false;
            }
        }
    }
    private static void applyTiCDamage(LivingHurtEvent event, Player player, LivingEntity target) {
        ItemStack cadStack = PsiAPI.getPlayerCAD(player);
        if (cadStack != null && cadStack.getItem() instanceof IModifiable) {
            IToolStackView cad = ToolStack.from(cadStack);
            ToolAttackContext context = new ToolAttackContext(
                    player,
                    player,
                    InteractionHand.MAIN_HAND,
                    target,
                    target,
                    false,
                    1,
                    false
            );
            float damage = cad.getStats().get(ToolStats.ATTACK_DAMAGE);
            for (ModifierEntry entry : cad.getModifierList()) {
                damage = entry.getHook(ModifierHooks.MELEE_DAMAGE).getMeleeDamage(cad, entry, context, 0, damage);
            }
            if (damage <= 0) {
                event.setCanceled(true);
                return;
            }
            event.setAmount(damage);
            for (ModifierEntry entry : cad.getModifierList()) {
                entry.getHook(ModifierHooks.MELEE_HIT).beforeMeleeHit(cad, entry, context, damage, 0, 0);
            }
            for (ModifierEntry entry : cad.getModifierList()) {
                entry.getHook(ModifierHooks.MELEE_HIT).afterMeleeHit(cad, entry, context, damage);
            }
        }
    }
    public static void tick(TickEvent e){
        SpellTriggerContext.remove();
    }
    private static void ticDamage(Player player, LivingEntity target) {
        ItemStack cadStack = PsiAPI.getPlayerCAD(player);

        if (cadStack != null && cadStack.getItem() instanceof IModifiable) {
            IToolStackView cad = ToolStack.from(cadStack);


            ToolAttackContext context = new ToolAttackContext(
                    player,
                    player,
                    InteractionHand.MAIN_HAND,
                    target,
                    target,
                    false,
                    1.0F,
                    false
            );

            float damage = cad.getStats().get(ToolStats.ATTACK_DAMAGE);
            for (ModifierEntry entry : cad.getModifierList()) {
                damage = entry.getHook(ModifierHooks.MELEE_DAMAGE).getMeleeDamage(cad, entry, context, 0, damage);
            }

            if (damage <= 0) return;

            for (ModifierEntry entry : cad.getModifierList()) {
                entry.getHook(ModifierHooks.MELEE_HIT).beforeMeleeHit(cad, entry, context, damage, 0, 0);
            }

            DamageSource lightningSrc = target.damageSources().lightningBolt();

            target.hurt(lightningSrc, damage);


            for (ModifierEntry entry : cad.getModifierList()) {
                entry.getHook(ModifierHooks.MELEE_HIT).afterMeleeHit(cad, entry, context, damage);
            }

        }
    }
}
