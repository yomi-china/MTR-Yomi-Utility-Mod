package com.yomi.mtryum.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.yomi.mtryum.block.AbstractLiftArrivalLightBlock;
import mtr.client.ClientData;
import mtr.data.Lift;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.concurrent.atomic.AtomicInteger;

public abstract class RendererAbstractLiftArrivalLight<T extends AbstractLiftArrivalLightBlock.AbstractEntity>
        implements BlockEntityRenderer<T> {

    protected RendererAbstractLiftArrivalLight() {
    }

    @Override
    public void render(T entity,
                       float tickDelta,
                       PoseStack matrices,
                       MultiBufferSource vertexConsumers,
                       int light,
                       int overlay) {
        final Level world = entity.getLevel();
        if (world == null) return;

        final BlockPos trackPosition = entity.getTrackPosition(world);
        if (trackPosition == null) return;

        final BlockState state = entity.getBlockState();
        final Block block = state.getBlock();

        if (!(block instanceof AbstractLiftArrivalLightBlock abstractBlock)) return;

        final int blinkIntervalTicks = abstractBlock.getBlinkIntervalTicks();
        final double arrivalThreshold = abstractBlock.getArrivalThreshold();
        final double advanceBlinkDistance = abstractBlock.getAdvanceBlinkDistance();

        final int floorY = trackPosition.getY();

        boolean isLiftArrived = false;
        boolean shouldBlink = false;

        for (Lift lift : ClientData.LIFTS) {
            if (!lift.hasFloor(trackPosition)) continue;

            final double liftY = lift.getPositionY();
            final boolean isStopped = lift.getLiftDirection() == Lift.LiftDirection.NONE;

            final AtomicInteger targetFloor = new AtomicInteger(Integer.MIN_VALUE);
            lift.liftInstructions.getTargetFloor(targetFloor::set);
            final boolean isTargetFloor = targetFloor.get() == floorY;

            // 提前闪烁
            final boolean isApproaching = isTargetFloor && Math.abs(liftY - floorY) < advanceBlinkDistance;

            // 到站闪烁
            final boolean isAtExactFloor = Math.abs(liftY - floorY) < arrivalThreshold;
            final boolean isArrived = isAtExactFloor && isStopped;

            if (isApproaching || isArrived) {
                isLiftArrived = true;
                final long gameTime = world.getGameTime();
                shouldBlink = (gameTime / blinkIntervalTicks) % 2 == 0;
                break;
            }
        }

        if (isLiftArrived) {
            if (state.getValue(AbstractLiftArrivalLightBlock.LIT) != shouldBlink) {
                world.setBlock(entity.getBlockPos(),
                        state.setValue(AbstractLiftArrivalLightBlock.LIT, shouldBlink), 3);
            }
        } else {
            if (state.getValue(AbstractLiftArrivalLightBlock.LIT)) {
                world.setBlock(entity.getBlockPos(),
                        state.setValue(AbstractLiftArrivalLightBlock.LIT, false), 3);
            }
        }
    }
}