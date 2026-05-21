package io.github.hutuneko.psi_ex.api.client;

import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;

@OnlyIn(Dist.CLIENT)
public final class RenderUtils {
    private RenderUtils() {}

    public static void renderWireCubeRelative(PoseStack poseStack,
                                              MultiBufferSource buffers,
                                              float size,
                                              float[] colorRGB) {
        VertexConsumer builder = buffers.getBuffer(RenderType.LINES);
        Matrix4f mat = poseStack.last().pose();
        Matrix3f nmat = poseStack.last().normal();
        float s = size / 2;
        line(mat, nmat, builder, -s, -s, -s,  s, -s, -s,  colorRGB, 1f);
        line(mat, nmat, builder,  s, -s, -s,  s, -s,  s,  colorRGB, 1f);
        line(mat, nmat, builder,  s, -s,  s, -s, -s,  s,  colorRGB, 1f);
        line(mat, nmat, builder, -s, -s,  s, -s, -s, -s,  colorRGB, 1f);

        line(mat, nmat, builder, -s,  s, -s,  s,  s, -s,  colorRGB, 1f); 
        line(mat, nmat, builder,  s,  s, -s,  s,  s,  s,  colorRGB, 1f); 
        line(mat, nmat, builder,  s,  s,  s, -s,  s,  s,  colorRGB, 1f); 
        line(mat, nmat, builder, -s,  s,  s, -s,  s, -s,  colorRGB, 1f); 

        line(mat, nmat, builder, -s, -s, -s, -s,  s, -s,  colorRGB, 1f);
        line(mat, nmat, builder,  s, -s, -s,  s,  s, -s,  colorRGB, 1f);
        line(mat, nmat, builder,  s, -s,  s,  s,  s,  s,  colorRGB, 1f);
        line(mat, nmat, builder, -s, -s,  s, -s,  s,  s,  colorRGB, 1f);
    }
    public static void renderWireCube(PoseStack poseStack,
                                      MultiBufferSource buffers,
                                      BlockPos pos,
                                      float[] colorRGB,
                                      float alpha,
                                      float size) {
        VertexConsumer builder = buffers.getBuffer(RenderType.LINES);
        Matrix4f mat = poseStack.last().pose();
        Matrix3f nmat = poseStack.last().normal();
        poseStack.pushPose();
        float x = pos.getX();
        float y = pos.getY();
        float z = pos.getZ();
        poseStack.translate(x, y, z);

        line(mat, nmat, builder, x,   y+ size, z,   x+ size, y+ size, z,   colorRGB, alpha);
        line(mat, nmat, builder, x+ size, y+ size, z,   x+ size, y+ size, z+ size, colorRGB, alpha);
        line(mat, nmat, builder, x+ size, y+ size, z+ size, x,   y+ size, z+ size, colorRGB, alpha);
        line(mat, nmat, builder, x,   y+ size, z+ size, x,   y+ size, z,   colorRGB, alpha);

        line(mat, nmat, builder, x,   y, z,   x+ size, y, z,   colorRGB, alpha);
        line(mat, nmat, builder, x+ size, y, z,   x+ size, y, z+ size, colorRGB, alpha);
        line(mat, nmat, builder, x+ size, y, z+ size, x,   y, z+ size, colorRGB, alpha);
        line(mat, nmat, builder, x,   y, z+ size, x,   y, z,   colorRGB, alpha);

        line(mat, nmat, builder, x,   y, z,   x,   y+ size, z,   colorRGB, alpha);
        line(mat, nmat, builder, x+ size, y, z,   x+ size, y+ size, z,   colorRGB, alpha);
        line(mat, nmat, builder, x+ size, y, z+ size, x+ size, y+ size, z+ size, colorRGB, alpha);
        line(mat, nmat, builder, x,   y, z+ size, x,   y+ size, z+ size, colorRGB, alpha);
    }

    public static void line(Matrix4f mat, Matrix3f nmat,
                            VertexConsumer builder,
                            float x1, float y1, float z1,
                            float x2, float y2, float z2,
                            float[] rgb, float a) {
        Vec3 dir = new Vec3(x2-x1, y2-y1, z2-z1).normalize();
        builder.vertex(mat, x1, y1, z1)
                .color(rgb[0], rgb[1], rgb[2], a)
                .normal(nmat, (float)dir.x, (float)dir.y, (float)dir.z)
                .endVertex();
        builder.vertex(mat, x2, y2, z2)
                .color(rgb[0], rgb[1], rgb[2], a)
                .normal(nmat, (float)dir.x, (float)dir.y, (float)dir.z)
                .endVertex();
    }

    public static Vec3 forwardBlocks(double distance) {
        var camera = Minecraft.getInstance().gameRenderer.getMainCamera();
        Vector3f vec3f = camera.getLookVector();
        Vec3 look = new Vec3(vec3f);
        Vec3 camPos = camera.getPosition();
        return camPos.add(look.scale(distance));
    }
}