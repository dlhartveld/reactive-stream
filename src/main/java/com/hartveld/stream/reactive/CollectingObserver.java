package com.hartveld.stream.reactive;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.stream.Collector;

class CollectingObserver<T, R> {

	private final Observable<? extends T> source;
	private final Collector<? super T, R> collector;

	CollectingObserver(final Observable<? extends T> source, final Collector<? super T, R> collector) {
		checkNotNull(source, "source");
		checkNotNull(collector, "collector");

		this.source = source;
		this.collector = collector;
	}

	R getResult() {
		final R r = this.collector.resultSupplier().get();
		final Holder<R> result = new Holder<>(r);

		this.source.forEach(element -> {
			result.value = this.collector.accumulator().apply(result.value, element);
		});

		return result.value;
	}

	private static class Holder<T> {
		T value;

		Holder(T value) {
			this.value = value;
		}
	}

}
