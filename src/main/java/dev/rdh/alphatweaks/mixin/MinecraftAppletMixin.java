package dev.rdh.alphatweaks.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import org.objectweb.asm.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dev.rdh.alphatweaks.NoAppletMinecraft;

import net.minecraft.client.Minecraft;
import net.minecraft.client.MinecraftApplet;

import javax.swing.*;
import java.applet.*;
import java.awt.*;

@SuppressWarnings("removal")
@Mixin(MinecraftApplet.class)
public class MinecraftAppletMixin extends Applet {
	@Shadow
	private Minecraft minecraft;

	@Shadow
	private Canvas canvas;

	@Inject(method = "init", at = @At(value = "FIELD", target = "Lnet/minecraft/client/MinecraftApplet;canvas:Ljava/awt/Canvas;", shift = At.Shift.AFTER))
	private void onInit(CallbackInfo ci) {
		if (System.getProperty("org.prismlauncher.window.dimensions") != null) {
			String[] dimensions = System.getProperty("org.prismlauncher.window.dimensions").split("x");
			if (dimensions.length == 2) {
				try {
					int prismWidth = Integer.parseInt(dimensions[0]);
					int prismHeight = Integer.parseInt(dimensions[1]);
					this.setSize(prismWidth, prismHeight);
				} catch (NumberFormatException ignored) {
				}
			}

			this.canvas = null;
		}
	}

	@Redirect(method = "init", at = @At(value = "FIELD", target = "Lnet/minecraft/client/MinecraftApplet;minecraft:Lnet/minecraft/client/Minecraft;", opcode = Opcodes.PUTFIELD))
	private void noApplet(MinecraftApplet instance, Minecraft minecraft, @Local int fullscreen) {
		// fullscreen is actually a boolean but proguard messed up the bytecode enough that everybody thinks it's an int
		this.minecraft = new NoAppletMinecraft(this.getWidth(), this.getHeight(), fullscreen != 0);
	}

	@Inject(method = "init", at = @At(value = "FIELD", target = "Lnet/minecraft/client/Minecraft;appletMode:Z"), cancellable = true)
	private void beforeAppletInit(CallbackInfo ci) {
		ci.cancel();
		SwingUtilities.invokeLater(() -> {
			hideThemAll(this.getParent().getParent().getParent());
			hideThemAll(this.getParent().getParent());
			hideThemAll(this.getParent());
			hideThemAll(this);
		});

		this.minecraft.appletMode = false;
		minecraft.run();
	}

	@Unique
	private void hideThemAll(Container container) {
		try {
			if (container instanceof Frame) {
				((Frame) container).dispose();
			}
			for (Component component : container.getComponents()) {
				component.setVisible(false);
			}
		} catch (NullPointerException ignored) {
		}
	}
}
