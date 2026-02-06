package com.yomi.mtryum.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.yomi.mtryum.Mtryum;
import com.yomi.mtryum.block.MitsubishiStyleLiftButtonsBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class MitsubishiStyleLiftButtonsRenderer extends AbstractLiftButtonsRenderer<MitsubishiStyleLiftButtonsBlockEntity> {

    private static final ResourceLocation MITSUBISHI_BUTTON_NORMAL = new ResourceLocation(Mtryum.MOD_ID, "textures/block/lift_button_normal.png");
    private static final ResourceLocation MITSUBISHI_BUTTON_PRESSED = new ResourceLocation(Mtryum.MOD_ID, "textures/block/lift_button_pressed.png");

    public MitsubishiStyleLiftButtonsRenderer(BlockEntityRendererProvider.Context context) {
        super(context);

        this.buttonNormalTexture = MITSUBISHI_BUTTON_NORMAL;
        this.buttonPressedTexture = MITSUBISHI_BUTTON_PRESSED;

        this.floorNumberColor = 0xFFFF8C00;
        this.arrowColor = 0xFFFF8C00;
        this.buttonTintColor = 0xFFFFFF;

        this.fontName = "mitsubishi-modern";
        this.defaultFloorText = "??";

        this.baseYOffset = -0.135f;
        this.floorNumberY = 0.78f;
        this.floorNumberXOffset = 0.5f;
        this.arrowY = 0.88f;
        this.buttonUpY = 0.48f;
        this.buttonDownY = 0.35f;

        this.buttonSize = 0.075f;
        this.arrowWidth = 0.125f;
        this.arrowHeight = 0.125f;
        this.depthOffset = 0.002f;

        this.floorScaleTwoChars = 0.0009f;
        this.floorScaleThreeChars = 0.00085f;
        this.floorScaleExtraReduction = 0.0001f;
        this.floorScaleMin = 0.0001f;
        this.arrowScale = 0.0008f;

        this.arrowRenderMode = ArrowRenderMode.FONT;


        this.arrowUpText = "<";
        this.arrowDownText = ">";

        this.singleButtonOffset = 0.06f;
    }

    @Override
    protected String getFacingPropertyName() {
        return "facing";
    }

    @Override
    protected boolean isUpButtonPressed(MitsubishiStyleLiftButtonsBlockEntity entity) {
        return entity.isUpButtonPressed();
    }

    @Override
    protected boolean isDownButtonPressed(MitsubishiStyleLiftButtonsBlockEntity entity) {
        return entity.isDownButtonPressed();
    }

    @Override
    protected BlockPos getTrackPosition(MitsubishiStyleLiftButtonsBlockEntity entity, Level world) {
        return entity.getTrackPosition(world);
    }

    @Override
    protected void updateLiftDirection(MitsubishiStyleLiftButtonsBlockEntity entity, mtr.data.Lift.LiftDirection liftDirection) {
        entity.updateLiftDirection(liftDirection);
    }

    @Override
    protected void liftArrived(MitsubishiStyleLiftButtonsBlockEntity entity) {
        entity.liftArrived();
    }

    @Override
    protected Direction getFacingFromState(BlockState state) {
        if (state.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
            return state.getValue(BlockStateProperties.HORIZONTAL_FACING);
        }
        return super.getFacingFromState(state);
    }

    @Override
    protected void renderButton(PoseStack matrices, MultiBufferSource vertexConsumers,
                                float minY, float maxY,
                                boolean isPressed, boolean rotate180,
                                int light, int overlay, Direction facing) {
        super.renderButton(matrices, vertexConsumers, minY, maxY, isPressed, rotate180, light, overlay, facing);
    }

    @Override
    protected void renderFloorNumber(PoseStack matrices, MultiBufferSource vertexConsumers,
                                     String floor, int light) {
        super.renderFloorNumber(matrices, vertexConsumers, floor, light);
    }

    @Override
    protected void renderAllElements(PoseStack matrices, MultiBufferSource vertexConsumers,
                                     int light, int overlay, String floorNumber,
                                     mtr.data.Lift.LiftDirection liftDirection,
                                     boolean upPressed, boolean downPressed,
                                     Direction facing, boolean isTopFloor, boolean isBottomFloor) {
        super.renderAllElements(matrices, vertexConsumers, light, overlay, floorNumber,
                liftDirection, upPressed, downPressed, facing, isTopFloor, isBottomFloor);
    }

    @Override
    protected void applyBaseTransform(PoseStack matrices, Direction facing) {
        super.applyBaseTransform(matrices, facing);
    }

    @Override
    protected float calculateFloorNumberScale(String floor) {
        int length = floor.length();

        if (length <= 2) {
            return floorScaleTwoChars;
        } else if (length == 3) {
            return floorScaleThreeChars;
        } else {
            float scale = 0.0007f - (length - 3) * 0.0001f;
            return Math.max(scale, 0.0001f);
        }
    }
}