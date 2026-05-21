package io.github.hutuneko.psi_ex.spell.trick;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import vazkii.psi.api.internal.Vector3;
import vazkii.psi.api.spell.*;
import vazkii.psi.api.spell.param.ParamNumber;
import vazkii.psi.api.spell.param.ParamVector;
import vazkii.psi.api.spell.piece.PieceTrick;

import java.util.UUID;

public class PieceTrick_Ejection extends PieceTrick {
    private ParamVector posParam;
    private ParamNumber powerParam;
    private ParamVector dirParam;

    public PieceTrick_Ejection(Spell spell) {
        super(spell);
    }

    @Override
    public void initParams() {

        addParam(posParam = new ParamVector(SpellParam.GENERIC_NAME_VECTOR,SpellParam.GREEN,false,false
        ));
        addParam(powerParam = new ParamNumber(SpellParam.GENERIC_NAME_NUMBER,SpellParam.RED,false,false
        ));
        addParam(dirParam = new ParamVector(SpellParam.GENERIC_NAME_VECTOR1,SpellParam.GREEN,false,false
        ));
    }


    @Override
    public void addToMetadata(SpellMetadata meta) throws SpellCompilationException {
        super.addToMetadata(meta);
        Double powerVal = this.<Double>getParamEvaluation(powerParam);
        if(powerVal == null) {
            powerVal = 1D;
        }
        meta.addStat(EnumSpellStat.POTENCY, 50);
        meta.addStat(EnumSpellStat.COST, (int) (50 * Math.abs(powerVal)));
    }

    @Override
    public EnumPieceType getPieceType() {
        return EnumPieceType.TRICK;
    }

    @Override
    public Class<?> getEvaluationType() {
        return Void.class;
    }
    public static void shootLikeVanilla(ServerLevel level, ServerPlayer shooter,
                                        float power, double x, double y, double z,
                                        Vec3 dir) {
        if (power < 0.1F) return;

        if (!level.hasChunkAt(BlockPos.containing(x, y, z))) return;

        Arrow arrow = new Arrow(level, shooter);
        arrow.setOwner(shooter);
        arrow.pickup = AbstractArrow.Pickup.ALLOWED;

        arrow.setPos(x, y - 0.1, z);

        arrow.shoot(dir.x, dir.y, dir.z, 3.0F * power, 1.0F);
        arrow.setOwner(shooter);

        level.addFreshEntity(arrow);
    }

    @Override
    public Object execute(SpellContext context) throws SpellRuntimeException {
        Vector3 d = this.getParamValue(context, dirParam);
        Vector3 pos = this.getParamValue(context, posParam);
        Number n = this.getParamValue(context, powerParam);
        float power = n.floatValue();


        Player player = context.caster;
        if (player.level().isClientSide) return null;

        Vec3 dir = new Vec3(d.x, d.y, d.z).normalize();


        shootLikeVanilla((ServerLevel)player.level(), (ServerPlayer)player, power,
                pos.x, pos.y, pos.z,dir);
        return null;
    }


}

