package com.yomi.mtryum.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.yomi.mtryum.Mtryum;
import com.yomi.mtryum.block.MLFMBlock;
import com.yomi.mtryum.block.MLFMEntity;
import mtr.data.Lift;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

public class MLFMRender extends AbstractLiftFloorMonitorRenderer<MLFMEntity> {

    public MLFMRender() {
        this.arrowTexture = new ResourceLocation(Mtryum.MOD_ID, "textures/block/lift_arrow.png");
        this.arrowRenderMode = ArrowRenderMode.TEXTURE;
        this.floorColor = 0xFFFF8C00;
    }

    @Override
    protected BlockPos getTrackPosition(MLFMEntity entity, Level world) {
        return entity.getTrackPosition(world);
    }

    @Override
    protected Direction getFacing(MLFMEntity entity) {
        return entity.getBlockState().getValue(MLFMBlock.FACING);
    }

    @Override
    protected void updateLiftDirection(MLFMEntity entity, Lift.LiftDirection direction) {
        entity.updateLiftDirection(direction);
    }

    @Override
    protected void applyFacingTransform(PoseStack matrices, Direction facing) {
        super.applyFacingTransform(matrices, facing);
    }
}