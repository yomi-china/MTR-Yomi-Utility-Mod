package com.yomi.mtryum.block;

import com.yomi.mtryum.item.ItemLiftPartsLinkModifier;
import mtr.Items;
import mtr.block.BlockLiftButtons;
import mtr.block.IBlock;
import mtr.data.LiftInstructions;
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
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.List;

public abstract class AbstractLiftButtonsBlock extends BlockLiftButtons {

    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty DUAL = BooleanProperty.create("dual");

    protected AbstractLiftButtonsBlock() {
        super();
        registerDefaultState(defaultBlockState()
                .setValue(FACING, Direction.NORTH)
                .setValue(UNLOCKED, true)
                .setValue(DUAL, false));
    }

    @Override
    public BlockEntityType<? extends BlockEntityMapper> getType() {
        return getBlockEntityType();
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, UNLOCKED, DUAL);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        final Direction facing = ctx.getHorizontalDirection();
        return defaultBlockState().setValue(FACING, facing).setValue(DUAL, false);
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
    }

    @Override
    public void playerWillDestroy(Level world, BlockPos pos, BlockState state, Player player) {
    }

    @Override
    public void appendHoverText(ItemStack stack, BlockGetter world, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.mtryum.connect")
                .setStyle(Style.EMPTY.withColor(ChatFormatting.GRAY)));
    }

    @Override
    public final VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        Direction facing = state.getValue(FACING);
        VoxelShape baseShape = state.getValue(DUAL) ? getDualBaseShape() : getSingleBaseShape();
        return rotateShape(baseShape, facing);
    }

    protected abstract VoxelShape getSingleBaseShape();

    protected abstract VoxelShape getDualBaseShape();

    protected static VoxelShape rotateShape(VoxelShape baseShape, Direction facing) {
        if (facing == Direction.NORTH) {
            return baseShape;
        }
        VoxelShape[] resultHolder = {Shapes.empty()};
        baseShape.forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) -> {
            // 将归一化坐标转为像素坐标 (0-16)
            double px = minX * 16.0, py = minY * 16.0, pz = minZ * 16.0;
            double px2 = maxX * 16.0, py2 = maxY * 16.0, pz2 = maxZ * 16.0;

            double nx1, nx2, nz1, nz2;
            switch (facing) {
                case SOUTH -> {
                    nx1 = px;
                    nx2 = px2;
                    nz1 = 16.0 - pz2;
                    nz2 = 16.0 - pz;
                }
                case EAST -> {
                    nx1 = 16.0 - pz2;
                    nx2 = 16.0 - pz;
                    nz1 = px;
                    nz2 = px2;
                }
                case WEST -> {
                    nx1 = pz;
                    nx2 = pz2;
                    nz1 = px;
                    nz2 = px2;
                }
                default -> { return; }
            }

            resultHolder[0] = Shapes.or(resultHolder[0], Block.box(
                    Math.min(nx1, nx2), py, Math.min(nz1, nz2),
                    Math.max(nx1, nx2), py2, Math.max(nz1, nz2)
            ));
        });
        return resultHolder[0];
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos,
                                 Player player, InteractionHand hand, BlockHitResult hit) {
        final BlockEntity blockEntity = world.getBlockEntity(pos);
        handleSpecialLogic(state, world, pos, blockEntity);

        final InteractionResult result = IBlock.checkHoldingBrush(world, player, () -> {
            final boolean unlocked = !IBlock.getStatePropertySafe(state, UNLOCKED);
            world.setBlockAndUpdate(pos, state.setValue(UNLOCKED, unlocked));
            player.displayClientMessage(
                    unlocked
                            ? Text.translatable("gui.mtr.lift_buttons_unlocked")
                            : Text.translatable("gui.mtr.lift_buttons_locked"),
                    true);
        });

        if (world.isClientSide || result == InteractionResult.SUCCESS) {
            return InteractionResult.SUCCESS;
        }

        if (player.isHolding(Items.LIFT_BUTTONS_LINK_CONNECTOR.get())
                || player.isHolding(Items.LIFT_BUTTONS_LINK_REMOVER.get())
                || player.getMainHandItem().getItem() instanceof ItemLiftPartsLinkModifier
                || player.getOffhandItem().getItem() instanceof ItemLiftPartsLinkModifier) {
            return InteractionResult.PASS;
        }

        final boolean unlocked = IBlock.getStatePropertySafe(state, UNLOCKED);
        if (unlocked) {
            final double y = hit.getLocation().y;
            final boolean topHalfClicked = y - Math.floor(y) > 0.25;
            LiftInstructions.addInstruction(world, pos, topHalfClicked);
            return InteractionResult.SUCCESS;
        } else {
            return InteractionResult.FAIL;
        }
    }

    public abstract BlockEntityType<? extends BlockEntityMapper> getBlockEntityType();

    public abstract BlockEntityMapper createBlockEntity(BlockPos pos, BlockState state);

    protected void handleSpecialLogic(BlockState state, Level world, BlockPos pos, BlockEntity blockEntity) {
    }
}