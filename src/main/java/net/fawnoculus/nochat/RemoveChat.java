package net.fawnoculus.nochat;


import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fawnoculus.nochat.commands.RemoveChatCommands;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class RemoveChat implements ModInitializer {
	public static final String MOD_ID = "remove-chat";
	public static final String MOD_NAME = Objects.requireNonNull(FabricLoader.getInstance().getModContainer(MOD_ID).orElse(null)).getMetadata().getName();
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

	@Override
	public void onInitialize() {
		RemoveChatConfig.initialize();
		RemoveChatCommands.initialize();
	}
}