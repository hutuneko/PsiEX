package com.hutuneko.psi_ex.entity;

import com.hutuneko.psi_ex.PsiEX;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class RailgunRenderer extends EntityRenderer<Railgun> {
    public RailgunRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull Railgun pEntity) {
        return new ResourceLocation(PsiEX.MOD_ID, "textures/misc/white.png");
    }
}
