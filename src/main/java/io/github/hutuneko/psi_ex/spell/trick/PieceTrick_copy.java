
package io.github.hutuneko.psi_ex.spell.trick;

import io.github.hutuneko.psi_ex.item.ItemStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import vazkii.psi.api.internal.Vector3;
import vazkii.psi.api.spell.*;
import vazkii.psi.api.spell.piece.PieceTrick;
import vazkii.psi.api.spell.param.ParamAny;
import vazkii.psi.api.spell.param.ParamVector;

public class PieceTrick_copy extends PieceTrick {
    private static final Logger LOGGER = LogManager.getLogger();

    private ParamVector posParam;
    private ParamAny    storageParam;

    public PieceTrick_copy(Spell spell) {
        super(spell);
    }

    @Override
    public void initParams() {
        addParam(posParam = new ParamVector(SpellParam.GENERIC_NAME_VECTOR,SpellParam.RED,false,false));
        addParam(storageParam = new ParamAny("scrollStack",10, false));
    }

    @Override
    public void addToMetadata(SpellMetadata meta) throws SpellCompilationException {
        super.addToMetadata(meta);
        meta.addStat(EnumSpellStat.POTENCY, 50);
        meta.addStat(EnumSpellStat.COST,    100);
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
        Vector3 v = getParamValue(context, posParam);
        BlockPos target = new BlockPos(
                (int) Math.floor(v.x),
                (int) Math.floor(v.y),
                (int) Math.floor(v.z)
        );
        Player player = context.caster;
        Level world = player.level();
        Object raw = getParamValue(context, storageParam);
        if (!(raw instanceof ItemStack storageStack)) {
            throw new SpellRuntimeException("右隣アイテムのデータを受け取れませんでした");
        }

        ListTag stored = ItemStorage.getStoredBlocks(storageStack);
        if (stored == null || stored.isEmpty()) {
            throw new SpellRuntimeException("このアイテムにストレージデータがありません");
        }
        if (world instanceof ServerLevel sWorld) {
            LOGGER.info("Stored entries = {}, target = {}", stored.size(), target);

            for (Tag t : stored) {
                if (!(t instanceof CompoundTag entry)) continue;
                CompoundTag stateTag = entry.getCompound("block_state");
                BlockPos pos = entry.contains("Pos")
                        ? BlockPos.of(entry.getLong("Pos"))
                        : new BlockPos(
                        entry.getInt("x"),
                        entry.getInt("y"),
                        entry.getInt("z")
                );

                if (entry.contains("block_state", Tag.TAG_COMPOUND)) {
                    @SuppressWarnings("unchecked")
                    HolderGetter<Block> getter =
                            (HolderGetter<Block>) sWorld
                                    .registryAccess()
                                    .registryOrThrow(Registries.BLOCK);

                    BlockState state = NbtUtils.readBlockState(getter, stateTag);
                    sWorld.setBlockAndUpdate(pos, state);
                }
                if (entry.contains("block_entity", Tag.TAG_COMPOUND)) {
                    CompoundTag beTag = entry.getCompound("block_entity");
                    beTag.remove("x");
                    beTag.remove("y");
                    beTag.remove("z");
                    BlockEntity be = sWorld.getBlockEntity(pos);
                    if (be != null) {
                        be.load(beTag);
                        be.setChanged();
                    }
                }
            }
        }

        return null;
    }
}
