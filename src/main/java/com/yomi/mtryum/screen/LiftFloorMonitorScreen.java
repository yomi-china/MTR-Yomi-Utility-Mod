package com.yomi.mtryum.screen;

import com.yomi.mtryum.block.LiftFloorMonitorEntity;
import com.yomi.mtryum.network.LiftFloorMonitorPacket;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntity;

public class LiftFloorMonitorScreen extends Screen {
    private final BlockPos pos;
    private EditBox colorInput;
    private Button style1Button;
    private Button style2Button;
    private int currentStyle = 1;

    public LiftFloorMonitorScreen(BlockPos pos) {
        super(Component.translatable("screen.mtryum.set_style_title"));
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
                currentStyle = tile.getArrowStyle();
            }
        }

        // 创建输入框
        colorInput = new EditBox(
                this.font,
                this.width / 2 - 100,
                this.height / 2 - 20,
                200, 20,
                Component.literal("颜色代码")
        );
        colorInput.setMaxLength(6);
        colorInput.setValue(String.format("%06X", currentColor));
        addRenderableWidget(colorInput);

        // 创建确认按钮
        Button confirmButton = Button.builder(
                Component.literal("确认"),
                button -> saveAndClose()
        ).bounds(
                this.width / 2 - 50,
                this.height / 2 + 80,
                100, 20
        ).build();
        addRenderableWidget(confirmButton);

        style1Button = Button.builder(
                Component.literal("样式1"),
                button -> setStyle(1)
        ).bounds(
                this.width / 2 - 105,
                this.height / 2 + 45,
                100, 20
        ).build();
        addRenderableWidget(style1Button);

        style2Button = Button.builder(
                Component.literal("样式2"),
                button -> setStyle(2)
        ).bounds(
                this.width / 2 + 5,
                this.height / 2 + 45,
                100, 20
        ).build();
        addRenderableWidget(style2Button);

        // 初始选中状态
        updateButtonStyles();
    }

    private void saveAndClose() {
        try {
            int newColor = Integer.parseInt(colorInput.getValue(), 16);

            // 发送颜色设置到服务端
            FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
            buf.writeBlockPos(pos);
            buf.writeInt(newColor);
            ClientPlayNetworking.send(LiftFloorMonitorPacket.SET_COLOR, buf);

        } catch (NumberFormatException e) {
            System.out.println("无效颜色代码: " + colorInput.getValue());
        }
        this.onClose();
    }

    private void setStyle(int style) {
        // 发送样式设置到服务端
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeBlockPos(pos);
        buf.writeInt(style);
        ClientPlayNetworking.send(LiftFloorMonitorPacket.SET_ARROW_STYLE, buf);

        // 更新本地缓存和按钮状态
        currentStyle = style;
        updateButtonStyles();
    }

    private void updateButtonStyles() {
        style1Button.active = currentStyle != 1;
        style2Button.active = currentStyle != 2;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        // 绘制标题
        guiGraphics.drawCenteredString(
                this.font,
                this.title,
                this.width / 2,
                40,
                0xFFFFFF
        );

        // 绘制说明文本
        guiGraphics.drawString(
                this.font,
                "十六进制颜色代码 (常用: 白FFFFFF, 红FF0000)",
                this.width / 2 - 100,
                this.height / 2 - 40,
                0xAAAAAA
        );
        guiGraphics.drawString(
                this.font,
                "箭头样式（输入数字）",
                this.width / 2 - 100,
                this.height / 2 + 25,
                0xAAAAAA
        );
    }
}