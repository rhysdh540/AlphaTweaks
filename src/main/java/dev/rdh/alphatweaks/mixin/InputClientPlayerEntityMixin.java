package dev.rdh.alphatweaks.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.mob.player.InputClientPlayerEntity;
import net.minecraft.entity.mob.player.PlayerEntity;

@Mixin(InputClientPlayerEntity.class)
public abstract class InputClientPlayerEntityMixin extends PlayerEntity {
	private InputClientPlayerEntityMixin() {
		super(null);
	}

	@Shadow
	private Minecraft minecraft;

	@Inject(method = "sendChat", at = @At("HEAD"))
	private void onSendChat(String content, CallbackInfo ci) {
		this.minecraft.gui.addChatMessage("<" + this.name + "> " + content);
	}
}
