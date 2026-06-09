package com.yomi.mtryum.block;

import mtr.block.BlockLiftPanelBase;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
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
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.List;

public abstract class AbstractLiftArrivalLightBlock extends BlockLiftPanelBase {

    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final EnumProperty<Position> POSITION = EnumProperty.create("position", Position.class);
    public static final BooleanProperty LIT = BooleanProperty.create("lit");

    protected static final ResourceLocation BRUSH_ITEM_ID = new ResourceLocation("mtr", "brush");

    public AbstractLiftArrivalLightBlock() {
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
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return defaultBlockState().setValue(FACING, ctx.getHorizontalDirection());
    }

    @Override
    public void appendHoverText(ItemStack stack, BlockGetter world, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.mtryum.lift_arrival_light.line1")
                .setStyle(Style.EMPTY.withColor(ChatFormatting.GRAY)));
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        final ItemStack stack = player.getItemInHand(hand);
        if (Registry.ITEM.getKey(stack.getItem()).equals(BRUSH_ITEM_ID)) {
            final Position currentPosition = state.getValue(POSITION);
            world.setBlockAndUpdate(pos, state.setValue(POSITION,
                    currentPosition == Position.CENTER ? Position.RIGHT : Position.CENTER));
            return InteractionResult.SUCCESS;
        }
        return super.use(state, world, pos, player, hand, hit);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        final Direction facing = state.getValue(FACING);
        final Position position = state.getValue(POSITION);

        if (position == Position.CENTER) {
            return getCenterShape(facing);
        } else {
            return getRightShape(facing);
        }
    }

    protected abstract VoxelShape getCenterShape(Direction facing);

    protected abstract VoxelShape getRightShape(Direction facing);

    public int getBlinkIntervalTicks() {
        return 5;
    }

    public double getArrivalThreshold() {
        return 1.0;
    }

    public double getAdvanceBlinkDistance() {
        return 5.0;
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

    public abstract static class AbstractEntity extends TileEntityLiftPanel1Base {

        public AbstractEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
            super(type, pos, state, false);
        }
    }
}