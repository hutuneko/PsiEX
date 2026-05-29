package io.github.hutuneko.psi_ex.compat.cc;

import dan200.computercraft.api.pocket.AbstractPocketUpgrade;
import dan200.computercraft.api.pocket.IPocketAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import io.github.hutuneko.psi_ex.compat.PsiEXRegistry;
import io.github.hutuneko.psi_ex.compat.cc.peripherals.HandheldPsiCasterPeripheral;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Pocket Computer用PsiCaster Upgrade
 * 手持ち版：威力60%、ソケット0-2のみ
 */
public class PsiCasterPocketUpgrade extends AbstractPocketUpgrade {

    public PsiCasterPocketUpgrade(ResourceLocation location) {
        super(location, new ItemStack(PsiEXRegistry.PSI_CASTER_POCKET_UPGRADE.get()));
    }

    @Nullable
    @Override
    public IPeripheral createPeripheral(@NotNull IPocketAccess access) {
        return new HandheldPsiCasterPeripheral(access);
    }
}