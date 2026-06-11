package com.yomi.mtryum.block;

import com.yomi.mtryum.registry.MtryumBlockEntities;
import mtr.mappings.BlockEntityMapper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * 具体的电梯到站灯方块。
 * 继承 AbstractLiftArrivalLightBlock，提供默认的形状定义。
 */
public class LiftArrivalLightBlock extends AbstractLiftArrivalLightBlock {

    // ==================== 居中形状 ====================
    private static final VoxelShape CENTER_EAST  = Block.box(15, 3, 3, 16, 13, 13);
    private static final VoxelShape CENTER_WEST  = Block.box(0, 3, 3, 1, 13, 13);
    private static final VoxelShape CENTER_SOUTH = Block.box(3, 3, 15, 13, 13, 16);
    private static final VoxelShape CENTER_NORTH = Block.box(3, 3, 0, 13, 13, 1);

    // ==================== 靠右形状 ====================
    private static final VoxelShape RIGHT_EAST  = Block.box(15, 3, 11, 16, 13, 21);
    private static final VoxelShape RIGHT_WEST  = Block.box(0, 3, -5, 1, 13, 5);
    private static final VoxelShape RIGHT_SOUTH = Block.box(-5, 3, 15, 5, 13, 16);
    private static final VoxelShape RIGHT_NORTH = Block.box(11, 3, 0, 21, 13, 1);

    @Override
    protected VoxelShape getCenterShape(Direction facing) {
        return switch (facing) {
            case EAST  -> CENTER_EAST;
            case WEST  -> CENTER_WEST;
            case SOUTH -> CENTER_SOUTH;
            default    -> CENTER_NORTH;
        };
    }

    @Override
    protected VoxelShape getRightShape(Direction facing) {
        return switch (facing) {
            case EAST  -> RIGHT_EAST;
            case WEST  -> RIGHT_WEST;
            case SOUTH -> RIGHT_SOUTH;
            default    -> RIGHT_NORTH;
        };
    }

    @Override
    public BlockEntityMapper createBlockEntity(BlockPos pos, BlockState state) {
        return new Entity(pos, state);
    }

    /**
     * 具体的 BlockEntity 实现
     */
    public static class Entity extends AbstractEntity {
        public Entity(BlockPos pos, BlockState state) {
            super(MtryumBlockEntities.LIFT_ARRIVAL_LIGHT_BLOCK_ENTITY, pos, state);
        }
    }
}