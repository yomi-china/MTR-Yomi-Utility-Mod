package com.yomi.mtryum.block;

import com.yomi.mtryum.MtryumClient;
import com.yomi.mtryum.registry.MtryumBlockEntities;
import mtr.block.BlockLiftButtons;
import mtr.mappings.BlockEntityMapper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.List;

public class MLFMBlock extends BlockLiftButtons {

    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    private static final ResourceLocation BRUSH_ITEM_ID = new ResourceLocation("mtr", "brush");

    private static final VoxelShape NORTH = Block.box(3, 8, 0, 13, 12, 1);
    private static final VoxelShape SOUTH = Block.box(3, 8, 15, 13, 12, 16);
    private static final VoxelShape EAST = Block.box(15, 8, 3, 16, 12, 13);
    private static final VoxelShape WEST = Block.box(0, 8, 3, 1, 12, 13);

    public MLFMBlock() {
        super();
        registerDefaultState(defaultBlockState()
                .setValue(FACING, Direction.NORTH)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockEntityMapper createBlockEntity(BlockPos pos, BlockState state) {
        return new MLFMEntity(MtryumBlockEntities.MLFM_ENTITY, pos, state);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return defaultBlockState().setValue(FACING, ctx.getHorizontalDirection());
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        final Direction facing = state.getValue(FACING);
        return switch (facing) {
            case EAST -> EAST;
            case WEST -> WEST;
            case SOUTH -> SOUTH;
            default -> NORTH;
        };
    }

    @Override
    public void appendHoverText(ItemStack stack, BlockGetter world, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(new TranslatableComponent("tooltip.mtryum.floor_monitor.line1").setStyle(Style.EMPTY.withColor(ChatFormatting.GRAY)));
        tooltip.add(new TranslatableComponent("tooltip.mtryum.floor_monitor.line2").setStyle(Style.EMPTY.withColor(ChatFormatting.GRAY)));
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack stack = player.getItemInHand(hand);
        final Item item = stack.getItem();

        // 手持刷子
        if (Registry.ITEM.getKey(item).equals(BRUSH_ITEM_ID)) {
            if (!world.isClientSide()) {
                BlockEntity entity = world.getBlockEntity(pos);
                if (entity instanceof MLFMEntity tile) {
                    // 切换锁定状态
                    tile.toggleLock();
                    world.sendBlockUpdated(pos, state, state, 3);
                    player.displayClientMessage(
                            new TranslatableComponent(tile.isLocked() ? "msg.mtryum.screen_locked" : "msg.mtryum.screen_unlocked"),
                            true
                    );
                }
            }
            return InteractionResult.sidedSuccess(world.isClientSide());
        }
        // 空手
        else if (stack.isEmpty() && world.isClientSide && hand == InteractionHand.MAIN_HAND) {
            MtryumClient.scheduleFMScreenOpen(pos);
            return InteractionResult.sidedSuccess(world.isClientSide());
        }
        return super.use(state, world, pos, player, hand, hit);
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {

    }

    @Override
    public void playerWillDestroy(Level world, BlockPos pos, BlockState state, Player player) {

    }


}