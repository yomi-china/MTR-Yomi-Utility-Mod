package com.yomi.mtryum.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import com.yomi.mtryum.block.LiftFloorMonitorEntity;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.level.block.entity.BlockEntity;

public class LiftFloorMonitorScreen extends Screen {
    private final BlockPos pos;
    private EditBox colorInput;
    private Button style1Button;
    private Button style2Button;

    public LiftFloorMonitorScreen(BlockPos pos) {
        super(new TextComponent("电梯楼层显示器设置"));
        this.pos = pos;
    }

    @Override
    protected void init() {
        super.init();

        int currentColor = 0xFFFFFF;
        if (minecraft != null && minecraft.level != null) {
            BlockEntity entity = minecraft.level.getBlockEntity(pos);
            if (entity instanceof LiftFloorMonitorEntity tile) {
                currentColor = tile.getTextColor();
            }
        }

        // 创建输入框
        colorInput = new EditBox(
                this.font,
                this.width / 2 - 100,
                this.height / 2 - 20,
                200, 20,
                new TextComponent("颜色代码")
        );
        colorInput.setMaxLength(6);
        colorInput.setValue(String.format("%06X", currentColor));
        addRenderableWidget(colorInput);

        // 创建确认按钮 - 使用 1.19.2 的 Button 构造函数
        Button confirmButton = new Button(
                this.width / 2 - 50,
                this.height / 2 + 80,
                100, 20,
                new TextComponent("确认"),
                button -> saveAndClose()
        );
        addRenderableWidget(confirmButton);

        style1Button = new Button(
                this.width / 2 - 105,
                this.height / 2 + 45,
                100, 20,
                new TextComponent("样式1"),
                button -> setStyle(1)
        );
        addRenderableWidget(style1Button);

        style2Button = new Button(
                this.width / 2 + 5,
                this.height / 2 + 45,
                100, 20,
                new TextComponent("样式2"),
                button -> setStyle(2)
        );
        addRenderableWidget(style2Button);

        // 初始选中状态
        updateButtonStyles();
    }

    private void saveAndClose() {
        try {
            int newColor = Integer.parseInt(colorInput.getValue(), 16);

            if (minecraft != null && minecraft.level != null) {
                BlockEntity entity = minecraft.level.getBlockEntity(pos);
                if (entity instanceof LiftFloorMonitorEntity tile) {
                    tile.setTextColor(newColor);
                }
            }
        } catch (NumberFormatException e) {
            System.out.println("无效颜色代码: " + colorInput.getValue());
        }
        this.onClose();
    }

    private void setStyle(int style) {
        if (minecraft != null && minecraft.level != null) {
            BlockEntity entity = minecraft.level.getBlockEntity(pos);
            if (entity instanceof LiftFloorMonitorEntity tile) {
                tile.setArrowStyle(style);
                updateButtonStyles();
            }
        }
    }

    private void updateButtonStyles() {
        if (minecraft != null && minecraft.level != null) {
            BlockEntity entity = minecraft.level.getBlockEntity(pos);
            if (entity instanceof LiftFloorMonitorEntity tile) {
                int currentStyle = tile.getArrowStyle();
                style1Button.active = currentStyle != 1;
                style2Button.active = currentStyle != 2;
            }
        }
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(poseStack);
        super.render(poseStack, mouseX, mouseY, partialTick);

        // 绘制标题
        this.font.draw(
                poseStack,
                this.title,
                this.width / 2 - this.font.width(this.title) / 2,
                40,
                0xFFFFFF
        );

        // 绘制说明文本
        this.font.draw(
                poseStack,
                "十六进制颜色代码 (常用: 白FFFFFF, 红FF0000)",
                this.width / 2 - 100,
                this.height / 2 - 40,
                0xAAAAAA
        );
        this.font.draw(
                poseStack,
                "箭头样式（输入数字）",
                this.width / 2 - 100,
                this.height / 2 + 25,
                0xAAAAAA
        );
    }
}