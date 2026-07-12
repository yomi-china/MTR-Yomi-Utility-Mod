package com.yomi.mtryum.block;

import mtr.block.BlockLiftButtons;
import mtr.block.BlockLiftTrackFloor;
import mtr.data.Lift;
import mtr.data.RailwayData;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashSet;
import java.util.Set;

public abstract class AbstractLiftButtonsBlockEntity extends BlockLiftButtons.TileEntityLiftButtons {

    private static final String KEY_IS_LEGACY = "is_legacy";
    private static final String KEY_AUTO_UNLOCKED = "auto_unlocked";

    private boolean isLegacy = false;
    private boolean autoUnlocked = false;

    public AbstractLiftButtonsBlockEntity(BlockPos pos, BlockState state) {
        super(pos, state);
    }

    @Override
    public void readCompoundTag(CompoundTag compoundTag) {
        // 旧版兼容
        if (compoundTag.contains("track_floor_pos", Tag.TAG_LONG)) {
            final long data = compoundTag.getLong("track_floor_pos");
            compoundTag.remove("track_floor_pos");
            compoundTag.putLongArray("track_floor_pos", data != 0 ? new long[]{data} : new long[0]);
            isLegacy = true;
        }

        super.readCompoundTag(compoundTag);

        if (compoundTag.contains(KEY_IS_LEGACY)) {
            isLegacy = compoundTag.getBoolean(KEY_IS_LEGACY);
        }
        autoUnlocked = compoundTag.getBoolean(KEY_AUTO_UNLOCKED);
    }

    @Override
    public void writeCompoundTag(CompoundTag compoundTag) {
        super.writeCompoundTag(compoundTag);
        compoundTag.putBoolean(KEY_IS_LEGACY, isLegacy);
        compoundTag.putBoolean(KEY_AUTO_UNLOCKED, autoUnlocked);
    }

    @Override
    public void registerFloor(BlockPos pos, boolean isAdd) {
        super.registerFloor(pos, isAdd);
        updateDualState();
    }

    @Override
    public void tick() {
        if (level != null && !level.isClientSide) {
            forEachTrackPosition(level, null);
            setChanged();
            syncData();
            updateDualState();
        }
    }

    private void updateDualState() {
        if (level == null || level.isClientSide) return;
        final BlockState state = level.getBlockState(worldPosition);
        if (!(state.getBlock() instanceof AbstractLiftButtonsBlock)) return;

        final RailwayData railwayData = RailwayData.getInstance(level);
        if (railwayData == null) return;

        final Set<Lift> uniqueLifts = new HashSet<>();
        forEachTrackPosition(level, (trackPos, tile) -> {
            for (final Lift lift : railwayData.lifts) {
                if (lift.hasFloor(trackPos)) {
                    uniqueLifts.add(lift);
                }
            }
        });

        final boolean dual = uniqueLifts.size() >= 2;
        if (state.getValue(AbstractLiftButtonsBlock.DUAL) != dual) {
            level.setBlockAndUpdate(worldPosition, state.setValue(AbstractLiftButtonsBlock.DUAL, dual));
        }
    }

    // 旧版兼容
    public boolean isLegacy() {
        return isLegacy;
    }

    public boolean isAutoUnlocked() {
        return autoUnlocked;
    }

    public void markAsAutoUnlocked() {
        this.autoUnlocked = true;
        this.isLegacy = false;
        setChanged();
    }
}