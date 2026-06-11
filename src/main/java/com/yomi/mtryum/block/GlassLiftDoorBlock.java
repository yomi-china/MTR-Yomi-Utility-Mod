package com.yomi.mtryum.block;

import com.yomi.mtryum.registry.MtryumBlockEntities;
import com.yomi.mtryum.registry.MtryumItems;
import mtr.block.BlockPSDAPGDoorBase;
import mtr.mappings.BlockEntityMapper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockState;

public class GlassLiftDoorBlock extends BlockPSDAPGDoorBase {

    @Override
    public BlockEntityMapper createBlockEntity(BlockPos pos, BlockState state) {
        return new GlassLiftDoorTileEntity(pos, state);
    }

    @Override
    public Item asItem() {
        return MtryumItems.GLASS_LIFT_DOOR_1;
    }

    public static class GlassLiftDoorTileEntity extends TileEntityPSDAPGDoorBase {

        public GlassLiftDoorTileEntity(BlockPos pos, BlockState state) {
            super(MtryumBlockEntities.GLASS_LIFT_DOOR_TILE_ENTITY, pos, state);
        }
    }
}