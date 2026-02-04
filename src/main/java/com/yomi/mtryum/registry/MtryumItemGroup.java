package com.yomi.mtryum.registry;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class MtryumItemGroup {
    public static final CreativeModeTab TOOL_GROUP;
    public static final CreativeModeTab BLOCK_GROUP;

    static {
        TOOL_GROUP = FabricItemGroup.builder(new ResourceLocation("mtryum", "tool_group"))
                .title(Component.translatable("itemGroup.mtryum.tool_group"))
                .icon(() -> new ItemStack(MtryumItems.MOD_LOGO))
                .displayItems((parameters, output) -> {
                    output.accept(new ItemStack(MtryumItems.LIFT_FLOOR_SETTER));
                    output.accept(new ItemStack(MtryumItems.LIFT_BUTTON_AUTO_CONNECTOR));
                    output.accept(new ItemStack(MtryumItems.ONBOARD_TOOL));
                })
                .build();

        BLOCK_GROUP = FabricItemGroup.builder(new ResourceLocation("mtryum", "block_group"))
                .title(Component.translatable("itemGroup.mtryum.block_group"))
                .icon(() -> new ItemStack(MtryumItems.MOD_LOGO))
                .displayItems((parameters, output) -> {
                    output.accept(new ItemStack(MtryumItems.LIFT_ARRIVAL_LIGHT));
                    output.accept(new ItemStack(MtryumItems.LIFT_ARRIVAL_SOUND_PLAYER));
                    output.accept(new ItemStack(MtryumItems.LIFT_FLOOR_MONITOR));
                    output.accept(new ItemStack(MtryumItems.MLFM));
                    output.accept(new ItemStack(MtryumItems.MITSUBISHI_STYLE_LIFT_BUTTONS));
                    output.accept(new ItemStack(MtryumItems.TK_STYLE_LIFT_BUTTONS));
                })
                .build();
    }

    public static void register() {

    }
}