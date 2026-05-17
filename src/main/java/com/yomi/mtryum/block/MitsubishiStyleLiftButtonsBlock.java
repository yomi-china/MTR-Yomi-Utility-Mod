package com.yomi.mtryum.block;

import com.yomi.mtryum.registry.MtryumBlockEntities;
import mtr.block.IBlock;
import mtr.mappings.BlockEntityMapper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;

public class MitsubishiStyleLiftButtonsBlock extends AbstractLiftButtonsBlock {

    private static final VoxelShape SHAPE_SINGLE_NORTH = Block.box(6.5, 1, 0, 9.5, 14, 0.1);
    private static final VoxelShape SHAPE_SINGLE_SOUTH = Block.box(6.5, 1, 15.9, 9.5, 14, 16);
    private static final VoxelShape SHAPE_SINGLE_EAST  = Block.box(15.9, 1, 6.5, 16, 14, 9.5);
    private static final VoxelShape SHAPE_SINGLE_WEST  = Block.box(0, 1, 6.5, 0.1, 14, 9.5);

    private static final VoxelShape SHAPE_DUAL_NORTH = Block.box(5, 1, 0, 11, 14, 0.1);
    private static final VoxelShape SHAPE_DUAL_SOUTH = Block.box(5, 1, 15.9, 11, 14, 16);
    private static final VoxelShape SHAPE_DUAL_EAST  = Block.box(15.9, 1, 5, 16, 14, 11);
    private static final VoxelShape SHAPE_DUAL_WEST  = Block.box(0, 1, 5, 0.1, 14, 11);

    @Override
    public BlockEntityType<? extends BlockEntityMapper> getBlockEntityType() {
        return MtryumBlockEntities.MITSUBISHI_STYLE_LIFT_BUTTONS_ENTITY;
    }

    @Override
    public BlockEntityMapper createBlockEntity(BlockPos pos, BlockState state) {
        return new MitsubishiStyleLiftButtonsBlockEntity(pos, state);
    }

    @Override
    protected VoxelShape getSingleVoxelShape(Direction facing) {
        return switch (facing) {
            case EAST  -> SHAPE_SINGLE_EAST;
            case WEST  -> SHAPE_SINGLE_WEST;
            case SOUTH -> SHAPE_SINGLE_SOUTH;
            default    -> SHAPE_SINGLE_NORTH;
        };
    }

    @Override
    protected VoxelShape getDualVoxelShape(Direction facing) {
        return switch (facing) {
            case EAST  -> SHAPE_DUAL_EAST;
            case WEST  -> SHAPE_DUAL_WEST;
            case SOUTH -> SHAPE_DUAL_SOUTH;
            default    -> SHAPE_DUAL_NORTH;
        };
    }

    @Override
    protected void handleSpecialLogic(BlockState state, Level world, BlockPos pos, BlockEntity blockEntity) {
        if (blockEntity instanceof AbstractLiftButtonsBlockEntity liftEntity) {
            if (liftEntity.isLegacy() && !liftEntity.isAutoUnlocked()) {
                if (!IBlock.getStatePropertySafe(state, UNLOCKED)) {
                    world.setBlockAndUpdate(pos, state.setValue(UNLOCKED, true));
                    liftEntity.markAsAutoUnlocked();
                }
            }
        }
    }
}