package io.github.hutuneko.psi_ex.compat.cc.peripherals;

import dan200.computercraft.api.peripheral.IPeripheral;
import io.github.hutuneko.psi_ex.block.PsiCasterBlockEntity;
import org.jetbrains.annotations.Nullable;

/**
 * ブロック設置型PsiCaster：フルパワー
 */
public class BlockPsiCasterPeripheral extends PsiCasterPeripheralBase {

    private final PsiCasterBlockEntity blockEntity;

    public BlockPsiCasterPeripheral(PsiCasterBlockEntity blockEntity) {
        super(new BlockEntityPeripheralOwner(blockEntity));
        this.blockEntity = blockEntity;
    }

    @Override
    public boolean equals(@Nullable IPeripheral other) {
        if (other == this) return true;
        if (!(other instanceof BlockPsiCasterPeripheral otherBlock)) return false;
        return this.blockEntity == otherBlock.blockEntity;
    }
}