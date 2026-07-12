package com.yomi.mtryum.item;

import mtr.block.BlockLiftTrack;
import mtr.block.BlockLiftTrackFloor;
import mtr.data.Lift;
import mtr.data.LiftServer;
import mtr.data.RailwayData;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class ItemLiftAttributeCopier extends Item {

    private static final Field FIELD_ACCELERATION;
    private static final Field FIELD_MAX_SPEED;
    private static final Field FIELD_DISPLAY_COLOR;

    static {
        Field accel = null;
        Field maxSpeed = null;
        Field displayColor = null;
        try {
            accel = Lift.class.getField("acceleration");
            maxSpeed = Lift.class.getField("maxSpeed");
            displayColor = Lift.class.getField("displayColor");
        } catch (NoSuchFieldException ignored) {
        }
        FIELD_ACCELERATION = accel;
        FIELD_MAX_SPEED = maxSpeed;
        FIELD_DISPLAY_COLOR = displayColor;
    }

    public ItemLiftAttributeCopier(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        final Level world = context.getLevel();
        if (world.isClientSide) {
            return InteractionResult.PASS;
        }

        final BlockPos clickedPos = context.getClickedPos();
        final Player player = context.getPlayer();
        if (player == null) {
            return InteractionResult.PASS;
        }

        final Block clickedBlock = world.getBlockState(clickedPos).getBlock();
        if (!(clickedBlock instanceof BlockLiftTrack)) {
            return InteractionResult.PASS;
        }

        final RailwayData railwayData = RailwayData.getInstance(world);
        if (railwayData == null) {
            return InteractionResult.PASS;
        }

        final ItemStack stack = context.getItemInHand();

        if (player.isShiftKeyDown()) {
            // 潜行右键复制电梯属性
            return copyAttributes(world, clickedPos, player, railwayData, stack);
        } else {
            // 右键粘贴电梯属性
            return pasteAttributes(world, clickedPos, player, railwayData, stack);
        }
    }

    private InteractionResult copyAttributes(Level world, BlockPos clickedPos, Player player, RailwayData railwayData, ItemStack stack) {
        final LiftServer lift = findLiftFromTrack(world, clickedPos, railwayData);
        if (lift == null) {
            player.displayClientMessage(
                    Component.translatable("msg.mtryum.lift_attr_copy.no_lift_found"),
                    true
            );
            return InteractionResult.FAIL;
        }

        final CompoundTag tag = stack.getOrCreateTag();
        tag.putBoolean("copied", true);

        tag.putInt("lift_height", lift.liftHeight);
        tag.putInt("lift_width", lift.liftWidth);
        tag.putInt("lift_depth", lift.liftDepth);
        tag.putInt("lift_offset_x", lift.liftOffsetX);
        tag.putInt("lift_offset_y", lift.liftOffsetY);
        tag.putInt("lift_offset_z", lift.liftOffsetZ);
        tag.putBoolean("is_double_sided", lift.isDoubleSided);
        tag.putString("lift_style", lift.liftStyle.name());
        tag.putFloat("facing", lift.facing.toYRot());

        // 1.20.1的YMTR特有属性
        copyYMTRFields(lift, tag);

        player.sendSystemMessage(
                Component.translatable("msg.mtryum.lift_attr_copy.copy_details_standard",
                        lift.liftHeight, lift.liftWidth, lift.liftDepth,
                        lift.liftOffsetX, lift.liftOffsetY, lift.liftOffsetZ,
                        lift.isDoubleSided ? "Yes" : "No",
                        lift.liftStyle.name(),
                        lift.facing.getName()
                ).withStyle(ChatFormatting.GRAY)
        );
        if (FIELD_ACCELERATION != null) {
            try {
                final float accel = FIELD_ACCELERATION.getFloat(lift);
                final float speed = FIELD_MAX_SPEED.getFloat(lift);
                final Object colorObj = FIELD_DISPLAY_COLOR.get(lift);
                final String colorName = colorObj != null ? ((Enum<?>) colorObj).name() : "RED";
                player.sendSystemMessage(
                        Component.translatable("msg.mtryum.lift_attr_copy.copy_details_ymtr",
                                accel, speed, colorName
                        ).withStyle(ChatFormatting.GRAY)
                );
            } catch (IllegalAccessException ignored) {
            }
        }

        player.displayClientMessage(
                Component.translatable("msg.mtryum.lift_attr_copy.copy_success"),
                true
        );
        return InteractionResult.SUCCESS;
    }

    private InteractionResult pasteAttributes(Level world, BlockPos clickedPos, Player player, RailwayData railwayData, ItemStack stack) {
        final CompoundTag tag = stack.getTag();
        if (tag == null || !tag.getBoolean("copied")) {
            player.displayClientMessage(
                    Component.translatable("msg.mtryum.lift_attr_copy.no_attributes"),
                    true
            );
            return InteractionResult.FAIL;
        }

        final LiftServer lift = findLiftFromTrack(world, clickedPos, railwayData);
        if (lift == null) {
            player.displayClientMessage(
                    Component.translatable("msg.mtryum.lift_attr_copy.no_lift_found"),
                    true
            );
            return InteractionResult.FAIL;
        }

        lift.liftHeight = tag.getInt("lift_height");
        lift.liftWidth = tag.getInt("lift_width");
        lift.liftDepth = tag.getInt("lift_depth");
        lift.liftOffsetX = tag.getInt("lift_offset_x");
        lift.liftOffsetY = tag.getInt("lift_offset_y");
        lift.liftOffsetZ = tag.getInt("lift_offset_z");
        lift.isDoubleSided = tag.getBoolean("is_double_sided");
        lift.liftStyle = Lift.LiftStyle.valueOf(tag.getString("lift_style"));
        lift.facing = Direction.fromYRot(tag.getFloat("facing"));

        pasteYMTRFields(lift, tag);

        player.displayClientMessage(
                Component.translatable("msg.mtryum.lift_attr_copy.paste_success"),
                true
        );
        return InteractionResult.SUCCESS;
    }

    // 通过反射读取YMTR特有字段
    private static void copyYMTRFields(Lift lift, CompoundTag tag) {
        if (FIELD_ACCELERATION == null) {
            return;
        }
        try {
            tag.putFloat("acceleration", FIELD_ACCELERATION.getFloat(lift));
            tag.putFloat("max_speed", FIELD_MAX_SPEED.getFloat(lift));

            final Object colorObj = FIELD_DISPLAY_COLOR.get(lift);
            tag.putString("display_color", colorObj != null ? ((Enum<?>) colorObj).name() : "");
        } catch (IllegalAccessException ignored) {
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void pasteYMTRFields(Lift lift, CompoundTag tag) {
        if (FIELD_ACCELERATION == null) {
            return;
        }
        try {
            FIELD_ACCELERATION.setFloat(lift, tag.getFloat("acceleration"));
            FIELD_MAX_SPEED.setFloat(lift, tag.getFloat("max_speed"));

            final String colorName = tag.getString("display_color");
            if (!colorName.isEmpty()) {
                final Object color = Enum.valueOf((Class) FIELD_DISPLAY_COLOR.getType(), colorName);
                FIELD_DISPLAY_COLOR.set(lift, color);
            }
        } catch (IllegalAccessException | IllegalArgumentException ignored) {
        }
    }

    private static LiftServer findLiftFromTrack(Level world, BlockPos clickedPos, RailwayData railwayData) {
        final List<BlockPos> floors = new ArrayList<>();
        int i = 0;
        boolean scanForFloors = false;

        while (true) {
            final BlockPos checkPos = clickedPos.below(i);
            final Block checkBlock = world.getBlockState(checkPos).getBlock();
            if (!(checkBlock instanceof BlockLiftTrack)) {
                if (scanForFloors) {
                    break;
                } else {
                    scanForFloors = true;
                }
            }
            if (scanForFloors && checkBlock instanceof BlockLiftTrackFloor) {
                floors.add(checkPos);
            }
            i += (scanForFloors ? -1 : 1);
        }

        for (final BlockPos floor : floors) {
            for (final LiftServer lift : railwayData.lifts) {
                if (lift.hasFloor(floor)) {
                    return lift;
                }
            }
        }
        return null;
    }

    @Override
    public void appendHoverText(ItemStack stack, Level world, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.mtryum.lift_attr_copy.line1")
                .setStyle(Style.EMPTY.withColor(ChatFormatting.GRAY)));
        tooltip.add(Component.translatable("tooltip.mtryum.lift_attr_copy.line2")
                .setStyle(Style.EMPTY.withColor(ChatFormatting.GRAY)));
        final CompoundTag tag = stack.getTag();
        if (tag != null && tag.getBoolean("copied")) {
            tooltip.add(Component.translatable("tooltip.mtryum.lift_attr_copy.has_data")
                    .setStyle(Style.EMPTY.withColor(ChatFormatting.GREEN)));
        } else {
            tooltip.add(Component.translatable("tooltip.mtryum.lift_attr_copy.no_data")
                    .setStyle(Style.EMPTY.withColor(ChatFormatting.RED)));
        }
    }
}
