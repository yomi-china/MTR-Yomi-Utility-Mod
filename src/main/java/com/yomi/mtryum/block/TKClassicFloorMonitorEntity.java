package com.yomi.mtryum.block;

import mtr.block.BlockLiftPanelBase;
import mtr.data.Lift;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class TKClassicFloorMonitorEntity extends BlockLiftPanelBase.TileEntityLiftPanel1Base {

    private BlockPos trackPosition;
    private boolean isLocked = false;
    private int textColor = 0xFF0000;
    private int arrowStyle = 1;
    private Lift.LiftDirection liftDirection = Lift.LiftDirection.NONE;

    public TKClassicFloorMonitorEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
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
        if (compoundTag.contains("text_color")) {
            textColor = compoundTag.getInt("text_color");
        }
        if (compoundTag.contains("arrow_style")) {
            arrowStyle = compoundTag.getInt("arrow_style");
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
        compoundTag.putInt("text_color", textColor);
        compoundTag.putInt("arrow_style", arrowStyle);
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

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int color) {
        textColor = color;
        setChanged();

        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
        }
        syncData();
    }

    public BlockPos getTrackPosition(Level world) {
        return trackPosition;
    }

    public int getArrowStyle() {
        return arrowStyle;
    }

    public void setArrowStyle(int style) {
        arrowStyle = Math.max(1, Math.min(2, style));
        setChanged();
        syncData();
    }

    public void updateLiftDirection(Lift.LiftDirection direction) {
        liftDirection = direction;
    }
}