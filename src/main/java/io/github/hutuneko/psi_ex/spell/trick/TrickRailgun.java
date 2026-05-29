package io.github.hutuneko.psi_ex.spell.trick;

import io.github.hutuneko.psi_ex.compat.PsiEXRegistry;
import io.github.hutuneko.psi_ex.entity.Railgun;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import vazkii.psi.api.internal.Vector3;
import vazkii.psi.api.spell.*;
import vazkii.psi.api.spell.param.ParamNumber;
import vazkii.psi.api.spell.param.ParamVector;
import vazkii.psi.api.spell.piece.PieceTrick;

import java.util.List;

public class TrickRailgun extends PieceTrick {
    private ParamVector dirParam;
    private ParamNumber speedParam;
    public TrickRailgun(Spell spell) {
        super(spell);
    }

    @Override
    public void initParams() {
        addParam(dirParam = new ParamVector(SpellParam.GENERIC_NAME_VECTOR,SpellParam.GREEN,false,false
        ));
        addParam(speedParam = new ParamNumber(SpellParam.GENERIC_NAME_NUMBER,SpellParam.GREEN,false,false
        ));
    }
    @Override
    public void addToMetadata(SpellMetadata meta) throws SpellCompilationException {
        super.addToMetadata(meta);
        meta.addStat(EnumSpellStat.POTENCY, 20);
        meta.addStat(EnumSpellStat.COST,   1000);
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
        Vec3 pos = player.position();
        Level level = player.level();
        Number s = getParamValue(context, speedParam);
        Vector3 vector3 = getParamValue(context, dirParam);
        Vec3 look = vector3.toVec3D();
        float speed = s.floatValue();
        if (level.isClientSide) return null;
        double radius = 5.0;
        AABB area = new AABB(
                pos.x - radius, pos.y - radius, pos.z - radius,
                pos.x + radius, pos.y + radius, pos.z + radius
        );
        List<ItemEntity> items = level.getEntitiesOfClass(ItemEntity.class, area);
        if (items.isEmpty())return null;
        for (ItemEntity item : items){
            item.kill();
            speed ++;
        }
        var bullet = new Railgun(PsiEXRegistry.RAILGUN.get(), player, level);
        bullet.setSpeed(speed);
        bullet.setDeltaMovement(look.x * speed, look.y * speed, look.z * speed);
        bullet.setPos(pos.x(), pos.y() - 0.1, pos.z());
        level.addFreshEntity(bullet);
        return null;
    }
}
