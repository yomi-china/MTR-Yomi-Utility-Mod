package com.yomi.mtryum.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.yomi.mtryum.Mtryum;
import mtr.client.ClientData;
import mtr.data.Lift;
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
import org.jetbrains.annotations.NotNull;

/**
 * 通用电梯按钮渲染器抽象类
 * @param <T> 电梯按钮方块实体类型
 */
public abstract class AbstractLiftButtonsRenderer<T extends BlockEntity> implements BlockEntityRenderer<T> {

    
    // 纹理相关
    protected ResourceLocation buttonNormalTexture = new ResourceLocation(Mtryum.MOD_ID, "textures/block/lift_button_normal.png");
    protected ResourceLocation buttonPressedTexture = new ResourceLocation(Mtryum.MOD_ID, "textures/block/lift_button_pressed.png");
    protected ResourceLocation arrowTexture = new ResourceLocation(Mtryum.MOD_ID, "textures/block/lift_arrow.png");
    
    // 颜色相关
    protected int floorNumberColor = 0xFFFFFFFF;
    protected int arrowColor = 0xFFFFFFFF;
    protected int buttonTintColor = 0xFFFFFF;
    
    // 字体相关
    protected String fontName = "default";
    protected String defaultFloorText = "??";
    
    // 位置相关
    protected float baseYOffset = -0.135f;
    protected float floorNumberY = 0.78f;
    protected float arrowY = 0.88f;
    protected float buttonUpY = 0.48f;
    protected float buttonDownY = 0.35f;
    
    // 尺寸相关
    protected float buttonSize = 0.075f;
    protected float arrowWidth = 0.125f;
    protected float arrowHeight = 0.125f;
    protected float depthOffset = 0.002f;
    
    // 缩放相关
    protected float floorScaleTwoChars = 0.0009f;// 2个字符的缩放
    protected float floorScaleThreeChars = 0.00085f;// 3个字符的缩放
    protected float floorScaleExtraReduction = 0.0001f;// 每多一个字的缩放减少
    protected float floorScaleMin = 0.0001f;// 最小缩放
    protected float arrowScale = 0.0008f;// 箭头缩放
    
    // 箭头渲染模式，有字体的用字体，没有的用纹理。
    // TODO：动画渲染
    protected ArrowRenderMode arrowRenderMode = ArrowRenderMode.TEXTURE;

    //很多电梯字体中，上下箭头用大于和小于号来表示
    protected String arrowUpText = "<";
    protected String arrowDownText = ">";

    // 单按钮时位置
    protected float singleButtonOffset = 0.06f;

    public enum ArrowRenderMode {
        TEXTURE,
        FONT
    }

    public AbstractLiftButtonsRenderer(BlockEntityRendererProvider.Context context) {

    }

    public AbstractLiftButtonsRenderer(BlockEntityRendererProvider.Context context,
                                      ResourceLocation buttonNormalTexture,
                                      ResourceLocation buttonPressedTexture,
                                      ResourceLocation arrowTexture,
                                      String fontName,
                                      ArrowRenderMode arrowRenderMode) {
        this.buttonNormalTexture = buttonNormalTexture;
        this.buttonPressedTexture = buttonPressedTexture;
        this.arrowTexture = arrowTexture;
        this.fontName = fontName;
        this.arrowRenderMode = arrowRenderMode;
    }

    @NotNull
    protected abstract String getFacingPropertyName();

    protected abstract boolean isUpButtonPressed(T entity);

    protected abstract boolean isDownButtonPressed(T entity);

    protected abstract BlockPos getTrackPosition(T entity, Level world);

    protected void updateLiftDirection(T entity, Lift.LiftDirection liftDirection) {

    }

    protected void liftArrived(T entity) {
    }

    @Override
    public void render(T entity, float tickDelta, PoseStack matrices,
                      MultiBufferSource vertexConsumers, int light, int overlay) {
        if (entity == null || entity.getLevel() == null) return;
        
        // 获取按钮状态
        boolean upPressed = isUpButtonPressed(entity);
        boolean downPressed = isDownButtonPressed(entity);
        
        // 轨道位置
        final Level world = entity.getLevel();
        final BlockPos trackPosition = getTrackPosition(entity, world);
        if (trackPosition == null) return;
        
        // 电梯数据
        LiftData liftData = getLiftData(entity, world, trackPosition);
        if (liftData == null || liftData.floorNumber.isEmpty()) return;
        
        // 检查是否到达
        checkLiftArrival(entity, world, trackPosition);
        
        // 方块朝向
        BlockState state = entity.getBlockState();
        Direction facing = getFacingFromState(state);
        if (facing == null) return;

        applyBaseTransform(matrices, facing);
        
        // 渲染
        renderAllElements(matrices, vertexConsumers, light, overlay,
                liftData.floorNumber, liftData.liftDirection,
                upPressed, downPressed, facing,
                liftData.isTopFloor, liftData.isBottomFloor);
        
        matrices.popPose();
    }
    
    // 核心渲染逻辑

    protected void renderAllElements(PoseStack matrices, MultiBufferSource vertexConsumers,
                                    int light, int overlay, String floorNumber,
                                    Lift.LiftDirection liftDirection,
                                    boolean upPressed, boolean downPressed,
                                    Direction facing, boolean isTopFloor, boolean isBottomFloor) {
        
        // 渲染楼层数字
        renderFloorNumber(matrices, vertexConsumers, floorNumber, light);
        
        // 渲染方向箭头
        if (liftDirection != Lift.LiftDirection.NONE) {
            renderDirectionArrow(matrices, vertexConsumers, liftDirection, light, overlay);
        }
        
        // 渲染上按钮
        if (!isTopFloor) {
            float yOffset = (isBottomFloor && !isTopFloor) ? -singleButtonOffset : 0f;
            renderButton(matrices, vertexConsumers,
                    buttonUpY - buttonSize / 2 + yOffset,
                    buttonUpY + buttonSize / 2 + yOffset,
                    upPressed, false, light, overlay, facing);
        }
        
        // 渲染下按钮
        if (!isBottomFloor) {
            float yOffset = (isTopFloor && !isBottomFloor) ? singleButtonOffset : 0f;
            renderButton(matrices, vertexConsumers,
                    buttonDownY - buttonSize / 2 + yOffset,
                    buttonDownY + buttonSize / 2 + yOffset,
                    downPressed, true, light, overlay, facing);
        }
    }

    protected void renderFloorNumber(PoseStack matrices, MultiBufferSource vertexConsumers,
                                    String floor, int light) {
        if (floor == null || floor.isEmpty()) {
            floor = defaultFloorText;
        }
        
        matrices.pushPose();

        matrices.translate(0.5f, floorNumberY, depthOffset);

        float scale = calculateFloorNumberScale(floor);
        matrices.scale(scale, -scale, scale);

        matrices.mulPose(Axis.YP.rotationDegrees(180));

        OptimizedFontRenderer.renderText(
                matrices,
                vertexConsumers,
                floor,
                floorNumberColor,
                0, 0, 0,
                1.0f,
                light,
                true,
                fontName
        );
        
        matrices.popPose();
    }

    protected float calculateFloorNumberScale(String floor) {
        int length = floor.length();
        
        if (length <= 2) {
            return floorScaleTwoChars;
        } else if (length == 3) {
            return floorScaleThreeChars;
        } else {
            float scale = floorScaleThreeChars - (length - 3) * floorScaleExtraReduction;
            return Math.max(scale, floorScaleMin);
        }
    }

    // 两种箭头渲染方式
    protected void renderDirectionArrow(PoseStack matrices, MultiBufferSource vertexConsumers,
                                       Lift.LiftDirection direction, int light, int overlay) {
        
        if (arrowRenderMode == ArrowRenderMode.TEXTURE) {
            renderTextureArrow(matrices, vertexConsumers, direction, light, overlay);
        } else {
            renderFontArrow(matrices, vertexConsumers, direction, light);
        }
    }

    protected void renderTextureArrow(PoseStack matrices, MultiBufferSource vertexConsumers,
                                     Lift.LiftDirection direction, int light, int overlay) {
        
        boolean rotateArrow = direction == Lift.LiftDirection.DOWN;
        
        matrices.pushPose();
        
        float centerX = 0.5f;
        float minX = centerX - arrowWidth / 2;
        float maxX = centerX + arrowWidth / 2;
        float minY = arrowY - arrowHeight / 2;
        float maxY = arrowY + arrowHeight / 2;
        
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderType.entityCutout(arrowTexture));
        
        if (rotateArrow) {
            matrices.translate(centerX, (minY + maxY) / 2.0f, 0);
            matrices.mulPose(Axis.ZP.rotationDegrees(180));
            matrices.translate(-centerX, -(minY + maxY) / 2.0f, 0);
        }
        
        var matrix = matrices.last().pose();
        var normal = matrices.last().normal();
        float normalZ = 1.0f;
        
        vertexConsumer.vertex(matrix, minX, minY, depthOffset)
                .color(255, 255, 255, 255)
                .uv(0.0f, 1.0f)
                .overlayCoords(overlay)
                .uv2(light)
                .normal(normal, 0, 0, normalZ)
                .endVertex();
        
        vertexConsumer.vertex(matrix, minX, maxY, depthOffset)
                .color(255, 255, 255, 255)
                .uv(0.0f, 0.0f)
                .overlayCoords(overlay)
                .uv2(light)
                .normal(normal, 0, 0, normalZ)
                .endVertex();
        
        vertexConsumer.vertex(matrix, maxX, maxY, depthOffset)
                .color(255, 255, 255, 255)
                .uv(1.0f, 0.0f)
                .overlayCoords(overlay)
                .uv2(light)
                .normal(normal, 0, 0, normalZ)
                .endVertex();
        
        vertexConsumer.vertex(matrix, maxX, minY, depthOffset)
                .color(255, 255, 255, 255)
                .uv(1.0f, 1.0f)
                .overlayCoords(overlay)
                .uv2(light)
                .normal(normal, 0, 0, normalZ)
                .endVertex();
        
        matrices.popPose();
    }

    protected void renderFontArrow(PoseStack matrices, MultiBufferSource vertexConsumers,
                                  Lift.LiftDirection direction, int light) {
        
        String arrowText = direction == Lift.LiftDirection.UP ? arrowUpText : arrowDownText;
        
        matrices.pushPose();
        
        matrices.translate(0.5f, arrowY, depthOffset);
        matrices.scale(arrowScale, -arrowScale, arrowScale);
        matrices.mulPose(Axis.YP.rotationDegrees(180));
        
        OptimizedFontRenderer.renderText(
                matrices,
                vertexConsumers,
                arrowText,
                arrowColor,
                0, 0, 0,
                1.0f,
                light,
                true,
                fontName
        );
        
        matrices.popPose();
    }

    protected void renderButton(PoseStack matrices, MultiBufferSource vertexConsumers,
                               float minY, float maxY,
                               boolean isPressed, boolean rotate180,
                               int light, int overlay, Direction facing) {
        
        ResourceLocation texture = isPressed ? buttonPressedTexture : buttonNormalTexture;
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderType.entityCutout(texture));

        float uStart, uEnd;
        if (facing == Direction.SOUTH || facing == Direction.WEST) {
            uStart = 1.0f;
            uEnd = 0.0f;
        } else {
            uStart = 0.0f;
            uEnd = 1.0f;
        }
        
        matrices.pushPose();
        
        if (rotate180) {
            float centerX = 0.5f;
            float centerY = (minY + maxY) / 2.0f;
            matrices.translate(centerX, centerY, 0);
            matrices.mulPose(Axis.ZP.rotationDegrees(180));
            matrices.translate(-centerX, -centerY, 0);
        }
        
        var matrix = matrices.last().pose();
        var normal = matrices.last().normal();
        float normalZ = 1.0f;

        int r = (buttonTintColor >> 16) & 0xFF;
        int g = (buttonTintColor >> 8) & 0xFF;
        int b = buttonTintColor & 0xFF;
        
        vertexConsumer.vertex(matrix, 0.4625f, minY, depthOffset)
                .color(r, g, b, 255)
                .uv(uStart, 1.0f)
                .overlayCoords(overlay)
                .uv2(light)
                .normal(normal, 0, 0, normalZ)
                .endVertex();
        
        vertexConsumer.vertex(matrix, 0.4625f, maxY, depthOffset)
                .color(r, g, b, 255)
                .uv(uStart, 0.0f)
                .overlayCoords(overlay)
                .uv2(light)
                .normal(normal, 0, 0, normalZ)
                .endVertex();
        
        vertexConsumer.vertex(matrix, 0.5375f, maxY, depthOffset)
                .color(r, g, b, 255)
                .uv(uEnd, 0.0f)
                .overlayCoords(overlay)
                .uv2(light)
                .normal(normal, 0, 0, normalZ)
                .endVertex();
        
        vertexConsumer.vertex(matrix, 0.5375f, minY, depthOffset)
                .color(r, g, b, 255)
                .uv(uEnd, 1.0f)
                .overlayCoords(overlay)
                .uv2(light)
                .normal(normal, 0, 0, normalZ)
                .endVertex();
        
        matrices.popPose();
    }

    protected Direction getFacingFromState(BlockState state) {
        try {
            return state.getValue(DirectionPropertyCache.getDirectionProperty(getFacingPropertyName()));
        } catch (Exception e) {
            return Direction.NORTH; // 默认朝向
        }
    }

    protected void applyBaseTransform(PoseStack matrices, Direction facing) {
        matrices.pushPose();
        
        switch (facing) {
            case SOUTH:
                matrices.translate(0, baseYOffset, 0.99);
                break;
            case EAST:
                matrices.translate(0.99, baseYOffset, 1);
                matrices.mulPose(Axis.YP.rotationDegrees(90));
                break;
            case NORTH:
                matrices.translate(1, baseYOffset, 0.01);
                matrices.mulPose(Axis.YP.rotationDegrees(180));
                break;
            case WEST:
                matrices.translate(0.01, baseYOffset, 0);
                matrices.mulPose(Axis.YP.rotationDegrees(270));
                break;
            default:
                break;
        }
    }

    protected LiftData getLiftData(T entity, Level world, BlockPos trackPosition) {
        Lift.LiftDirection liftDirection = Lift.LiftDirection.NONE;
        boolean isTopFloor = false;
        boolean isBottomFloor = false;
        String floorNumber = defaultFloorText;
        
        int currentFloorY = trackPosition.getY();
        
        for (Lift lift : ClientData.LIFTS) {
            if (lift.hasFloor(trackPosition)) {
                final BlockPos currentFloor = lift.getCurrentFloorBlockPos();
                final BlockEntity blockEntity = world.getBlockEntity(currentFloor);
                
                if (blockEntity instanceof mtr.block.BlockLiftTrackFloor.TileEntityLiftTrackFloor) {
                    floorNumber = ((mtr.block.BlockLiftTrackFloor.TileEntityLiftTrackFloor) blockEntity).getFloorNumber();
                    liftDirection = lift.getLiftDirection();
                    updateLiftDirection(entity, liftDirection);
                    
                    boolean[] hasButton = new boolean[2];
                    lift.hasUpDownButtonForFloor(currentFloorY, hasButton);
                    
                    isTopFloor = !hasButton[0];
                    isBottomFloor = !hasButton[1];
                    break;
                }
            }
        }
        
        return new LiftData(floorNumber, liftDirection, isTopFloor, isBottomFloor);
    }

    protected void checkLiftArrival(T entity, Level world, BlockPos trackPosition) {
        for (Lift lift : ClientData.LIFTS) {
            if (lift.hasFloor(trackPosition)) {
                final BlockPos currentFloor = lift.getCurrentFloorBlockPos();
                if (currentFloor != null && currentFloor.equals(trackPosition)) {
                    liftArrived(entity);
                    break;
                }
            }
        }
    }

    protected static class LiftData {
        public final String floorNumber;
        public final Lift.LiftDirection liftDirection;
        public final boolean isTopFloor;
        public final boolean isBottomFloor;
        
        public LiftData(String floorNumber, Lift.LiftDirection liftDirection, 
                       boolean isTopFloor, boolean isBottomFloor) {
            this.floorNumber = floorNumber;
            this.liftDirection = liftDirection;
            this.isTopFloor = isTopFloor;
            this.isBottomFloor = isBottomFloor;
        }
    }

    private static class DirectionPropertyCache {
        private static net.minecraft.world.level.block.state.properties.Property<Direction> directionProperty;
        
        public static net.minecraft.world.level.block.state.properties.Property<Direction> getDirectionProperty(String propertyName) {
            if (directionProperty == null) {
                directionProperty = net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING;
            }
            return directionProperty;
        }
    }
}