package io.github.hutuneko.psi_ex.spell.trick;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import vazkii.psi.api.internal.Vector3;
import vazkii.psi.api.spell.*;
import vazkii.psi.api.spell.param.ParamEntity;
import vazkii.psi.api.spell.param.ParamVector;
import vazkii.psi.api.spell.piece.PieceTrick;

public class PieceTrick_DirChange extends PieceTrick {
    private ParamVector dirParam;
    private ParamEntity tParam;
    public PieceTrick_DirChange(Spell spell) {
        super(spell);
    }

    @Override
    public void initParams() {
        addParam(dirParam = new ParamVector(SpellParam.GENERIC_NAME_VECTOR,SpellParam.GREEN,false,false
        ));
        addParam(tParam = new ParamEntity(SpellParam.GENERIC_NAME_NUMBER,SpellParam.GREEN,false,false
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
        Entity entity = getParamValue(context, tParam);
        Vector3 vector3 = getParamValue(context, dirParam);
        Vec3 diff = vector3.toVec3D();
        double yaw = Math.toDegrees(Math.atan2(-diff.x, diff.z));

        double pitch = Math.toDegrees(-Math.atan2(diff.y, Math.sqrt(diff.x * diff.x + diff.z * diff.z)));

        entity.yRotO = entity.getYRot();
        entity.xRotO = entity.getXRot();

        // 実際に設定
        entity.setYRot((float) yaw);
        entity.setXRot((float) pitch);
        entity.setYHeadRot((float) yaw);
        entity.setYBodyRot((float) yaw);
        return null;
    }
}
