package com.fawnoculus.nochat.mixin;

import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(ServerPlayNetworkHandler.class)
public abstract class RemoveChatMixin {

	@Shadow public abstract ServerPlayerEntity getPlayer();

	@Inject(method = "onChatMessage", at = @At("HEAD"), cancellable = true)
	private void init(ChatMessageC2SPacket packet, CallbackInfo ci) {
		this.getPlayer().sendMessageToClient(Text.of("Chat, /msg, /tell, /w, /me & /teammsg are disabled"),false);
		ci.cancel();
	}
}