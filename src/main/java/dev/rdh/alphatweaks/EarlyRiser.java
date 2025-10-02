package dev.rdh.alphatweaks;

import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;

import java.io.PrintStream;

public class EarlyRiser implements PreLaunchEntrypoint {
	public static final PrintStream STDOUT = System.out;
	public static final PrintStream STDERR = System.err;

	@Override
	public void onPreLaunch() {
		System.setErr(new LoggedPrintStream("STDERR", STDERR));
		System.setOut(new LoggedPrintStream("STDOUT", STDOUT));
	}
}
