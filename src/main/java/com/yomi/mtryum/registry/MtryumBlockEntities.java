package com.yomi.mtryum.registry;

import com.yomi.mtryum.Mtryum;
import com.yomi.mtryum.block.LiftArrivalLightBlock;
import com.yomi.mtryum.block.LiftArrivalSoundPlayerEntity;
import com.yomi.mtryum.block.MLFMEntity;
import com.yomi.mtryum.block.MitsubishiStyleLiftButtonsBlockEntity;
import com.yomi.mtryum.block.OTIS3StyleLiftButtonsBlockEntity;
import com.yomi.mtryum.block.OpaqueLiftDoorBlock;
import com.yomi.mtryum.block.TKClassicFloorMonitorEntity;
import com.yomi.mtryum.block.TKClassicLiftButtonsBlockEntity;
import com.yomi.mtryum.block.SmartAnnouncerBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class MtryumBlockEntities {
    public static BlockEntityType<LiftArrivalSoundPlayerEntity> LIFT_ARRIVAL_SOUND_PLAYER_ENTITY;
    public static BlockEntityType<LiftArrivalLightBlock.Entity> LIFT_ARRIVAL_LIGHT_BLOCK_ENTITY;
    public static BlockEntityType<TKClassicFloorMonitorEntity> LIFT_FLOOR_MONITOR;
    public static BlockEntityType<MLFMEntity> MLFM_ENTITY;
    public static BlockEntityType<MitsubishiStyleLiftButtonsBlockEntity> MITSUBISHI_STYLE_LIFT_BUTTONS_ENTITY;
    public static BlockEntityType<TKClassicLiftButtonsBlockEntity> TK_STYLE_LIFT_BUTTONS_ENTITY;
    public static BlockEntityType<OTIS3StyleLiftButtonsBlockEntity> OTIS3_STYLE_LIFT_BUTTONS_ENTITY;
    public static BlockEntityType<OpaqueLiftDoorBlock.OpaqueLiftDoorTileEntity> OPAQUE_LIFT_DOOR_TILE_ENTITY;
    public static BlockEntityType<SmartAnnouncerBlockEntity> SMART_ANNOUNCER_TILE_ENTITY;

    public static void register() {
        LIFT_ARRIVAL_LIGHT_BLOCK_ENTITY = Registry.register(
                BuiltInRegistries.BLOCK_ENTITY_TYPE,
                new ResourceLocation(Mtryum.MOD_ID, "lift_arrival_light_block_entity"),
                FabricBlockEntityTypeBuilder.create(
                        LiftArrivalLightBlock.Entity::new,
                        MtryumBlocks.LIFT_ARRIVAL_LIGHT_BLOCK
                ).build(null)
        );
        LIFT_ARRIVAL_SOUND_PLAYER_ENTITY = Registry.register(
                BuiltInRegistries.BLOCK_ENTITY_TYPE,
                new ResourceLocation(Mtryum.MOD_ID, "lift_arrival_sound_player_entity"),
                FabricBlockEntityTypeBuilder.create(
                        LiftArrivalSoundPlayerEntity::new,
                        MtryumBlocks.LIFT_ARRIVAL_SOUND_PLAYER_BLOCK
                ).build(null)
        );
        LIFT_FLOOR_MONITOR = Registry.register(
                BuiltInRegistries.BLOCK_ENTITY_TYPE,
                new ResourceLocation(Mtryum.MOD_ID, "lift_floor_monitor"),
                FabricBlockEntityTypeBuilder.create(
                        (pos, state) -> new TKClassicFloorMonitorEntity(LIFT_FLOOR_MONITOR, pos, state),
                        MtryumBlocks.LIFT_FLOOR_MONITOR
                ).build(null)
        );
        MLFM_ENTITY = Registry.register(
                BuiltInRegistries.BLOCK_ENTITY_TYPE,
                new ResourceLocation(Mtryum.MOD_ID, "mitsubishi_floor_monitor"),
                FabricBlockEntityTypeBuilder.create(
                        (pos, state) -> new MLFMEntity(MLFM_ENTITY, pos, state),
                        MtryumBlocks.MLFM
                ).build(null)
        );
        MITSUBISHI_STYLE_LIFT_BUTTONS_ENTITY = Registry.register(
                BuiltInRegistries.BLOCK_ENTITY_TYPE,
                new ResourceLocation(Mtryum.MOD_ID, "mitsubishi_style_lift_buttons"),
                FabricBlockEntityTypeBuilder.create(
                        MitsubishiStyleLiftButtonsBlockEntity::new,
                        MtryumBlocks.MITSUBISHI_STYLE_LIFT_BUTTONS
                ).build(null)
        );
        TK_STYLE_LIFT_BUTTONS_ENTITY = Registry.register(
                BuiltInRegistries.BLOCK_ENTITY_TYPE,
                new ResourceLocation(Mtryum.MOD_ID, "tk_style_lift_buttons"),
                FabricBlockEntityTypeBuilder.create(
                        TKClassicLiftButtonsBlockEntity::new,
                        MtryumBlocks.TK_STYLE_LIFT_BUTTONS
                ).build(null)
        );
        OTIS3_STYLE_LIFT_BUTTONS_ENTITY = Registry.register(
                BuiltInRegistries.BLOCK_ENTITY_TYPE,
                new ResourceLocation(Mtryum.MOD_ID, "otis_3200_style_lift_buttons"),
                FabricBlockEntityTypeBuilder.create(
                        OTIS3StyleLiftButtonsBlockEntity::new,
                        MtryumBlocks.OTIS3_STYLE_LIFT_BUTTONS
                ).build(null)
        );
        OPAQUE_LIFT_DOOR_TILE_ENTITY = Registry.register(
                BuiltInRegistries.BLOCK_ENTITY_TYPE,
                new ResourceLocation(Mtryum.MOD_ID, "opaque_lift_door_tile_entity"),
                FabricBlockEntityTypeBuilder.create(
                        OpaqueLiftDoorBlock.OpaqueLiftDoorTileEntity::new,
                        MtryumBlocks.OPAQUE_LIFT_DOOR_1
                ).build(null)
        );
        SMART_ANNOUNCER_TILE_ENTITY = Registry.register(
                BuiltInRegistries.BLOCK_ENTITY_TYPE,
                new ResourceLocation(Mtryum.MOD_ID, "smart_announcer_tile_entity"),
                FabricBlockEntityTypeBuilder.create(
                        SmartAnnouncerBlockEntity::new,
                        MtryumBlocks.SMART_ANNOUNCER
                ).build(null)
        );
    }
}