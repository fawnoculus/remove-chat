package com.fawnoculus.nochat.mixin;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.MessageCommand;
import net.minecraft.server.command.ServerCommandSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MessageCommand.class)
public abstract class RemoveMessageCommandMixin {
    @Inject(method = "register", at = @At("HEAD"), cancellable = true)
    private static void init(CommandDispatcher<ServerCommandSource> dispatcher, CallbackInfo ci) {
        ci.cancel();
    }
}