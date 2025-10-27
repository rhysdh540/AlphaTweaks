package dev.rdh.alphatweaks.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.PixelFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.ProgressRenderer;

import java.io.PrintStream;

import static org.lwjgl.opengl.GL11.*;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {
	@SuppressWarnings("LoggerInitializedWithForeignClass")
	@Unique
	private static final Logger LOGGER = LoggerFactory.getLogger(Minecraft.class);

	@Shadow public volatile boolean running;

	@Shadow public int width;
	@Shadow public int height;

	@Shadow private boolean fullscreen;
	@Shadow public Screen screen;

	@Shadow
	protected abstract void resize(int width, int height);

	@Shadow
	public abstract void shutdown();

	@Unique private boolean isMSeriesMac;
	@Unique private boolean decidedIfMSeriesMac = false;

	@Unique private int previousWidth;
	@Unique private int previousHeight;

	@Inject(method = "run", at = @At("TAIL"))
	private void endRun(CallbackInfo ci) {
		this.shutdown();
	}

	@Redirect(method = "setWorld(Lnet/minecraft/world/World;Ljava/lang/String;Lnet/minecraft/entity/mob/player/PlayerEntity;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/ProgressRenderer;progressStart(Ljava/lang/String;)V"))
	private void redirectProgressStart(ProgressRenderer instance, String title) {
		instance.progressStartNoAbort(title);
	}

	@Inject(method = "tick", at = @At("HEAD"))
	private void onTick(CallbackInfo ci) {
		if (!this.running) return;
		if (!decidedIfMSeriesMac) {
			isMSeriesMac = glGetString(GL_RENDERER).contains("Mac M");
			decidedIfMSeriesMac = true;
		}

		if (isMSeriesMac) {
			glEnable(GL30.GL_FRAMEBUFFER_SRGB);
		}

		if (Display.getWidth() != this.width || Display.getHeight() != this.height) {
			this.resize(Display.getWidth(), Display.getHeight());
		}
	}

	@Inject(method = "init", at = @At("HEAD"))
	private void onInitHead(CallbackInfo ci) {
		Display.setResizable(true);
	}

	@Inject(method = "init", at = @At("RETURN"))
	private void onInitReturn(CallbackInfo ci) {
		try {
			Display.makeCurrent();
			Display.update();
		} catch (LWJGLException e) {
			LOGGER.error("Error while making display current", e);
		}
	}

	@Redirect(method = "init", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/Display;create()V", remap = false))
	private void createDisplayWith24Bits() throws LWJGLException {
		Display.create(new PixelFormat().withBitsPerPixel(24));
	}

	/**
	 * @author rdh
	 * @reason lazy
	 */
	@Overwrite
	public void toggleFullscreen() {
		try {
			this.fullscreen = !this.fullscreen;
			if (this.fullscreen) {
				this.previousWidth = Display.getWidth();
				this.previousHeight = Display.getHeight();

				Display.setDisplayMode(Display.getDesktopDisplayMode());
				this.width = Display.getDisplayMode().getWidth();
				this.height = Display.getDisplayMode().getHeight();
			} else {
				this.width = this.previousWidth;
				this.height = this.previousHeight;
				Display.setDisplayMode(new DisplayMode(this.width, this.height));
			}

			if (this.width <= 0) {
				this.width = 1;
			}

			if (this.height <= 0) {
				this.height = 1;
			}

			if (this.screen != null) {
				this.resize(this.width, this.height);
			}

			Display.setFullscreen(this.fullscreen);
			Display.update();
		} catch (Exception e) {
			LOGGER.error("Couldn't toggle fullscreen", e);
		}
	}

	@Dynamic // not actually, just makes mcdev shut up
	@Redirect(method = "*", at = @At(value = "INVOKE", target = "Ljava/io/PrintStream;println(Ljava/lang/String;)V", remap = false), require = 0)
	private void logPrints(PrintStream printStream, String string) {
		LOGGER.info(string);
	}

	@Dynamic
	@Redirect(method = "*", at = @At(value = "INVOKE", target = "Ljava/lang/Throwable;printStackTrace()V", remap = false), require = 0)
	private void logStackTraces(Throwable throwable) {
		LOGGER.error("Exception thrown", throwable);
	}

	@ModifyExpressionValue(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;isMultiplayer()Z", ordinal = 0))
	private boolean openChat(boolean original) {
		return true;
	}
}
