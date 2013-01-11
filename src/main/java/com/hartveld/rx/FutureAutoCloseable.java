package com.hartveld.rx;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

public class FutureAutoCloseable implements AutoCloseable {

	private AutoCloseable future = null;

	public void set(AutoCloseable future) {
		checkState(this.future == null, "The set method is already called.");

		this.future = checkNotNull(future, "future must be non-null");
	}

	@Override
	public void close() throws Exception {
		checkState(future != null, "The set method is not yet called.");

		future.close();
	}

}
