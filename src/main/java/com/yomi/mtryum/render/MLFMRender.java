package com.yomi.mtryum.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import com.yomi.mtryum.Mtryum;
import com.yomi.mtryum.block.MLFMBlock;
import com.yomi.mtryum.block.MLFMEntity;
import mtr.client.ClientData;
import mtr.data.Lift;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class MLFMRender implements BlockEntityRenderer<MLFMEntity> {
    private static final ResourceLocation ARROW_UP = new ResourceLocation(Mtryum.MOD_ID, "textures/block/lift_arrow_up.png");
    private static final ResourceLocation ARROW_DOWN = new ResourceLocation(Mtryum.MOD_ID, "textures/block/lift_arrow_down.png");
    private static final float DEPTH_OFFSET = 0.002f;

    @Override
    public void render(MLFMEntity entity, float tickDelta, PoseStack matrices,
                       MultiBufferSource vertexConsumers, int light, int overlay) {
        final Level world = entity.getLevel();
        if (world == null) return;

        // 获取轨道位置
        final BlockPos trackPosition = entity.getTrackPosition(world);
        if (trackPosition == null) return;

        // 获取电梯方向
        Lift.LiftDirection liftDirection = Lift.LiftDirection.NONE;

        // 获取电梯数据
        String floorNumber = "??";
        for (Lift lift : ClientData.LIFTS) {
            if (lift.hasFloor(trackPosition)) {
                final BlockPos currentFloor = lift.getCurrentFloorBlockPos();
                final BlockEntity blockEntity = world.getBlockEntity(currentFloor);

                if (blockEntity instanceof mtr.block.BlockLiftTrackFloor.TileEntityLiftTrackFloor) {
                    floorNumber = ((mtr.block.BlockLiftTrackFloor.TileEntityLiftTrackFloor) blockEntity).getFloorNumber();
                    liftDirection = lift.getLiftDirection();
                    entity.updateLiftDirection(liftDirection);
                    break;
                }
            }
        }
        if (floorNumber.isEmpty()) return;

        Direction facing = entity.getBlockState().getValue(MLFMBlock.FACING);

        matrices.pushPose();

        switch (facing) {
            case SOUTH:
                matrices.translate(0, -0.135, 0.99);
                break;
            case EAST:
                matrices.translate(0.99, -0.135, 1);
                matrices.mulPose(Vector3f.YP.rotationDegrees(90));
                break;
            case NORTH:
                matrices.translate(1, -0.135, 0.01);
                matrices.mulPose(Vector3f.YP.rotationDegrees(180));
                break;
            case WEST:
                matrices.translate(0.01, -0.135, 0);
                matrices.mulPose(Vector3f.YP.rotationDegrees(270));
                break;
        }

        renderFloorAndArrow(matrices, vertexConsumers, light, overlay,
                floorNumber, liftDirection, facing);

        matrices.popPose();
    }

    private void renderFloorAndArrow(PoseStack matrices, MultiBufferSource vertexConsumers,
                                     int light, int overlay, String floorNumber,
                                     Lift.LiftDirection liftDirection, Direction facing) {
        final float ARROW_Y = 0.74f;
        final float ARROW_HEIGHT = 0.125f;
        final float ARROW_WIDTH = 0.125f;

        // 渲染楼层数字
        renderFloorNumber(matrices, vertexConsumers, floorNumber, light);

        // 渲染方向箭头
        if (liftDirection != Lift.LiftDirection.NONE) {
            ResourceLocation arrowTexture = liftDirection == Lift.LiftDirection.UP ? ARROW_UP : ARROW_DOWN;
            renderArrow(matrices, vertexConsumers.getBuffer(RenderType.entityCutout(arrowTexture)),
                    0.725f - ARROW_WIDTH/2, 0.725f + ARROW_WIDTH/2,
                    ARROW_Y - ARROW_HEIGHT/2, ARROW_Y + ARROW_HEIGHT/2,
                    0.0f, 1.0f, 0.0f, 1.0f,
                    light, overlay);
        }
    }

    private void renderArrow(PoseStack matrices, VertexConsumer vertexConsumer,
                             float minX, float maxX, float minY, float maxY,
                             float uStart, float uEnd, float vStart, float vEnd,
                             int light, int overlay) {
        var matrix = matrices.last().pose();
        var normal = matrices.last().normal();
        float normalZ = 1.0f;

        vertexConsumer.vertex(matrix, minX, minY, DEPTH_OFFSET)
                .color(255, 255, 255, 255)
                .uv(uStart, vEnd)
                .overlayCoords(overlay)
                .uv2(light)
                .normal(normal, 0, 0, normalZ)
                .endVertex();

        vertexConsumer.vertex(matrix, minX, maxY, DEPTH_OFFSET)
                .color(255, 255, 255, 255)
                .uv(uStart, vStart)
                .overlayCoords(overlay)
                .uv2(light)
                .normal(normal, 0, 0, normalZ)
                .endVertex();

        vertexConsumer.vertex(matrix, maxX, maxY, DEPTH_OFFSET)
                .color(255, 255, 255, 255)
                .uv(uEnd, vStart)
                .overlayCoords(overlay)
                .uv2(light)
                .normal(normal, 0, 0, normalZ)
                .endVertex();

        vertexConsumer.vertex(matrix, maxX, minY, DEPTH_OFFSET)
                .color(255, 255, 255, 255)
                .uv(uEnd, vEnd)
                .overlayCoords(overlay)
                .uv2(light)
                .normal(normal, 0, 0, normalZ)
                .endVertex();
    }

    private void renderFloorNumber(PoseStack matrices, MultiBufferSource vertexConsumers,
                                   String floor, int light) {
        if (floor == null || floor.isEmpty()) {
            floor = "??";
        }

        matrices.pushPose();

        matrices.translate(0.5f, 0.78f, 0.001f);

        float fn_scale;

        if (floor.length() <= 2) {
            fn_scale = 0.0009f;
        } else if (floor.length() == 3) {
            fn_scale = 0.00085f;
        } else {
            fn_scale = 0.0007f-(floor.length() - 3) * 0.0001f;
            if (fn_scale < 0.0001f) {
                fn_scale = 0.0001f;
            }
        }

        matrices.scale(fn_scale, -fn_scale, fn_scale);
        matrices.mulPose(Vector3f.YP.rotationDegrees(180));

        int color = 0xFFFF8C00;

        OptimizedFontRenderer.renderText(
                matrices,
                vertexConsumers,
                floor,
                color,
                0, 0, 0,
                1.0f,
                light,
                true
        );

        matrices.popPose();
    }
}