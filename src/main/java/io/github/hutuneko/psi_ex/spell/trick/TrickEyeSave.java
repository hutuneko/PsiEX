package io.github.hutuneko.psi_ex.spell.trick;

import io.github.hutuneko.psi_ex.compat.PsiEXRegistry;
import io.github.hutuneko.psi_ex.system.CuriosUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotResult;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellParam;
import vazkii.psi.api.spell.SpellRuntimeException;
import vazkii.psi.api.spell.param.ParamEntity;
import vazkii.psi.api.spell.piece.PieceTrick;

import java.util.Optional;
import java.util.UUID;

public class TrickEyeSave extends PieceTrick {

    private ParamEntity entity;
    public TrickEyeSave(Spell spell) {
        super(spell);
    }

    @Override
    public void initParams() {
        entity = new ParamEntity(SpellParam.GENERIC_NAME_TARGET,20,false,false);
        addParam(entity);
    }

    @Override
    public Object execute(SpellContext ctx) throws SpellRuntimeException {
        UUID v = this.getParamValue(ctx, entity).getUUID();
        Player p = ctx.caster;
        if (p == null) throw new SpellRuntimeException("No caster");
        Optional<SlotResult> res = CuriosUtil.findFirstByItem(p, PsiEXRegistry.PSI_SPIRITS_EYE.get());
        if (res.isEmpty()) throw new SpellRuntimeException("psi_ex.spellerror.nocurios");
        ItemStack eye = res.get().stack();
        if (eye.isEmpty()) throw new SpellRuntimeException("psi_ex.spellerror.nocurios");
        CompoundTag tag = eye.getOrCreateTag();
        tag.putUUID("psi_ex:eyeuuid",v);
        return null;
    }
}
