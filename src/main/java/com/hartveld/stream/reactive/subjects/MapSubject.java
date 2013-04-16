package com.hartveld.stream.reactive.subjects;

import com.hartveld.stream.reactive.subjects.AbstractSubject;
import com.hartveld.stream.reactive.subjects.Subject;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MapSubject<T, R, Source> extends AbstractSubject<T, R, Source> implements Subject<T, R> {

	private static final Logger LOG = LoggerFactory.getLogger(MapSubject.class);
	private final Function<T, R> mapper;

	public MapSubject(final Function<T, R> mapper) {
		checkNotNull(mapper, "mapper");

		this.mapper = mapper;
	}

	@Override
	public void onNext(final T value) {
		super.broadcastNext(this.mapper.apply(value));
	}

}
