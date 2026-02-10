package com.yomi.mtryum.block;

import com.yomi.mtryum.registry.MtryumBlockEntities;
import mtr.block.BlockLiftPanelBase;
import mtr.mappings.BlockEntityMapper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class OTIS3StyleLiftButtonsBlock extends AbstractLiftButtonsBlock {

    @Override
    public BlockEntityType<? extends BlockEntityMapper> getBlockEntityType() {
        return MtryumBlockEntities.OTIS3_STYLE_LIFT_BUTTONS_ENTITY;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        Direction facing = state.getValue(FACING);
        return switch (facing) {
            case EAST -> Block.box(15.9, 1, 6.5, 16, 12, 9.5);
            case WEST -> Block.box(0, 1, 6.5, 0.1, 12, 9.5);
            case SOUTH -> Block.box(6.5, 1, 15.9, 9.5, 12, 16);
            case NORTH -> Block.box(6.5, 1, 0, 9.5, 12, 0.1);
            default -> Shapes.block();
        };
    }

    @Override
    public BlockEntityMapper createBlockEntity(BlockPos pos, BlockState state) {
        return new OTIS3StyleLiftButtonsBlockEntity(pos, state);
    }

    @Override
    protected void getServerTicker(Level level, BlockPos pos, BlockState state, BlockLiftPanelBase.TileEntityLiftPanel1Base entity) {
        OTIS3StyleLiftButtonsBlockEntity.serverTick(level, (OTIS3StyleLiftButtonsBlockEntity) entity);
    }

    @Override
    protected InteractionResult handleCallLift(BlockEntity blockEntity, boolean isUpButton) {
        if (blockEntity instanceof OTIS3StyleLiftButtonsBlockEntity) {
            ((OTIS3StyleLiftButtonsBlockEntity) blockEntity).callLift(isUpButton);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }
}