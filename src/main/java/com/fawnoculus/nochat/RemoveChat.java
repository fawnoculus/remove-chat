package com.fawnoculus.nochat;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RemoveChat implements ModInitializer {
	public static final String MOD_ID = "remove-chat";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Remove Chat is Loaded");
	}
}