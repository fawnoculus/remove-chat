package net.fawnoculus.nochat;


import net.fawnoculus.nochat.mixin.ServerPlayerEntityInvoker;
import net.fawnoculus.nochat.util.config.ConfigFile;
import net.fawnoculus.nochat.util.config.filetype.JsonConfigFile;
import net.fawnoculus.nochat.util.config.options.BooleanOption;
import net.fawnoculus.nochat.util.config.options.StringListOption;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.List;

public class RemoveChatConfig {
	public static ConfigFile CONFIG_FILE = new ConfigFile("remove-chat", new JsonConfigFile(), RemoveChat.LOGGER);
	public static final BooleanOption DISABLE_CHAT = CONFIG_FILE.newBooleanOption("disable_chat", true, "Whether the Chat should be disabled");
	public static final BooleanOption DISABLE_MSG_COMMANDS = CONFIG_FILE.newBooleanOption("disable_msg_commands", true, "Whether the /msg commands should be disabled");
	public static final BooleanOption DISABLE_TEAM_MSG = CONFIG_FILE.newBooleanOption("disable_team_msg", true, "Whether the /teammsg command should be disabled");
	public static final BooleanOption EXCLUDE_OPS = CONFIG_FILE.newBooleanOption("exclude_ops", true, "Whether the Operators should be excluded from disabled chats/commands");
	public static final StringListOption EXCLUDED_PLAYERS = CONFIG_FILE.newStringListOption("excluded_players", List.of("ExamplePlayer", "OtherPlayer"), "players that should be excluded from disabled chats or commands");
	public static final BooleanOption LOG_STACKTRACE = CONFIG_FILE.newBooleanOption("log_stacktrace", false, "Whether to log Stacktrace on Exceptions ,only usefully for debugging");
	
	public static boolean playerNotExcluded(ServerPlayerEntity player){
		if(EXCLUDE_OPS.getValue() && ((ServerPlayerEntityInvoker) player).REMOVE_CHAT$getPermissionLevel() >= 2){
			return false;
		}
    return !EXCLUDED_PLAYERS.getValue().contains(player.getName().getLiteralString());
  }
	
	public static void initialize(){
		CONFIG_FILE.initialize();
	}
}