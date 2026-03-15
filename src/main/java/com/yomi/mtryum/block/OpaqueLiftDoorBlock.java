package com.yomi.mtryum.block;

import com.yomi.mtryum.registry.MtryumItems;
import mtr.block.BlockLiftDoor;
import mtr.mappings.BlockEntityMapper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockState;

public class OpaqueLiftDoorBlock extends BlockLiftDoor {

    @Override
    public BlockEntityMapper createBlockEntity(BlockPos pos, BlockState state) {
        return new OpaqueLiftDoorTileEntity(pos, state);
    }

    @Override
    public Item asItem() {
        return MtryumItems.OPAQUE_LIFT_DOOR_1;
    }

    public static class OpaqueLiftDoorTileEntity extends TileEntityLiftDoor {

        public OpaqueLiftDoorTileEntity(BlockPos pos, BlockState state) {
            super(pos, state);
        }
    }
}