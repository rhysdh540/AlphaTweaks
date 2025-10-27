package dev.rdh.alphatweaks.mixin.client;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dev.rdh.alphatweaks.SkinUtil;

import net.minecraft.client.Minecraft;
import net.minecraft.client.Session;
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

	@SuppressWarnings("HttpUrlsUsage")
	@Definition(id = "skin", field = "Lnet/minecraft/client/entity/mob/player/InputClientPlayerEntity;skin:Ljava/lang/String;")
	@Expression("this.skin = @('http://www.minecraft.net/skin/' + ? + '.png')")
	@ModifyExpressionValue(method = "<init>", at = @At("MIXINEXTRAS:EXPRESSION"))
	private String newSkinUrl(String original, @Local(argsOnly = true) Session session) {
		return SkinUtil.getSkinDownloadUrl(session.username);
	}
}
