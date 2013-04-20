package com.hartveld.stream.reactive;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ForwardingAutoCloseable<Source> implements AutoCloseable {

	private static final Logger LOG = LoggerFactory.getLogger(ForwardingAutoCloseable.class);

	private Source source = null;
	private AutoCloseable future = null;

	public void set(final AutoCloseable future) {
		checkState(this.future == null, "The set method is already called.");

		this.future = checkNotNull(future, "future must be non-null");
	}

	public Source getSource() {
		return this.source;
	}

	public void setSource(final Source source) {
		this.source = source;
	}

	@Override
	public void close() throws Exception {
		LOG.trace("close()");

		//checkState(future != null, "The set method is not yet called.");

		if (future == null) {
			LOG.warn("Set method is not yet called.");
			return;
		}

		future.close();
	}

}
