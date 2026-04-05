package com.hutuneko.psi_ex.spell.trick.skill;

import com.hutuneko.psi_ex.compat.PsiEXRegistry;
import com.hutuneko.psi_ex.api.piece.PieceTrickExclusive;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import vazkii.psi.api.PsiAPI;
import vazkii.psi.api.spell.*;
import vazkii.psi.api.spell.param.ParamEntity;

public class PieceTrick_TiCAttack extends PieceTrickExclusive {
    private ParamEntity tParam;
    public PieceTrick_TiCAttack(Spell spell) {
        super(spell);
    }

    @Override
    public boolean isCast(Player caster, SpellContext ctx) {
        return (PsiAPI.getPlayerCAD(caster).getItem() instanceof IModifiable);
    }

    @Override
    public void initParams() {
        addParam(tParam = new ParamEntity(SpellParam.GENERIC_NAME_NUMBER,SpellParam.GREEN,false,false
        ));
    }
    @Override
    public void addToMetadata(SpellMetadata meta) throws SpellCompilationException {
        super.addToMetadata(meta);
        meta.addStat(EnumSpellStat.POTENCY, 50);
        meta.addStat(EnumSpellStat.COST, 500);
    }
    @Override
    public Object execute(SpellContext context) throws SpellRuntimeException {
        Entity entity = getParamValue(context, tParam);
        if (entity instanceof LivingEntity livingEntity){
            if (!(context.caster instanceof ServerPlayer player)) return null;

            ServerLevel serverLevel = player.serverLevel();
            RegistryAccess registryAccess = serverLevel.registryAccess();

            Holder<DamageType> damageTypeHolder = registryAccess.registryOrThrow(Registries.DAMAGE_TYPE)
                    .getHolderOrThrow(PsiEXRegistry.PSI_FAKE_DAMAGE);

            DamageSource source = new DamageSource(damageTypeHolder, player);

            livingEntity.hurt(source, 1);
        }
        return null;
    }
}
