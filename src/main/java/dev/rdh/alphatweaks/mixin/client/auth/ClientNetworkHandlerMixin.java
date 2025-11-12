package dev.rdh.alphatweaks.mixin.client.auth;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.client.network.handler.ClientNetworkHandler;

@Mixin(ClientNetworkHandler.class)
public abstract class ClientNetworkHandlerMixin {
	@SuppressWarnings("HttpUrlsUsage")
	@ModifyExpressionValue(method = "handleHandshake", at = @At(value = "CONSTANT", args = "stringValue=http://www.minecraft.net/game/joinserver.jsp?user="))
	private String fixUrl(String original) {
		return original.replace("www.minecraft.net", "session.minecraft.net");
	}
}
