package com.yomi.mtryum.block;

import com.yomi.mtryum.MtryumClient;
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
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class LiftArrivalSoundPlayerBlock extends BlockLiftButtons {

    public LiftArrivalSoundPlayerBlock() {
        super();
        registerDefaultState(defaultBlockState()
                .setValue(FACING, Direction.NORTH)
        );
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
        return type == MtryumBlockEntities.LIFT_ARRIVAL_SOUND_PLAYER_ENTITY ?
                (world.isClientSide ? null : (level1, blockPos, blockState, t) ->
                        LiftArrivalSoundPlayerEntity.LASPTick(level1, blockPos, blockState, (LiftArrivalSoundPlayerEntity) t)) :
                null;
    }

    @Override
    public BlockEntityMapper createBlockEntity(BlockPos pos, BlockState state) {
        return new LiftArrivalSoundPlayerEntity(pos, state);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return defaultBlockState().setValue(FACING, ctx.getHorizontalDirection());
    }

    @Override
    public void appendHoverText(ItemStack stack, BlockGetter world, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(new TranslatableComponent("tooltip.mtryum.lift_arrival_sound_player.line1").setStyle(Style.EMPTY.withColor(ChatFormatting.GRAY)));
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.getItem().getDescriptionId().equals("item.mtr.brush")) {
            if (!world.isClientSide) {
                BlockEntity be = world.getBlockEntity(pos);
                if (be instanceof LiftArrivalSoundPlayerEntity entity) {
                    entity.playArrivalSound();
                }
            }
            return InteractionResult.sidedSuccess(world.isClientSide);
        }
        if (stack.isEmpty() && world.isClientSide && hand == InteractionHand.MAIN_HAND) {
            MtryumClient.scheduleASPScreenOpen(pos);
            return InteractionResult.sidedSuccess(world.isClientSide());
        }
        return super.use(state, world, pos, player, hand, hit);
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {

    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        Direction facing = state.getValue(FACING);
        return switch (facing) {
            case EAST -> Block.box(15, 3, 3, 16, 13, 13); // 东市买骏马
            case WEST -> Block.box(0, 3, 3, 1, 13, 13); // 西市买鞍鞯
            case SOUTH -> Block.box(3, 3, 15, 13, 13, 16); // 南市买辔头
            case NORTH -> Block.box(3, 3, 0, 13, 13, 1); // 北市买长鞭
            default -> Shapes.block();
        };
    }

    @Override
    public void playerWillDestroy(Level world, BlockPos pos, BlockState state, Player player) {

    }

    @Override
    public BlockEntityType<? extends BlockEntityMapper> getType() {
        return MtryumBlockEntities.LIFT_ARRIVAL_SOUND_PLAYER_ENTITY;
    }
}