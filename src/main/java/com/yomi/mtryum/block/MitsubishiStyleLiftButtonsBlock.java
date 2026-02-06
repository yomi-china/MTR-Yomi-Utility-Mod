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

public class MitsubishiStyleLiftButtonsBlock extends AbstractLiftButtonsBlock {

    @Override
    public BlockEntityType<? extends BlockEntityMapper> getBlockEntityType() {
        return MtryumBlockEntities.MITSUBISHI_STYLE_LIFT_BUTTONS_ENTITY;
    }

    @Override
    public BlockEntityMapper createBlockEntity(BlockPos pos, BlockState state) {
        return new MitsubishiStyleLiftButtonsBlockEntity(pos, state);
    }

    @Override
    protected void getServerTicker(Level level, BlockPos pos, BlockState state, BlockLiftPanelBase.TileEntityLiftPanel1Base entity) {
        MitsubishiStyleLiftButtonsBlockEntity.serverTick(level, pos, state, (MitsubishiStyleLiftButtonsBlockEntity) entity);
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
        if (blockEntity instanceof MitsubishiStyleLiftButtonsBlockEntity) {
            ((MitsubishiStyleLiftButtonsBlockEntity) blockEntity).callLift(isUpButton);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }
}