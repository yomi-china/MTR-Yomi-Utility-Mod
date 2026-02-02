package com.yomi.mtryum.block;

import com.yomi.mtryum.registry.MtryumBlockEntities;
import mtr.block.BlockLiftPanelBase;
import mtr.mappings.BlockEntityMapper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
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
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.List;

public class LiftArrivalLightBlock extends BlockLiftPanelBase {

    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final EnumProperty<Position> POSITION = EnumProperty.create("position", Position.class);
    public static final BooleanProperty LIT = BooleanProperty.create("lit");
    private static final ResourceLocation BRUSH_ITEM_ID = new ResourceLocation("mtr", "brush");

    // 居中状态
    private static final VoxelShape CENTER_EAST = Block.box(15, 3, 3, 16, 13, 13);
    private static final VoxelShape CENTER_WEST = Block.box(0, 3, 3, 1, 13, 13);
    private static final VoxelShape CENTER_SOUTH = Block.box(3, 3, 15, 13, 13, 16);
    private static final VoxelShape CENTER_NORTH = Block.box(3, 3, 0, 13, 13, 1);

    // 靠右状态
    private static final VoxelShape RIGHT_EAST = Block.box(15, 3, 11, 16, 13, 21);
    private static final VoxelShape RIGHT_WEST = Block.box(0, 3, -5, 1, 13, 5);
    private static final VoxelShape RIGHT_SOUTH = Block.box(-5, 3, 15, 5, 13, 16);
    private static final VoxelShape RIGHT_NORTH = Block.box(11, 3, 0, 21, 13, 1);

    public LiftArrivalLightBlock() {
        super(false, true);
        registerDefaultState(defaultBlockState()
                .setValue(FACING, Direction.NORTH)
                .setValue(POSITION, Position.CENTER)
                .setValue(LIT, false)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, POSITION, LIT);
    }

    @Override
    public void playerWillDestroy(Level world, BlockPos pos, BlockState state, Player player) {

    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return defaultBlockState().setValue(FACING, ctx.getHorizontalDirection());
    }

    @Override
    public void appendHoverText(ItemStack stack, BlockGetter world, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(new TranslatableComponent("tooltip.mtryum.lift_arrival_light.line1").setStyle(Style.EMPTY.withColor(ChatFormatting.GRAY)));
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        final ItemStack stack = player.getItemInHand(hand);
        if (Registry.ITEM.getKey(stack.getItem()).equals(BRUSH_ITEM_ID)) {
            final Position currentPosition = state.getValue(POSITION);
            world.setBlockAndUpdate(pos, state.setValue(POSITION, currentPosition == Position.CENTER ? Position.RIGHT : Position.CENTER));
            return InteractionResult.SUCCESS;
        }
        return super.use(state, world, pos, player, hand, hit);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        final Direction facing = state.getValue(FACING);
        final Position position = state.getValue(POSITION);

        if (position == Position.CENTER) {
            return switch (facing) {
                case EAST -> CENTER_EAST;
                case WEST -> CENTER_WEST;
                case SOUTH -> CENTER_SOUTH;
                default -> CENTER_NORTH;
            };
        } else {
            return switch (facing) {
                case EAST -> RIGHT_EAST;
                case WEST -> RIGHT_WEST;
                case SOUTH -> RIGHT_SOUTH;
                default -> RIGHT_NORTH;
            };
        }
    }

    @Override
    public BlockEntityMapper createBlockEntity(BlockPos pos, BlockState state) {
        return new Entity(pos, state);
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {

    }

    public int getLightEmission(BlockState state, BlockGetter world, BlockPos pos) {
        return state.getValue(LIT) ? 15 : 0;
    }

    public enum Position implements StringRepresentable {
        CENTER("center"),
        RIGHT("right");

        private final String name;

        Position(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return name;
        }
    }

    public static class Entity extends TileEntityLiftPanel1Base {
        public Entity(BlockPos pos, BlockState state) {
            super(MtryumBlockEntities.LIFT_ARRIVAL_LIGHT_BLOCK_ENTITY, pos, state, false);
        }
    }
}