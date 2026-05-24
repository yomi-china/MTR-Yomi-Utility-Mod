package com.yomi.mtryum.item;

import com.yomi.mtryum.block.LiftArrivalLightBlock;
import com.yomi.mtryum.block.LiftArrivalSoundPlayerEntity;
import mtr.block.BlockLiftButtons;
import mtr.block.BlockLiftPanelBase;
import mtr.block.BlockLiftTrackFloor;
import mtr.item.ItemLiftButtonsLinkModifier;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class ItemLiftAutoConnector extends ItemLiftButtonsLinkModifier {

    private static final Logger LOGGER = LogManager.getLogger("LiftAutoConnector");
    private static final int MAX_FLOORS = 512;
    private static final String LIFT_TRACK_ID = "mtr:lift_track_floor_1";
    private static final String BUTTON_ID = "mtr:lift_buttons_1";
    private static final String ID1 = "mtryum:lift_arrival_sound_player";
    private static final String ID2 = "mtryum:mitsubishi_style_lift_buttons";
    private static final String ID3 = "mtr:lift_panel_odd_1";
    private static final String ID4 = "mtr:lift_panel_odd_2";
    private static final String ID5 = "mtryum:lift_arrival_light";
    private static final String ID6 = "mtryum:otis_3200_style_lift_buttons";
    private static final String ID7 = "mtryum:lift_floor_monitor";
    private static final String ID8 = "mtryum:mitsubishi_floor_monitor";
    private static final String ID9 = "mtryum:tk_style_lift_buttons";

    public ItemLiftAutoConnector() {
        super(true);
    }

    @Override
    protected void onStartClick(UseOnContext context, CompoundTag compoundTag) {
        final Level world = context.getLevel();
        final BlockPos pos = context.getClickedPos();
        final BlockState state = world.getBlockState(pos);
        final ResourceLocation blockId = BuiltInRegistries.BLOCK.getKey(state.getBlock());

        if (world.isClientSide) return;

        String key;
        if (state.getBlock() instanceof BlockLiftTrackFloor) {
            key = "msg.mtryum.clicked_track";
        } else if (isConnectableBlock(blockId)) {
            key = "msg.mtryum.clicked_device";
        } else {
            key = "msg.mtryum.clicked_invalid";
        }

        if (context.getPlayer() != null) {
            context.getPlayer().displayClientMessage(
                    Component.translatable(key, pos.getX(), pos.getY(), pos.getZ()),
                    false
            );
        }
    }

    @Override
    protected void onEndClick(UseOnContext context, BlockPos posEnd, CompoundTag compoundTag) {
        final Level world = context.getLevel();
        final BlockPos posCurrent = context.getClickedPos();

        if (world.isClientSide) return;

        BlockPos trackPos = null;
        BlockPos devicePos = null;

        BlockState stateEnd = world.getBlockState(posEnd);
        BlockState stateCurrent = world.getBlockState(posCurrent);

        boolean isEndTrack = stateEnd.getBlock() instanceof BlockLiftTrackFloor;
        boolean isCurrentTrack = stateCurrent.getBlock() instanceof BlockLiftTrackFloor;
        boolean isEndDevice = isConnectableBlock(BuiltInRegistries.BLOCK.getKey(stateEnd.getBlock()));
        boolean isCurrentDevice = isConnectableBlock(BuiltInRegistries.BLOCK.getKey(stateCurrent.getBlock()));

        if (isEndTrack && isCurrentDevice) {
            trackPos = posEnd;
            devicePos = posCurrent;
        } else if (isEndDevice && isCurrentTrack) {
            trackPos = posCurrent;
            devicePos = posEnd;
        } else {
            if (context.getPlayer() != null) {
                context.getPlayer().displayClientMessage(
                        Component.translatable("msg.mtryum.invalid_pair",
                                posEnd.getX(), posEnd.getY(), posEnd.getZ(),
                                posCurrent.getX(), posCurrent.getY(), posCurrent.getZ()),
                        false
                );
            }
            return;
        }

        // 开始连接
        if (context.getPlayer() != null) {
            context.getPlayer().displayClientMessage(
                    Component.translatable("msg.mtryum.connecting_start",
                            trackPos.getX(), trackPos.getY(), trackPos.getZ(),
                            devicePos.getX(), devicePos.getY(), devicePos.getZ()),
                    false
            );
        }

        // 计算偏移
        final BlockPos offset = devicePos.subtract(trackPos);
        LOGGER.info("偏移量计算: {}", offset);

        // 连接当前楼层
        int connected = 0;
        if (connectDevice(world, trackPos, devicePos)) {
            connected++;
        }

        // 向上遍历所有楼层
        BlockPos currentTrack = trackPos;
        while (connected < MAX_FLOORS) {
            BlockPos nextTrack = findNextTrack(world, currentTrack);
            if (nextTrack == null) break;

            BlockPos targetDevice = nextTrack.offset(offset);
            if (connectDevice(world, nextTrack, targetDevice)) {
                connected++;
            }
            currentTrack = nextTrack;
        }

        if (context.getPlayer() != null) {
            context.getPlayer().displayClientMessage(
                    Component.translatable("msg.mtryum.connection_complete", connected),
                    false
            );
        }
    }

    // 查找下一个轨道
    private BlockPos findNextTrack(Level world, BlockPos current) {
        for (int y = 1; y <= MAX_FLOORS; y++) {
            final BlockPos checkPos = current.above(y);
            if (world.getBlockState(checkPos).getBlock() instanceof BlockLiftTrackFloor) {
                return checkPos;
            }
        }
        return null;
    }

    private boolean connectDevice(Level world, BlockPos trackPos, BlockPos devicePos) {
        try {
            final BlockEntity be = world.getBlockEntity(devicePos);
            if (be instanceof BlockLiftButtons.TileEntityLiftButtons) {
                ((BlockLiftButtons.TileEntityLiftButtons) be).registerFloor(trackPos, true);
                return true;
            }
            if (be instanceof BlockLiftPanelBase.TileEntityLiftPanel1Base) {
                ((BlockLiftPanelBase.TileEntityLiftPanel1Base) be).registerFloor(trackPos, true);
                return true;
            }
            if (be instanceof LiftArrivalLightBlock.Entity) {
                ((LiftArrivalLightBlock.Entity) be).registerFloor(trackPos, true);
                return true;
            }
            if (be instanceof LiftArrivalSoundPlayerEntity) {
                ((LiftArrivalSoundPlayerEntity) be).registerFloor(trackPos, true);
                return true;
            }
            return false;
        } catch (Exception e) {
            LOGGER.error("连接失败: {} → {}", trackPos, devicePos, e);
            return false;
        }
    }

    private boolean isConnectableBlock(ResourceLocation blockId) {
        final String idStr = blockId.toString();
        return idStr.equals(BUTTON_ID) ||
                idStr.equals(ID1) ||
                idStr.equals(ID2) ||
                idStr.equals(ID3) ||
                idStr.equals(ID4) ||
                idStr.equals(ID5) ||
                idStr.equals(ID6) ||
                idStr.equals(ID7) ||
                idStr.equals(ID8) ||
                idStr.equals(ID9);
    }

    @Override
    public void appendHoverText(ItemStack stack, Level world, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.mtryum.auto_connector.line1").setStyle(Style.EMPTY.withColor(ChatFormatting.GRAY)));
        tooltip.add(Component.translatable("tooltip.mtryum.auto_connector.line2").setStyle(Style.EMPTY.withColor(ChatFormatting.GRAY)));
        tooltip.add(Component.translatable("tooltip.mtryum.auto_connector.shift_hint").setStyle(Style.EMPTY.withColor(ChatFormatting.RED)));
    }
}