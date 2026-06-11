package com.yomi.mtryum.item;

import com.yomi.mtryum.registry.MtryumBlocks;
import mtr.block.BlockPSDAPGBase;
import mtr.block.IBlock;
import mtr.item.ItemPSDAPGBase;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;

public class ItemOpaqueLiftDoor extends Item implements IBlock {

    public ItemOpaqueLiftDoor(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        final int horizontalBlocks = 2;

        if (ItemPSDAPGBase.blocksNotReplaceable(context, horizontalBlocks, 2, MtryumBlocks.OPAQUE_LIFT_DOOR_1)) {
            return InteractionResult.FAIL;
        }

        final Level world = context.getLevel();
        final Direction playerFacing = context.getHorizontalDirection();
        final BlockPos pos = context.getClickedPos().relative(context.getClickedFace());

        for (int x = 0; x < horizontalBlocks; x++) {
            final BlockPos newPos = pos.relative(playerFacing.getClockWise(), x);
            for (int y = 0; y < 2; y++) {
                final BlockState state = MtryumBlocks.OPAQUE_LIFT_DOOR_1
                        .defaultBlockState()
                        .setValue(BlockPSDAPGBase.FACING, playerFacing)
                        .setValue(HALF, y == 1 ? DoubleBlockHalf.UPPER : DoubleBlockHalf.LOWER)
                        .setValue(SIDE, x == 0 ? EnumSide.LEFT : EnumSide.RIGHT);
                world.setBlockAndUpdate(newPos.above(y), state);
            }
        }

        context.getItemInHand().shrink(1);
        return InteractionResult.SUCCESS;
    }
}