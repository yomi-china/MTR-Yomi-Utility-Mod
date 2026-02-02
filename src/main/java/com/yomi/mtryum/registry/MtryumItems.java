package com.yomi.mtryum.registry;

import com.yomi.mtryum.Mtryum;
import com.yomi.mtryum.item.LiftAutoConnectorItem;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;

public class MtryumItems {
    public static final Item LIFT_FLOOR_SETTER = registerItem("lift_floor_setter", new Item(new FabricItemSettings().stacksTo(1)));
    public static final Item MOD_LOGO = registerItem("mod_logo", new Item(new FabricItemSettings().stacksTo(1)));
    public static final Item LIFT_BUTTON_AUTO_CONNECTOR = registerItem("lift_button_auto_connector", new LiftAutoConnectorItem());
    public static final Item LIFT_ARRIVAL_LIGHT = registerBlockItem("lift_arrival_light", MtryumBlocks.LIFT_ARRIVAL_LIGHT_BLOCK);
    public static final Item LIFT_ARRIVAL_SOUND_PLAYER = registerBlockItem("lift_arrival_sound_player", MtryumBlocks.LIFT_ARRIVAL_SOUND_PLAYER_BLOCK);
    public static final Item MITSUBISHI_STYLE_LIFT_BUTTONS = registerBlockItem("mitsubishi_style_lift_buttons", MtryumBlocks.MITSUBISHI_STYLE_LIFT_BUTTONS);
    public static final Item TK_STYLE_LIFT_BUTTONS = registerBlockItem("tk_style_lift_buttons", MtryumBlocks.TK_STYLE_LIFT_BUTTONS);
    public static final Item LIFT_FLOOR_MONITOR = registerBlockItem("lift_floor_monitor", MtryumBlocks.LIFT_FLOOR_MONITOR);
    public static final Item MLFM = registerBlockItem("mitsubishi_floor_monitor", MtryumBlocks.MLFM);
    public static final Item ONBOARD_TOOL = registerItem("onboard_tool", new Item(new FabricItemSettings().stacksTo(1)));

    private static Item registerItem(String name, Item item) {
        return Registry.register(Registry.ITEM, new ResourceLocation(Mtryum.MOD_ID, name), item);
    }

    private static Item registerBlockItem(String name, net.minecraft.world.level.block.Block block) {
        return Registry.register(Registry.ITEM, new ResourceLocation(Mtryum.MOD_ID, name), new BlockItem(block, new FabricItemSettings()));
    }

    public static void register() {

    }
}