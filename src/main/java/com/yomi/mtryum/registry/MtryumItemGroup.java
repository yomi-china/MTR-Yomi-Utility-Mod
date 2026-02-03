package com.yomi.mtryum.registry;

import com.yomi.mtryum.Mtryum;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class MtryumItemGroup {
    public static final CreativeModeTab TOOL_GROUP = FabricItemGroup.builder(new ResourceLocation(Mtryum.MOD_ID, "tool_group"))
            .title(Component.translatable("itemGroup.mtryum_tool_group"))
            .icon(() -> new ItemStack(MtryumItems.MOD_LOGO))
            .displayItems((parameters, output) -> {
                output.accept(new ItemStack(MtryumItems.LIFT_FLOOR_SETTER));
                output.accept(new ItemStack(MtryumItems.LIFT_BUTTON_AUTO_CONNECTOR));

            })
            .build();

    public static final CreativeModeTab BLOCK_GROUP = FabricItemGroup.builder(new ResourceLocation(Mtryum.MOD_ID, "block_group"))
            .title(Component.translatable("itemGroup.mtryum_block_group"))
            .icon(() -> new ItemStack(MtryumItems.MOD_LOGO))
            .displayItems((parameters, output) -> {
                output.accept(new ItemStack(MtryumItems.LIFT_ARRIVAL_LIGHT));
                output.accept(new ItemStack(MtryumItems.LIFT_ARRIVAL_SOUND_PLAYER));
                output.accept(new ItemStack(MtryumItems.LIFT_FLOOR_MONITOR));
                output.accept(new ItemStack(MtryumItems.MLFM));
                output.accept(new ItemStack(MtryumItems.MITSUBISHI_STYLE_LIFT_BUTTONS));
            })
            .build();

    public static void register() {

    }
}