package io.github.hutuneko.psi_ex.spell.trick;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import vazkii.psi.api.spell.*;
import vazkii.psi.api.spell.EnumPieceType;
import vazkii.psi.api.spell.EnumSpellStat;
import vazkii.psi.api.spell.param.ParamEntity;
import vazkii.psi.api.spell.param.ParamNumber;
import vazkii.psi.api.spell.piece.PieceTrick;

public class TrickItemAttack extends PieceTrick {
    private ParamEntity targetParam;
    private ParamNumber slotParam;
    public TrickItemAttack(Spell spell) {
        super(spell);
    }

    @Override
    public void initParams() {
        addParam(targetParam = new ParamEntity(SpellParam.GENERIC_NAME_TARGET,SpellParam.RED,false,false
        ));
        addParam(slotParam = new ParamNumber("slot",SpellParam.BLUE,false,false));
    }

    @Override
    public void addToMetadata(SpellMetadata meta) throws SpellCompilationException {
        super.addToMetadata(meta);
        meta.addStat(EnumSpellStat.POTENCY, 10);
        meta.addStat(EnumSpellStat.COST,    300);
    }

    @Override
    public EnumPieceType getPieceType() {
        return EnumPieceType.TRICK;
    }

    @Override
    public Class<?> getEvaluationType() {
        return Void.class;
    }

    @Override
    public Object execute(SpellContext context) throws SpellRuntimeException {
        Player player = context.caster;
        Level world = player.level();
        if (world.isClientSide) {
            return null;
        }

        Object raw = getParamValue(context, targetParam);
        if (!(raw instanceof LivingEntity target)) {
            throw new SpellRuntimeException("有効なターゲットを選択してください");
        }
        int slot = getParamValue(context, slotParam).intValue();
        if (!context.isInRadius(target)) {
            throw new SpellRuntimeException(SpellRuntimeException.OUTSIDE_RADIUS);
        }

        ItemStack main   = player.getMainHandItem();
        ItemStack slotitem = player.getInventory().getItem(slot);
        if (slotitem.isEmpty()) {
            throw new SpellRuntimeException("オフハンドに武器を持ってください");
        }

        player.setItemSlot(EquipmentSlot.MAINHAND, slotitem);

        player.attack(target);
        player.swing(InteractionHand.MAIN_HAND);

        player.setItemSlot(EquipmentSlot.MAINHAND, main);

        return null;
    }
}

