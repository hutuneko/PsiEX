package com.hutuneko.psi_ex.compat.iron;

import com.hutuneko.psi_ex.compat.PsiEXRegistry;
import com.hutuneko.psi_ex.system.CuriosUtil;
import moffy.ticex.modules.general.TicEXRegistry;
import net.minecraft.world.entity.player.Player;

public class IfTiCEX {
    public static boolean hasMySpecialSpellbook(Player p) {
        boolean hasBullet = CuriosUtil.findFirstByItem(p, PsiEXRegistry.PSI_SPELLBOOK.get()).isPresent();
        boolean hasCatalyst = CuriosUtil.findFirstByItem(p, TicEXRegistry.CATALYST_IRONS_SPELLBOOK.get()).isPresent();
        return hasBullet || hasCatalyst;
    }
}
