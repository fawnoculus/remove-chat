package net.fawnoculus.nochat.mixin;

import net.fawnoculus.nochat.RemoveChatConfig;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.server.command.MessageCommand;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;

@Mixin(MessageCommand.class)
public abstract class MessageCommandMixin {
  @Inject(method = "execute", at = @At("HEAD"), cancellable = true)
  private static void init(ServerCommandSource source, Collection<ServerPlayerEntity> targets, SignedMessage message, CallbackInfo ci) {
    ServerPlayerEntity player = source.getPlayer();
    if (RemoveChatConfig.DISABLE_MSG_COMMANDS.getValue() && player != null && RemoveChatConfig.playerNotExcluded(player)) {
      player.sendMessage(Text.literal("The /msg Command is currently Disabled").formatted(Formatting.RED));
      ci.cancel();
    }
    
  }
}