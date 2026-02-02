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

public class MitsubishiStyleLiftButtonsBlockEntity extends BlockLiftPanelBase.TileEntityLiftPanel1Base {

    private static final String KEY_UP_PRESSED = "up_pressed";
    private static final String KEY_DOWN_PRESSED = "down_pressed";
    private boolean upButtonPressed;
    private boolean downButtonPressed;
    private Lift.LiftDirection liftDirection = Lift.LiftDirection.NONE;
    public static final Logger LOGGER = LoggerFactory.getLogger("MSLB");

    public MitsubishiStyleLiftButtonsBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state, false);
    }

    public MitsubishiStyleLiftButtonsBlockEntity(BlockPos pos, BlockState state) {
        this(MtryumBlockEntities.MITSUBISHI_STYLE_LIFT_BUTTONS_ENTITY, pos, state);
    }

    @Override
    public void readCompoundTag(CompoundTag compoundTag) {
        super.readCompoundTag(compoundTag);
        upButtonPressed = compoundTag.getBoolean(KEY_UP_PRESSED);
        downButtonPressed = compoundTag.getBoolean(KEY_DOWN_PRESSED);
    }

    @Override
    public void writeCompoundTag(CompoundTag compoundTag) {
        super.writeCompoundTag(compoundTag);
        compoundTag.putBoolean(KEY_UP_PRESSED, upButtonPressed);
        compoundTag.putBoolean(KEY_DOWN_PRESSED, downButtonPressed);
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
                    LOGGER.error("MTRYUM Call lift:RailwayData is null");
                }
            }
        }
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, MitsubishiStyleLiftButtonsBlockEntity entity) {
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
            LOGGER.error("MTRYUM liftArrived:Level is null");
            return;
        }

        if (!level.isClientSide) {
            upButtonPressed = false;
            downButtonPressed = false;
            setChanged();
            syncData();
        }
    }

    public void updateButtonStates(boolean upPressed, boolean downPressed) {
        this.upButtonPressed = upPressed;
        this.downButtonPressed = downPressed;
    }

    public boolean isUpButtonPressed() {
        return upButtonPressed;
    }

    public boolean isDownButtonPressed() {
        return downButtonPressed;
    }

    public void updateLiftDirection(Lift.LiftDirection direction) {
        liftDirection = direction;
    }

    public Lift.LiftDirection getLiftDirection() {
        return liftDirection;
    }
}