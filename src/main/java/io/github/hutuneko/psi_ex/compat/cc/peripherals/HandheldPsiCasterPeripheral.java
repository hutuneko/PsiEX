package io.github.hutuneko.psi_ex.compat.cc.peripherals;

import dan200.computercraft.api.pocket.IPocketAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import org.jetbrains.annotations.Nullable;

public class HandheldPsiCasterPeripheral extends PsiCasterPeripheralBase {

    private final IPocketAccess pocketAccess;

    public HandheldPsiCasterPeripheral(IPocketAccess access) {
        super(new PocketPeripheralOwner(access));
        this.pocketAccess = access;
        this.potency = 0.6f;
        this.maxSocketIndex = 2;
        this.consumesDurability = true;
    }

    @Override
    public boolean equals(@Nullable IPeripheral other) {
        if (other == this) return true;
        if (!(other instanceof HandheldPsiCasterPeripheral otherCaster)) return false;
        return this.pocketAccess == otherCaster.pocketAccess;
    }
}