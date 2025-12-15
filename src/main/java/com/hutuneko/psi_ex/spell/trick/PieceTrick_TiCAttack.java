package com.hutuneko.psi_ex.spell.trick;

import com.hutuneko.psi_ex.compat.PsiEXRegistry;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import vazkii.psi.api.PsiAPI;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellParam;
import vazkii.psi.api.spell.SpellRuntimeException;
import vazkii.psi.api.spell.param.ParamEntity;
import vazkii.psi.api.spell.piece.PieceTrick;

public class PieceTrick_TiCAttack extends PieceTrick {
    private ParamEntity tParam;
    public PieceTrick_TiCAttack(Spell spell) {
        super(spell);
    }
    @Override
    public void initParams() {
        addParam(tParam = new ParamEntity(SpellParam.GENERIC_NAME_NUMBER,SpellParam.GREEN,false,false
        ));
    }
    @Override
    public Object execute(SpellContext context) throws SpellRuntimeException {
        Entity entity = getParamValue(context, tParam);
        if (entity instanceof LivingEntity livingEntity){
            if (!(context.caster instanceof ServerPlayer player)) return null;
            if (!(PsiAPI.getPlayerCAD(player).getItem() instanceof IModifiable)) throw new SpellRuntimeException("TiCCAD限定");

            ServerLevel serverLevel = player.serverLevel();
            RegistryAccess registryAccess = serverLevel.registryAccess();

            Holder<DamageType> damageTypeHolder = registryAccess.registryOrThrow(Registries.DAMAGE_TYPE)
                    .getHolderOrThrow(PsiEXRegistry.PSI_FAKE_DAMAGE);

            // 修正後の DamageSource 構築
            DamageSource source = new DamageSource(damageTypeHolder, player);

            livingEntity.hurt(source, 1);
        }
        return null;
    }
}
