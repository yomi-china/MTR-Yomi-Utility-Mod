package com.yomi.mtryum;

import com.yomi.mtryum.block.LiftArrivalSoundPlayerEntity;
import com.yomi.mtryum.block.TKClassicFloorMonitorEntity;
import com.yomi.mtryum.network.SetSoundIndexPacket;
import com.yomi.mtryum.registry.MtryumBlockEntities;
import com.yomi.mtryum.registry.MtryumBlocks;
import com.yomi.mtryum.registry.MtryumCustomFontManager;
import com.yomi.mtryum.registry.MtryumItemProperties;
import com.yomi.mtryum.render.RenderGlassLiftDoor;
import com.yomi.mtryum.render.RenderMLFM;
import com.yomi.mtryum.render.RenderOpaqueLiftDoor;
import com.yomi.mtryum.render.RendererCustomFont;
import com.yomi.mtryum.render.RendererLiftArrivalLight;
import com.yomi.mtryum.render.RendererMitsubishiStyleLiftButtons;
import com.yomi.mtryum.render.RendererOTIS3StyleLiftButtons;
import com.yomi.mtryum.render.RendererTKClassicFloorMonitor;
import com.yomi.mtryum.render.RendererTKClassicLiftButtons;
import com.yomi.mtryum.screen.LiftArrivalSoundPlayerScreen;
import com.yomi.mtryum.screen.TKClassicFloorMonitorScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
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
        BlockRenderLayerMap.INSTANCE.putBlock(MtryumBlocks.LIFT_ARRIVAL_LIGHT_BLOCK, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(MtryumBlocks.LIFT_ARRIVAL_SOUND_PLAYER_BLOCK, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(MtryumBlocks.LIFT_FLOOR_MONITOR, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(MtryumBlocks.MLFM, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(MtryumBlocks.MITSUBISHI_STYLE_LIFT_BUTTONS, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(MtryumBlocks.TK_STYLE_LIFT_BUTTONS, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(MtryumBlocks.OTIS3_STYLE_LIFT_BUTTONS, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(MtryumBlocks.OPAQUE_LIFT_DOOR_1, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(MtryumBlocks.GLASS_LIFT_DOOR_1, RenderType.cutout());

        BlockEntityRenderers.register(
                MtryumBlockEntities.LIFT_ARRIVAL_LIGHT_BLOCK_ENTITY,
                context -> new RendererLiftArrivalLight()
        );
        BlockEntityRenderers.register(
                MtryumBlockEntities.LIFT_FLOOR_MONITOR,
                ctx -> new RendererTKClassicFloorMonitor()
        );
        BlockEntityRenderers.register(
                MtryumBlockEntities.MLFM_ENTITY,
                ctx -> new RenderMLFM()
        );
        BlockEntityRenderers.register(
                MtryumBlockEntities.MITSUBISHI_STYLE_LIFT_BUTTONS_ENTITY,
                context -> new RendererMitsubishiStyleLiftButtons()
        );
        BlockEntityRenderers.register(
                MtryumBlockEntities.TK_STYLE_LIFT_BUTTONS_ENTITY,
                context1 -> new RendererTKClassicLiftButtons()
        );
        BlockEntityRenderers.register(
                MtryumBlockEntities.OTIS3_STYLE_LIFT_BUTTONS_ENTITY,
                context -> new RendererOTIS3StyleLiftButtons()
        );
        BlockEntityRenderers.register(
                MtryumBlockEntities.OPAQUE_LIFT_DOOR_TILE_ENTITY,
                RenderOpaqueLiftDoor::new
        );
        BlockEntityRenderers.register(
                MtryumBlockEntities.GLASS_LIFT_DOOR_TILE_ENTITY,
                RenderGlassLiftDoor::new
        );
        MtryumCustomFontManager fontManager = MtryumCustomFontManager.getInstance();
        fontManager.initialize();
        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES)
                .registerReloadListener(new SimpleSynchronousResourceReloadListener() {
                    @Override
                    public void onResourceManagerReload(ResourceManager resourceManager) {
                        MtryumCustomFontManager.getInstance().clearCache();
                        RendererCustomFont.cleanupAll();
                    }

                    @Override
                    public ResourceLocation getFabricId() {
                        return new ResourceLocation(Mtryum.MOD_ID, "font_reloader");
                    }
                });
        SetSoundIndexPacket.registerClient();
        MtryumItemProperties.register();
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player != null) {
                if (client.screen == null) {
                    if (pendingFMScreenPos != null) {
                        if (client.level != null) {
                            BlockEntity entity = client.level.getBlockEntity(pendingFMScreenPos);
                            if (entity instanceof TKClassicFloorMonitorEntity) {
                                client.setScreen(new TKClassicFloorMonitorScreen(pendingFMScreenPos));
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