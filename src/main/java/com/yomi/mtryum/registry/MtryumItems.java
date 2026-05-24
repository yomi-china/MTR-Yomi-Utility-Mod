package com.yomi.mtryum.registry;

import com.yomi.mtryum.Mtryum;
import com.yomi.mtryum.item.ItemLiftPartsLinkModifier;
import com.yomi.mtryum.item.ItemLiftAutoConnector;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;

public class MtryumItems {
    public static final Item LIFT_FLOOR_SETTER =
            registerItem("lift_floor_setter",
                    new Item(new FabricItemSettings().stacksTo(1)));
    public static final Item MOD_LOGO =
            registerItem("mod_logo",
                    new Item(new FabricItemSettings().stacksTo(1)));
    public static final Item LIFT_BUTTON_AUTO_CONNECTOR =
            registerItem("lift_button_auto_connector",
                    new ItemLiftAutoConnector());
    public static final Item LIFT_ARRIVAL_LIGHT =
            registerBlockItem("lift_arrival_light",
                    MtryumBlocks.LIFT_ARRIVAL_LIGHT_BLOCK);
    public static final Item LIFT_ARRIVAL_SOUND_PLAYER =
            registerBlockItem("lift_arrival_sound_player",
                    MtryumBlocks.LIFT_ARRIVAL_SOUND_PLAYER_BLOCK);
    public static final Item MITSUBISHI_STYLE_LIFT_BUTTONS =
            registerBlockItem("mitsubishi_style_lift_buttons",
                    MtryumBlocks.MITSUBISHI_STYLE_LIFT_BUTTONS);
    public static final Item TK_STYLE_LIFT_BUTTONS =
            registerBlockItem("tk_style_lift_buttons",
                    MtryumBlocks.TK_STYLE_LIFT_BUTTONS);
    public static final Item OTIS3_STYLE_LIFT_BUTTONS =
            registerBlockItem("otis_3200_style_lift_buttons",
                    MtryumBlocks.OTIS3_STYLE_LIFT_BUTTONS);
    public static final Item LIFT_FLOOR_MONITOR =
            registerBlockItem("lift_floor_monitor",
                    MtryumBlocks.LIFT_FLOOR_MONITOR);
    public static final Item MLFM =
            registerBlockItem("mitsubishi_floor_monitor",
                    MtryumBlocks.MLFM);
    public static final Item ONBOARD_TOOL =
            registerItem("onboard_tool",
                    new Item(new FabricItemSettings().stacksTo(1)));
    public static final Item OPAQUE_LIFT_DOOR_1 =
            registerBlockItem("opaque_lift_door_1",
                    MtryumBlocks.OPAQUE_LIFT_DOOR_1);
    public static final Item SMART_ANNOUNCER =
            registerBlockItem("smart_announcer",
                    MtryumBlocks.SMART_ANNOUNCER);
    public static final Item LIFT_PARTS_LINK_CONNECTOR =
            registerItem("lift_parts_link_connector",
                    new ItemLiftPartsLinkModifier(true));
    public static final Item LIFT_PARTS_LINK_REMOVER =
            registerItem("lift_parts_link_remover",
                    new ItemLiftPartsLinkModifier(false));

    private static Item registerItem(String name, Item item) {
        return Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(Mtryum.MOD_ID, name), item);
    }

    private static Item registerBlockItem(String name, net.minecraft.world.level.block.Block block) {
        return Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(Mtryum.MOD_ID, name), new BlockItem(block, new FabricItemSettings()));
    }

    public static void register() {
    }
}