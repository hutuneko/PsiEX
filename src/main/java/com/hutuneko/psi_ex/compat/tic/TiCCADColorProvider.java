package com.hutuneko.psi_ex.compat.tic;

import net.minecraft.world.item.ItemStack;
import net.minecraft.client.color.item.ItemColor;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.client.materials.MaterialRenderInfo;
import slimeknights.tconstruct.library.client.materials.MaterialRenderInfoLoader;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.nbt.MaterialNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import java.awt.*;
import java.util.Optional;

public class TiCCADColorProvider implements ItemColor {

    @Override
    public int getColor(@NotNull ItemStack stack, int tintIndex) {
        if (tintIndex != 0) {
            if (!(stack.getItem() instanceof IModifiable))return 0xFFFFFF;
            ToolStack ts = ToolStack.from(stack);
            MaterialNBT nbt = ts.getMaterials();
            MaterialVariantId material = nbt.get(tintIndex-1).getVariant();
            Optional<MaterialRenderInfo> optional = MaterialRenderInfoLoader.INSTANCE.getRenderInfo(material);
            if (optional.isPresent()) {
                int argbColor = optional.get().vertexColor();
                return argbColor & 0x00FFFFFF;
            }
        }
        return 0xFFFFFF;
    }
}