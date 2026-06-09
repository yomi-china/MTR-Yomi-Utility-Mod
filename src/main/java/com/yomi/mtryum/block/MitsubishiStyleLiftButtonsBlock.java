package com.yomi.mtryum.block;

import com.yomi.mtryum.registry.MtryumBlockEntities;
import mtr.block.IBlock;
import mtr.mappings.BlockEntityMapper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;

public class MitsubishiStyleLiftButtonsBlock extends AbstractLiftButtonsBlock {

    private static final VoxelShape SHAPE_SINGLE = Block.box(6.5, 1, 0, 9.5, 14, 0.1);
    private static final VoxelShape SHAPE_DUAL = Block.box(5, 1, 0, 11, 14, 0.1);

    @Override
    public BlockEntityType<? extends BlockEntityMapper> getBlockEntityType() {
        return MtryumBlockEntities.MITSUBISHI_STYLE_LIFT_BUTTONS_ENTITY;
    }

    @Override
    public BlockEntityMapper createBlockEntity(BlockPos pos, BlockState state) {
        return new MitsubishiStyleLiftButtonsBlockEntity(pos, state);
    }

    @Override
    protected VoxelShape getSingleBaseShape() {
        return SHAPE_SINGLE;
    }

    @Override
    protected VoxelShape getDualBaseShape() {
        return SHAPE_DUAL;
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