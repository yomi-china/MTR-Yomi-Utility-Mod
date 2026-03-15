package com.yomi.mtryum.block;

import com.yomi.mtryum.registry.MtryumBlockEntities;
import mtr.block.BlockLiftPanelBase;
import mtr.block.IBlock;
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

public class TKClassicLiftButtonsBlock extends AbstractLiftButtonsBlock {

    @Override
    public BlockEntityType<? extends BlockEntityMapper> getBlockEntityType() {
        return MtryumBlockEntities.TK_STYLE_LIFT_BUTTONS_ENTITY;
    }

    @Override
    public BlockEntityMapper createBlockEntity(BlockPos pos, BlockState state) {
        return new TKClassicLiftButtonsBlockEntity(pos, state);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        Direction facing = state.getValue(FACING);
        return switch (facing) {
            case EAST -> Block.box(15.9, 1, 7, 16, 12, 9.5);
            case WEST -> Block.box(0, 1, 7, 0.1, 12, 9.5);
            case SOUTH -> Block.box(7.5, 1, 15.9, 9.5, 12, 16);
            case NORTH -> Block.box(7, 1, 0, 9.5, 12, 0.1);
            default -> Shapes.block();
        };
    }

    @Override
    protected void getServerTicker(Level level, BlockPos pos, BlockState state, BlockLiftPanelBase.TileEntityLiftPanel1Base entity) {
        TKClassicLiftButtonsBlockEntity.serverTick(level, (TKClassicLiftButtonsBlockEntity) entity);
    }

    @Override
    protected void handleSpecialLogic(BlockState state, Level world, BlockPos pos, BlockEntity blockEntity) {
        if (blockEntity instanceof MitsubishiStyleLiftButtonsBlockEntity liftEntity) {
            if (liftEntity.isLegacy() && !liftEntity.isAutoUnlocked()) {
                boolean isLocked = IBlock.getStatePropertySafe(state, UNLOCKED);
                if (!isLocked) {
                    world.setBlockAndUpdate(pos, state.setValue(UNLOCKED, true));
                    liftEntity.markAsAutoUnlocked();
                }
            }
        }
    }

    @Override
    protected InteractionResult handleCallLift(BlockEntity blockEntity, boolean isUpButton) {
        if (blockEntity instanceof TKClassicLiftButtonsBlockEntity) {
            ((TKClassicLiftButtonsBlockEntity) blockEntity).callLift(isUpButton);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }
}