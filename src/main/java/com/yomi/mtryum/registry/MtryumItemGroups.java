package com.yomi.mtryum.registry;

import com.yomi.mtryum.Mtryum;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;


public class MtryumItemGroups {
    public static final CreativeModeTab TOOL_GROUP = FabricItemGroupBuilder.create(new ResourceLocation(Mtryum.MOD_ID, "tool_group"))
            .icon(() -> new ItemStack(MtryumItems.MOD_LOGO))
            .appendItems(stacks -> {
                stacks.add(new ItemStack(MtryumItems.LIFT_FLOOR_SETTER));
                stacks.add(new ItemStack(MtryumItems.LIFT_BUTTON_AUTO_CONNECTOR));
                stacks.add(new ItemStack(MtryumItems.LIFT_PARTS_LINK_CONNECTOR));
                stacks.add(new ItemStack(MtryumItems.LIFT_PARTS_LINK_REMOVER));
                stacks.add(new ItemStack(MtryumItems.ONBOARD_TOOL));
                stacks.add(new ItemStack(MtryumItems.LIFT_ATTRIBUTE_COPIER));
            })
            .build();
    public static final CreativeModeTab BLOCK_GROUP = FabricItemGroupBuilder.create(new ResourceLocation(Mtryum.MOD_ID, "block_group"))
            .icon(() -> new ItemStack(MtryumItems.MOD_LOGO))
            .appendItems(stacks -> {
                stacks.add(new ItemStack(MtryumItems.LIFT_ARRIVAL_LIGHT));
                stacks.add(new ItemStack(MtryumItems.LIFT_ARRIVAL_SOUND_PLAYER));
                stacks.add(new ItemStack(MtryumItems.LIFT_FLOOR_MONITOR));
                stacks.add(new ItemStack(MtryumItems.MLFM));
                stacks.add(new ItemStack(MtryumItems.MITSUBISHI_STYLE_LIFT_BUTTONS));
                stacks.add(new ItemStack(MtryumItems.TK_STYLE_LIFT_BUTTONS));
                stacks.add(new ItemStack(MtryumItems.OTIS3_STYLE_LIFT_BUTTONS));
                stacks.add(new ItemStack(MtryumItems.OPAQUE_LIFT_DOOR_1));
                stacks.add(new ItemStack(MtryumItems.GLASS_LIFT_DOOR_1));
            })
            .build();

    public static void register() {

    }
}