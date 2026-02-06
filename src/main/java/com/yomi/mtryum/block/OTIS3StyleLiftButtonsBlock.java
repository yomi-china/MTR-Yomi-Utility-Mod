package com.yomi.mtryum.block;

import com.yomi.mtryum.registry.MtryumBlockEntities;
import mtr.block.BlockLiftPanelBase;
import mtr.block.IBlock;
import mtr.mappings.BlockEntityMapper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class OTIS3StyleLiftButtonsBlock extends AbstractLiftButtonsBlock {

    @Override
    public BlockEntityType<? extends BlockEntityMapper> getBlockEntityType() {
        return MtryumBlockEntities.OTIS3_STYLE_LIFT_BUTTONS_ENTITY;
    }

    @Override
    public BlockEntityMapper createBlockEntity(BlockPos pos, BlockState state) {
        return new OTIS3StyleLiftButtonsBlockEntity(pos, state);
    }

    @Override
    protected void getServerTicker(Level level, BlockPos pos, BlockState state, BlockLiftPanelBase.TileEntityLiftPanel1Base entity) {
        OTIS3StyleLiftButtonsBlockEntity.serverTick(level, pos, state, (OTIS3StyleLiftButtonsBlockEntity) entity);
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