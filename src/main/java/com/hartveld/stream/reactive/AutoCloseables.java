package com.hartveld.stream.reactive;

import static com.google.common.base.Preconditions.checkNotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AutoCloseables {

	private static final Logger LOG = LoggerFactory.getLogger(AutoCloseables.class);

	public static AutoCloseable noop() {
		return () -> { };
	}

	public static AutoCloseable composite(final AutoCloseable ... closeables) {
		checkNotNull(closeables, "closeables");

		return () -> closeAllIfNotNull(closeables);
	}

	public static void closeAllIfNotNull(AutoCloseable ... closeables) throws Exception {
		checkNotNull(closeables, "closeables");

		final Exception possible = new Exception("One or more inner AutoCloseables caused an exception on close");

		for (final AutoCloseable inner : closeables) {
			if (inner == null) {
				LOG.trace("AutoCloseable == null");
			} else {
				LOG.trace("Closing ...");
				try {
					inner.close();
				} catch (final Exception e) {
					LOG.trace("Caught exception: {} - added to suppressed exceptions.", e.getMessage(), e);
					possible.addSuppressed(e);
				}
			}
		}

		if (possible.getSuppressed().length > 0) {
			throw possible;
		}
	}

	private AutoCloseables() { }

}
