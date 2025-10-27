package dev.rdh.alphatweaks.mixin.client.auth;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.Session;

import java.util.UUID;
import java.util.regex.Pattern;

@Mixin(Session.class)
public class SessionMixin {
	@Unique
	private String accessToken;

	@Unique
	private UUID uuid;

	@Unique
	private static final Pattern UUID_PATTERN = Pattern.compile("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})");

	@Inject(method = "<init>", at = @At("RETURN"))
	private void onInit(String username, String sessionId, CallbackInfo ci) {
		String[] split = sessionId.split(":");
		if (split.length == 3 && split[0].equalsIgnoreCase("token")) {
			accessToken = split[1];
			this.uuid = UUID.fromString(UUID_PATTERN.matcher(split[2]).replaceAll("$1-$2-$3-$4-$5"));
		}
	}
}
