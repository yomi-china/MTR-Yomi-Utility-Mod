package com.yomi.mtryum.registry;

import com.yomi.mtryum.Mtryum;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;


public class MtryumItemGroup {
    public static final CreativeModeTab TOOL_GROUP = FabricItemGroupBuilder.create(new ResourceLocation(Mtryum.MOD_ID, "tool_group"))
            .icon(() -> new ItemStack(MtryumItems.MOD_LOGO))
            .appendItems(stacks -> {
                stacks.add(new ItemStack(MtryumItems.LIFT_FLOOR_SETTER));
                stacks.add(new ItemStack(MtryumItems.LIFT_BUTTON_AUTO_CONNECTOR));
            })
            .build();
    public static final CreativeModeTab BLOCK_GROUP = FabricItemGroupBuilder.create(new ResourceLocation(Mtryum.MOD_ID, "block_group"))
            .icon(() -> new ItemStack(MtryumItems.MOD_LOGO))
            .appendItems(stacks -> {
                stacks.add(new ItemStack(MtryumItems.LIFT_ARRIVAL_SOUND_PLAYER));
                stacks.add(new ItemStack(MtryumBlocks.LIFT_FLOOR_MONITOR));
                stacks.add(new ItemStack(MtryumBlocks.MLFM));
                stacks.add(new ItemStack(MtryumBlocks.MITSUBISHI_STYLE_LIFT_BUTTONS));
                stacks.add(new ItemStack(MtryumBlocks.TK_STYLE_LIFT_BUTTONS));
            })
            .build();

    public static void register() {

    }
}