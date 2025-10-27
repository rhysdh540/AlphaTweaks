package dev.rdh.alphatweaks.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;

@Mixin(TitleScreen.class)
public class TitleScreenMixin extends Screen {
	@Inject(
			method = "init",
			at = @At(
					value = "INVOKE",
					target = "Ljava/util/List;add(Ljava/lang/Object;)Z",
					shift = At.Shift.AFTER
			),
			slice = @Slice(
					from = @At(value = "CONSTANT", args = "stringValue=Options...")
			)
	)
	private void addQuitButton(CallbackInfo ci) {
		for(ButtonWidget b : this.buttons) {
			b.y -= 16;
		}

		ButtonWidget lastButton = this.buttons.getLast();
		this.buttons.add(new ButtonWidget(4, lastButton.x, lastButton.y + 24, "Quit Game"));
	}

	@Inject(method = "buttonClicked", at = @At("HEAD"))
	private void addQuitButtonHandler(ButtonWidget button, CallbackInfo ci) {
		if (button.id == 4) {
			this.minecraft.stop();
		}
	}
}
