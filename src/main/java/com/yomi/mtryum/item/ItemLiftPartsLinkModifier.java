package com.yomi.mtryum.item;

import com.yomi.mtryum.block.AbstractLiftButtonsBlock;
import com.yomi.mtryum.block.AbstractLiftButtonsBlockEntity;
import mtr.CreativeModeTabs;
import mtr.block.BlockLiftTrackFloor;
import mtr.item.ItemBlockClickingBase;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class ItemLiftPartsLinkModifier extends ItemBlockClickingBase {

    private final boolean isConnector;

    public ItemLiftPartsLinkModifier(boolean isConnector) {
        super(CreativeModeTabs.ESCALATORS_LIFTS, properties -> properties.stacksTo(1));
        this.isConnector = isConnector;
    }

    @Override
    protected void onStartClick(UseOnContext context, CompoundTag compoundTag) {

    }

    @Override
    protected void onEndClick(UseOnContext context, BlockPos posEnd, CompoundTag compoundTag) {
        final Level world = context.getLevel();
        final BlockPos posStart = context.getClickedPos();
        final BlockState stateStart = world.getBlockState(posStart);
        final BlockState stateEnd = world.getBlockState(posEnd);
        final Block blockStart = stateStart.getBlock();
        final Block blockEnd = stateEnd.getBlock();

        final boolean startIsFloor = blockStart instanceof BlockLiftTrackFloor;
        final boolean endIsFloor = blockEnd instanceof BlockLiftTrackFloor;
        final boolean startIsButtons = blockStart instanceof AbstractLiftButtonsBlock;
        final boolean endIsButtons = blockEnd instanceof AbstractLiftButtonsBlock;

        if ((startIsFloor && endIsButtons) || (startIsButtons && endIsFloor)) {
            final BlockPos posFloor = startIsFloor ? posStart : posEnd;
            final BlockPos posButtons = startIsFloor ? posEnd : posStart;

            final BlockEntity blockEntity = world.getBlockEntity(posButtons);
            if (blockEntity instanceof AbstractLiftButtonsBlockEntity liftEntity) {
                liftEntity.registerFloor(posFloor, isConnector);
            }
        }
    }

    @Override
    protected boolean clickCondition(UseOnContext context) {
        final Block block = context.getLevel().getBlockState(context.getClickedPos()).getBlock();
        return block instanceof BlockLiftTrackFloor || block instanceof AbstractLiftButtonsBlock;
    }
}