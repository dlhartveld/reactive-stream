package com.hartveld.stream.reactive;

public class AutoCloseables {

	public static AutoCloseable noop() {
		return () -> { };
	}

	public static AutoCloseable composite(final AutoCloseable ... closeables) {
		return () -> {
			final Exception possible = new Exception("One or more inner AutoCloseables caused an exception on close");

			for (AutoCloseable inner : closeables) {
				try {
					inner.close();
				} catch (Exception e) {
					possible.addSuppressed(e);
				}
			}

			if (possible.getSuppressed().length > 0) {
				throw possible;
			}
		};
	}

	private AutoCloseables() { }

}
