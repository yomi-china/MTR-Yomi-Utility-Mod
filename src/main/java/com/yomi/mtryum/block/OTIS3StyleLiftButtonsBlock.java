package com.yomi.mtryum.block;

import com.yomi.mtryum.registry.MtryumBlockEntities;
import mtr.mappings.BlockEntityMapper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;

public class OTIS3StyleLiftButtonsBlock extends AbstractLiftButtonsBlock {

    private static final VoxelShape SHAPE_SINGLE_NORTH = Block.box(6.5, 1, 0, 9.5, 12, 0.1);
    private static final VoxelShape SHAPE_SINGLE_SOUTH = Block.box(6.5, 1, 15.9, 9.5, 12, 16);
    private static final VoxelShape SHAPE_SINGLE_EAST  = Block.box(15.9, 1, 6.5, 16, 12, 9.5);
    private static final VoxelShape SHAPE_SINGLE_WEST  = Block.box(0, 1, 6.5, 0.1, 12, 9.5);

    private static final VoxelShape SHAPE_DUAL_NORTH = Block.box(5, 1, 0, 11, 12, 0.1);
    private static final VoxelShape SHAPE_DUAL_SOUTH = Block.box(5, 1, 15.9, 11, 12, 16);
    private static final VoxelShape SHAPE_DUAL_EAST  = Block.box(15.9, 1, 5, 16, 12, 11);
    private static final VoxelShape SHAPE_DUAL_WEST  = Block.box(0, 1, 5, 0.1, 12, 11);

    @Override
    public BlockEntityType<? extends BlockEntityMapper> getBlockEntityType() {
        return MtryumBlockEntities.OTIS3_STYLE_LIFT_BUTTONS_ENTITY;
    }

    @Override
    public BlockEntityMapper createBlockEntity(BlockPos pos, BlockState state) {
        return new OTIS3StyleLiftButtonsBlockEntity(pos, state);
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
}