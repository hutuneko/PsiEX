package io.github.hutuneko.psi_ex.compat.cc.peripherals;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * Peripheralの所有者情報を抽象化
 */
public interface IPeripheralOwner {

    /** 所有者プレイヤー（ポケットの場合は装着者、ブロックの場合は最後に操作したプレイヤーなど） */
    Player getPlayer();

    /** 位置（ブロックの場合はその位置、ポケットの場合はプレイヤー位置） */
    BlockPos getPos();

    /** レベル */
    Level getLevel();

    /** 手持ちかどうか（ポケット/タートル=true、ブロック=false） */
    boolean isHandheld();

    /** コスト消費（手持ちの場合の耐久度減少など） */
    void applyCost();
}