package com.yomi.mtryum.network;

import com.yomi.mtryum.block.LiftArrivalSoundPlayerEntity;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.level.block.entity.BlockEntity;

public class SetSoundIndexPacket {
    public static final ResourceLocation ID = new ResourceLocation("mtryum", "set_sound_index");

    public static void register() {
        ServerPlayNetworking.registerGlobalReceiver(
                ID,
                SetSoundIndexPacket::receive
        );
    }

    public static void registerClient() {

    }

    public static void send(BlockPos pos, int soundIndex) {
        FriendlyByteBuf buf = PacketByteBufs.create();
        buf.writeBlockPos(pos);
        buf.writeInt(soundIndex);
        ClientPlayNetworking.send(ID, buf);
    }

    public static void receive(MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl handler, FriendlyByteBuf buf, PacketSender responseSender) {
        BlockPos pos = buf.readBlockPos();
        int soundIndex = buf.readInt();

        server.execute(() -> {
            ServerLevel world = (ServerLevel) player.level();
            if (world.hasChunkAt(pos)) {
                BlockEntity entity = world.getBlockEntity(pos);
                if (entity instanceof LiftArrivalSoundPlayerEntity) {
                    ((LiftArrivalSoundPlayerEntity) entity).setSoundIndexViaCommand(soundIndex);
                }
            }
        });
    }
}