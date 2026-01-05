package com.hutuneko.psi_ex.api;

import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix4f;
import org.joml.Vector3f;

@OnlyIn(Dist.CLIENT)
public final class RenderUtils {
    private RenderUtils() {} // インスタンス化禁止

    /**
     * ワイヤーフレームの立方体（矩形）を描画
     *
     * @param poseStack PoseStack（座標変換用）
     * @param buffers   MultiBufferSource
     * @param pos       描画起点座標（ワールド座標）
     * @param colorRGB  色 {r, g, b} 0.0〜1.0
     * @param alpha     透明度 0.0〜1.0
     * @param size      一辺の長さ（ブロック単位）
     */
    public static void renderWireCube(PoseStack poseStack,
                                      MultiBufferSource buffers,
                                      BlockPos pos,
                                      float[] colorRGB,
                                      float alpha,
                                      float size) {
        VertexConsumer builder = buffers.getBuffer(RenderType.LINES);
        Matrix4f mat = poseStack.last().pose();

        float x = pos.getX();
        float y = pos.getY();
        float z = pos.getZ();

        // 上面
        line(mat, builder, x,   y+ size, z,   x+ size, y+ size, z,   colorRGB, alpha);
        line(mat, builder, x+ size, y+ size, z,   x+ size, y+ size, z+ size, colorRGB, alpha);
        line(mat, builder, x+ size, y+ size, z+ size, x,   y+ size, z+ size, colorRGB, alpha);
        line(mat, builder, x,   y+ size, z+ size, x,   y+ size, z,   colorRGB, alpha);

        // 下面
        line(mat, builder, x,   y, z,   x+ size, y, z,   colorRGB, alpha);
        line(mat, builder, x+ size, y, z,   x+ size, y, z+ size, colorRGB, alpha);
        line(mat, builder, x+ size, y, z+ size, x,   y, z+ size, colorRGB, alpha);
        line(mat, builder, x,   y, z+ size, x,   y, z,   colorRGB, alpha);

        // 縦辺
        line(mat, builder, x,   y, z,   x,   y+ size, z,   colorRGB, alpha);
        line(mat, builder, x+ size, y, z,   x+ size, y+ size, z,   colorRGB, alpha);
        line(mat, builder, x+ size, y, z+ size, x+ size, y+ size, z+ size, colorRGB, alpha);
        line(mat, builder, x,   y, z+ size, x,   y+ size, z+ size, colorRGB, alpha);
    }

    private static void line(Matrix4f mat,
                             VertexConsumer builder,
                             float x1, float y1, float z1,
                             float x2, float y2, float z2,
                             float[] rgb, float a) {
        builder.vertex(mat, x1, y1, z1).color(rgb[0], rgb[1], rgb[2], a).endVertex();
        builder.vertex(mat, x2, y2, z2).color(rgb[0], rgb[1], rgb[2], a).endVertex();
    }

    public static Vec3 forwardBlocks(double distance) {
        var camera = Minecraft.getInstance().gameRenderer.getMainCamera();
        Vector3f vec3f = camera.getLookVector();
        Vec3 look = new Vec3(vec3f);// 正規化済み
        Vec3 camPos = camera.getPosition();           // カメラ座標
        return camPos.add(look.scale(distance));      // distanceブロック進む
    }
}