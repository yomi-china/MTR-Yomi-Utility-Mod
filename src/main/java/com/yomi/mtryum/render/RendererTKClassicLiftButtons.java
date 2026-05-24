package com.yomi.mtryum.render;

import com.yomi.mtryum.Mtryum;
import com.yomi.mtryum.block.TKClassicLiftButtonsBlockEntity;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class RendererTKClassicLiftButtons extends RendererAbstractLiftButtons<TKClassicLiftButtonsBlockEntity> {

    private static final ResourceLocation TK_UP_NORMAL = new ResourceLocation(Mtryum.MOD_ID, "textures/button/tk_normal.png");
    private static final ResourceLocation TK_UP_PRESSED = new ResourceLocation(Mtryum.MOD_ID, "textures/button/tk_pressed.png");

    public RendererTKClassicLiftButtons() {
        super();

        // 单电梯参数
        this.buttonNormalTexture = TK_UP_NORMAL;
        this.buttonPressedTexture = TK_UP_PRESSED;
        this.floorNumberColor = 0xFFFF0000;
        this.arrowColor = 0xFFFF0000;
        this.buttonTintColor = 0xFFFFFF;
        this.fontName = "old-thyssenkrupp";
        this.defaultFloorText = "JU";
        this.baseYOffset = -0.135f;
        this.floorNumberY = 0.675f;
        this.floorNumberXOffset = 0.49f;
        this.arrowX = 0.49f;
        this.arrowY = 0.745f;
        this.buttonX = 0.492f;
        this.buttonUpY = 0.47f;
        this.buttonDownY = 0.36f;
        this.buttonSize = 0.062f;
        this.arrowWidth = 0.125f;
        this.arrowHeight = 0.125f;
        this.depthOffset = 0.0015f;
        this.floorScaleTwoChars = 0.0009f;
        this.floorScaleThreeChars = 0.0007f;
        this.floorScaleExtraReduction = 0.00015f;
        this.floorScaleMin = 0.0003f;
        this.arrowScale = 0.0009f;
        this.arrowRenderMode = ArrowRenderMode.FONT;
        this.arrowUpText = "<";
        this.arrowDownText = ">";
        this.singleButtonOffset = 0.06f;
        this.autoPadSingleDigit = true;
        this.enableCustomSpacing = true;
        this.characterSpacing = 2.0f;
        this.letterSpacingFactor = 1.0f;

        // 双电梯参数
        this.dualDisplayOffset = 0.16f;
        this.dualFloorScaleTwoChars = 0.0009f;
        this.dualFloorScaleThreeChars = 0.0007f;
        this.dualFloorScaleExtraReduction = 0.00015f;
        this.dualFloorScaleMin = 0.0003f;
        this.dualArrowScale = 0.0009f;
        this.dualArrowWidth = 0.125f;
        this.dualArrowHeight = 0.125f;
    }

    @NotNull
    @Override
    protected String getFacingPropertyName() {
        return "facing";
    }
}