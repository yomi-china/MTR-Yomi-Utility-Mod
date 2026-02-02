package com.yomi.mtryum.item;

import com.yomi.mtryum.block.LiftArrivalLightBlock;
import com.yomi.mtryum.block.LiftArrivalSoundPlayerBlock;
import com.yomi.mtryum.block.MitsubishiStyleLiftButtonsBlock;
import mtr.block.BlockLiftButtons;
import mtr.block.BlockLiftPanelBase;
import mtr.block.BlockLiftTrackFloor;
import mtr.item.ItemLiftButtonsLinkModifier;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TranslatableComponent;
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

public class LiftAutoConnectorItem extends ItemLiftButtonsLinkModifier {

    private static final Logger LOGGER = LogManager.getLogger("LiftAutoConnector");
    private static final String KEY_OFFSET = "offset";
    private static final int MAX_FLOORS = 512;
    private static final String LIFT_TRACK_ID = "mtr:lift_track_floor_1";
    private static final String BUTTON_ID = "mtr:lift_buttons_1";
    private static final String ID1 = "mtryum:lift_arrival_sound_player";
    private static final String ID2 = "mtryum:mitsubishi_style_lift_buttons";
    private static final String ID3 = "mtr:lift_panel_odd_1";
    private static final String ID4 = "mtr:lift_panel_odd_2";
    private static final String ID5 = "mtryum:lift_arrival_light";

    public LiftAutoConnectorItem() {
        super(true);
    }

    @Override
    protected void onStartClick(UseOnContext context, CompoundTag compoundTag) {

    }

    @Override
    protected void onEndClick(UseOnContext context, BlockPos posEnd, CompoundTag compoundTag) {
        final Level world = context.getLevel();
        final BlockPos clickedPos = context.getClickedPos();
        BlockState state = world.getBlockState(clickedPos);

        if (world.isClientSide) return;

        try {
            final ResourceLocation clickedblockId = Registry.BLOCK.getKey(state.getBlock());

            if (LIFT_TRACK_ID.equals(clickedblockId.toString())) {
                handleTrackConnection(context, compoundTag, clickedPos);
            }
            else if (isConnectableBlock(clickedblockId)) {
                handleDeviceConnection(context, world, compoundTag, clickedPos);
            }
        } catch (Exception e) {
            LOGGER.error("操作失败: ", e);
        }
    }

    // 记录基准轨道位置并计算初始偏移
    private void handleTrackConnection(UseOnContext context, CompoundTag compoundTag, BlockPos trackPos) {
        compoundTag.putLong("base_track", trackPos.asLong());
        if (context.getPlayer() != null) {
            context.getPlayer().displayClientMessage(
                    new TranslatableComponent("msg.mtryum.track_selected",
                            trackPos.getX(), trackPos.getY(), trackPos.getZ()),
                    false
            );
        }
        LOGGER.info("基准轨道已记录: {}", trackPos);
    }

    // 计算偏移并连接所有楼层
    private void handleDeviceConnection(UseOnContext context, Level level, CompoundTag compoundTag, BlockPos buttonPos) {
        if (!compoundTag.contains("base_track")) {
            LOGGER.warn("未检测到基准轨道数据");
            if (context.getPlayer() != null) {
                context.getPlayer().displayClientMessage(
                        new TranslatableComponent("msg.mtryum.no_base_track"),
                        false
                );
            }
            return;
        }

        final BlockPos baseTrack = BlockPos.of(compoundTag.getLong("base_track"));
        if (!(level.getBlockState(baseTrack).getBlock() instanceof BlockLiftTrackFloor)) {
            LOGGER.error("基准轨道已失效: {}", baseTrack);
            if (context.getPlayer() != null) {
                context.getPlayer().displayClientMessage(
                        new TranslatableComponent("msg.mtryum.invalid_base_track"),
                        false
                );
            }
            return;
        }

        // 计算xyz偏移量
        final BlockPos offset = buttonPos.subtract(baseTrack);
        compoundTag.putIntArray(KEY_OFFSET, new int[]{offset.getX(), offset.getY(), offset.getZ()});
        LOGGER.info("偏移量计算: {}", offset);

        // 遍历所有上层轨道
        int connected = 0;
        BlockPos currentTrack = baseTrack;
        while (connected < MAX_FLOORS) {
            final BlockPos upperTrack = findNextTrack(level, currentTrack);
            if (upperTrack == null) break;

            // 应用xyz偏移量
            final BlockPos targetButton = upperTrack.offset(offset);
            if (connectDevice(level, upperTrack, targetButton)) {
                connected++;
                LOGGER.debug("成功连接: {} → {}", upperTrack, targetButton);
            }

            currentTrack = upperTrack;
        }
        if (context.getPlayer() != null) {
            context.getPlayer().displayClientMessage(
                    new TranslatableComponent("msg.mtryum.connection_complete",
                            connected),
                    false
            );
        }
        compoundTag.remove("base_track");
        compoundTag.remove(KEY_OFFSET);
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

    private boolean connectDevice(Level world, BlockPos trackPos, BlockPos buttonPos) {
        try {
            final BlockEntity be = world.getBlockEntity(buttonPos);
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
            return false;
        } catch (Exception e) {
            LOGGER.error("连接失败: {} → {}", trackPos, buttonPos, e);
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
                idStr.equals(ID5);
    }

    @Override
    public void appendHoverText(ItemStack stack, Level world, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(new TranslatableComponent("tooltip.mtryum.auto_connector.line1").setStyle(Style.EMPTY.withColor(ChatFormatting.GRAY)));
        tooltip.add(new TranslatableComponent("tooltip.mtryum.auto_connector.line2").setStyle(Style.EMPTY.withColor(ChatFormatting.GRAY)));
    }

    @Override
    protected boolean clickCondition(UseOnContext context) {
        final BlockState state = context.getLevel().getBlockState(context.getClickedPos());
        return state.getBlock() instanceof BlockLiftTrackFloor
                || state.getBlock() instanceof BlockLiftButtons
                || state.getBlock() instanceof MitsubishiStyleLiftButtonsBlock
                || state.getBlock() instanceof LiftArrivalLightBlock
                || state.getBlock() instanceof LiftArrivalSoundPlayerBlock;
    }
}