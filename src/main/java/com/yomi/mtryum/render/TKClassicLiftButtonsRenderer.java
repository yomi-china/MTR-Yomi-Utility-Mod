package com.yomi.mtryum.render;

import com.yomi.mtryum.Mtryum;
import com.yomi.mtryum.block.TKClassicLiftButtonsBlockEntity;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class TKClassicLiftButtonsRenderer extends AbstractLiftButtonsRenderer<TKClassicLiftButtonsBlockEntity> {

    private static final ResourceLocation TK_UP_NORMAL = new ResourceLocation(Mtryum.MOD_ID, "textures/block/tk_up_normal.png");
    private static final ResourceLocation TK_UP_PRESSED = new ResourceLocation(Mtryum.MOD_ID, "textures/block/tk_up_pressed.png");

    public TKClassicLiftButtonsRenderer(BlockEntityRendererProvider.Context context) {
        super(context);

        this.buttonNormalTexture = TK_UP_NORMAL;
        this.buttonPressedTexture = TK_UP_PRESSED;

        this.floorNumberColor = 0xFFFF0000;
        this.arrowColor = 0xFFFF0000;
        this.buttonTintColor = 0xFFFFFF;

        this.fontName = "old-thyssenkrupp";
        this.defaultFloorText = "JU";

        this.baseYOffset = -0.135f;
        this.floorNumberY = 0.675f;
        this.arrowY = 0.75f;
        this.buttonUpY = 0.47f;
        this.buttonDownY = 0.36f;

        this.buttonSize = 0.06f;
        this.arrowWidth = 0.125f;
        this.arrowHeight = 0.125f;
        this.depthOffset = 0.0015f;

        this.floorScaleTwoChars = 0.0012f;
        this.floorScaleThreeChars = 0.0010f;
        this.floorScaleExtraReduction = 0.00015f;
        this.floorScaleMin = 0.0003f;
        this.arrowScale = 0.0012f;

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
    protected boolean isUpButtonPressed(TKClassicLiftButtonsBlockEntity entity) {
        return entity.isUpButtonPressed();
    }

    @Override
    protected boolean isDownButtonPressed(TKClassicLiftButtonsBlockEntity entity) {
        return entity.isDownButtonPressed();
    }

    @Override
    protected net.minecraft.core.BlockPos getTrackPosition(TKClassicLiftButtonsBlockEntity entity, net.minecraft.world.level.Level world) {
        return entity.getTrackPosition(world);
    }

    @Override
    protected void updateLiftDirection(TKClassicLiftButtonsBlockEntity entity, mtr.data.Lift.LiftDirection liftDirection) {
        entity.updateLiftDirection(liftDirection);
    }

    @Override
    protected void liftArrived(TKClassicLiftButtonsBlockEntity entity) {
        entity.liftArrived();
    }

    @Override
    protected float calculateFloorNumberScale(String floor) {
        int length = floor.length();

        if (length <= 2) {
            return floorScaleTwoChars;
        } else if (length == 3) {
            return floorScaleThreeChars;
        } else {
            float scale = 0.00085f - (length - 3) * 0.00015f;
            return Math.max(scale, 0.0003f);
        }
    }
}