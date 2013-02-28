package com.hartveld.rx.concurrency;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import javax.swing.SwingUtilities;

public class Schedulers {

	public static final Executor DEFAULT = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

	public static final Executor EDT = SwingUtilities::invokeLater;

	public static final Executor IMMEDIATE = (runnable) -> runnable.run();

	private Schedulers() { }

}
