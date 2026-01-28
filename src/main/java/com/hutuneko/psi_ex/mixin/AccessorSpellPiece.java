package com.hutuneko.psi_ex.mixin;

import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import vazkii.psi.api.spell.SpellPiece;

@Mixin(SpellPiece.class)
public interface AccessorSpellPiece {
    /** Psi 側で SpellPiece に保持されている登録キー*/
    @Accessor("registryKey")
    ResourceLocation getRegistryKey();
}
