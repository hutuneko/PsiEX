package io.github.hutuneko.psi_ex.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
import io.github.hutuneko.psi_ex.PsiEX;
import org.jetbrains.annotations.NotNull;

public class PsiBarrierRenderer extends EntityRenderer<PsiBarrierEntity> {

    private static final ResourceLocation WHITE =
            new ResourceLocation(PsiEX.MOD_ID, "textures/misc/white.png"); // 1x1 の白画像

    public PsiBarrierRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
    }

    @Override
    public void render(@NotNull PsiBarrierEntity e, float entityYaw, float partialTicks,
                       @NotNull PoseStack pose, @NotNull MultiBufferSource buffers, int packedLight) {
        super.render(e, entityYaw, partialTicks, pose, buffers, packedLight);

        // サイズは AABB から取得（幅・高さ）。厚みは見た目用に薄く付ける
        AABB box = e.getBoundingBox();
        float width  = e.getBbWidth();   // XZ の直径
        float height = e.getBbHeight();  // Y 高さ
        float halfW  = width * 0.5f;
        float halfH  = height * 0.5f;
        float thick  = 0.06f; // 見た目の厚み

        // 色とアルファ（半透明）
        float r = 0.4f, g = 0.8f, b = 1.0f, a = 0.35f;

        pose.pushPose();

        // 中心へ移動（エンティティ原点を中央に）
        pose.translate(0.0, halfH, 0.0);

        // エンティティの向きに合わせてY回転（正面がZ+方向になる想定）
        pose.mulPose(Axis.YP.rotationDegrees(-e.getYRot()));

        // 書き込み先（半透明）
        VertexConsumer vc = buffers.getBuffer(RenderType.entityTranslucent(WHITE));

        // 前面（Z = +thick/2）と背面（Z = -thick/2）を両面描画
        float zF = thick * 0.5f;
        float zB = -thick * 0.5f;

        // 前面（2三角形）
        quad(vc, pose, -halfW, -halfH, zF,  0f, 1f,
                +halfW, -halfH, zF,  1f, 1f,
                +halfW, +halfH, zF,  1f, 0f,
                -halfW, +halfH, zF,  0f, 0f, r, g, b, a, packedLight);

        // 背面（裏向き。両面にするため巻き順を逆に）
        quad(vc, pose, -halfW, -halfH, zB,  0f, 1f,
                -halfW, +halfH, zB,  0f, 0f,
                +halfW, +halfH, zB,  1f, 0f,
                +halfW, -halfH, zB,  1f, 1f, r, g, b, a, packedLight);

        pose.popPose();
    }

    private static void quad(VertexConsumer vc, PoseStack pose,
                             float x1, float y1, float z1, float u1, float v1,
                             float x2, float y2, float z2, float u2, float v2,
                             float x3, float y3, float z3, float u3, float v3,
                             float x4, float y4, float z4, float u4, float v4,
                             float r, float g, float b, float a, int light) {
        var mat = pose.last().pose();
        vc.vertex(mat, x1, y1, z1).color(r, g, b, a).uv(u1, v1)
                .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(0, 0, 1).endVertex();
        vc.vertex(mat, x2, y2, z2).color(r, g, b, a).uv(u2, v2)
                .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(0, 0, 1).endVertex();
        vc.vertex(mat, x3, y3, z3).color(r, g, b, a).uv(u3, v3)
                .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(0, 0, 1).endVertex();
        vc.vertex(mat, x4, y4, z4).color(r, g, b, a).uv(u4, v4)
                .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(0, 0, 1).endVertex();
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull PsiBarrierEntity entity) {
        return WHITE;
    }
}
