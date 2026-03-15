package com.yomi.mtryum.screen;

import com.yomi.mtryum.block.LiftArrivalSoundPlayerEntity;
import com.yomi.mtryum.registry.MtryumSounds;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.entity.BlockEntity;

public class LiftArrivalSoundPlayerScreen extends Screen {
    private final BlockPos pos;
    private EditBox soundIndexInput;
    private int currentSoundIndex = 1;
    private Button minusButton;
    private Button plusButton;

    public LiftArrivalSoundPlayerScreen(BlockPos pos) {
        super(new TranslatableComponent("screen.mtryum.lift_arrival_sound_player.title"));
        this.pos = pos;
    }

    @Override
    protected void init() {
        super.init();

        if (minecraft != null && minecraft.level != null) {
            BlockEntity entity = minecraft.level.getBlockEntity(pos);
            if (entity instanceof LiftArrivalSoundPlayerEntity tile) {
                currentSoundIndex = tile.getSoundIndex();
            }
        }

        int centerX = this.width / 2;
        int centerY = this.height / 2;

        int buttonWidth = 25;
        int inputWidth = 50;
        int spacing = 10;

        int totalWidth = buttonWidth * 2 + inputWidth + spacing * 2;
        int startX = centerX - totalWidth / 2;

        minusButton = new Button(
                startX,
                centerY - 30,
                buttonWidth, 20,
                new TextComponent("-"),
                button -> decrementSoundIndex()
        );
        addRenderableWidget(minusButton);

        soundIndexInput = new EditBox(
                this.font,
                startX + buttonWidth + spacing,
                centerY - 30,
                inputWidth, 20,
                new TranslatableComponent("screen.mtryum.lift_arrival_sound_player.input_label")
        );
        soundIndexInput.setMaxLength(2);
        soundIndexInput.setFilter(s -> {
            if (s.isEmpty()) return true;
            try {
                int value = Integer.parseInt(s);
                return value >= 1 && value <= 10;
            } catch (NumberFormatException e) {
                return false;
            }
        });
        soundIndexInput.setValue(String.valueOf(currentSoundIndex));
        soundIndexInput.setResponder(text -> {
            updateButtonsState();
        });
        addRenderableWidget(soundIndexInput);

        plusButton = new Button(
                startX + buttonWidth + spacing + inputWidth + spacing,
                centerY - 30,
                buttonWidth, 20,
                new TextComponent("+"),
                button -> incrementSoundIndex()
        );
        addRenderableWidget(plusButton);

        updateButtonsState();

        // 1.3.0版本将试听功能移到了这里，解决了玩家误触的问题：）
        Button previewButton = new Button(
                centerX - 105,
                centerY + 10,
                100, 20,
                new TranslatableComponent("screen.mtryum.lift_arrival_sound_player.preview"),
                button -> previewSound()
        );
        addRenderableWidget(previewButton);

        Button confirmButton = new Button(
                centerX + 5,
                centerY + 10,
                100, 20,
                new TranslatableComponent("screen.mtryum.lift_arrival_sound_player.confirm"),
                button -> saveAndClose()
        );
        addRenderableWidget(confirmButton);
    }

    private void decrementSoundIndex() {
        try {
            int currentValue = Integer.parseInt(soundIndexInput.getValue());
            if (currentValue > 1) {
                soundIndexInput.setValue(String.valueOf(currentValue - 1));
                updateButtonsState();
            }
        } catch (NumberFormatException e) {
            soundIndexInput.setValue("1");
            updateButtonsState();
        }
    }

    private void incrementSoundIndex() {
        try {
            int currentValue = Integer.parseInt(soundIndexInput.getValue());
            if (currentValue < 10) {
                soundIndexInput.setValue(String.valueOf(currentValue + 1));
                updateButtonsState();
            }
        } catch (NumberFormatException e) {
            soundIndexInput.setValue("1");
            updateButtonsState();
        }
    }

    private void updateButtonsState() {
        try {
            int currentValue = Integer.parseInt(soundIndexInput.getValue());
            minusButton.active = currentValue > 1;
            plusButton.active = currentValue < 10;
        } catch (NumberFormatException e) {
            // 输入无效时禁用两个按钮
            minusButton.active = false;
            plusButton.active = false;
        }
    }

    private void previewSound() {
        try {
            int soundIndex = Integer.parseInt(soundIndexInput.getValue());
            if (minecraft != null && minecraft.level != null && soundIndex >= 1 && soundIndex <= 10) {
                switch (soundIndex) {
                    case 1 -> minecraft.level.playLocalSound(pos, MtryumSounds.LIFT_ARRIVAL_SOUND_1, SoundSource.BLOCKS, 1.0F, 1.0F, false);
                    case 2 -> minecraft.level.playLocalSound(pos, MtryumSounds.LIFT_ARRIVAL_SOUND_2, SoundSource.BLOCKS, 1.0F, 1.0F, false);
                    case 3 -> minecraft.level.playLocalSound(pos, MtryumSounds.LIFT_ARRIVAL_SOUND_3, SoundSource.BLOCKS, 1.0F, 1.0F, false);
                    case 4 -> minecraft.level.playLocalSound(pos, MtryumSounds.LIFT_ARRIVAL_SOUND_4, SoundSource.BLOCKS, 1.0F, 1.0F, false);
                    case 5 -> minecraft.level.playLocalSound(pos, MtryumSounds.LIFT_ARRIVAL_SOUND_5, SoundSource.BLOCKS, 1.0F, 1.0F, false);
                    case 6 -> minecraft.level.playLocalSound(pos, MtryumSounds.LIFT_ARRIVAL_SOUND_6, SoundSource.BLOCKS, 1.0F, 1.0F, false);
                    case 7 -> minecraft.level.playLocalSound(pos, MtryumSounds.LIFT_ARRIVAL_SOUND_7, SoundSource.BLOCKS, 1.0F, 1.0F, false);
                    case 8 -> minecraft.level.playLocalSound(pos, MtryumSounds.LIFT_ARRIVAL_SOUND_8, SoundSource.BLOCKS, 1.0F, 1.0F, false);
                    case 9 -> minecraft.level.playLocalSound(pos, MtryumSounds.LIFT_ARRIVAL_SOUND_9, SoundSource.BLOCKS, 1.0F, 1.0F, false);
                    case 10 -> minecraft.level.playLocalSound(pos, MtryumSounds.LIFT_ARRIVAL_SOUND_10, SoundSource.BLOCKS, 1.0F, 1.0F, false);
                }
            }
        } catch (NumberFormatException ignored) {

        }
    }

    private void saveAndClose() {
        try {
            int soundIndex = Integer.parseInt(soundIndexInput.getValue());
            if (soundIndex >= 1 && soundIndex <= 10 && minecraft != null) {
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
                new TranslatableComponent("screen.mtryum.lift_arrival_sound_player.desc"),
                this.width / 2 - 100,
                this.height / 2 - 40,
                0xAAAAAA
        );
    }
}