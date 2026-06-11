package com.yomi.mtryum.item;

import com.yomi.mtryum.registry.MtryumItems;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class ItemLiftFloorSetter {
    private static final String ELEVATOR_FLOOR_ID = "mtr:lift_track_floor_1";
    private static final String ELEVATOR_TRACK_ID = "mtr:lift_track_1";
    private static final String FLOOR_NBT_KEY = "floor_number";
    private static final String SOUND_PLAYER_BLOCK_ID = "mtryum:lift_arrival_sound_player";
    private static final String SOUND_INDEX_NBT_KEY = "sound_index";

    public static InteractionResult onBlockUse(Player player, Level world, InteractionHand hand, BlockHitResult hitResult) {
        if (world.isClientSide()) return InteractionResult.PASS;

        ItemStack stack = player.getItemInHand(hand);
        if (!stack.is(MtryumItems.LIFT_FLOOR_SETTER)) return InteractionResult.PASS;

        BlockPos startPos = hitResult.getBlockPos();
        BlockState state = world.getBlockState(startPos);
        String blockId = Registry.BLOCK.getKey(state.getBlock()).toString();

        if (blockId.equals(SOUND_PLAYER_BLOCK_ID)) {
            int selectedSound = getSoundIndexFromBlockEntity(world, startPos);
            if (selectedSound > 0) {
                player.displayClientMessage(
                        new TranslatableComponent("item.mtryum.lift_floor_setter.sound_start")
                                .withStyle(ChatFormatting.BLUE),
                        false
                );

                processSoundPlayerChain(world, startPos, selectedSound);

                player.displayClientMessage(
                        new TranslatableComponent("item.mtryum.lift_floor_setter.sound_complete")
                                .withStyle(ChatFormatting.GREEN),
                        false
                );
                return InteractionResult.SUCCESS;
            }

            player.displayClientMessage(
                    new TranslatableComponent("item.mtryum.lift_floor_setter.no_sound_found")
                            .withStyle(ChatFormatting.YELLOW),
                    false
            );
            return InteractionResult.PASS;
        }

        if (!blockId.equals(ELEVATOR_FLOOR_ID)) {
            player.displayClientMessage(
                    new TranslatableComponent("item.mtryum.lift_floor_setter.wrong_block")
                            .withStyle(ChatFormatting.RED),
                    false
            );
            return InteractionResult.PASS;
        }

        player.displayClientMessage(
                new TranslatableComponent("item.mtryum.lift_floor_setter.elevator_start")
                        .withStyle(ChatFormatting.BLUE),
                false
        );

        processElevatorChain(world, startPos);

        player.displayClientMessage(
                new TranslatableComponent("item.mtryum.lift_floor_setter.elevator_complete")
                        .withStyle(ChatFormatting.GREEN),
                false
        );
        return InteractionResult.SUCCESS;
    }

    private static int getSoundIndexFromBlockEntity(Level world, BlockPos pos) {
        BlockEntity be = world.getBlockEntity(pos);
        if (be == null) return -1;
        CompoundTag nbt = be.saveWithFullMetadata();
        return nbt.contains(SOUND_INDEX_NBT_KEY) ? nbt.getInt(SOUND_INDEX_NBT_KEY) : -1;
    }

    private static void processSoundPlayerChain(Level world, BlockPos startPos, int soundIndex) {
        int minY = startPos.getY();
        int maxY = 315;
        int count = 0;

        for (int y = minY; y <= maxY; y++) {
            BlockPos pos = new BlockPos(startPos.getX(), y, startPos.getZ());
            BlockState state = world.getBlockState(pos);
            String blockId = Registry.BLOCK.getKey(state.getBlock()).toString();
            if (blockId.equals(SOUND_PLAYER_BLOCK_ID)) {
                updateSoundIndexNBT(world, pos, soundIndex);
                count++;
            }
        }

        if (count > 0) {
            Player player = world.getNearestPlayer(startPos.getX(), startPos.getY(), startPos.getZ(), 10, false);
            if (player != null) {
                player.displayClientMessage(
                        new TranslatableComponent("item.mtryum.lift_floor_setter.sound_progress", count)
                                .withStyle(ChatFormatting.GRAY),
                        false
                );
            }
        }
    }

    private static void updateSoundIndexNBT(Level world, BlockPos pos, int soundIndex) {
        BlockEntity be = world.getBlockEntity(pos);
        if (be == null) return;
        CompoundTag originalNbt = be.saveWithFullMetadata();
        CompoundTag newNbt = originalNbt.copy();
        newNbt.putInt(SOUND_INDEX_NBT_KEY, soundIndex);
        if (originalNbt.getInt(SOUND_INDEX_NBT_KEY) != soundIndex) {
            be.load(newNbt);
            be.setChanged();
            world.sendBlockUpdated(pos, be.getBlockState(), be.getBlockState(), Block.UPDATE_ALL);
        }
    }

    private static void processElevatorChain(Level world, BlockPos startPos) {
        int currentFloor = 1;
        BlockPos currentPos = startPos;
        int count = 0;

        while (true) {
            BlockState state = world.getBlockState(currentPos);
            String blockId = Registry.BLOCK.getKey(state.getBlock()).toString();

            if (blockId.equals(ELEVATOR_FLOOR_ID)) {
                updateFloorNBT(world, currentPos, currentFloor++);
                count++;
            } else if (blockId.equals(ELEVATOR_TRACK_ID)) {
                currentPos = currentPos.above();
                continue;
            } else {
                break;
            }

            currentPos = currentPos.above();
        }

        if (count > 0) {
            Player player = world.getNearestPlayer(startPos.getX(), startPos.getY(), startPos.getZ(), 10, false);
            if (player != null) {
                player.displayClientMessage(
                        new TranslatableComponent("item.mtryum.lift_floor_setter.elevator_progress", count)
                                .withStyle(ChatFormatting.GRAY),
                        false
                );
            }
        }
    }

    private static void updateFloorNBT(Level world, BlockPos pos, int floorNumber) {
        BlockEntity be = world.getBlockEntity(pos);
        if (be == null) return;

        CompoundTag originalNbt = be.saveWithFullMetadata();
        CompoundTag newNbt = originalNbt.copy();

        String formattedFloor = String.valueOf(floorNumber);
        newNbt.putString(FLOOR_NBT_KEY, formattedFloor);

        if (!formattedFloor.equals(originalNbt.getString(FLOOR_NBT_KEY))) {
            be.load(newNbt);
            be.setChanged();

            world.sendBlockUpdated(pos,
                    be.getBlockState(),
                    be.getBlockState(),
                    Block.UPDATE_ALL
            );
        }
    }
}