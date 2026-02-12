package com.yomi.mtryum.registry;

import com.yomi.mtryum.Mtryum;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class MtryumItemGroup {
    public static final CreativeModeTab TOOL_GROUP = Registry.register(
            BuiltInRegistries.CREATIVE_MODE_TAB,
            new ResourceLocation(Mtryum.MOD_ID, "tool_group"),
            FabricItemGroup.builder()
                    .title(Component.translatable("itemGroup.mtryum.tool_group"))
                    .icon(() -> new ItemStack(MtryumItems.MOD_LOGO))
                    .displayItems((parameters, output) -> {
                        output.accept(MtryumItems.LIFT_FLOOR_SETTER);
                        output.accept(MtryumItems.LIFT_BUTTON_AUTO_CONNECTOR);
                        output.accept(MtryumItems.ONBOARD_TOOL);

                    })
                    .build()
    );

    public static final CreativeModeTab BLOCK_GROUP = Registry.register(
            BuiltInRegistries.CREATIVE_MODE_TAB,
            new ResourceLocation(Mtryum.MOD_ID, "block_group"),
            FabricItemGroup.builder()
                    .title(Component.translatable("itemGroup.mtryum.block_group"))
                    .icon(() -> new ItemStack(MtryumItems.MOD_LOGO))
                    .displayItems((parameters, output) -> {
                        output.accept(new ItemStack(MtryumItems.LIFT_ARRIVAL_LIGHT));
                        output.accept(new ItemStack(MtryumItems.LIFT_ARRIVAL_SOUND_PLAYER));
                        output.accept(new ItemStack(MtryumItems.LIFT_FLOOR_MONITOR));
                        output.accept(new ItemStack(MtryumItems.MLFM));
                        output.accept(new ItemStack(MtryumItems.MITSUBISHI_STYLE_LIFT_BUTTONS));
                        output.accept(new ItemStack(MtryumItems.TK_STYLE_LIFT_BUTTONS));
                        output.accept(new ItemStack(MtryumItems.OTIS3_STYLE_LIFT_BUTTONS));
                        output.accept(new ItemStack(MtryumItems.OPAQUE_LIFT_DOOR_1));
                    })
                    .build()
    );

    public static void register() {

    }
}