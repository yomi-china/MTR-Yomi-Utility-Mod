package com.yomi.mtryum;

import com.yomi.mtryum.block.LiftArrivalSoundPlayerEntity;
import com.yomi.mtryum.block.TKClassicFloorMonitorEntity;
import com.yomi.mtryum.network.SetSoundIndexPacket;
import com.yomi.mtryum.registry.MtryumBlockEntities;
import com.yomi.mtryum.registry.MtryumBlocks;
import com.yomi.mtryum.render.CustomFontManager;
import com.yomi.mtryum.render.CustomFontRenderer;
import com.yomi.mtryum.render.LiftArrivalLightRenderer;
import com.yomi.mtryum.render.MLFMRender;
import com.yomi.mtryum.render.MitsubishiStyleLiftButtonsRenderer;
import com.yomi.mtryum.render.TKClassicFloorMonitorRenderer;
import com.yomi.mtryum.render.TKClassicLiftButtonsRenderer;
import com.yomi.mtryum.screen.LiftArrivalSoundPlayerScreen;
import com.yomi.mtryum.screen.LiftFloorMonitorScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.block.entity.BlockEntity;

public class MtryumClient implements ClientModInitializer {

    private static BlockPos pendingFMScreenPos;
    private static BlockPos pendingASPScreenPos;

    public static void scheduleFMScreenOpen(BlockPos pos) {
        pendingFMScreenPos = pos;
    }
    public static void scheduleASPScreenOpen(BlockPos pos) {
        pendingASPScreenPos = pos;
    }


    @Override
    public void onInitializeClient() {
        BlockRenderLayerMap.INSTANCE.putBlock(
                MtryumBlocks.LIFT_ARRIVAL_LIGHT_BLOCK,
                RenderType.cutout()
        );
        BlockRenderLayerMap.INSTANCE.putBlock(
                MtryumBlocks.LIFT_ARRIVAL_SOUND_PLAYER_BLOCK,
                RenderType.cutout()
        );
        BlockRenderLayerMap.INSTANCE.putBlock(
                MtryumBlocks.LIFT_FLOOR_MONITOR,
                RenderType.cutout()
        );
        BlockRenderLayerMap.INSTANCE.putBlock(
                MtryumBlocks.MLFM,
                RenderType.cutout()
        );
        BlockRenderLayerMap.INSTANCE.putBlock(
                MtryumBlocks.MITSUBISHI_STYLE_LIFT_BUTTONS,
                RenderType.cutout()
        );
        BlockRenderLayerMap.INSTANCE.putBlock(
                MtryumBlocks.TK_STYLE_LIFT_BUTTONS,
                RenderType.cutout()
        );
        BlockEntityRendererRegistry.register(
                MtryumBlockEntities.LIFT_ARRIVAL_LIGHT_BLOCK_ENTITY,
                LiftArrivalLightRenderer::new
        );
        BlockEntityRendererRegistry.register(
                MtryumBlockEntities.LIFT_FLOOR_MONITOR,
                ctx -> new TKClassicFloorMonitorRenderer()
        );
        BlockEntityRendererRegistry.register(
                MtryumBlockEntities.MLFM_ENTITY,
                ctx -> new MLFMRender()
        );
        BlockEntityRendererRegistry.register(
                MtryumBlockEntities.MITSUBISHI_STYLE_LIFT_BUTTONS_ENTITY,
                MitsubishiStyleLiftButtonsRenderer::new
        );
        BlockEntityRendererRegistry.register(
                MtryumBlockEntities.TK_STYLE_LIFT_BUTTONS_ENTITY,
                TKClassicLiftButtonsRenderer::new
        );
        CustomFontManager fontManager = CustomFontManager.getInstance();
        fontManager.initialize();
        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES)
                .registerReloadListener(new SimpleSynchronousResourceReloadListener() {
                    @Override
                    public void onResourceManagerReload(ResourceManager resourceManager) {
                        CustomFontManager.getInstance().clearCache();
                        CustomFontRenderer.cleanupAll();
                    }

                    @Override
                    public ResourceLocation getFabricId() {
                        return new ResourceLocation(Mtryum.MOD_ID, "font_reloader");
                    }
                });
        SetSoundIndexPacket.registerClient();
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player != null) {
                if (client.screen == null) {
                    if (pendingFMScreenPos != null) {
                        if (client.level != null) {
                            BlockEntity entity = client.level.getBlockEntity(pendingFMScreenPos);
                            if (entity instanceof TKClassicFloorMonitorEntity) {
                                client.setScreen(new LiftFloorMonitorScreen(pendingFMScreenPos));
                            }
                        }
                        pendingFMScreenPos = null;
                    }

                    if (pendingASPScreenPos != null) {
                        if (client.level != null) {
                            BlockEntity entity = client.level.getBlockEntity(pendingASPScreenPos);
                            if (entity instanceof LiftArrivalSoundPlayerEntity) {
                                client.setScreen(new LiftArrivalSoundPlayerScreen(pendingASPScreenPos));
                            }
                        }
                        pendingASPScreenPos = null;
                    }
                }
            }
        });
    }
}