package com.hutuneko.psi_ex.compat.tic;

import com.hutuneko.psi_ex.PsiEX;
import com.hutuneko.psi_ex.compat.PsiEXRegistry;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
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
    public static void onDamage(LivingHurtEvent event) {
        LivingEntity target = event.getEntity();
        DamageSource source = event.getSource();
        if (source.typeHolder()
                .unwrapKey() // Optional<ResourceKey<DamageType>> が返る
                .map(key -> key.equals(PsiEXRegistry.PSI_FAKE_DAMAGE)) // Keyを比較し、結果を返す
                .orElse(false)) {
            if (source.getEntity() instanceof Player player) {
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
        }
    }
}
