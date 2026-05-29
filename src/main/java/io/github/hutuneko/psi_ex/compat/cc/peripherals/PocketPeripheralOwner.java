package io.github.hutuneko.psi_ex.compat.cc.peripherals;

import dan200.computercraft.api.pocket.IPocketAccess;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class PocketPeripheralOwner implements IPeripheralOwner {

    private final IPocketAccess pocket;

    public PocketPeripheralOwner(IPocketAccess pocket) {
        this.pocket = pocket;
    }

    @Override
    public Player getPlayer() {
        return pocket.getEntity() instanceof Player player ? player : null;
    }

    @Override
    public BlockPos getPos() {
        Player player = getPlayer();
        return player != null ? player.blockPosition() : BlockPos.ZERO;
    }

    @Override
    public Level getLevel() {
        Player player = getPlayer();
        return player != null ? player.level() : null;
    }

    @Override
    public boolean isHandheld() {
        return true;  // ポケットは手持ち扱い
    }

    @Override
    public void applyCost() {
        // Pocket Computerの場合はアップグレードアイテムに耐久度減少
        // またはクールダウンを設定
        IPocketAccess access = pocket;
        // access経由でアイテムスタックを取得してdamage
    }
}