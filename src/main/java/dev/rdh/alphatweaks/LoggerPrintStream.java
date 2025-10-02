package dev.rdh.alphatweaks;

import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.OutputStream;
import java.io.PrintStream;

public class LoggerPrintStream extends PrintStream {
	private final Logger log;
	protected final String name;

	public LoggerPrintStream(String name, OutputStream out) {
		super(out);
		this.name = name;
		this.log = LoggerFactory.getLogger(name);
	}

	public void println(@Nullable String x) {
		this.logLine(x);
	}

	public void println(Object x) {
		this.logLine(String.valueOf(x));
	}

	protected void logLine(@Nullable String message) {
		log.info(message);
	}
}
