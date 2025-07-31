package net.fawnoculus.nochat.mixin;

import net.fawnoculus.nochat.RemoveChatConfig;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Predicate;

@Mixin(PlayerManager.class)
public abstract class PlayerManagerMixin {
  
  @Inject(method = "broadcast(" +
      "Lnet/minecraft/network/message/SignedMessage;" +
      "Ljava/util/function/Predicate;" +
      "Lnet/minecraft/server/network/ServerPlayerEntity;" +
      "Lnet/minecraft/network/message/MessageType$Parameters;" +
      ")V"
      , at = @At("HEAD")
      , cancellable = true)
  private void init(SignedMessage message, Predicate<ServerPlayerEntity> shouldSendFiltered, @Nullable ServerPlayerEntity sender, MessageType.Parameters params, CallbackInfo ci) {
    // Only Cancel Messages send by Players
    if (RemoveChatConfig.DISABLE_CHAT.getValue() && sender != null && RemoveChatConfig.playerNotExcluded(sender)) {
      sender.sendMessage(Text.literal("The Chat is currently Disabled").formatted(Formatting.RED));
      ci.cancel();
    }
  }
}