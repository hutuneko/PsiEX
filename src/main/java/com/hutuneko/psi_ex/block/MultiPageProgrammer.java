package com.hutuneko.psi_ex.block;

import com.hutuneko.psi_ex.api.client.gui.MultiProgrammerScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import vazkii.psi.api.internal.VanillaPacketDispatcher;
import vazkii.psi.common.block.BlockProgrammer;

import javax.annotation.Nonnull;

public class MultiPageProgrammer extends BlockProgrammer{
    public MultiPageProgrammer(Properties props) {
        super(props);
    }
    @Override
    public BlockEntity newBlockEntity(@Nonnull BlockPos pos, @Nonnull BlockState state) {
        return new MultiPageTileProgrammer(pos, state);
    }

    @Override
    public @NotNull InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand hand, BlockHitResult rayTraceResult) {
        ItemStack heldItem = player.getItemInHand(hand);
        MultiPageTileProgrammer programmer = (MultiPageTileProgrammer)worldIn.getBlockEntity(pos);
        if (programmer == null) {
            return InteractionResult.PASS;
        } else {
            InteractionResult result = this.setSpell(worldIn, pos, player, heldItem);
            if (result != InteractionResult.SUCCESS) {
                boolean enabled = programmer.isEnabled();
                if (!enabled || programmer.playerLock.isEmpty()) {
                    programmer.playerLock = player.getName().getString();
                }

                if (player instanceof ServerPlayer) {
                    VanillaPacketDispatcher.dispatchTEToPlayer(programmer, (ServerPlayer) player);
                }
                if (worldIn.isClientSide) {
                    MultiProgrammerScreen.openGUI(programmer, programmer.getCurrentPage());
                }

            }
            return InteractionResult.SUCCESS;
        }
    }
}
