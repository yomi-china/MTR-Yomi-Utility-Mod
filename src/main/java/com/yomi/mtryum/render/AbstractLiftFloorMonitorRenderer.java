package com.yomi.mtryum.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
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

public abstract class AbstractLiftFloorMonitorRenderer<T extends BlockEntity> implements BlockEntityRenderer<T> {

    public static class ArrowRenderMode {
        public static final int TEXTURE = 0;
        public static final int TEXT = 1;
    }
    
    // 箭头渲染模式
    protected int arrowRenderMode = ArrowRenderMode.TEXTURE;
    
    // 纹理相关
    protected ResourceLocation arrowTexture = new ResourceLocation("mtryum", "textures/block/lift_arrow.png");
    protected int arrowColor = 0xFFFFFFFF;
    
    // 字体相关
    protected String arrowUpText = "<";
    protected String arrowDownText = ">";
    protected String arrowFont = "mitsubishi-modern";
    protected int arrowTextColor = 0xFFFFFFFF;
    
    // 楼层字体相关
    protected String floorFont = "mitsubishi-modern";
    protected int floorColor = 0xFFFF8C00;
    
    // 位置相关
    protected float floorYOffset = 0.78f;
    protected float arrowYOffset = 0.74f;
    
    // 尺寸相关
    protected float arrowHeight = 0.125f;
    protected float arrowWidth = 0.125f;
    
    // 缩放相关
    protected float floorScaleTwoChars = 0.0009f;       // 2个字符的缩放
    protected float floorScaleThreeChars = 0.00085f;    // 3个字符的缩放
    protected float floorScaleExtraReduction = 0.0001f; // 每多一个字的缩放减少
    protected float floorScaleMin = 0.0001f;            // 最小缩放
    protected float arrowScale = 0.0008f;               // 箭头缩放
    protected float arrowTextScale = 0.0012f;           // 文字箭头缩放
    
    // 深度
    protected float depthOffset = 0.002f;
    
    @Override
    public void render(T entity, float tickDelta, PoseStack matrices,
                       MultiBufferSource vertexConsumers, int light, int overlay) {
        final Level world = entity.getLevel();
        if (world == null) return;

        // 获取轨道位置
        final BlockPos trackPosition = getTrackPosition(entity, world);
        if (trackPosition == null) return;

        // 获取电梯方向
        Lift.LiftDirection liftDirection = Lift.LiftDirection.NONE;
        String floorNumber = "??";

        // 获取电梯数据
        for (Lift lift : ClientData.LIFTS) {
            if (lift.hasFloor(trackPosition)) {
                final BlockPos currentFloor = lift.getCurrentFloorBlockPos();
                final BlockEntity blockEntity = world.getBlockEntity(currentFloor);

                if (blockEntity instanceof mtr.block.BlockLiftTrackFloor.TileEntityLiftTrackFloor) {
                    floorNumber = ((mtr.block.BlockLiftTrackFloor.TileEntityLiftTrackFloor) blockEntity).getFloorNumber();
                    liftDirection = lift.getLiftDirection();
                    updateLiftDirection(entity, liftDirection);
                    break;
                }
            }
        }
        if (floorNumber.isEmpty()) return;

        Direction facing = getFacing(entity);

        matrices.pushPose();

        applyFacingTransform(matrices, facing);

        renderFloorAndArrow(matrices, vertexConsumers, light, overlay,
                floorNumber, liftDirection, facing);

        matrices.popPose();
    }

    protected abstract BlockPos getTrackPosition(T entity, Level world);
    protected abstract Direction getFacing(T entity);
    protected abstract void updateLiftDirection(T entity, Lift.LiftDirection direction);

    protected void applyFacingTransform(PoseStack matrices, Direction facing) {
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
    }

    protected void renderFloorAndArrow(PoseStack matrices, MultiBufferSource vertexConsumers,
                                     int light, int overlay, String floorNumber,
                                     Lift.LiftDirection liftDirection, Direction facing) {
        // 渲染楼层数字
        renderFloorNumber(matrices, vertexConsumers, floorNumber, light);

        // 渲染方向箭头
        if (liftDirection != Lift.LiftDirection.NONE) {
            boolean isDown = liftDirection == Lift.LiftDirection.DOWN;
            renderArrow(matrices, vertexConsumers, light, overlay, isDown);
        }
    }
    
    protected void renderArrow(PoseStack matrices, MultiBufferSource vertexConsumers,
                             int light, int overlay, boolean isDown) {
        if (arrowRenderMode == ArrowRenderMode.TEXTURE) {
            renderTextureArrow(matrices, vertexConsumers, light, overlay, isDown);
        } else {
            renderTextArrow(matrices, vertexConsumers, light, overlay, isDown);
        }
    }
    
    protected void renderTextureArrow(PoseStack matrices, MultiBufferSource vertexConsumers,
                                    int light, int overlay, boolean rotate180) {
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderType.entityCutout(arrowTexture));
        
        float centerX = 0.725f;
        float centerY = arrowYOffset;
        float halfWidth = arrowWidth / 2.0f;
        float halfHeight = arrowHeight / 2.0f;
        
        matrices.pushPose();

        if (rotate180) {
            matrices.translate(centerX, centerY, 0);
            matrices.mulPose(Vector3f.ZP.rotationDegrees(180));
            matrices.translate(-centerX, -centerY, 0);
        }

        var matrix = matrices.last().pose();
        var normal = matrices.last().normal();
        float normalZ = 1.0f;

        int alpha = (arrowColor >> 24) & 0xFF;
        int red = (arrowColor >> 16) & 0xFF;
        int green = (arrowColor >> 8) & 0xFF;
        int blue = arrowColor & 0xFF;

        vertexConsumer.vertex(matrix, centerX - halfWidth, centerY - halfHeight, depthOffset)
                .color(red, green, blue, alpha)
                .uv(0.0f, 1.0f)
                .overlayCoords(overlay)
                .uv2(light)
                .normal(normal, 0, 0, normalZ)
                .endVertex();

        vertexConsumer.vertex(matrix, centerX - halfWidth, centerY + halfHeight, depthOffset)
                .color(red, green, blue, alpha)
                .uv(0.0f, 0.0f)
                .overlayCoords(overlay)
                .uv2(light)
                .normal(normal, 0, 0, normalZ)
                .endVertex();

        vertexConsumer.vertex(matrix, centerX + halfWidth, centerY + halfHeight, depthOffset)
                .color(red, green, blue, alpha)
                .uv(1.0f, 0.0f)
                .overlayCoords(overlay)
                .uv2(light)
                .normal(normal, 0, 0, normalZ)
                .endVertex();

        vertexConsumer.vertex(matrix, centerX + halfWidth, centerY - halfHeight, depthOffset)
                .color(red, green, blue, alpha)
                .uv(1.0f, 1.0f)
                .overlayCoords(overlay)
                .uv2(light)
                .normal(normal, 0, 0, normalZ)
                .endVertex();

        matrices.popPose();
    }
    
    protected void renderTextArrow(PoseStack matrices, MultiBufferSource vertexConsumers,
                                 int light, int overlay, boolean isDown) {
        String arrowText = isDown ? arrowDownText : arrowUpText;
        
        matrices.pushPose();

        matrices.translate(0.725f, arrowYOffset, depthOffset);
        matrices.scale(arrowTextScale, -arrowTextScale, arrowTextScale);
        matrices.mulPose(Vector3f.YP.rotationDegrees(180));

        float textWidth = getTextWidth(arrowText) * arrowTextScale;
        matrices.translate(-textWidth / 2, 0, 0);

        int alpha = (arrowTextColor >> 24) & 0xFF;
        int red = (arrowTextColor >> 16) & 0xFF;
        int green = (arrowTextColor >> 8) & 0xFF;
        int blue = arrowTextColor & 0xFF;

        CustomFontRenderer.renderText(
                matrices,
                vertexConsumers,
                arrowText,
                (alpha << 24) | (red << 16) | (green << 8) | blue,
                0, 0, 0,
                1.0f,
                light,
                true,
                arrowFont
        );

        matrices.popPose();
    }
    
    protected void renderFloorNumber(PoseStack matrices, MultiBufferSource vertexConsumers,
                                   String floor, int light) {
        if (floor == null || floor.isEmpty()) {
            floor = "??";
        }

        matrices.pushPose();

        matrices.translate(0.5f, floorYOffset, depthOffset);

        float fn_scale = calculateFloorScale(floor.length());

        matrices.scale(fn_scale, -fn_scale, fn_scale);
        matrices.mulPose(Vector3f.YP.rotationDegrees(180));

        int alpha = (floorColor >> 24) & 0xFF;
        int red = (floorColor >> 16) & 0xFF;
        int green = (floorColor >> 8) & 0xFF;
        int blue = floorColor & 0xFF;

        CustomFontRenderer.renderText(
                matrices,
                vertexConsumers,
                floor,
                (alpha << 24) | (red << 16) | (green << 8) | blue,
                0, 0, 0,
                1.0f,
                light,
                true,
                floorFont
        );

        matrices.popPose();
    }
    
    protected float calculateFloorScale(int length) {
        if (length <= 2) {
            return floorScaleTwoChars;
        } else if (length == 3) {
            return floorScaleThreeChars;
        } else {
            float scale = floorScaleThreeChars - (length - 3) * floorScaleExtraReduction;
            return Math.max(scale, floorScaleMin);
        }
    }
    
    protected float getTextWidth(String text) {
        return text.length() * 0.5f;
    }
}