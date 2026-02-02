package com.yomi.mtryum;

import com.yomi.mtryum.item.LiftFloorSetterItem;
import com.yomi.mtryum.network.SetSoundIndexPacket;
import com.yomi.mtryum.registry.MtryumBlockEntities;
import com.yomi.mtryum.registry.MtryumBlocks;
import com.yomi.mtryum.registry.MtryumItemGroup;
import com.yomi.mtryum.registry.MtryumItems;
import com.yomi.mtryum.registry.MtryumSounds;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Mtryum implements ModInitializer {
	public static final String MOD_ID = "mtryum";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		MtryumBlocks.register();
		MtryumItems.register();
		MtryumBlockEntities.register();
		MtryumItemGroup.register();
		MtryumSounds.register();
        SetSoundIndexPacket.register();
		UseBlockCallback.EVENT.register(LiftFloorSetterItem::onBlockUse);
		LOGGER.info("MTR Yomi Utility Mod Initialized!");
	}
}