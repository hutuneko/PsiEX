package io.github.hutuneko.psi_ex.spell.trick;

import io.github.hutuneko.psi_ex.compat.PsiEXRegistry;
import io.github.hutuneko.psi_ex.entity.PsiAirEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import vazkii.psi.api.internal.Vector3;
import vazkii.psi.api.spell.*;
import vazkii.psi.api.spell.param.ParamNumber;
import vazkii.psi.api.spell.param.ParamVector;
import vazkii.psi.api.spell.piece.PieceTrick;

public class PieceTrick_CompressedAir extends PieceTrick {
    private ParamVector dirParam;
    private ParamNumber speedParam;
    private ParamNumber damageParam;
    public PieceTrick_CompressedAir(Spell spell) {
        super(spell);
    }

    @Override
    public void initParams() {
        addParam(dirParam = new ParamVector(SpellParam.GENERIC_NAME_VECTOR,SpellParam.GREEN,false,false
        ));
        addParam(speedParam = new ParamNumber(SpellParam.GENERIC_NAME_NUMBER,SpellParam.GREEN,false,false
        ));
        addParam(damageParam = new ParamNumber(SpellParam.GENERIC_NAME_NUMBER3,SpellParam.GREEN,false,false
        ));
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
        Level level = player.level();
        Number s = getParamValue(context, speedParam);
        Vector3 vector3 = getParamValue(context, dirParam);
        Vec3 diff = vector3.toVec3D();
        Number d = getParamValue(context, damageParam);
        float damage = d.floatValue();
        float speed = s.floatValue();
        if (level.isClientSide) return null;

        var proj = new PsiAirEntity(PsiEXRegistry.PSI_COMPRESSIONAIR_ENTITY.get(), level, player);
        proj.setPos(player.getX(), player.getEyeY() - 0.1, player.getZ());
        proj.setDamage(damage);
        proj.launchFrom(player, diff.normalize(), speed, 0.0F);
        level.addFreshEntity(proj);
        return null;
    }
}
