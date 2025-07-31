package net.fawnoculus.nochat.mixin;

import net.fawnoculus.nochat.RemoveChatConfig;
import net.minecraft.entity.Entity;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.command.TeamMsgCommand;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(TeamMsgCommand.class)
public abstract class TeamMsgCommandMixin {
  @Inject(method = "execute", at = @At("HEAD"), cancellable = true)
  private static void init(ServerCommandSource source, Entity entity, Team team, List<ServerPlayerEntity> recipients, SignedMessage message, CallbackInfo ci) {
    ServerPlayerEntity player = source.getPlayer();
    if(RemoveChatConfig.DISABLE_TEAM_MSG.getValue() && player != null && RemoveChatConfig.playerNotExcluded(player)){
      player.sendMessage(Text.literal("The /teammsg Command is currently Disabled").formatted(Formatting.RED));
      ci.cancel();
    }
  }
}