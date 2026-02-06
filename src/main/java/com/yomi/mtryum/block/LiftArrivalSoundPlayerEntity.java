package com.yomi.mtryum.block;

import com.yomi.mtryum.registry.MtryumBlockEntities;
import com.yomi.mtryum.registry.MtryumSounds;
import mtr.block.BlockLiftPanelBase;
import mtr.data.Lift;
import mtr.data.RailwayData;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LiftArrivalSoundPlayerEntity extends BlockLiftPanelBase.TileEntityLiftPanel1Base {
    private static final Logger LOGGER = LogManager.getLogger("LiftArrivalSoundPlayer");
    private boolean hasPlayed = false;
    private int soundIndex = 1;

    public LiftArrivalSoundPlayerEntity(BlockPos pos, BlockState state) {
        super(MtryumBlockEntities.LIFT_ARRIVAL_SOUND_PLAYER_ENTITY, pos, state, false);
    }

    @Override
    public void readCompoundTag(CompoundTag compoundTag) {
        super.readCompoundTag(compoundTag);
        if (compoundTag.contains("sound_index")) {
            soundIndex = compoundTag.getInt("sound_index");
        }
    }

    @Override
    public void writeCompoundTag(CompoundTag compoundTag) {
        super.writeCompoundTag(compoundTag);
        compoundTag.putInt("sound_index", soundIndex);
    }

    public void playArrivalSound() {
        if (level == null || level.isClientSide) return;
        switch (soundIndex) {
            case 1:
                level.playSound(null, worldPosition, MtryumSounds.LIFT_ARRIVAL_SOUND_1, SoundSource.BLOCKS, 1.0F, 1.0F);
                break;
            case 2:
                level.playSound(null, worldPosition, MtryumSounds.LIFT_ARRIVAL_SOUND_2, SoundSource.BLOCKS, 1.0F, 1.0F);
                break;
            case 3:
                level.playSound(null, worldPosition, MtryumSounds.LIFT_ARRIVAL_SOUND_3, SoundSource.BLOCKS, 1.0F, 1.0F);
                break;
            case 4:
                level.playSound(null, worldPosition, MtryumSounds.LIFT_ARRIVAL_SOUND_4, SoundSource.BLOCKS, 1.0F, 1.0F);
                break;
            case 5:
                level.playSound(null, worldPosition, MtryumSounds.LIFT_ARRIVAL_SOUND_5, SoundSource.BLOCKS, 1.0F, 1.0F);
                break;
            case 6:
                level.playSound(null, worldPosition, MtryumSounds.LIFT_ARRIVAL_SOUND_6, SoundSource.BLOCKS, 1.0F, 1.0F);
                break;
            case 7:
                level.playSound(null, worldPosition, MtryumSounds.LIFT_ARRIVAL_SOUND_7, SoundSource.BLOCKS, 1.0F, 1.0F);
                break;
            case 8:
                level.playSound(null, worldPosition, MtryumSounds.LIFT_ARRIVAL_SOUND_8, SoundSource.BLOCKS, 1.0F, 1.0F);
                break;
            case 9:
                level.playSound(null, worldPosition, MtryumSounds.LIFT_ARRIVAL_SOUND_9, SoundSource.BLOCKS, 1.0F, 1.0F);
                break;
            case 10:
                level.playSound(null, worldPosition, MtryumSounds.LIFT_ARRIVAL_SOUND_10, SoundSource.BLOCKS, 1.0F, 1.0F);
                break;
            default:
                level.playSound(null, worldPosition, MtryumSounds.LIFT_ARRIVAL_SOUND_1, SoundSource.BLOCKS, 1.0F, 1.0F);
                break;
        }
    }

    public static void LASPTick(Level level, BlockPos pos, BlockState state, LiftArrivalSoundPlayerEntity entity) {
        final Level world = entity.getLevel();
        if (world == null || world.isClientSide) return;

        final BlockPos trackPosition = entity.getTrackPosition(world);
        if (trackPosition == null) return;

        final RailwayData railwayData = RailwayData.getInstance(world);
        if (railwayData == null) return;

        Lift.LiftDirection liftDirection = Lift.LiftDirection.NONE;

        boolean shouldPlay = false;
        for (Lift lift : railwayData.lifts) {
            if (lift.hasFloor(trackPosition)) {
                final BlockPos currentFloor = lift.getCurrentFloorBlockPos();
                liftDirection = lift.getLiftDirection();
                if (currentFloor != null && currentFloor.equals(trackPosition) && liftDirection == Lift.LiftDirection.NONE) {
                    shouldPlay = true;
                    break;
                }
            }
        }

        if (shouldPlay) {
            if (!entity.hasPlayed) {
                entity.playArrivalSound();
                entity.hasPlayed = true;
            }
        } else {
            entity.hasPlayed = false;
        }
    }

    public int getSoundIndex() {
        return soundIndex;
    }

    public void setSoundIndex(int index) {
        this.soundIndex = index;
        setChanged();

        if (level != null && !level.isClientSide) {
            setSoundIndexViaCommand(index);
        }
    }

    public void setSoundIndexViaCommand(int index) {
        this.soundIndex = index;
        setChanged();
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
        }
    }
}