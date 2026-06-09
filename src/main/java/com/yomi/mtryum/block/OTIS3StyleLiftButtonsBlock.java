package com.yomi.mtryum.block;

import com.yomi.mtryum.registry.MtryumBlockEntities;
import mtr.mappings.BlockEntityMapper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;

public class OTIS3StyleLiftButtonsBlock extends AbstractLiftButtonsBlock {

    private static final VoxelShape SHAPE_SINGLE = Block.box(6.5, 1, 0, 9.5, 13, 0.1);
    private static final VoxelShape SHAPE_DUAL = Block.box(5, 1, 0, 11, 13, 0.1);

    @Override
    public BlockEntityType<? extends BlockEntityMapper> getBlockEntityType() {
        return MtryumBlockEntities.OTIS3_STYLE_LIFT_BUTTONS_ENTITY;
    }

    @Override
    public BlockEntityMapper createBlockEntity(BlockPos pos, BlockState state) {
        return new OTIS3StyleLiftButtonsBlockEntity(pos, state);
    }

    @Override
    protected VoxelShape getSingleBaseShape() {
        return SHAPE_SINGLE;
    }

    @Override
    protected VoxelShape getDualBaseShape() {
        return SHAPE_DUAL;
    }
}