package io.github.hutuneko.psi_ex.block;

import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.shared.Capabilities;
import io.github.hutuneko.psi_ex.compat.cc.CCCuriosModule;
import io.github.hutuneko.psi_ex.compat.cc.peripherals.BlockEntityPeripheralOwner;
import io.github.hutuneko.psi_ex.compat.cc.peripherals.BlockPsiCasterPeripheral;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PsiCasterBlockEntity extends BlockEntity {

    private final BlockPsiCasterPeripheral peripheral;
    private final LazyOptional<IPeripheral> peripheralCap;

    public PsiCasterBlockEntity(BlockPos pos, BlockState state) {
        super(CCCuriosModule.PSI_CASTER_BE.get(), pos, state);
        this.peripheral = new BlockPsiCasterPeripheral(this);
        this.peripheralCap = LazyOptional.of(() -> peripheral);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == Capabilities.CAPABILITY_PERIPHERAL) {  // CCの周辺機器Capability
            return peripheralCap.cast();
        }
        return super.getCapability(cap, side);
    }

    public void setPlayer(Player player) {
        if (peripheral.getOwner() instanceof BlockEntityPeripheralOwner owner){
            owner.setLastPlayer(player);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        pTag.putUUID("psi_ex:casterblock",peripheral.getOwner().getPlayer().getUUID());
    }

    @Override
    public void load(@NotNull CompoundTag pTag) {
        if (peripheral.getOwner() instanceof BlockEntityPeripheralOwner owner){
            if (level != null) {
                owner.setLastPlayer(level.players().stream()
                        .filter(player -> player.getUUID().equals(pTag.getUUID("psi_ex:casterblock")))
                        .toList().get(0));
            }
        }
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        peripheralCap.invalidate();
    }
}