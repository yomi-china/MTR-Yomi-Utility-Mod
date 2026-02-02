package com.yomi.mtryum.block;

import com.yomi.mtryum.registry.MtryumBlockEntities;
import mtr.block.BlockLiftButtons;
import mtr.mappings.BlockEntityMapper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MitsubishiStyleLiftButtonsBlock extends BlockLiftButtons {

    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

    public MitsubishiStyleLiftButtonsBlock() {
        super();
        registerDefaultState(defaultBlockState()
                .setValue(FACING, Direction.NORTH)
        );
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
        return type == MtryumBlockEntities.MITSUBISHI_STYLE_LIFT_BUTTONS_ENTITY ?
                (world.isClientSide ? null : (level1, blockPos, blockState, t) ->
                        MitsubishiStyleLiftButtonsBlockEntity.serverTick(level1, blockPos, blockState, (MitsubishiStyleLiftButtonsBlockEntity) t)) :
                null;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {

    }

    @Override
    public void playerWillDestroy(Level world, BlockPos pos, BlockState state, Player player) {

    }

    @Override
    public void appendHoverText(ItemStack stack, BlockGetter world, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(new TranslatableComponent("tooltip.mtryum.lift_arrival_light.line1").setStyle(Style.EMPTY.withColor(ChatFormatting.GRAY)));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        Direction facing = state.getValue(FACING);
        return switch (facing) {
            case EAST -> Block.box(15.9, 1, 6.5, 16, 14, 9.5);
            case WEST -> Block.box(0, 1, 6.5, 0.1, 14, 9.5);
            case SOUTH -> Block.box(6.5, 1, 15.9, 9.5, 14, 16);
            case NORTH -> Block.box(6.5, 1, 0, 9.5, 14, 0.1);
            default -> Shapes.block();
        };
    }

    @Override
    public BlockEntityMapper createBlockEntity(BlockPos pos, BlockState state) {
        return new MitsubishiStyleLiftButtonsBlockEntity(pos, state);
    }

    @Override
    public BlockEntityType<? extends BlockEntityMapper> getType() {
        return MtryumBlockEntities.MITSUBISHI_STYLE_LIFT_BUTTONS_ENTITY;
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (world.isClientSide) return InteractionResult.SUCCESS;

        double relativeY = hit.getLocation().y - pos.getY();
        boolean isUpButton = relativeY > 0.3;

        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof MitsubishiStyleLiftButtonsBlockEntity) {
            ((MitsubishiStyleLiftButtonsBlockEntity) blockEntity).callLift(isUpButton);
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }
}