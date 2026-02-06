package com.yomi.mtryum.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.yomi.mtryum.block.LiftArrivalLightBlock;
import mtr.client.ClientData;
import mtr.data.Lift;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class LiftArrivalLightRenderer implements BlockEntityRenderer<LiftArrivalLightBlock.Entity> {

    private static final int BLINK_INTERVAL_TICKS = 5; // 闪烁间隔
    private static final double ARRIVAL_THRESHOLD = 0.01; // 到达阈值（电梯Y坐标与楼层Y坐标的差值）

    public LiftArrivalLightRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(LiftArrivalLightBlock.Entity entity, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
        final Level world = entity.getLevel();
        if (world == null) return;

        final BlockPos trackPosition = entity.getTrackPosition(world);
        if (trackPosition == null) return;

        // 检测电梯是否到达该楼层
        boolean isLiftArrived = false;
        boolean shouldBlink = false;
        for (Lift lift : ClientData.LIFTS) {
            if (lift.hasFloor(trackPosition)) {
                // 获取电梯的实际位置
                final double liftY = lift.getPositionY();
                final double floorY = trackPosition.getY();

                final boolean isAtExactFloor = Math.abs(liftY - floorY) < ARRIVAL_THRESHOLD;

                // 检查电梯是否停止移动
                final boolean isStopped = lift.getLiftDirection() == Lift.LiftDirection.NONE;

                // 只有当电梯精确停在该楼层且停止移动时，才认为是到达
                if (isAtExactFloor && isStopped) {
                    isLiftArrived = true;
                    // 计算闪烁状态
                    final long gameTime = world.getGameTime();
                    shouldBlink = (gameTime / BLINK_INTERVAL_TICKS) % 2 == 0;
                    break;
                }
            }
        }

        final BlockState state = entity.getBlockState();
        if (isLiftArrived) {
            if (state.getValue(LiftArrivalLightBlock.LIT) != shouldBlink) {
                world.setBlock(entity.getBlockPos(), state.setValue(LiftArrivalLightBlock.LIT, shouldBlink), 3);
            }
        } else {
            if (state.getValue(LiftArrivalLightBlock.LIT)) {
                world.setBlock(entity.getBlockPos(), state.setValue(LiftArrivalLightBlock.LIT, false), 3);
            }
        }
    }
}