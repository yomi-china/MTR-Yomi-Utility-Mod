package com.yomi.mtryum.network;

import com.yomi.mtryum.Mtryum;
import com.yomi.mtryum.block.TKClassicFloorMonitorEntity;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

public class LiftFloorMonitorPacket {
    public static final ResourceLocation SET_COLOR = new ResourceLocation(Mtryum.MOD_ID, "set_color");
    public static final ResourceLocation SET_ARROW_STYLE = new ResourceLocation(Mtryum.MOD_ID, "set_arrow_style");
    public static final ResourceLocation TOGGLE_LOCK = new ResourceLocation(Mtryum.MOD_ID, "toggle_lock");

    public static void register() {
        ServerPlayNetworking.registerGlobalReceiver(SET_COLOR, LiftFloorMonitorPacket::handleSetColor);
        ServerPlayNetworking.registerGlobalReceiver(SET_ARROW_STYLE, LiftFloorMonitorPacket::handleSetArrowStyle);
        ServerPlayNetworking.registerGlobalReceiver(TOGGLE_LOCK, LiftFloorMonitorPacket::handleToggleLock);
    }

    private static void handleSetColor(MinecraftServer server, ServerPlayer player,
                                       ServerGamePacketListenerImpl handler,
                                       FriendlyByteBuf buf, PacketSender responseSender) {
        BlockPos pos = buf.readBlockPos();
        int color = buf.readInt();

        server.execute(() -> {
            if (player.level instanceof ServerLevel level) {
                if (level.getBlockEntity(pos) instanceof TKClassicFloorMonitorEntity tile) {
                    tile.setTextColor(color);
                    level.sendBlockUpdated(pos, level.getBlockState(pos), level.getBlockState(pos), 3);
                }
            }
        });
    }

    private static void handleSetArrowStyle(MinecraftServer server, ServerPlayer player,
                                            ServerGamePacketListenerImpl handler,
                                            FriendlyByteBuf buf, PacketSender responseSender) {
        BlockPos pos = buf.readBlockPos();
        int style = buf.readInt();

        server.execute(() -> {
            if (player.level instanceof ServerLevel level) {
                if (level.getBlockEntity(pos) instanceof TKClassicFloorMonitorEntity tile) {
                    tile.setArrowStyle(style);
                    level.sendBlockUpdated(pos, level.getBlockState(pos), level.getBlockState(pos), 3);
                }
            }
        });
    }

    private static void handleToggleLock(MinecraftServer server, ServerPlayer player,
                                         ServerGamePacketListenerImpl handler,
                                         FriendlyByteBuf buf, PacketSender responseSender) {
        BlockPos pos = buf.readBlockPos();

        server.execute(() -> {
            if (player.level instanceof ServerLevel level) {
                if (level.getBlockEntity(pos) instanceof TKClassicFloorMonitorEntity tile) {
                    tile.toggleLock();
                    level.sendBlockUpdated(pos, level.getBlockState(pos), level.getBlockState(pos), 3);
                }
            }
        });
    }
}