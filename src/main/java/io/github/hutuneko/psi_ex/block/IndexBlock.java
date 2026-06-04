package io.github.hutuneko.psi_ex.block;

import io.github.hutuneko.psi_ex.api.menu.IndexMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

/**
 * CraftingTableBlock を参考にしたブロック実装
 */
public class IndexBlock extends Block {
    private static final Component CONTAINER_TITLE = Component.translatable("container.psi_ex.spell_index");

    public IndexBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public @NotNull InteractionResult use(@NotNull BlockState pState, Level pLevel, @NotNull BlockPos pPos, @NotNull Player pPlayer, @NotNull InteractionHand pHand, @NotNull BlockHitResult pHit) {
        if (pLevel.isClientSide) {
            // クライアント側は SUCCESS を返して、サーバーに処理を任せる
            return InteractionResult.SUCCESS;
        } else {
            // サーバー側: Menu を開く
            pPlayer.openMenu(pState.getMenuProvider(pLevel, pPos));
            return InteractionResult.CONSUME;
        }
    }

    @Override
    public MenuProvider getMenuProvider(@NotNull BlockState pState, @NotNull Level pLevel, @NotNull BlockPos pPos) {
        return new SimpleMenuProvider((containerId, playerInventory, player) -> new IndexMenu(containerId, playerInventory, ContainerLevelAccess.create(pLevel, pPos)), CONTAINER_TITLE);
    }
}