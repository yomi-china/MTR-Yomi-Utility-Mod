package com.yomi.mtryum.registry;

import com.yomi.mtryum.Mtryum;
import com.yomi.mtryum.block.LiftArrivalLightBlock;
import com.yomi.mtryum.block.LiftArrivalSoundPlayerBlock;
import com.yomi.mtryum.block.LiftFloorMonitorBlock;
import com.yomi.mtryum.block.MLFMBlock;
import com.yomi.mtryum.block.MitsubishiStyleLiftButtonsBlock;
import com.yomi.mtryum.block.TKClassicLiftButtonsBlock;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

public class MtryumBlocks {
    public static final Block LIFT_ARRIVAL_LIGHT_BLOCK = new LiftArrivalLightBlock();
    public static final Block LIFT_ARRIVAL_SOUND_PLAYER_BLOCK = new LiftArrivalSoundPlayerBlock();
    public static final Block LIFT_FLOOR_MONITOR = new LiftFloorMonitorBlock();
    public static final Block MLFM = new MLFMBlock();
    public static final Block MITSUBISHI_STYLE_LIFT_BUTTONS = new MitsubishiStyleLiftButtonsBlock();
    public static final Block TK_STYLE_LIFT_BUTTONS = new TKClassicLiftButtonsBlock();


    public static void register() {
        Registry.register(
                Registry.BLOCK,
                new ResourceLocation(Mtryum.MOD_ID, "lift_arrival_light"),
                LIFT_ARRIVAL_LIGHT_BLOCK
        );
        Registry.register(
                Registry.BLOCK,
                new ResourceLocation(Mtryum.MOD_ID, "lift_arrival_sound_player"),
                LIFT_ARRIVAL_SOUND_PLAYER_BLOCK
        );
        Registry.register(
                Registry.BLOCK,
                new ResourceLocation(Mtryum.MOD_ID, "lift_floor_monitor"),
                LIFT_FLOOR_MONITOR
        );
        Registry.register(
                Registry.BLOCK,
                new ResourceLocation(Mtryum.MOD_ID, "mitsubishi_floor_monitor"),
                MLFM
        );
        Registry.register(
                Registry.BLOCK,
                new ResourceLocation(Mtryum.MOD_ID, "mitsubishi_style_lift_buttons"),
                MITSUBISHI_STYLE_LIFT_BUTTONS
        );
        Registry.register(
                Registry.BLOCK,
                new ResourceLocation(Mtryum.MOD_ID, "tk_style_lift_buttons"),
                TK_STYLE_LIFT_BUTTONS
        );
    }
}