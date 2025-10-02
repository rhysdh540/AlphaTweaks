package dev.rdh.alphatweaks.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.client.resource.ResourceDownloader;

@Mixin(ResourceDownloader.class)
public class ResourceDownloaderMixin {

	@SuppressWarnings("HttpUrlsUsage")
	@ModifyExpressionValue(method = "run", at = @At(value = "CONSTANT", args = "stringValue=http://s3.amazonaws.com/MinecraftResources/"))
	private String useBetacraft(String original) {
		return "http://s3.betacraft.uk:11705/MinecraftResources/";
	}
}
