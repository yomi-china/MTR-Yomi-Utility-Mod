package com.yomi.mtryum.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.yomi.mtryum.block.LiftArrivalLightBlock;
import mtr.client.ClientData;
import mtr.data.Lift;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class RendererLiftArrivalLight implements BlockEntityRenderer<LiftArrivalLightBlock.Entity> {

    private static final int BLINK_INTERVAL_TICKS = 5; // 闪烁间隔
    private static final double ARRIVAL_THRESHOLD = 1; // 似乎没啥用，因为还没开门

    public RendererLiftArrivalLight() {

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
                final double liftY = lift.getPositionY();
                final double floorY = trackPosition.getY();

                final boolean isAtExactFloor = Math.abs(liftY - floorY) < ARRIVAL_THRESHOLD;

                final boolean isStopped = lift.getLiftDirection() == Lift.LiftDirection.NONE;

                if (isAtExactFloor && isStopped) {
                    isLiftArrived = true;
                    // 闪烁状态
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