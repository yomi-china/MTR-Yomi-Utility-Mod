package com.yomi.mtryum.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.yomi.mtryum.block.SmartAnnouncerBlock;
import com.yomi.mtryum.block.entity.TileEntitySmartAnnouncer;
import mtr.block.BlockTrainSensorBase;
import mtr.data.*;
import mtr.path.PathData;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Mixin(TrainServer.class)
public abstract class TrainServerMixin {

    @Shadow
    protected abstract int getIndex(double railProgress, int trainSpacing, boolean head);

    @Shadow
    private List<PathData> path;

    @Shadow
    private double railProgress;

    @Shadow
    private float speed;

    @Shadow
    private long routeId;

    @Shadow
    private Set<UUID> ridingEntities;

    @Inject(method = "handlePositions", at = @At(value = "INVOKE", target = "Lmtr/data/TrainServer;checkBlock(Lnet/minecraft/core/BlockPos;Ljava/util/function/Consumer;)V", shift = At.Shift.AFTER))
    private void afterCheckBlock(Level world, Vec3[] positions, float ticksElapsed, CallbackInfoReturnable<Boolean> cir,
                                 @Local(name = "frontPos") BlockPos frontPos) {
        if (ridingEntities == null || ridingEntities.isEmpty() || !RailwayData.chunkLoaded(world, frontPos)) {
            return;
        }

        int checkRadius = (int) Math.floor(speed);
        for (int x = -checkRadius; x <= checkRadius; x++) {
            for (int z = -checkRadius; z <= checkRadius; z++) {
                for (int y = 0; y <= 3; y++) {
                    BlockPos checkPos = frontPos.offset(x, -y, z);
                    if (RailwayData.chunkLoaded(world, checkPos)) {
                        BlockState state = world.getBlockState(checkPos);
                        Block block = state.getBlock();
                        if (block instanceof SmartAnnouncerBlock && BlockTrainSensorBase.matchesFilter(world, checkPos, routeId, speed)) {
                            BlockEntity entity = world.getBlockEntity(checkPos);
                            if (entity instanceof TileEntitySmartAnnouncer announcer) {
                                String nextStation = getNextStationName(world);
                                String terminalStation = getTerminalStationName(world);
                                for (UUID uuid : ridingEntities) {
                                    Player player = world.getPlayerByUUID(uuid);
                                    if (player != null) {
                                        announcer.announceFromTrain(player, speed, nextStation, terminalStation);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Unique
    private String getNextStationName(Level world) {
        RailwayData railwayData = RailwayData.getInstance(world);
        if (railwayData == null) return "";

        int headIndex = getIndex(railProgress, 0, false);
        for (int i = headIndex; i < path.size(); i++) {
            PathData pd = path.get(i);
            if (pd.dwellTime > 0 && pd.savedRailBaseId != 0) {
                Platform platform = railwayData.dataCache.platformIdMap.get(pd.savedRailBaseId);
                if (platform != null && platform.name != null) {
                    return platform.name;
                }
            }
        }
        return "";
    }

    @Unique
    private String getTerminalStationName(Level world) {
        RailwayData railwayData = RailwayData.getInstance(world);
        if (railwayData == null) return "";

        for (int i = path.size() - 1; i >= 0; i--) {
            PathData pd = path.get(i);
            if (pd.dwellTime > 0 && pd.savedRailBaseId != 0) {
                Platform platform = railwayData.dataCache.platformIdMap.get(pd.savedRailBaseId);
                if (platform != null && platform.name != null) {
                    return platform.name;
                }
            }
        }
        return "";
    }
}