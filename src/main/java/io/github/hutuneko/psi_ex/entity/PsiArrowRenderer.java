package io.github.hutuneko.psi_ex.entity;

import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class PsiArrowRenderer extends ArrowRenderer<PsiArrowEntity> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation("minecraft", "textures/entity/projectiles/arrow.png"); // バニラ矢のテクスチャ

    public PsiArrowRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull PsiArrowEntity entity) {
        return TEXTURE;
    }
}
