package com.yomi.mtryum.block;

import mtr.Items;
import mtr.block.BlockLiftButtons;
import mtr.block.BlockLiftPanelBase;
import mtr.block.IBlock;
import mtr.mappings.BlockEntityMapper;
import mtr.mappings.Text;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
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

public abstract class AbstractLiftButtonsBlock extends BlockLiftButtons {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

    protected AbstractLiftButtonsBlock() {
        super();
        registerDefaultState(defaultBlockState()
                .setValue(FACING, Direction.NORTH)
                .setValue(UNLOCKED, true)
        );
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
        return type == getBlockEntityType() ?
                (world.isClientSide ? null : (level1, blockPos, blockState, t) ->
                        getServerTicker(level1, blockPos, blockState, (BlockLiftPanelBase.TileEntityLiftPanel1Base) t)) :
                null;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, UNLOCKED);
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        // 可以在这里添加放置时的通用逻辑
    }

    @Override
    public void playerWillDestroy(Level world, BlockPos pos, BlockState state, Player player) {
        // 可以在这里添加破坏时的通用逻辑
    }

    @Override
    public void appendHoverText(ItemStack stack, BlockGetter world, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.mtryum.lift_arrival_light.line1").setStyle(Style.EMPTY.withColor(ChatFormatting.GRAY)));
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
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        BlockEntity blockEntity = world.getBlockEntity(pos);

        // 调用子类的特殊处理（如旧版本兼容性检查）
        handleSpecialLogic(state, world, pos, blockEntity);

        final BlockState finalState = state;
        final BlockPos finalPos = pos;

        final InteractionResult result = IBlock.checkHoldingBrush(world, player, () -> {
            final boolean unlocked = !IBlock.getStatePropertySafe(finalState, UNLOCKED);
            world.setBlockAndUpdate(finalPos, finalState.setValue(UNLOCKED, unlocked));
            player.displayClientMessage(unlocked ? Text.translatable("gui.mtr.lift_buttons_unlocked") : Text.translatable("gui.mtr.lift_buttons_locked"), true);
        });

        if (world.isClientSide || result == InteractionResult.SUCCESS) {
            return InteractionResult.SUCCESS;
        } else {
            if (player.isHolding(Items.LIFT_BUTTONS_LINK_CONNECTOR.get()) || player.isHolding(Items.LIFT_BUTTONS_LINK_REMOVER.get())) {
                return InteractionResult.PASS;
            } else {
                final boolean unlocked = IBlock.getStatePropertySafe(finalState, UNLOCKED);
                if (unlocked) {
                    double relativeY = hit.getLocation().y - pos.getY();
                    boolean isUpButton = relativeY > 0.3;

                    // 这里需要子类实现具体的 callLift 调用
                    return handleCallLift(blockEntity, isUpButton);
                } else {
                    return InteractionResult.FAIL;
                }
            }
        }
    }

    // 抽象方法，子类必须实现
    public abstract BlockEntityType<? extends BlockEntityMapper> getBlockEntityType();

    public abstract BlockEntityMapper createBlockEntity(BlockPos pos, BlockState state);

    protected abstract void getServerTicker(Level level, BlockPos pos, BlockState state, BlockLiftPanelBase.TileEntityLiftPanel1Base entity);

    // 处理调用电梯的具体逻辑
    protected abstract InteractionResult handleCallLift(BlockEntity blockEntity, boolean isUpButton);

    // 可选的特殊逻辑处理，子类可以重写
    protected void handleSpecialLogic(BlockState state, Level world, BlockPos pos, BlockEntity blockEntity) {
        // 默认不执行任何特殊逻辑
    }
}