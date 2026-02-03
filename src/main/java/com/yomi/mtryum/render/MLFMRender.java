package com.yomi.mtryum.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.yomi.mtryum.block.MLFMBlock;
import com.yomi.mtryum.block.MLFMEntity;
import mtr.data.Lift;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class MLFMRender implements BlockEntityRenderer<MLFMEntity> {
    private static final float TEXT_OFFSET = 0.00625F; // 文字偏移
    private static final float TEXT_SCALE = 0.015F; // 文字大小

    @Override
    public void render(MLFMEntity entity, float tickDelta, PoseStack matrices,
                       MultiBufferSource buffer, int light, int combinedOverlay) {
        final Level world = entity.getLevel();
        if (world == null) return;

        // 获取轨道位置
        final BlockPos trackPosition = entity.getTrackPosition(world);
        if (trackPosition == null) return;
        // 获取电梯方向
        Lift.LiftDirection liftDirection = Lift.LiftDirection.NONE;
        // 获取电梯数据
        String floorNumber = "--";
        for (Lift lift : mtr.client.ClientData.LIFTS) {
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

        // 渲染文本
        matrices.pushPose();
        setupTransform(matrices, entity.getBlockState().getValue(MLFMBlock.FACING));
        if (entity.isLocked()) {
            renderText(matrices, buffer, "STOP "+floorNumber);
        } else {
            // 根据电梯方向添加箭头
            String displayText;
            switch (liftDirection) {
                case UP -> displayText = "↑ " + floorNumber;
                case DOWN -> displayText = "↓ " + floorNumber;
                default -> displayText = "  " + floorNumber;
            }
            renderText(matrices, buffer, displayText);
        }
        matrices.popPose();

    }

    private void setupTransform(PoseStack matrices, Direction facing) {
        switch (facing) {
            case NORTH -> {
                matrices.translate(0.47, 0.675, TEXT_OFFSET);
                matrices.mulPose(Axis.YP.rotationDegrees(180));
            }
            case SOUTH -> {
                matrices.translate(0.53, 0.675, 1 - TEXT_OFFSET);
                matrices.mulPose(Axis.YP.rotationDegrees(0));
            }
            case EAST -> {
                matrices.translate(1 - TEXT_OFFSET, 0.675, 0.47);
                matrices.mulPose(Axis.YP.rotationDegrees(90));
            }
            case WEST -> {
                matrices.translate(TEXT_OFFSET, 0.675, 0.53);
                matrices.mulPose(Axis.YP.rotationDegrees(-90));
            }
        }
        matrices.scale(TEXT_SCALE, TEXT_SCALE, TEXT_SCALE);
        matrices.mulPose(Axis.ZP.rotationDegrees(180));
    }

    private void renderText(PoseStack matrices, MultiBufferSource buffer, String text) {
        final Font font = Minecraft.getInstance().font;
        final int textWidth = font.width(text);
        matrices.translate(-textWidth / 2f, 0, 0);

        font.drawInBatch(
                text,
                0, 0,
                0xFF8C00,
                false,
                matrices.last().pose(),
                buffer,
                Font.DisplayMode.NORMAL,
                0,
                15728880
        );
    }
}