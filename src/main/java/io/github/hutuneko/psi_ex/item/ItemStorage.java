package io.github.hutuneko.psi_ex.item;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class ItemStorage extends Item {

    public ItemStorage(Properties props) {
        super(props);
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext ctx) {
        Level world = ctx.getLevel();
        if (world.isClientSide) return InteractionResult.SUCCESS;

        Player player = ctx.getPlayer();
        BlockPos pos = ctx.getClickedPos();
        ItemStack stack = ctx.getItemInHand();
        CompoundTag tag = stack.getOrCreateTag();

        if (!tag.contains("Pos1")) {
            tag.putLong("Pos1", pos.asLong());
            Objects.requireNonNull(player).displayClientMessage(
                    Component.literal("Pos1 set to " + pos),
                    true
            );
            return InteractionResult.SUCCESS;
        }

        if (!tag.contains("Pos2")) {
            tag.putLong("Pos2", pos.asLong());
            Objects.requireNonNull(player).displayClientMessage(
                    Component.literal("Pos2 set to " + pos),
                    true
            );

            BlockPos p1 = BlockPos.of(tag.getLong("Pos1"));
            BlockPos p2 = BlockPos.of(tag.getLong("Pos2"));
            int xMin = Math.min(p1.getX(), p2.getX()), xMax = Math.max(p1.getX(), p2.getX());
            int yMin = Math.min(p1.getY(), p2.getY()), yMax = Math.max(p1.getY(), p2.getY());
            int zMin = Math.min(p1.getZ(), p2.getZ()), zMax = Math.max(p1.getZ(), p2.getZ());

            ListTag list = new ListTag();
            for (int x = xMin; x <= xMax; x++) {
                for (int y = yMin; y <= yMax; y++) {
                    for (int z = zMin; z <= zMax; z++) {
                        BlockPos p = new BlockPos(x, y, z);
                        CompoundTag entry = new CompoundTag();

                        BlockState state = world.getBlockState(p);

                        entry.put("block_state",
                                NbtUtils.writeBlockState(state)
                        );

                        BlockEntity be = world.getBlockEntity(p);
                        if (be != null) {
                            CompoundTag beTag = be.saveWithFullMetadata();
                            entry.put("block_entity", beTag);
                        }

                        entry.putLong("Pos", p.asLong());

                        list.add(entry);

                    }
                }
            }
            tag.put("StoredBlocks", list);
            Objects.requireNonNull(player).displayClientMessage(
                    Component.literal("Stored " + list.size() + " blocks"),
                    true
            );
            return InteractionResult.SUCCESS;
        }

        // 3) Clear data on crouch
        if (Objects.requireNonNull(player).isCrouching()) {
            tag.remove("Pos1");
            tag.remove("Pos2");
            tag.remove("StoredBlocks");
            Objects.requireNonNull(player).displayClientMessage(
                    Component.literal("Storage data cleared"),
                    true
            );
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    public static ListTag getStoredBlocks(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("StoredBlocks", Tag.TAG_LIST)) {
            return tag.getList("StoredBlocks", Tag.TAG_COMPOUND);
        }
        return new ListTag();
    }
}
