package com.yomi.mtryum.block;

import com.yomi.mtryum.registry.MtryumBlockEntities;
import mtr.block.BlockLiftPanelBase;
import mtr.data.Lift;
import mtr.data.RailwayData;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TKClassicLiftButtonsBlockEntity extends BlockLiftPanelBase.TileEntityLiftPanel1Base {

    private static final String KEY_UP_PRESSED = "up_pressed";
    private static final String KEY_DOWN_PRESSED = "down_pressed";
    // 同样烦人的旧版兼容
    private static final String KEY_IS_LEGACY = "is_legacy";
    private static final String KEY_AUTO_UNLOCKED = "auto_unlocked";

    private boolean upButtonPressed;
    private boolean downButtonPressed;
    private boolean isLegacy = false; // 是否为旧版本方块
    private boolean autoUnlocked = false; // 是否已经自动解锁过
    public static final Logger LOGGER = LoggerFactory.getLogger("TKStyleLiftButtons");

    public TKClassicLiftButtonsBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state, false);
    }

    public TKClassicLiftButtonsBlockEntity(BlockPos pos, BlockState state) {
        this(MtryumBlockEntities.TK_STYLE_LIFT_BUTTONS_ENTITY, pos, state);
    }

    @Override
    public void readCompoundTag(CompoundTag compoundTag) {
        super.readCompoundTag(compoundTag);
        upButtonPressed = compoundTag.getBoolean(KEY_UP_PRESSED);
        downButtonPressed = compoundTag.getBoolean(KEY_DOWN_PRESSED);

        isLegacy = !compoundTag.contains(KEY_IS_LEGACY) || compoundTag.getBoolean(KEY_IS_LEGACY);
        autoUnlocked = compoundTag.getBoolean(KEY_AUTO_UNLOCKED);
    }

    @Override
    public void writeCompoundTag(CompoundTag compoundTag) {
        super.writeCompoundTag(compoundTag);
        compoundTag.putBoolean(KEY_UP_PRESSED, upButtonPressed);
        compoundTag.putBoolean(KEY_DOWN_PRESSED, downButtonPressed);

        compoundTag.putBoolean(KEY_IS_LEGACY, isLegacy);
        compoundTag.putBoolean(KEY_AUTO_UNLOCKED, autoUnlocked);
    }

    public void callLift(boolean callUp) {
        if (level != null && !level.isClientSide) {
            BlockPos trackPos = getTrackPosition(level);

            if (trackPos != null) {
                final RailwayData railwayData = RailwayData.getInstance(level);

                if (railwayData != null) {
                    for (Lift lift : railwayData.lifts) {

                        if (lift.hasFloor(trackPos)) {
                            lift.pressButton(trackPos.getY());

                            if (callUp) {
                                upButtonPressed = true;
                            } else {
                                downButtonPressed = true;
                            }
                            setChanged();
                            syncData();
                            return;
                        }
                    }
                } else {
                    LOGGER.error("Call lift:RailwayData is null");
                }
            }
        }
    }

    public static void serverTick(Level level, TKClassicLiftButtonsBlockEntity entity) {
        if (level != null && !level.isClientSide) {
            BlockPos trackPos = entity.getTrackPosition(level);
            if (trackPos != null) {
                RailwayData railwayData = RailwayData.getInstance(level);
                if (railwayData != null) {
                    for (Lift lift : railwayData.lifts) {
                        if (lift.hasFloor(trackPos)) {
                            BlockPos currentFloor = lift.getCurrentFloorBlockPos();
                            if (currentFloor != null && currentFloor.equals(trackPos)) {
                                entity.liftArrived();
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    public void liftArrived() {
        if (level == null) {
            LOGGER.error("liftArrived:Level is null");
            return;
        }
        upButtonPressed = false;
        downButtonPressed = false;
        setChanged();
        if (!level.isClientSide()) {
            syncData();
        }
    }

    public boolean isUpButtonPressed() {
        return upButtonPressed;
    }

    public boolean isDownButtonPressed() {
        return downButtonPressed;
    }

    public void updateLiftDirection() {
    }

}