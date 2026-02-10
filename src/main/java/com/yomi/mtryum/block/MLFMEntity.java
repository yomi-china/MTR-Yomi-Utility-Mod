package com.yomi.mtryum.block;

import mtr.block.BlockLiftPanelBase;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class MLFMEntity extends BlockLiftPanelBase.TileEntityLiftPanel1Base {

    private BlockPos trackPosition;
    private boolean isLocked = false;

    public MLFMEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state, false);
    }

    @Override
    public void readCompoundTag(CompoundTag compoundTag) {
        super.readCompoundTag(compoundTag);
        if (compoundTag.contains("track_floor_pos")) {
            trackPosition = BlockPos.of(compoundTag.getLong("track_floor_pos"));
        } else {
            trackPosition = null;
        }
        if (compoundTag.contains("is_locked")) {
            isLocked = compoundTag.getBoolean("is_locked");
        }
    }

    @Override
    public void writeCompoundTag(CompoundTag compoundTag) {
        super.writeCompoundTag(compoundTag);
        if (trackPosition != null) {
            compoundTag.putLong("track_floor_pos", trackPosition.asLong());
        }
        compoundTag.putBoolean("is_locked", isLocked);
    }

    @Override
    public void registerFloor(BlockPos pos, boolean isAdd) {
        trackPosition = isAdd ? pos : null;
        setChanged();
        syncData();
    }

    public void toggleLock() {
        isLocked = !isLocked;
        setChanged();
        syncData();
    }

    public boolean isLocked() {
        return isLocked;
    }


    public BlockPos getTrackPosition(Level world) {
        return trackPosition;
    }

    public void updateLiftDirection() {
    }
}