package com.hutuneko.psi_ex.api;

import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.*;
import net.minecraft.core.BlockPos;

@OnlyIn(Dist.CLIENT)
public class ClientRenderer {
    @SubscribeEvent
    public static void onRenderWorld(RenderLevelStageEvent e) {
        if (e.getStage() != RenderLevelStageEvent.Stage.AFTER_LEVEL) return;

        PoseStack ps = e.getPoseStack();
        MultiBufferSource.BufferSource buffers = Minecraft.getInstance().renderBuffers().bufferSource();
        Vec3 cam = e.getCamera().getPosition();

        ps.pushPose();
        ps.translate(-cam.x, -cam.y, -cam.z);
        Vec3 vec3 = RenderUtils.forwardBlocks(10);
        BlockPos pos = BlockPos.containing(vec3);
        // 呼び出しは1行
        RenderUtils.renderWireCube(ps, buffers,
                pos,
                new float[]{1, 0, 0},  // 赤
                0.5f,                  // 不透明
                1.0f);                 // 1ブロック

        ps.popPose();
        buffers.endBatch();
    }
}