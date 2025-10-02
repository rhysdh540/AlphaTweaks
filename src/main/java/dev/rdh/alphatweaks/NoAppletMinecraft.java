package dev.rdh.alphatweaks;

import org.lwjgl.opengl.Display;

import net.minecraft.client.CrashReportPanel;
import net.minecraft.client.Minecraft;
import net.minecraft.util.crash.CrashReport;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class NoAppletMinecraft extends Minecraft {

	public NoAppletMinecraft(int width, int height, boolean fullscreen) {
		super(null, null, null, width, height, fullscreen);
	}

	@Override
	public void handleCrash(CrashReport throwable) {
		Frame frame = new Frame("Minecraft Crash Report");
		frame.removeAll();
		frame.add(new CrashReportPanel(throwable), "Center");
		frame.validate();
		frame.setSize(this.width, this.height);
		frame.setLocationRelativeTo(null);
		frame.setAutoRequestFocus(true);
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
				frame.dispose();
				System.exit(1);
			}
		});
		frame.setVisible(true);
		Display.destroy();
	}
}
