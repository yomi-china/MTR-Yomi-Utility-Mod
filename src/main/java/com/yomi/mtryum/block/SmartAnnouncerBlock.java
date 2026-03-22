package com.yomi.mtryum.block;

import com.yomi.mtryum.registry.MtryumBlockEntities;
import mtr.block.BlockTrainAnnouncer;
import mtr.mappings.BlockEntityMapper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class SmartAnnouncerBlock extends BlockTrainAnnouncer {

    @Override
    public BlockEntityMapper createBlockEntity(BlockPos pos, BlockState state) {
        return new SmartAnnouncerEntity(pos, state);
    }

    @Override
    public BlockEntityType<? extends BlockEntityMapper> getType() {
        return MtryumBlockEntities.SMART_ANNOUNCER_TILE_ENTITY;
    }
}