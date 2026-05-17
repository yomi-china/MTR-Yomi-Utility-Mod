package com.yomi.mtryum.render;

import com.yomi.mtryum.Mtryum;
import com.yomi.mtryum.block.OTIS3StyleLiftButtonsBlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.NotNull;

public class OTIS3StyleLiftButtonsRenderer extends AbstractLiftButtonsRenderer<OTIS3StyleLiftButtonsBlockEntity> {

    private static final ResourceLocation OTIS3_BUTTON_NORMAL = new ResourceLocation(Mtryum.MOD_ID, "textures/button/otis3_normal.png");
    private static final ResourceLocation OTIS3_BUTTON_PRESSED = new ResourceLocation(Mtryum.MOD_ID, "textures/button/otis3_pressed.png");
    private static final ResourceLocation OTIS3_ARROW = new ResourceLocation(Mtryum.MOD_ID, "textures/arrow/otis3_arrow.png");

    public OTIS3StyleLiftButtonsRenderer() {
        super();

        // 单电梯参数
        this.buttonNormalTexture = OTIS3_BUTTON_NORMAL;
        this.buttonPressedTexture = OTIS3_BUTTON_PRESSED;
        this.arrowTexture = OTIS3_ARROW;
        this.floorNumberColor = 0xFFFF8C00;
        this.arrowColor = 0xFFFF8C00;
        this.buttonTintColor = 0xFFFFFF;
        this.fontName = "otis-series-1";
        this.defaultFloorText = "--";
        this.baseYOffset = -0.135f;
        this.floorNumberY = 0.7f;
        this.floorNumberXOffset = 0.5f;
        this.arrowY = 0.75f;
        this.buttonUpY = 0.48f;
        this.buttonDownY = 0.35f;
        this.buttonSize = 0.075f;
        this.arrowWidth = 0.073f;
        this.arrowHeight = 0.073f;
        this.depthOffset = 0.002f;
        this.floorScaleTwoChars = 0.002f;
        this.floorScaleThreeChars = 0.00085f;
        this.floorScaleExtraReduction = 0.0001f;
        this.floorScaleMin = 0.0001f;
        this.arrowScale = 0.00025f;
        this.arrowRenderMode = ArrowRenderMode.TEXTURE;
        this.arrowUpText = "<";
        this.arrowDownText = ">";
        this.singleButtonOffset = 0.06f;
        this.autoPadSingleDigit = true;
        this.enableCustomSpacing = true;
        this.characterSpacing = 4.0f;
        this.letterSpacingFactor = 1.0f;

        // 双电梯参数
        this.dualDisplayOffset = 0.16f;
        this.dualFloorScaleTwoChars = 0.002f;
        this.dualFloorScaleThreeChars = 0.00085f;
        this.dualFloorScaleExtraReduction = 0.0001f;
        this.dualFloorScaleMin = 0.0001f;
        this.dualArrowScale = 0.00025f;
        this.dualArrowWidth = 0.073f;
        this.dualArrowHeight = 0.073f;
    }

    @NotNull
    @Override
    protected String getFacingPropertyName() {
        return "facing";
    }

    @Override
    protected Direction getFacingFromState(BlockState state) {
        if (state.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
            return state.getValue(BlockStateProperties.HORIZONTAL_FACING);
        }
        return super.getFacingFromState(state);
    }
}