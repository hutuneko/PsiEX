package com.hutuneko.psi_ex.compat.iron;

import com.hutuneko.psi_ex.PsiEX;
import com.hutuneko.psi_ex.compat.PsiEXRegistry;
import com.hutuneko.psi_ex.item.PsiSpellBook;
import com.hutuneko.psi_ex.spell.trick.PieceTrick_CastScroll;
import com.hutuneko.psi_ex.system.CuriosUtil;
import io.redspace.ironsspellbooks.api.events.SpellDamageEvent;
import moffy.addonapi.AddonModule;
import moffy.ticex.modules.general.TicEXRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import top.theillusivec4.curios.api.SlotResult;
import vazkii.psi.api.PsiAPI;
import vazkii.psi.api.cad.ISocketable;
import vazkii.psi.api.spell.ISpellAcceptor;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellContext;

import java.util.Optional;

public class IronsCompatModule extends AddonModule {
    public IronsCompatModule() {
        PsiAPI.registerSpellPieceAndTexture(new ResourceLocation(PsiEX.MOD_ID, "piecetrick_castscroll"), PieceTrick_CastScroll.class);
        PsiEXRegistry.PSI_SPELLBOOK = PsiEXRegistry.ITEMS.register("psi_spellbook", () -> new PsiSpellBook(12));
        MinecraftForge.EVENT_BUS.addListener(this::onSpellDamage);
    }
    public void onSpellDamage(SpellDamageEvent e) {
        var level = e.getEntity().level();
        if (level.isClientSide) return;

        var caster = e.getSpellDamageSource().getEntity();
        if (!(caster instanceof Player p)) return;
        if (!hasMySpecialSpellbook(p)) return;

        Optional<SlotResult> res = CuriosUtil.findFirstByItem(p, PsiEXRegistry.PSI_SPELLBOOK.get());
        if (res.isEmpty()) return;
        ItemStack spellbook = res.get().stack();
        if (spellbook.isEmpty()) return;
        ISocketable sock = ISocketable.socketable(spellbook);
        if (sock == null ) return;
        int idx = PsiSpellBook.getIndex(spellbook);
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

    private static boolean hasMySpecialSpellbook(Player p) {
        boolean hasBullet = CuriosUtil.findFirstByItem(p, PsiEXRegistry.PSI_SPELLBOOK.get()).isPresent();
        boolean hasCatalyst = CuriosUtil.findFirstByItem(p, TicEXRegistry.CATALYST_IRONS_SPELLBOOK.get()).isPresent();

        // どちらか一方でも装備していれば true
        return hasBullet || hasCatalyst;
    }
}

