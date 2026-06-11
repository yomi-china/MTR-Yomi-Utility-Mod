package com.yomi.mtryum.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.yomi.mtryum.block.GlassLiftDoorBlock;
import mtr.MTRClient;
import mtr.block.BlockPSDAPGDoorBase;
import mtr.block.IBlock;
import mtr.data.IGui;
import mtr.mappings.BlockEntityRendererMapper;
import mtr.mappings.ModelDataWrapper;
import mtr.mappings.ModelMapper;
import mtr.mappings.UtilitiesClient;
import mtr.render.RenderTrains;
import mtr.render.StoredMatrixTransformations;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;

public class RenderGlassLiftDoor extends BlockEntityRendererMapper<GlassLiftDoorBlock.GlassLiftDoorTileEntity> implements IGui, IBlock {

    private static final EntityModel<Entity> MODEL_LEFT = new ModelSingleCube(28, 18, 0, 0, 0, 12, 16, 2);
    private static final EntityModel<Entity> MODEL_RIGHT = new ModelSingleCube(28, 18, 4, 0, 0, 12, 16, 2);
    private static final EntityModel<Entity> MODEL_LOCKED = new ModelSingleCube(6, 6, 5, 6, 1, 6, 6, 0);

    public RenderGlassLiftDoor(BlockEntityRendererProvider.Context context) {
        super(context.getBlockEntityRenderDispatcher());
    }

    @Override
    public void render(GlassLiftDoorBlock.GlassLiftDoorTileEntity entity, float tickDelta, PoseStack matrices,
                       MultiBufferSource vertexConsumers, int light, int overlay) {
        final Level world = entity.getLevel();
        if (world == null) return;

        final BlockPos pos = entity.getBlockPos();
        final Direction facing = IBlock.getStatePropertySafe(world, pos, BlockPSDAPGDoorBase.FACING);
        final boolean side = IBlock.getStatePropertySafe(world, pos, BlockPSDAPGDoorBase.SIDE) == EnumSide.RIGHT;
        final boolean half = IBlock.getStatePropertySafe(world, pos, BlockPSDAPGDoorBase.HALF) == DoubleBlockHalf.UPPER;
        final boolean unlocked = IBlock.getStatePropertySafe(world, pos, BlockPSDAPGDoorBase.UNLOCKED);
        final float open = Math.min(entity.getOpen(MTRClient.getLastFrameDuration()), 1);

        final StoredMatrixTransformations baseTransform = new StoredMatrixTransformations();
        baseTransform.add(matricesNew -> {
            matricesNew.translate(0.5 + entity.getBlockPos().getX(), entity.getBlockPos().getY(), 0.5 + entity.getBlockPos().getZ());
            UtilitiesClient.rotateYDegrees(matricesNew, -facing.toYRot());
            UtilitiesClient.rotateXDegrees(matricesNew, 180);
        });

        baseTransform.add(matricesNew -> matricesNew.translate(open * (side ? -1 : 1), 0, 0));

        final ResourceLocation texture = new ResourceLocation(String.format(
                "mtryum:textures/block/glass_lift_door_%s_%s_1.png",
                half ? "top" : "bottom",
                side ? "right" : "left"
        ));

        RenderTrains.scheduleRender(texture, false, RenderTrains.QueuedRenderLayer.EXTERIOR, (matricesNew, vertexConsumer) -> {
            baseTransform.transform(matricesNew);
            (side ? MODEL_RIGHT : MODEL_LEFT).renderToBuffer(matricesNew, vertexConsumer, light, overlay, 1, 1, 1, 1);
            matricesNew.popPose();
        });

        if (half && !unlocked) {
            RenderTrains.scheduleRender(
                    new ResourceLocation("mtr:textures/block/sign/door_not_in_use.png"),
                    false,
                    RenderTrains.QueuedRenderLayer.EXTERIOR,
                    (matricesNew, vertexConsumer) -> {
                        baseTransform.transform(matricesNew);
                        matricesNew.translate(side ? 0.125 : -0.125, 0, 0);
                        MODEL_LOCKED.renderToBuffer(matricesNew, vertexConsumer, light, overlay, 1, 1, 1, 1);
                        matricesNew.popPose();
                    }
            );
        }
    }

    @Override
    public boolean shouldRenderOffScreen(GlassLiftDoorBlock.GlassLiftDoorTileEntity blockEntity) {
        return true;
    }
    
    private static class ModelSingleCube extends EntityModel<Entity> {
        private final ModelMapper cube;

        ModelSingleCube(int textureWidth, int textureHeight, int x, int y, int z, int length, int height, int depth) {
            final ModelDataWrapper modelDataWrapper = new ModelDataWrapper(this, textureWidth, textureHeight);
            cube = new ModelMapper(modelDataWrapper);
            cube.texOffs(0, 0).addBox(x - 8, y - 16, z - 8, length, height, depth, 0, false);
            modelDataWrapper.setModelPart(textureWidth, textureHeight);
            cube.setModelPart();
        }

        @Override
        public void renderToBuffer(PoseStack matrices, VertexConsumer vertices, int packedLight, int packedOverlay,
                                   float red, float green, float blue, float alpha) {
            cube.render(matrices, vertices, 0, 0, 0, packedLight, packedOverlay);
        }

        @Override
        public void setupAnim(Entity entity, float limbAngle, float limbDistance, float animationProgress,
                              float headYaw, float headPitch) {}
    }
}