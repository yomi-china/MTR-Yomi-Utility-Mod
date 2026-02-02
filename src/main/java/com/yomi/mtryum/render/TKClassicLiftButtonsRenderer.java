package com.yomi.mtryum.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import com.yomi.mtryum.Mtryum;
import com.yomi.mtryum.block.TKClassicLiftButtonsBlock;
import com.yomi.mtryum.block.TKClassicLiftButtonsBlockEntity;
import mtr.client.ClientData;
import mtr.data.Lift;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class TKClassicLiftButtonsRenderer implements BlockEntityRenderer<TKClassicLiftButtonsBlockEntity> {

    // 纹理声明
    private static final ResourceLocation UP_NORMAL = new ResourceLocation(Mtryum.MOD_ID, "textures/block/tk_up_normal.png");
    private static final ResourceLocation UP_PRESSED = new ResourceLocation(Mtryum.MOD_ID, "textures/block/tk_up_pressed.png");
    private static final ResourceLocation DOWN_NORMAL = new ResourceLocation(Mtryum.MOD_ID, "textures/block/tk_down_normal.png");
    private static final ResourceLocation DOWN_PRESSED = new ResourceLocation(Mtryum.MOD_ID, "textures/block/tk_down_pressed.png");
    private static final ResourceLocation ARROW_UP = new ResourceLocation(Mtryum.MOD_ID, "textures/block/tk_arrow_up.png");
    private static final ResourceLocation ARROW_DOWN = new ResourceLocation(Mtryum.MOD_ID, "textures/block/tk_arrow_down.png");
    private static final float DEPTH_OFFSET = 0.002f;

    private final Font font;

    public TKClassicLiftButtonsRenderer(BlockEntityRendererProvider.Context ctx) {
        this.font = ctx.getFont();
    }

    @Override
    public void render(TKClassicLiftButtonsBlockEntity entity, float tickDelta, PoseStack matrices,
                       MultiBufferSource vertexConsumers, int light, int overlay) {
        if (entity == null || entity.getLevel() == null) return;

        // 获取按钮状态（从BlockEntity中读取，已在服务端更新）
        boolean upPressed = entity.isUpButtonPressed();
        boolean downPressed = entity.isDownButtonPressed();

        // 获取当前楼层
        final Level world = entity.getLevel();
        if (world == null) return;

        // 获取轨道位置
        final BlockPos trackPosition = entity.getTrackPosition(world);
        if (trackPosition == null) return;

        Lift.LiftDirection liftDirection = Lift.LiftDirection.NONE;
        String floorNumber = "JU";
        boolean isTopFloor = false;
        boolean isBottomFloor = false;

        // 只在客户端获取电梯信息，用于渲染
        for (Lift lift : ClientData.LIFTS) {
            if (lift.hasFloor(trackPosition)) {
                final BlockPos currentFloor = lift.getCurrentFloorBlockPos();
                final BlockEntity blockEntity = world.getBlockEntity(currentFloor);

                if (blockEntity instanceof mtr.block.BlockLiftTrackFloor.TileEntityLiftTrackFloor) {
                    floorNumber = ((mtr.block.BlockLiftTrackFloor.TileEntityLiftTrackFloor) blockEntity).getFloorNumber();
                    liftDirection = lift.getLiftDirection();

                    // 更新BlockEntity中的电梯方向（仅客户端渲染用）
                    entity.updateLiftDirection(liftDirection);

                    boolean[] hasButton = new boolean[2];
                    lift.hasUpDownButtonForFloor(trackPosition.getY(), hasButton);

                    isTopFloor = !hasButton[0];
                    isBottomFloor = !hasButton[1];
                    break;
                }
            }
        }

        if (floorNumber.isEmpty()) {
            floorNumber = "JU";
        }

        // 按钮纹理
        ResourceLocation upTexture = upPressed ? UP_PRESSED : UP_NORMAL;
        ResourceLocation downTexture = downPressed ? DOWN_PRESSED : DOWN_NORMAL;

        BlockState state = entity.getBlockState();
        Direction facing = state.getValue(TKClassicLiftButtonsBlock.FACING);

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

        renderAllElements(matrices, vertexConsumers, light, overlay,
                floorNumber, liftDirection, upTexture, downTexture, facing, isTopFloor, isBottomFloor);

        matrices.popPose();
    }

    // 渲染楼层、箭头、按钮
    private void renderAllElements(PoseStack matrices, MultiBufferSource vertexConsumers,
                                   int light, int overlay, String floorNumber,
                                   Lift.LiftDirection liftDirection,
                                   ResourceLocation upTexture, ResourceLocation downTexture,
                                   Direction facing, boolean isTopFloor, boolean isBottomFloor) {
        final float BUTTON_Y_UP = 0.48f;
        final float BUTTON_Y_DOWN = 0.35f;
        final float ARROW_Y = 0.65f;

        final float buttonSize = 0.075f;

        renderFloorNumber(matrices, vertexConsumers, floorNumber, light);

        // 渲染方向箭头
        if (liftDirection != Lift.LiftDirection.NONE) {
            if (liftDirection == Lift.LiftDirection.UP)
                renderArrow(matrices, vertexConsumers, "↑", light);
            else {
                renderArrow(matrices, vertexConsumers, "↓", light);
            }
        }

        // 渲染上按钮
        if (!isTopFloor) {
            float yOffset = 0;
            if (isBottomFloor) {
                yOffset = -0.06f;
            }
            renderQuad(matrices, vertexConsumers.getBuffer(RenderType.entityCutout(upTexture)),
                    BUTTON_Y_UP - buttonSize / 2 + yOffset, BUTTON_Y_UP + buttonSize / 2 + yOffset,
                    (facing == Direction.SOUTH || facing == Direction.WEST) ? 1 : 0,
                    (facing == Direction.SOUTH || facing == Direction.WEST) ? 0 : 1,
                    light, overlay);
        }

        // 渲染下按钮
        if (!isBottomFloor) {
            float yOffset = 0;
            if (isTopFloor) {
                yOffset = 0.06f;
            }
            renderQuad(matrices, vertexConsumers.getBuffer(RenderType.entityCutout(downTexture)),
                    BUTTON_Y_DOWN - buttonSize / 2 + yOffset, BUTTON_Y_DOWN + buttonSize / 2 + yOffset,
                    (facing == Direction.SOUTH || facing == Direction.WEST) ? 1 : 0,
                    (facing == Direction.SOUTH || facing == Direction.WEST) ? 0 : 1,
                    light, overlay);
        }
    }

    // 渲染楼层数字
    private void renderFloorNumber(PoseStack matrices, MultiBufferSource vertexConsumers,
                                   String floor, int light) {
        matrices.pushPose();
        matrices.scale(0.011F, -0.011F, 0.011F);
        matrices.translate(47, -61, 0);
        matrices.mulPose(Vector3f.YP.rotationDegrees(180));

        font.drawInBatch(
                floor,
                0, 0,
                0xFF0000,
                false,
                matrices.last().pose(),
                vertexConsumers,
                true,
                0,
                light
        );

        matrices.popPose();
    }

    private void renderArrow(PoseStack matrices, MultiBufferSource vertexConsumers,
                             String floor, int light) {
        matrices.pushPose();
        matrices.scale(0.011F, -0.011F, 0.011F);
        matrices.translate(47, -83, 0);
        matrices.mulPose(Vector3f.YP.rotationDegrees(180));

        font.drawInBatch(
                floor,
                0, 0,
                0xFF0000,
                false,
                matrices.last().pose(),
                vertexConsumers,
                true,
                0,
                light
        );

        matrices.popPose();
    }

    // 渲染四边形
    private void renderQuad(PoseStack matrices, VertexConsumer vertexConsumer,
                            float minY, float maxY,
                            float uStart, float uEnd,
                            int light, int overlay) {
        var matrix = matrices.last().pose();
        var normal = matrices.last().normal();
        float normalZ = 1.0f;

        vertexConsumer.vertex(matrix, (float) 0.4625, minY, DEPTH_OFFSET)
                .color(255, 255, 255, 255)
                .uv(uStart, (float) 1)
                .overlayCoords(overlay)
                .uv2(light)
                .normal(normal, 0, 0, normalZ)
                .endVertex();

        vertexConsumer.vertex(matrix, (float) 0.4625, maxY, DEPTH_OFFSET)
                .color(255, 255, 255, 255)
                .uv(uStart, (float) 0)
                .overlayCoords(overlay)
                .uv2(light)
                .normal(normal, 0, 0, normalZ)
                .endVertex();

        vertexConsumer.vertex(matrix, (float) 0.5375, maxY, DEPTH_OFFSET)
                .color(255, 255, 255, 255)
                .uv(uEnd, (float) 0)
                .overlayCoords(overlay)
                .uv2(light)
                .normal(normal, 0, 0, normalZ)
                .endVertex();

        vertexConsumer.vertex(matrix, (float) 0.5375, minY, DEPTH_OFFSET)
                .color(255, 255, 255, 255)
                .uv(uEnd, (float) 1)
                .overlayCoords(overlay)
                .uv2(light)
                .normal(normal, 0, 0, normalZ)
                .endVertex();
    }
}