package io.github.hutuneko.psi_ex.compat.iron;

import io.github.hutuneko.psi_ex.compat.PsiEXRegistry;
import io.github.hutuneko.psi_ex.item.PsiSpellBook;
import io.github.hutuneko.psi_ex.system.CuriosUtil;
import io.redspace.ironsspellbooks.api.events.SpellDamageEvent;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import moffy.ticex.modules.general.TicEXRegistry;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.ModList;
import top.theillusivec4.curios.api.SlotResult;
import vazkii.psi.api.cad.ISocketable;
import vazkii.psi.api.spell.ISpellAcceptor;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellContext;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class IronsEvent {
    public static void onSpellDamage(SpellDamageEvent e) {
        var level = e.getEntity().level();
        if (level.isClientSide) return;

        var caster = e.getSpellDamageSource().getEntity();
        if (!(caster instanceof Player p)) return;
        if ( ModList.get().isLoaded("ticex")){
            if (!IfTiCEX.hasMySpecialSpellbook(p)) return;
        }else {
            if (!hasMySpecialSpellbook(p)) return;
        }

        Optional<SlotResult> res = CuriosUtil.findFirstByItem(p, PsiEXRegistry.PSI_SPELLBOOK.get());
        if (res.isEmpty()) return;
        ItemStack spellbook = res.get().stack();
        if (spellbook.isEmpty()) return;
        ISocketable sock = ISocketable.socketable(spellbook);
        if (sock == null ) return;
        int idx = PsiSpellBook.getIndex(spellbook,p);
        ItemStack bullet = sock.getBulletInSocket(idx);
        if (bullet == null) return;
        if (!ISpellAcceptor.hasSpell(bullet)) return;
        ISpellAcceptor acc = ISpellAcceptor.acceptor(bullet);
        if (acc == null || !ISpellAcceptor.hasSpell(bullet)) return;
        Spell spell = acc.getSpell();
        Entity entity = e.getEntity();
        if (!(entity instanceof LivingEntity livingEntity)) return;
        SpellContext spellContext = new SpellContext();
        spellContext.attackedEntity = livingEntity;
        spellContext.setSpell(spell).setPlayer(p);

        spellContext.cspell.safeExecute(spellContext);
    }
    public static boolean hasMySpecialSpellbook(Player p) {
        return CuriosUtil.findFirstByItem(p, PsiEXRegistry.PSI_SPELLBOOK.get()).isPresent();
    }
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        int currentTick = event.getServer().getTickCount();

        activeFakePlayers.entrySet().removeIf(entry -> {
            FakePlayerTracker tracker = entry.getValue();

            // 詠唱終了時刻を過ぎた、または既に死んでいる
            if (currentTick >= tracker.endTick || !tracker.fakePlayer.isAlive()) {
                // 適当な場所に移動して無力化（またはremove）
                tracker.fakePlayer.setPos(tracker.returnPos.x, tracker.returnPos.y, tracker.returnPos.z);

                // 詠唱状態をリセット
                MagicData magicData = MagicData.getPlayerMagicData(tracker.fakePlayer);
                magicData.resetCastingState();
                magicData.setAdditionalCastData(null);

                return true; // Mapから削除
            }
            return false;
        });
    }
    public static final Map<UUID, FakePlayerTracker> activeFakePlayers = new ConcurrentHashMap<>();

    public static class FakePlayerTracker {
        final FakePlayer fakePlayer;
        final int endTick;
        final Vec3 returnPos;

        FakePlayerTracker(FakePlayer fakePlayer, int duration, Vec3 returnPos) {
            this.fakePlayer = fakePlayer;

            this.endTick = fakePlayer.level().getServer().getTickCount() + duration;
            this.returnPos = returnPos;
        }
        public static FakePlayerTracker newFakePlayer(FakePlayer fakePlayer, int duration, Vec3 returnPos){
            FakePlayerTracker fakePlayerTracker = new FakePlayerTracker(fakePlayer, duration, returnPos);
            activeFakePlayers.put(fakePlayer.getUUID(), fakePlayerTracker);
            return fakePlayerTracker;
        }
    }

    private static class IfTiCEX {
        public static boolean hasMySpecialSpellbook(Player p) {
            boolean hasBullet = CuriosUtil.findFirstByItem(p, PsiEXRegistry.PSI_SPELLBOOK.get()).isPresent();
            boolean hasCatalyst = CuriosUtil.findFirstByItem(p, TicEXRegistry.CATALYST_IRONS_SPELLBOOK.get()).isPresent();
            return hasBullet || hasCatalyst;
        }
    }
}
