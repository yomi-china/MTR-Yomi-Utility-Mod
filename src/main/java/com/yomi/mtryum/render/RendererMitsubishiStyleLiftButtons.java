package com.yomi.mtryum.render;

import com.yomi.mtryum.Mtryum;
import com.yomi.mtryum.block.MitsubishiStyleLiftButtonsBlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.NotNull;

public class RendererMitsubishiStyleLiftButtons extends RendererAbstractLiftButtons<MitsubishiStyleLiftButtonsBlockEntity> {

    private static final ResourceLocation MITSUBISHI_BUTTON_NORMAL =
            new ResourceLocation(Mtryum.MOD_ID, "textures/button/mitsubshi_normal.png");
    private static final ResourceLocation MITSUBISHI_BUTTON_PRESSED =
            new ResourceLocation(Mtryum.MOD_ID, "textures/button/mitsubshi_pressed.png");

    public RendererMitsubishiStyleLiftButtons() {
        super();

        // 单电梯参数
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
        this.enableCustomSpacing = true;
        this.characterSpacing = 2.0f;
        this.letterSpacingFactor = 1.0f;

        // 双电梯参数
        this.dualDisplayOffset = 0.095f;
        this.dualFloorScaleTwoChars = 0.0009f;
        this.dualFloorScaleThreeChars = 0.00085f;
        this.dualFloorScaleExtraReduction = 0.0001f;
        this.dualFloorScaleMin = 0.0001f;
        this.dualArrowScale = 0.0008f;
        this.dualArrowWidth = 0.125f;
        this.dualArrowHeight = 0.125f;
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