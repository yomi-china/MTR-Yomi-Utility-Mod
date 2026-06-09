package com.yomi.mtryum.registry;

import com.yomi.mtryum.Mtryum;
import com.yomi.mtryum.block.GlassLiftDoorBlock;
import com.yomi.mtryum.block.LiftArrivalLightBlock;
import com.yomi.mtryum.block.LiftArrivalSoundPlayerBlock;
import com.yomi.mtryum.block.MLFMBlock;
import com.yomi.mtryum.block.MitsubishiStyleLiftButtonsBlock;
import com.yomi.mtryum.block.OTIS3StyleLiftButtonsBlock;
import com.yomi.mtryum.block.OpaqueLiftDoorBlock;
import com.yomi.mtryum.block.TKClassicFloorMonitorBlock;
import com.yomi.mtryum.block.TKClassicLiftButtonsBlock;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

public class MtryumBlocks {
    public static final Block LIFT_ARRIVAL_LIGHT_BLOCK = new LiftArrivalLightBlock();
    public static final Block LIFT_ARRIVAL_SOUND_PLAYER_BLOCK = new LiftArrivalSoundPlayerBlock();
    public static final Block LIFT_FLOOR_MONITOR = new TKClassicFloorMonitorBlock();
    public static final Block MLFM = new MLFMBlock();
    public static final Block MITSUBISHI_STYLE_LIFT_BUTTONS = new MitsubishiStyleLiftButtonsBlock();
    public static final Block TK_STYLE_LIFT_BUTTONS = new TKClassicLiftButtonsBlock();
    public static final Block OTIS3_STYLE_LIFT_BUTTONS = new OTIS3StyleLiftButtonsBlock();
    public static final Block OPAQUE_LIFT_DOOR_1 = new OpaqueLiftDoorBlock();
    public static final Block GLASS_LIFT_DOOR_1 = new GlassLiftDoorBlock();


    public static void register() {
        registerBlock("lift_arrival_light", LIFT_ARRIVAL_LIGHT_BLOCK);
        registerBlock("lift_arrival_sound_player", LIFT_ARRIVAL_SOUND_PLAYER_BLOCK);
        registerBlock("lift_floor_monitor", LIFT_FLOOR_MONITOR);
        registerBlock("mitsubishi_floor_monitor", MLFM);
        registerBlock("mitsubishi_style_lift_buttons", MITSUBISHI_STYLE_LIFT_BUTTONS);
        registerBlock("tk_style_lift_buttons", TK_STYLE_LIFT_BUTTONS);
        registerBlock("otis_3200_style_lift_buttons", OTIS3_STYLE_LIFT_BUTTONS);
        registerBlock("opaque_lift_door_1", OPAQUE_LIFT_DOOR_1);
        registerBlock("glass_lift_door_1", GLASS_LIFT_DOOR_1);
    }

    private static void registerBlock(String path, Block block) {
        Registry.register(Registry.BLOCK, new ResourceLocation(Mtryum.MOD_ID, path), block);
    }
}