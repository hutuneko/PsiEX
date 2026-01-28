package com.hutuneko.psi_ex.spell.trick;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.Tags;
import vazkii.psi.api.internal.Vector3;
import vazkii.psi.api.spell.*;
import vazkii.psi.api.spell.param.ParamNumber;
import vazkii.psi.api.spell.param.ParamVector;
import vazkii.psi.api.spell.piece.PieceTrick;

import java.util.List;

public class PieceTrick_OreDouble extends PieceTrick {

    private ParamVector vectorParam;
    private ParamNumber doudleParam;

    public PieceTrick_OreDouble(Spell spell) {
        super(spell);
    }

    @Override
    public void initParams() {

        addParam(vectorParam = new ParamVector(SpellParam.GENERIC_NAME_TARGET,
                SpellParam.GREEN,
                false,
                false
        ));
        addParam(doudleParam = new ParamNumber(SpellParam.GENERIC_NAME_NUMBER,
                SpellParam.BLUE,
                false,
                true
        ));
    }


    @Override
    public void addToMetadata(SpellMetadata meta) throws SpellCompilationException {
        super.addToMetadata(meta);
        Double powerVal = this.<Double>getParamEvaluation(doudleParam);
        if(powerVal == null) {
            powerVal = 1D;
        }
        meta.addStat(EnumSpellStat.POTENCY, 20);
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

    @Override
    public Object execute(SpellContext context) throws SpellRuntimeException {
        Vector3 vec = getParamValue(context, vectorParam);
        BlockPos pos = BlockPos.containing(vec.x, vec.y, vec.z);
        Player player = context.caster;
        Level level = player.level();
        BlockState state = level.getBlockState(pos);
        Block block = state.getBlock();
        ItemStack item = block.asItem().getDefaultInstance();
        ServerLevel server = (ServerLevel) level;
        System.out.println(state.getTags());
        if (item.is(Tags.Items.ORES)) {
            Number nraw = this.getParamValue(context, doudleParam);
            int n = nraw.intValue();
            for (int i = 0; i < n; i++) {
            LootParams.Builder lootBuilder = new LootParams.Builder(server)
                    .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos))
                    .withOptionalParameter(LootContextParams.TOOL, player.getMainHandItem());

            List<ItemStack> drops = state.getDrops(lootBuilder);
                for (ItemStack drop : drops) {
                    double x = pos.getX() + 0.5;
                    double y = pos.getY() + 0.5;
                    double z = pos.getZ() + 0.5;
                    ItemEntity ent = new ItemEntity(server, x, y, z, drop);
                    ent.setDeltaMovement(0, 0, 0);
                        server.addFreshEntity(ent);
                }
            }
            level.destroyBlock(pos,false);
        }else{
            throw new SpellRuntimeException("鉱石ではありません。");
        }
        return null;
    }

}
