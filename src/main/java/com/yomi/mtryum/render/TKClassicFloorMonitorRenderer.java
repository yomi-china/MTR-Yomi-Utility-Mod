package com.yomi.mtryum.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import com.yomi.mtryum.block.TKClassicFloorMonitorBlock;
import com.yomi.mtryum.block.TKClassicFloorMonitorEntity;
import mtr.block.BlockLiftTrackFloor;
import mtr.data.Lift;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class TKClassicFloorMonitorRenderer implements BlockEntityRenderer<TKClassicFloorMonitorEntity> {
    private static final float TEXT_OFFSET = 0.00625F; // 文字偏移
    private static final float TEXT_SCALE = 0.015F; // 文字大小
    // TODO: 实现动画箭头
    private static final float ARROW_SIZE = 0.25F; // 箭头大小
    private static final float ARROW_OFFSET = 0.03F; // 箭头偏移量
    private static final int FRAME_COUNT = 10; // 箭头每套样式的帧数
    private static final long FRAME_DURATION = 100; // 箭头每帧持续时间

    @Override
    public void render(TKClassicFloorMonitorEntity entity, float tickDelta, PoseStack matrices,
                       MultiBufferSource buffer, int light, int combinedOverlay) {
        final Level world = entity.getLevel();
        if (world == null) return;

        // 获取轨道位置
        final BlockPos trackPosition = entity.getTrackPosition(world);
        if (trackPosition == null) return;
        // 获取设置的颜色
        int color = entity.getTextColor();
        // 获取电梯方向
        Lift.LiftDirection liftDirection = Lift.LiftDirection.NONE;
        // 获取电梯数据
        String floorNumber = "--";
        for (Lift lift : mtr.client.ClientData.LIFTS) {
            if (lift.hasFloor(trackPosition)) {
                final BlockPos currentFloor = lift.getCurrentFloorBlockPos();
                final BlockEntity blockEntity = world.getBlockEntity(currentFloor);

                if (blockEntity instanceof BlockLiftTrackFloor.TileEntityLiftTrackFloor) {
                    floorNumber = ((BlockLiftTrackFloor.TileEntityLiftTrackFloor) blockEntity).getFloorNumber();
                    // 更新电梯方向
                    liftDirection = lift.getLiftDirection();
                    entity.updateLiftDirection();
                    break;
                }
            }
        }
        if (floorNumber.isEmpty()) return;

        // 渲染文本
        matrices.pushPose();
        setupTransform(matrices, entity.getBlockState().getValue(TKClassicFloorMonitorBlock.FACING));
        if (entity.isLocked()) {
            renderText(matrices, buffer, "STOP", color);
        } else {
            if (entity.getArrowStyle() == 1) {
                String displayText;
                switch (liftDirection) {
                    case UP -> displayText = floorNumber + "⬆";
                    case DOWN -> displayText = floorNumber + "⬇";
                    default -> displayText = floorNumber + " ";
                }
                renderText(matrices, buffer, displayText, color);
            } else {
                String displayText;
                switch (liftDirection) {
                    case UP -> displayText = floorNumber + "▲";
                    case DOWN -> displayText = floorNumber + "▼";
                    default -> displayText = floorNumber + " ";
                }
                renderText(matrices, buffer, displayText, color);
            }
        }
        matrices.popPose();
    }

    private void setupTransform(PoseStack matrices, Direction facing) {
        switch (facing) {
            case NORTH -> {
                matrices.translate(0.47, 0.69, TEXT_OFFSET);
                matrices.mulPose(Vector3f.YP.rotationDegrees(180));
            }
            case SOUTH -> {
                matrices.translate(0.47, 0.69, 1 - TEXT_OFFSET);
                matrices.mulPose(Vector3f.YP.rotationDegrees(0));
            }
            case EAST -> {
                matrices.translate(1 - TEXT_OFFSET, 0.69, 0.47);
                matrices.mulPose(Vector3f.YP.rotationDegrees(90));
            }
            case WEST -> {
                matrices.translate(TEXT_OFFSET, 0.69, 0.47);
                matrices.mulPose(Vector3f.YP.rotationDegrees(-90));
            }
        }
        matrices.scale(TEXT_SCALE, TEXT_SCALE, TEXT_SCALE);
        matrices.mulPose(Vector3f.ZP.rotationDegrees(180));
    }

    private void renderText(PoseStack matrices, MultiBufferSource buffer, String text, int color) {
        final Font font = Minecraft.getInstance().font;
        final int textWidth = font.width(text);
        matrices.translate(-textWidth / 2f, 0, 0);

        font.drawInBatch(
                text,
                0, 0,
                color,
                false,
                matrices.last().pose(),
                buffer,
                true,
                0,
                15728880
        );
    }
}