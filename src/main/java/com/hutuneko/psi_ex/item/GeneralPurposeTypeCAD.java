package com.hutuneko.psi_ex.item;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.type.capability.ICurioItem;
import vazkii.psi.api.spell.ISpellAcceptor;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.common.core.handler.PlayerDataHandler;
import vazkii.psi.common.item.ItemCAD;
import vazkii.psi.common.item.base.ModItems;

public class GeneralPurposeTypeCAD extends ItemCAD implements ICurioItem {
    public GeneralPurposeTypeCAD(Properties properties) {
        super(properties.stacksTo(1));
    }
    public static void spellCast(Spell spell, Player caster,ItemStack cad){
        PlayerDataHandler.PlayerData data = PlayerDataHandler.get(caster);
        ItemStack bullet = new ItemStack(ModItems.spellBullet);
        ISpellAcceptor acceptor = ISpellAcceptor.acceptor(bullet);
        acceptor.setSpell(caster,spell);
        if (cad.getItem() instanceof GeneralPurposeTypeCAD) {
            ItemCAD.cast(
                    caster.getCommandSenderWorld(),
                    caster,
                    data,
                    bullet,
                    cad,
                    5,
                    10,
                    0.05F,
                    spellContext ->{}
            );
        }
    }
}
