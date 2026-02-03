package com.yomi.mtryum.block;

import com.yomi.mtryum.registry.MtryumBlockEntities;
import mtr.Items;
import mtr.block.BlockLiftButtons;
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

public class TKClassicLiftButtonsBlock extends BlockLiftButtons {

    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

    public TKClassicLiftButtonsBlock() {
        super();
        registerDefaultState(defaultBlockState()
                .setValue(FACING, Direction.NORTH)
                .setValue(UNLOCKED, true)
        );
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
        return type == MtryumBlockEntities.TK_STYLE_LIFT_BUTTONS_ENTITY ?
                (world.isClientSide ? null : (level1, blockPos, blockState, t) ->
                        TKClassicLiftButtonsBlockEntity.serverTick(level1, blockPos, blockState, (TKClassicLiftButtonsBlockEntity) t)) :
                null;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, UNLOCKED);
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {

    }

    @Override
    public void playerWillDestroy(Level world, BlockPos pos, BlockState state, Player player) {

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
    public BlockEntityMapper createBlockEntity(BlockPos pos, BlockState state) {
        return new TKClassicLiftButtonsBlockEntity(pos, state);
    }

    @Override
    public BlockEntityType<? extends BlockEntityMapper> getType() {
        return MtryumBlockEntities.TK_STYLE_LIFT_BUTTONS_ENTITY;
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        final InteractionResult result = IBlock.checkHoldingBrush(world, player, () -> {
            final boolean unlocked = !IBlock.getStatePropertySafe(state, UNLOCKED);
            world.setBlockAndUpdate(pos, state.setValue(UNLOCKED, unlocked));
            player.displayClientMessage(unlocked ? Text.translatable("gui.mtr.lift_buttons_unlocked") : Text.translatable("gui.mtr.lift_buttons_locked"), true);
        });

        if (world.isClientSide || result == InteractionResult.SUCCESS) {
            return InteractionResult.SUCCESS;
        } else {
            if (player.isHolding(Items.LIFT_BUTTONS_LINK_CONNECTOR.get()) || player.isHolding(Items.LIFT_BUTTONS_LINK_REMOVER.get())) {
                return InteractionResult.PASS;
            } else {
                final boolean unlocked = IBlock.getStatePropertySafe(state, UNLOCKED);
                if (unlocked) {
                    double relativeY = hit.getLocation().y - pos.getY();
                    boolean isUpButton = relativeY > 0.3;

                    BlockEntity blockEntity = world.getBlockEntity(pos);
                    if (blockEntity instanceof TKClassicLiftButtonsBlockEntity) {
                        ((TKClassicLiftButtonsBlockEntity) blockEntity).callLift(isUpButton);
                        return InteractionResult.SUCCESS;
                    }
                    return InteractionResult.PASS;
                } else {
                    return InteractionResult.FAIL;
                }
            }
        }
    }
}