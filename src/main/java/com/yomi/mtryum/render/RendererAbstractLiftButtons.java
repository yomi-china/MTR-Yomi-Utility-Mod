package com.yomi.mtryum.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import com.yomi.mtryum.Mtryum;
import com.yomi.mtryum.block.AbstractLiftButtonsBlock;
import com.yomi.mtryum.block.AbstractLiftButtonsBlockEntity;
import com.yomi.mtryum.item.ItemLiftPartsLinkModifier;
import mtr.block.BlockLiftPanelBase;
import mtr.block.BlockLiftTrackFloor;
import mtr.block.IBlock;
import mtr.client.ClientData;
import mtr.client.IDrawing;
import mtr.data.Lift;
import mtr.item.ItemLiftButtonsLinkModifier;
import mtr.mappings.Utilities;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public abstract class RendererAbstractLiftButtons<T extends BlockEntity> implements BlockEntityRenderer<T> {

    protected ResourceLocation buttonNormalTexture = new ResourceLocation(Mtryum.MOD_ID, "textures/button/mitsubshi_normal.png");
    protected ResourceLocation buttonPressedTexture = new ResourceLocation(Mtryum.MOD_ID, "textures/button/mitsubshi_pressed.png");
    protected ResourceLocation arrowTexture = new ResourceLocation(Mtryum.MOD_ID, "textures/arrow/mitsubshi_arrow.png");

    protected int floorNumberColor = 0xFFFFFFFF;
    protected int arrowColor = 0xFFFFFFFF;
    protected int buttonTintColor = 0xFFFFFF;

    protected String fontName = "default";
    protected String defaultFloorText = "??";

    protected float characterSpacing = 0.0f;
    protected float letterSpacingFactor = 1.0f;
    protected boolean enableCustomSpacing = false;

    protected float baseYOffset = -0.135f;
    protected float floorNumberY = 0.78f;
    protected float floorNumberXOffset = 0.5f;
    protected float arrowX = 0.5f;
    protected float arrowY = 0.88f;
    protected float buttonX = 0.5f;
    protected float buttonUpY = 0.48f;
    protected float buttonDownY = 0.35f;
    protected float buttonSize = 0.075f;
    protected float arrowWidth = 0.125f;
    protected float arrowHeight = 0.125f;
    protected float depthOffset = 0.002f;

    protected float floorScaleTwoChars = 0.0009f;
    protected float floorScaleThreeChars = 0.00085f;
    protected float floorScaleExtraReduction = 0.0001f;
    protected float floorScaleMin = 0.0001f;
    protected float arrowScale = 0.0008f;

    protected ArrowRenderMode arrowRenderMode = ArrowRenderMode.TEXTURE;
    protected String arrowUpText = "<";
    protected String arrowDownText = ">";
    protected float singleButtonOffset = 0.06f;
    protected boolean autoPadSingleDigit = false;

    protected boolean enableSeparateArrowX = false;
    protected float arrowUpX = 0.5f;
    protected float arrowDownX = 0.5f;

    protected int linkR = 255;
    protected int linkG = 255;
    protected int linkB = 255;

    // 双梯额外参数
    protected float dualDisplayOffset = 0.18f;
    protected float dualFloorScaleTwoChars = 0.0006f;
    protected float dualFloorScaleThreeChars = 0.00055f;
    protected float dualFloorScaleExtraReduction = 0.00008f;
    protected float dualFloorScaleMin = 0.0001f;
    protected float dualArrowScale = 0.0005f;
    protected float dualArrowWidth = 0.09f;
    protected float dualArrowHeight = 0.09f;

    public enum ArrowRenderMode { TEXTURE, FONT }

    protected static class LiftDisplayEntry {
        public final double x;
        public final double z;
        public final String floorNumber;
        public final Lift.LiftDirection direction;

        public LiftDisplayEntry(double x, double z, String floorNumber, Lift.LiftDirection direction) {
            this.x = x;
            this.z = z;
            this.floorNumber = floorNumber;
            this.direction = direction;
        }
    }

    public RendererAbstractLiftButtons() {}

    @NotNull
    protected abstract String getFacingPropertyName();

    protected Direction getFacingFromState(BlockState state) {
        try {
            return state.getValue(DirectionPropertyCache.getDirectionProperty(getFacingPropertyName()));
        } catch (Exception e) {
            return Direction.NORTH;
        }
    }

    protected void applyBaseTransform(PoseStack matrices, Direction facing) {
        matrices.pushPose();
        switch (facing) {
            case SOUTH -> matrices.translate(0, baseYOffset, 0.99);
            case EAST  -> { matrices.translate(0.99, baseYOffset, 1); matrices.mulPose(Vector3f.YP.rotationDegrees(90)); }
            case NORTH -> { matrices.translate(1, baseYOffset, 0.01); matrices.mulPose(Vector3f.YP.rotationDegrees(180)); }
            case WEST  -> { matrices.translate(0.01, baseYOffset, 0); matrices.mulPose(Vector3f.YP.rotationDegrees(270)); }
        }
    }

    protected boolean shouldPadSingleDigit() { return autoPadSingleDigit; }
    protected boolean shouldEnableCustomSpacing() { return enableCustomSpacing; }
    protected float getCharacterSpacing() { return characterSpacing; }
    protected float getLetterSpacingFactor() { return letterSpacingFactor; }

    protected String processFloorNumber(String floorNumber) {
        if (!shouldPadSingleDigit() || floorNumber == null || floorNumber.isEmpty()) return floorNumber;
        if (floorNumber.length() == 1) return " " + floorNumber;
        return floorNumber;
    }

    @Override
    public void render(T entity, float tickDelta, PoseStack matrices,
                       MultiBufferSource vertexConsumers, int light, int overlay) {
        if (entity == null || entity.getLevel() == null) return;

        final Level world = entity.getLevel();
        final BlockState state = entity.getBlockState();
        final Direction facing = getFacingFromState(state);
        if (facing == null) return;

        final Player player = Minecraft.getInstance().player;
        if (player == null) return;

        final BlockPos pos = entity.getBlockPos();

        // 检查玩家是否手持电梯相关物品
        final boolean holdingLinker = Utilities.isHolding(player, item ->
                        item instanceof ItemLiftPartsLinkModifier
                        || item instanceof ItemLiftButtonsLinkModifier
                        || Block.byItem(item) instanceof AbstractLiftButtonsBlock
                        || Block.byItem(item) instanceof BlockLiftPanelBase);

        final List<LiftDisplayEntry> entries = new ArrayList<>();
        final boolean[] hasBtnOverall = {false, false};
        final boolean[] pressedOverall = {false, false};
        final boolean[] topOverall = {true};
        final boolean[] bottomOverall = {true};
        final String[] primaryFloor = {defaultFloorText};
        final Lift.LiftDirection[] primaryDir = {Lift.LiftDirection.NONE};

        if (entity instanceof AbstractLiftButtonsBlockEntity liftEntity) {
            // 连线渲染
            if (holdingLinker) {
                matrices.pushPose();
                matrices.translate(0.5, 0, 0.5);
                liftEntity.forEachTrackPosition(world, (trackPos, trackFloor) -> {
                    renderLiftObjectColorfulLink(matrices, vertexConsumers,
                            world, pos, trackPos, facing, true, linkR, linkG, linkB);
                });
                matrices.popPose();
            }

            liftEntity.forEachTrackPosition(world, (trackPos, trackFloor) -> {
                for (final Lift lift : ClientData.LIFTS) {
                    if (lift.hasFloor(trackPos)) {
                        // 当前所在楼层
                        final BlockPos currentFloor = lift.getCurrentFloorBlockPos();
                        final BlockEntity be = world.getBlockEntity(currentFloor);
                        String floorNumber = defaultFloorText;
                        if (be instanceof BlockLiftTrackFloor.TileEntityLiftTrackFloor tile) {
                            floorNumber = tile.getFloorNumber();
                        }

                        final Lift.LiftDirection dir = lift.getLiftDirection();
                        entries.add(new LiftDisplayEntry(
                                lift.getPositionX(), lift.getPositionZ(),
                                floorNumber, dir));

                        boolean[] hasBtn = new boolean[2];
                        lift.hasUpDownButtonForFloor(trackPos.getY(), hasBtn);
                        if (hasBtn[0]) hasBtnOverall[0] = true;
                        if (hasBtn[1]) hasBtnOverall[1] = true;

                        // 按钮按下状态
                        if (lift.liftInstructions.containsInstruction(trackPos.getY(), true))
                            pressedOverall[0] = true;
                        if (lift.liftInstructions.containsInstruction(trackPos.getY(), false))
                            pressedOverall[1] = true;

                        if (hasBtn[0]) topOverall[0] = false;
                        if (hasBtn[1]) bottomOverall[0] = false;

                        if (primaryDir[0] == Lift.LiftDirection.NONE) {
                            primaryDir[0] = dir;
                            primaryFloor[0] = floorNumber;
                        }
                        break;
                    }
                }
            });
        }

        entries.sort(Comparator.comparingDouble(entry ->
                facing.getStepX() * (entry.z - pos.getZ())
                        - facing.getStepZ() * (entry.x - pos.getX())));

        applyBaseTransform(matrices, facing);

        if (entries.size() >= 2) {
            final boolean swap = facing.getAxis() == Direction.Axis.Z;
            final LiftDisplayEntry leftEntry = swap ? entries.get(1) : entries.get(0);
            final LiftDisplayEntry rightEntry = swap ? entries.get(0) : entries.get(1);
            renderDualLiftDisplays(matrices, vertexConsumers, light, overlay,
                    leftEntry, rightEntry, facing);
            renderButtons(matrices, vertexConsumers, light, overlay, facing,
                    hasBtnOverall[0], hasBtnOverall[1],
                    pressedOverall[0], pressedOverall[1],
                    topOverall[0], bottomOverall[0]);
        } else if (entries.size() == 1) {
            final LiftDisplayEntry entry = entries.get(0);
            renderAllElements(matrices, vertexConsumers, light, overlay,
                    processFloorNumber(entry.floorNumber), entry.direction,
                    pressedOverall[0], pressedOverall[1],
                    facing, topOverall[0], bottomOverall[0]);
        } else {
            renderAllElements(matrices, vertexConsumers, light, overlay,
                    defaultFloorText, Lift.LiftDirection.NONE,
                    false, false, facing, true, true);
        }

        matrices.popPose();
    }

    protected void renderAllElements(PoseStack matrices, MultiBufferSource vertexConsumers,
                                     int light, int overlay, String floorNumber,
                                     Lift.LiftDirection liftDirection,
                                     boolean upPressed, boolean downPressed,
                                     Direction facing, boolean isTopFloor, boolean isBottomFloor) {
        renderFloorNumberAt(matrices, vertexConsumers, floorNumber, light,
                floorNumberXOffset, floorNumberY,
                calculateFloorScale(floorNumber, false));

        if (liftDirection != Lift.LiftDirection.NONE) {
            float xPos = arrowX;
            if (enableSeparateArrowX) {
                xPos = (liftDirection == Lift.LiftDirection.UP) ? arrowUpX : arrowDownX;
            }
            renderDirectionArrowAt(matrices, vertexConsumers, liftDirection, light, overlay,
                    xPos, arrowY, false);
        }

        if (!isTopFloor) {
            float yOffset = (isBottomFloor && !isTopFloor) ? -singleButtonOffset : 0f;
            renderButtonAt(matrices, vertexConsumers,
                    buttonUpY - buttonSize / 2 + yOffset,
                    buttonUpY + buttonSize / 2 + yOffset,
                    upPressed, false, light, overlay, facing);
        }
        if (!isBottomFloor) {
            float yOffset = (isTopFloor && !isBottomFloor) ? singleButtonOffset : 0f;
            renderButtonAt(matrices, vertexConsumers,
                    buttonDownY - buttonSize / 2 + yOffset,
                    buttonDownY + buttonSize / 2 + yOffset,
                    downPressed, true, light, overlay, facing);
        }
    }

    protected void renderDualLiftDisplays(PoseStack matrices, MultiBufferSource vertexConsumers,
                                          int light, int overlay,
                                          LiftDisplayEntry left, LiftDisplayEntry right,
                                          Direction facing) {
        float leftX = 0.5f - dualDisplayOffset;
        renderFloorNumberAt(matrices, vertexConsumers,
                processFloorNumber(left.floorNumber), light,
                leftX, floorNumberY,
                calculateFloorScale(left.floorNumber, true));
        if (left.direction != Lift.LiftDirection.NONE) {
            renderDirectionArrowAt(matrices, vertexConsumers, left.direction, light, overlay,
                    leftX, arrowY, true);
        }

        float rightX = 0.5f + dualDisplayOffset;
        renderFloorNumberAt(matrices, vertexConsumers,
                processFloorNumber(right.floorNumber), light,
                rightX, floorNumberY,
                calculateFloorScale(right.floorNumber, true));
        if (right.direction != Lift.LiftDirection.NONE) {
            renderDirectionArrowAt(matrices, vertexConsumers, right.direction, light, overlay,
                    rightX, arrowY, true);
        }
    }

    protected void renderButtons(PoseStack matrices, MultiBufferSource vertexConsumers,
                                 int light, int overlay, Direction facing,
                                 boolean hasUp, boolean hasDown,
                                 boolean upPressed, boolean downPressed,
                                 boolean isTopFloor, boolean isBottomFloor) {
        if (!isTopFloor && hasUp) {
            float yOffset = (isBottomFloor && !isTopFloor) ? -singleButtonOffset : 0f;
            renderButtonAt(matrices, vertexConsumers,
                    buttonUpY - buttonSize / 2 + yOffset,
                    buttonUpY + buttonSize / 2 + yOffset,
                    upPressed, false, light, overlay, facing);
        }
        if (!isBottomFloor && hasDown) {
            float yOffset = (isTopFloor && !isBottomFloor) ? singleButtonOffset : 0f;
            renderButtonAt(matrices, vertexConsumers,
                    buttonDownY - buttonSize / 2 + yOffset,
                    buttonDownY + buttonSize / 2 + yOffset,
                    downPressed, true, light, overlay, facing);
        }
    }

    protected void renderFloorNumberAt(PoseStack matrices, MultiBufferSource vertexConsumers,
                                       String floor, int light, float x, float y, float scale) {
        if (floor == null || floor.isEmpty()) floor = defaultFloorText;
        matrices.pushPose();
        matrices.translate(x, y, depthOffset);
        matrices.scale(scale, -scale, scale);
        matrices.mulPose(Vector3f.YP.rotationDegrees(180));

        float spacing = shouldEnableCustomSpacing() ? getCharacterSpacing() : 0.0f;
        float spacingFactor = shouldEnableCustomSpacing() ? getLetterSpacingFactor() : 1.0f;

        RendererCustomFont.renderText(matrices, vertexConsumers, floor, floorNumberColor,
                0, 0, 0, 1.0f, light, true, fontName, spacing, spacingFactor);
        matrices.popPose();
    }

    protected float calculateFloorScale(String floor, boolean dual) {
        int length = floor.length();
        float scaleTwo, scaleThree, scaleExtra, scaleMin;
        if (dual) {
            scaleTwo = dualFloorScaleTwoChars;
            scaleThree = dualFloorScaleThreeChars;
            scaleExtra = dualFloorScaleExtraReduction;
            scaleMin = dualFloorScaleMin;
        } else {
            scaleTwo = floorScaleTwoChars;
            scaleThree = floorScaleThreeChars;
            scaleExtra = floorScaleExtraReduction;
            scaleMin = floorScaleMin;
        }
        if (length <= 2) return scaleTwo;
        else if (length == 3) return scaleThree;
        else return Math.max(scaleThree - (length - 3) * scaleExtra, scaleMin);
    }

    protected void renderDirectionArrowAt(PoseStack matrices, MultiBufferSource vertexConsumers,
                                          Lift.LiftDirection direction, int light, int overlay,
                                          float x, float y, boolean dual) {
        if (arrowRenderMode == ArrowRenderMode.TEXTURE) {
            renderTextureArrowAt(matrices, vertexConsumers, direction, light, overlay, x, y, dual);
        } else {
            renderFontArrowAt(matrices, vertexConsumers, direction, light, x, y, dual);
        }
    }

    protected void renderTextureArrowAt(PoseStack matrices, MultiBufferSource vertexConsumers,
                                        Lift.LiftDirection direction, int light, int overlay,
                                        float centerX, float centerY, boolean dual) {
        boolean rotateArrow = direction == Lift.LiftDirection.DOWN;
        matrices.pushPose();

        float w = dual ? dualArrowWidth : arrowWidth;
        float h = dual ? dualArrowHeight : arrowHeight;
        float minX = centerX - w / 2;
        float maxX = centerX + w / 2;
        float minY = centerY - h / 2;
        float maxY = centerY + h / 2;

        VertexConsumer vc = vertexConsumers.getBuffer(RenderType.entityCutout(arrowTexture));
        if (rotateArrow) {
            matrices.translate(centerX, (minY + maxY) / 2f, 0);
            matrices.mulPose(Vector3f.ZP.rotationDegrees(180));
            matrices.translate(-centerX, -(minY + maxY) / 2f, 0);
        }
        var m = matrices.last().pose();
        var n = matrices.last().normal();
        float nz = 1f;
        vc.vertex(m, minX, minY, depthOffset).color(255,255,255,255).uv(0,1).overlayCoords(overlay).uv2(light).normal(n,0,0,nz).endVertex();
        vc.vertex(m, minX, maxY, depthOffset).color(255,255,255,255).uv(0,0).overlayCoords(overlay).uv2(light).normal(n,0,0,nz).endVertex();
        vc.vertex(m, maxX, maxY, depthOffset).color(255,255,255,255).uv(1,0).overlayCoords(overlay).uv2(light).normal(n,0,0,nz).endVertex();
        vc.vertex(m, maxX, minY, depthOffset).color(255,255,255,255).uv(1,1).overlayCoords(overlay).uv2(light).normal(n,0,0,nz).endVertex();
        matrices.popPose();
    }

    protected void renderFontArrowAt(PoseStack matrices, MultiBufferSource vertexConsumers,
                                     Lift.LiftDirection direction, int light,
                                     float x, float y, boolean dual) {
        String text = direction == Lift.LiftDirection.UP ? arrowUpText : arrowDownText;
        matrices.pushPose();
        matrices.translate(x, y, depthOffset);
        float scale = dual ? dualArrowScale : arrowScale;
        matrices.scale(scale, -scale, scale);
        matrices.mulPose(Vector3f.YP.rotationDegrees(180));
        float spacing = shouldEnableCustomSpacing() ? getCharacterSpacing() : 0f;
        float factor = shouldEnableCustomSpacing() ? getLetterSpacingFactor() : 1f;
        RendererCustomFont.renderText(matrices, vertexConsumers, text, arrowColor,
                0, 0, 0, 1f, light, true, fontName, spacing, factor);
        matrices.popPose();
    }

    protected void renderButtonAt(PoseStack matrices, MultiBufferSource vertexConsumers,
                                  float minY, float maxY, boolean isPressed, boolean rotate180,
                                  int light, int overlay, Direction facing) {
        ResourceLocation tex = isPressed ? buttonPressedTexture : buttonNormalTexture;
        VertexConsumer vc = vertexConsumers.getBuffer(RenderType.entityCutout(tex));

        float cx = buttonX, hw = buttonSize / 2;
        float minX = cx - hw, maxX = cx + hw;
        float uStart, uEnd;
        if (facing == Direction.SOUTH || facing == Direction.WEST) { uStart = 1f; uEnd = 0f; }
        else { uStart = 0f; uEnd = 1f; }

        matrices.pushPose();
        if (rotate180) {
            float cy = (minY + maxY) / 2f;
            matrices.translate(cx, cy, 0);
            matrices.mulPose(Vector3f.ZP.rotationDegrees(180));
            matrices.translate(-cx, -cy, 0);
        }
        var m = matrices.last().pose();
        var n = matrices.last().normal();
        float nz = 1f;
        int r = (buttonTintColor >> 16) & 0xFF;
        int g = (buttonTintColor >> 8) & 0xFF;
        int b = buttonTintColor & 0xFF;
        vc.vertex(m, minX, minY, depthOffset).color(r,g,b,255).uv(uStart,1).overlayCoords(overlay).uv2(light).normal(n,0,0,nz).endVertex();
        vc.vertex(m, minX, maxY, depthOffset).color(r,g,b,255).uv(uStart,0).overlayCoords(overlay).uv2(light).normal(n,0,0,nz).endVertex();
        vc.vertex(m, maxX, maxY, depthOffset).color(r,g,b,255).uv(uEnd,0).overlayCoords(overlay).uv2(light).normal(n,0,0,nz).endVertex();
        vc.vertex(m, maxX, minY, depthOffset).color(r,g,b,255).uv(uEnd,1).overlayCoords(overlay).uv2(light).normal(n,0,0,nz).endVertex();
        matrices.popPose();
    }

    private static class DirectionPropertyCache {
        private static Property<Direction> directionProperty;
        public static Property<Direction> getDirectionProperty(String name) {
            if (directionProperty == null) directionProperty = BlockStateProperties.HORIZONTAL_FACING;
            return directionProperty;
        }
    }

    public static void renderLiftObjectColorfulLink(PoseStack matrices, MultiBufferSource vertexConsumers, Level world, BlockPos pos, BlockPos trackPosition, Direction facing, boolean holdingLinker, int r, int g, int b) {
        if (holdingLinker) {
            Direction trackFacing = (Direction)IBlock.getStatePropertySafe(world, trackPosition, HorizontalDirectionalBlock.FACING);
            IDrawing.drawLine(matrices, vertexConsumers, (float)(trackPosition.getX() - pos.getX()) + (float)trackFacing.getStepX() / 2.0F, (float)(trackPosition.getY() - pos.getY()) + 0.5F, (float)(trackPosition.getZ() - pos.getZ()) + (float)trackFacing.getStepZ() / 2.0F, (float)facing.getStepX() / 2.0F, 0.25F, (float)facing.getStepZ() / 2.0F, r, g, b);
        }

    }
}