package io.github.hutuneko.psi_ex.compat.cc.peripherals;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class BlockEntityPeripheralOwner implements IPeripheralOwner {

    private final BlockEntity blockEntity;
    private Player lastPlayer;  // 最後に操作したプレイヤー

    public BlockEntityPeripheralOwner(BlockEntity blockEntity) {
        this.blockEntity = blockEntity;
    }

    @Override
    public Player getPlayer() {
        return lastPlayer;  // ブロックの場合は外部から設定する必要がある
    }

    public void setLastPlayer(Player player) {
        this.lastPlayer = player;
    }

    @Override
    public BlockPos getPos() {
        return blockEntity.getBlockPos();
    }

    @Override
    public Level getLevel() {
        return blockEntity.getLevel();
    }

    @Override
    public boolean isHandheld() {
        return false;  // ブロックはフルパワー
    }

    @Override
    public void applyCost() {
        // ブロックはコストなし
    }
}