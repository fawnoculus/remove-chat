package net.fawnoculus.nochat.commands;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fawnoculus.nochat.RemoveChatConfig;
import net.fawnoculus.nochat.util.config.options.BooleanOption;
import net.fawnoculus.nochat.util.config.options.Option;
import net.minecraft.command.DefaultPermissions;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RemoveChatCommands {
  public static void initialize() {
    CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
      dispatcher.register(CommandManager.literal("remove_chat")
          .requires(source -> source.getPermissions().hasPermission(DefaultPermissions.MODERATORS))
          .then(CommandManager.literal("disable_chat")
              .executes(context -> getOptionInfo(context, RemoveChatConfig.DISABLE_CHAT))
              .then(CommandManager.argument("boolean", BoolArgumentType.bool())
                  .executes(context -> setBoolOption(context, RemoveChatConfig.DISABLE_CHAT))
              )
          )
          .then(CommandManager.literal("disable_msg_commands")
              .executes(context -> getOptionInfo(context, RemoveChatConfig.DISABLE_MSG_COMMANDS))
              .then(CommandManager.argument("boolean", BoolArgumentType.bool())
                  .executes(context -> setBoolOption(context, RemoveChatConfig.DISABLE_MSG_COMMANDS))
              )
          )
          .then(CommandManager.literal("disable_teammsg")
              .executes(context -> getOptionInfo(context, RemoveChatConfig.DISABLE_TEAM_MSG))
              .then(CommandManager.argument("boolean", BoolArgumentType.bool())
                  .executes(context -> setBoolOption(context, RemoveChatConfig.DISABLE_TEAM_MSG))
              )
          )
          .then(CommandManager.literal("exclude_ops")
              .executes(context -> getOptionInfo(context, RemoveChatConfig.EXCLUDE_OPS))
              .then(CommandManager.argument("boolean", BoolArgumentType.bool())
                  .executes(context -> setBoolOption(context, RemoveChatConfig.EXCLUDE_OPS))
              )
          )
          .then(CommandManager.literal("excluded_players")
              .executes(context -> getOptionInfo(context, RemoveChatConfig.EXCLUDED_PLAYERS))
              .then(CommandManager.literal("add")
                  .then(CommandManager.argument("players", EntityArgumentType.players())
                      .executes(RemoveChatCommands::addExcludedPlayer))
              )
              .then(CommandManager.literal("remove")
                  .then(CommandManager.argument("players", EntityArgumentType.players())
                      .executes(RemoveChatCommands::removeExcludedPlayer))
              )
              .then(CommandManager.literal("list")
                  .executes(RemoveChatCommands::listExcludedPlayer)
              )
          )
      );
    });
  }
  
  public static int getOptionInfo(CommandContext<ServerCommandSource> context, Option<?> option){
    context.getSource().sendFeedback(() -> Text.literal(option.COMMENT), false);
    return 1;
  }
  public static int setBoolOption(CommandContext<ServerCommandSource> context, BooleanOption option){
    Boolean value = context.getArgument("boolean", Boolean.class);
    option.setValue(value);
    RemoveChatConfig.CONFIG_FILE.writeFile();
    context.getSource().sendFeedback(() -> Text.literal("Set Option " + option.NAME + " to '" + value + "'"), true);
    return 1;
  }
  public static int addExcludedPlayer(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
    Collection<ServerPlayerEntity> players = EntityArgumentType.getPlayers(context, "players");
    List<String> playerNames = new ArrayList<>(RemoveChatConfig.EXCLUDED_PLAYERS.getValue());
    for(ServerPlayerEntity player : players){
      playerNames.add(player.getName().getLiteralString());
    }
    RemoveChatConfig.EXCLUDED_PLAYERS.setValue(playerNames);
    RemoveChatConfig.CONFIG_FILE.writeFile();
    context.getSource().sendFeedback(() -> Text.literal("Added " + players.size() + " Players to Excluded Player List"), true);
    return 1;
  }
  public static int removeExcludedPlayer(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
    Collection<ServerPlayerEntity> players = EntityArgumentType.getPlayers(context, "players");
    List<String> playerNames = new ArrayList<>(RemoveChatConfig.EXCLUDED_PLAYERS.getValue());
    for(ServerPlayerEntity player : players){
      playerNames.remove(player.getName().getLiteralString());
    }
    RemoveChatConfig.EXCLUDED_PLAYERS.setValue(playerNames);
    RemoveChatConfig.CONFIG_FILE.writeFile();
    context.getSource().sendFeedback(() -> Text.literal("Removed " + players.size() + " Players from Excluded Player List"), true);
    return 1;
  }
  public static int listExcludedPlayer(CommandContext<ServerCommandSource> context){
    ServerCommandSource source = context.getSource();
    List<String> playerNames = RemoveChatConfig.EXCLUDED_PLAYERS.getValue();
    source.sendFeedback(() -> Text.literal("There are currently " + playerNames.size() + " Excluded Players:"), false);
    for(String name : playerNames){
      source.sendFeedback(() -> Text.literal(name).formatted(Formatting.GRAY), false);
    }
    return 1;
  }
}
