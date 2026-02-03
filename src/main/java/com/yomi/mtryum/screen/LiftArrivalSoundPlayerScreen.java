package com.yomi.mtryum.screen;

import com.yomi.mtryum.block.LiftArrivalSoundPlayerEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntity;

public class LiftArrivalSoundPlayerScreen extends Screen {
    private final BlockPos pos;
    private EditBox soundIndexInput;

    public LiftArrivalSoundPlayerScreen(BlockPos pos) {
        super(Component.translatable("screen.mtryum.lift_arrival_sound_player.title"));
        this.pos = pos;
    }

    @Override
    protected void init() {
        super.init();

        int currentSoundIndex = 1;

        if (minecraft != null && minecraft.level != null) {
            BlockEntity entity = minecraft.level.getBlockEntity(pos);
            if (entity instanceof LiftArrivalSoundPlayerEntity tile) {
                currentSoundIndex = tile.getSoundIndex();
            }
        }

        soundIndexInput = new EditBox(
                this.font,
                this.width / 2 - 100,
                this.height / 2 - 20,
                200, 20,
                Component.translatable("screen.mtryum.lift_arrival_sound_player.input_label")
        );
        soundIndexInput.setMaxLength(1);
        soundIndexInput.setFilter(s -> s.isEmpty() || s.matches("[1-9]"));
        soundIndexInput.setValue(String.valueOf(currentSoundIndex));
        addRenderableWidget(soundIndexInput);

        Button confirmButton = Button.builder(
                Component.translatable("screen.mtryum.lift_arrival_sound_player.confirm"),
                button -> saveAndClose()
        ).bounds(
                this.width / 2 - 50,
                this.height / 2 + 30,
                100, 20
        ).build();
        addRenderableWidget(confirmButton);
    }

    private void saveAndClose() {
        try {
            int soundIndex = Integer.parseInt(soundIndexInput.getValue());
            if (minecraft != null) {
                com.yomi.mtryum.network.SetSoundIndexPacket.send(pos, soundIndex);
            }
        } catch (NumberFormatException e) {
            System.out.println("无效声音索引: " + soundIndexInput.getValue());
        }
        this.onClose();
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(poseStack);
        super.render(poseStack, mouseX, mouseY, partialTick);

        drawCenteredString(
                poseStack,
                this.font,
                this.title,
                this.width / 2,
                40,
                0xFFFFFF
        );

        drawString(
                poseStack,
                this.font,
                Component.translatable("screen.mtryum.lift_arrival_sound_player.desc"),
                this.width / 2 - 100,
                this.height / 2 - 40,
                0xAAAAAA
        );
    }
}