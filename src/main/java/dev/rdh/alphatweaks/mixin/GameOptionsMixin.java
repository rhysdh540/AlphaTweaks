package dev.rdh.alphatweaks.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.client.options.GameOptions;

@Mixin(GameOptions.class)
public class GameOptionsMixin {
	@ModifyExpressionValue(method = "load", at = @At(value = "INVOKE", target = "Ljava/lang/String;split(Ljava/lang/String;)[Ljava/lang/String;"))
	private String[] ensure2(String[] original) {
		if (original.length == 1) {
			return new String[]{original[0], ""};
		} else {
			return original;
		}
	}
}
