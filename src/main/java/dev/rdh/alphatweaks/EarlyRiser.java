package dev.rdh.alphatweaks;

import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;

import java.io.PrintStream;

public class EarlyRiser implements PreLaunchEntrypoint {
	public static final PrintStream STDOUT = System.out;
	public static final PrintStream STDERR = System.err;

	@Override
	public void onPreLaunch() {
		System.setErr(new LoggerPrintStream("STDERR", STDERR));
		System.setOut(new LoggerPrintStream("STDOUT", STDOUT));

		String nativePath = System.getProperty("org.lwjgl.librarypath");
		if (nativePath != null) {
			System.setProperty("net.java.games.input.librarypath", nativePath);
		}
	}
}
