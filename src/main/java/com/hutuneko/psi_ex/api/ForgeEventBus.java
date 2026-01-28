package com.hutuneko.psi_ex.api;

import com.hutuneko.psi_ex.PsiEX;
import com.hutuneko.psi_ex.compat.PsiEXRegistry;
import com.hutuneko.psi_ex.system.attribute.PsiEXAttributes;
import com.hutuneko.psi_ex.system.PsiPieceConditionReloadListener;
import com.hutuneko.psi_ex.system.capability.PlayerDataProvider;
import com.hutuneko.psi_ex.system.capability.PsionProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingChangeTargetEvent;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import vazkii.psi.api.cad.ISocketable;
import vazkii.psi.api.spell.ISpellAcceptor;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellContext;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = PsiEX.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class ForgeEventBus {
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent e){
//        if (e.phase != TickEvent.Phase.END || e.player.level().isClientSide) return;
//        e.player.getCapability(PsionProvider.CAP).ifPresent(cap -> cap.tickRegain(e.player));
    }
    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (event.isWasDeath()) {
            Player oldPlayer = event.getOriginal();
            Player newPlayer = event.getEntity();
            newPlayer.getCapability(PsionProvider.CAP).ifPresent(cap -> cap.setPsion(newPlayer.getAttributeValue(PsiEXAttributes.PSI_PSION_POINT.get())));
        }
    }
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onChangeTarget(LivingChangeTargetEvent ev) {
        if (ev.getNewTarget() instanceof FakePlayer) {
            ev.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onLivingAttack(LivingAttackEvent ev) {
        if (ev.getEntity() instanceof FakePlayer) {
            ev.setCanceled(true);
        }
    }
    @SubscribeEvent
    public static void onAddReloadListener(AddReloadListenerEvent event) {
        event.addListener(new PsiPieceConditionReloadListener());
    }

    private final Map<UUID, Long> recentlyShotPlayers = new HashMap<>();
    private final Map<UUID, ItemStack> recentlyShotBows = new HashMap<>();

    @SubscribeEvent
    public void onArrowLoose(ArrowLooseEvent event) {
        Player player = event.getEntity();
        ItemStack bow = event.getBow();
        // 矢を撃ったプレイヤーを記録（タイムスタンプ付き）
        recentlyShotPlayers.put(player.getUUID(), System.currentTimeMillis());
        recentlyShotBows.put(player.getUUID(), bow);
    }

    @SubscribeEvent
    public void onArrowSpawn(EntityJoinLevelEvent event) {
        if (!(event.getEntity() instanceof AbstractArrow arrow)) return;

        Entity shooter = arrow.getOwner();
        if (!(shooter instanceof Player player)) return;

        // 直近で矢を撃ったプレイヤーかどうか確認
        Long shotTime = recentlyShotPlayers.get(player.getUUID());
        ItemStack bow = recentlyShotBows.get(player.getUUID());
        if (shotTime != null && System.currentTimeMillis() - shotTime < 200) {

            if (bow.getItem() == PsiEXRegistry.PSI_BOW.get()){
                if (bow.isEmpty()) return;
                ISocketable sock = ISocketable.socketable(bow);
                if (sock == null ) return;
                int idx = 1;
                ItemStack bullet = sock.getBulletInSocket(idx);
                ISpellAcceptor acc = ISpellAcceptor.acceptor(bullet);
                if (acc == null || !ISpellAcceptor.hasSpell(bullet)) return;
                Spell spell = acc.getSpell();
                SpellContext spellContext = new SpellContext();
                spellContext.focalPoint = shooter;
                spellContext.setSpell(spell).setPlayer(player);
                int cost = spellContext.cspell.metadata.hashCode();
                spellContext.cspell.safeExecute(spellContext);
            }

            // 一度使ったら削除
            recentlyShotPlayers.remove(player.getUUID());
            recentlyShotBows.remove(player.getUUID());
        }
    }
    @SubscribeEvent
    public static void onAttach(AttachCapabilitiesEvent<Entity> e){
        if (e.getObject() instanceof Player) {
            e.addCapability(new ResourceLocation(PsiEX.MOD_ID,"psion"), new PsionProvider());
            e.addCapability(new ResourceLocation(PsiEX.MOD_ID,"datas"), new PlayerDataProvider());
        }
    }

    @SubscribeEvent
    public static void onClone(PlayerEvent.Clone e){
        e.getOriginal().getCapability(PsionProvider.CAP).ifPresent(old ->
                e.getEntity().getCapability(PsionProvider.CAP).ifPresent(now -> now.setPsion(old.getPsion())));
    }
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void tick(TickEvent e){
        SpellTriggerContext.remove();
    }

}
