package com.yomi.mtryum.registry;

import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public class MtryumItemProperties {

    public static void register() {
        registerLinkingProperty(MtryumItems.LIFT_PARTS_LINK_CONNECTOR);
        registerLinkingProperty(MtryumItems.LIFT_PARTS_LINK_REMOVER);
    }

    private static void registerLinkingProperty(Item item) {
        ItemProperties.register(
                item,
                new ResourceLocation("mtryum", "linking"),
                (stack, level, entity, seed) -> stack.getOrCreateTag().contains("pos") ? 1.0F : 0.0F
        );
    }
}