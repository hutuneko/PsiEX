package com.hutuneko.psi_ex.api.client;

import com.hutuneko.psi_ex.PsiEX;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.*;
import net.minecraft.core.BlockPos;
import net.minecraftforge.fml.common.Mod;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = PsiEX.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE,value = Dist.CLIENT)
public class ClientRenderer {

    private static final Set<Integer> targetEntityIds = new HashSet<>();

    public static void updateTargets(List<Integer> ids) {
        targetEntityIds.clear();
        targetEntityIds.addAll(ids);
    }

    @SubscribeEvent
    public static void onRenderWorld(RenderLevelStageEvent e) {
        if (e.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return;
        MultiBufferSource.BufferSource buffers = mc.renderBuffers().bufferSource();
        PoseStack ps = e.getPoseStack();
        Vec3 cameraPos = e.getCamera().getPosition();

        ps.pushPose();
        ps.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);
        for (int entityId : targetEntityIds) {
            Entity entity = mc.level.getEntity(entityId);
            if (entity == null || !entity.isAlive()) continue;

            AABB bb = entity.getBoundingBox();

            float sizeX = (float)(bb.maxX - bb.minX);
            float sizeY = (float)(bb.maxY - bb.minY);
            float sizeZ = (float)(bb.maxZ - bb.minZ);
            ps.pushPose();
            ps.translate(entity.getX(), entity.getY() + entity.getEyeHeight(), entity.getZ()); // 位置適用
            RenderUtils.renderWireCubeRelative(ps, buffers, Math.max(sizeX, Math.max(sizeY, sizeZ)), new float[]{1.0f, 0.0f, 0.0f});
            ps.popPose();
        }
        ps.popPose();

        buffers.endBatch();
    }
}