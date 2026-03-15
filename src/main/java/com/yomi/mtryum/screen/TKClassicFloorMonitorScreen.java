package com.yomi.mtryum.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import com.yomi.mtryum.block.TKClassicFloorMonitorEntity;
import com.yomi.mtryum.network.LiftFloorMonitorPacket;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntity;

public class TKClassicFloorMonitorScreen extends Screen {
    private final BlockPos pos;
    private EditBox colorInput;
    private Button style1Button;
    private Button style2Button;
    private Button lockButton;
    private int currentStyle = 1;
    private boolean isLocked = false;

    public TKClassicFloorMonitorScreen(BlockPos pos) {
        super(Component.translatable("screen.mtryum.set_style_title"));
        this.pos = pos;
    }

    @Override
    protected void init() {
        super.init();

        int currentColor = 0xFFFFFF;
        if (minecraft != null && minecraft.level != null) {
            BlockEntity entity = minecraft.level.getBlockEntity(pos);
            if (entity instanceof TKClassicFloorMonitorEntity tile) {
                currentColor = tile.getTextColor();
                currentStyle = tile.getArrowStyle();
                isLocked = tile.isLocked();
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
        Button confirmButton = new Button(
                this.width / 2 - 50,
                this.height / 2 + 80,
                100, 20,
                Component.literal("确认"),
                button -> saveAndClose()
        );
        addRenderableWidget(confirmButton);

        style1Button = new Button(
                this.width / 2 - 105,
                this.height / 2 + 45,
                100, 20,
                Component.literal("样式1"),
                button -> setStyle(1)
        );
        addRenderableWidget(style1Button);

        style2Button = new Button(
                this.width / 2 + 5,
                this.height / 2 + 45,
                100, 20,
                Component.literal("样式2"),
                button -> setStyle(2)
        );
        addRenderableWidget(style2Button);

        // 锁定按钮
        lockButton = new Button(
                this.width / 2 - 50,
                this.height / 2 + 110,
                100, 20,
                Component.literal(isLocked ? "已锁定" : "已解锁"),
                button -> toggleLock()
        );
        addRenderableWidget(lockButton);

        updateButtonStyles();
    }

    private void saveAndClose() {
        try {
            int newColor = Integer.parseInt(colorInput.getValue(), 16);

            FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
            buf.writeBlockPos(pos);
            buf.writeInt(newColor);
            ClientPlayNetworking.send(LiftFloorMonitorPacket.SET_COLOR, buf);

        } catch (NumberFormatException e) {
            System.out.println("Invalid color code:" + colorInput.getValue());
        }
        this.onClose();
    }

    private void setStyle(int style) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeBlockPos(pos);
        buf.writeInt(style);
        ClientPlayNetworking.send(LiftFloorMonitorPacket.SET_ARROW_STYLE, buf);

        currentStyle = style;
        updateButtonStyles();
    }

    private void toggleLock() {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeBlockPos(pos);
        ClientPlayNetworking.send(LiftFloorMonitorPacket.TOGGLE_LOCK, buf);

        isLocked = !isLocked;
        lockButton.setMessage(Component.literal(isLocked ? "已锁定" : "已解锁"));
    }

    private void updateButtonStyles() {
        style1Button.active = currentStyle != 1;
        style2Button.active = currentStyle != 2;
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(poseStack);
        super.render(poseStack, mouseX, mouseY, partialTick);

        // 绘制标题
        drawCenteredString(
                poseStack,
                this.font,
                this.title,
                this.width / 2,
                40,
                0xFFFFFF
        );

        // 绘制说明文本
        drawString(
                poseStack,
                this.font,
                "颜色代码 (常用: 白FFFFFF, 红FF0000)",
                this.width / 2 - 100,
                this.height / 2 - 40,
                0xAAAAAA
        );
        drawString(
                poseStack,
                this.font,
                "箭头样式（输入数字）",
                this.width / 2 - 100,
                this.height / 2 + 25,
                0xAAAAAA
        );
    }
}