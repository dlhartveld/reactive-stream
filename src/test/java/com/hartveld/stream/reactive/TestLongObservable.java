package com.hartveld.stream.reactive;

import java.util.function.Consumer;
import java.util.function.LongConsumer;

public class TestLongObservable implements LongObservable {

	@Override
	public AutoCloseable subscribe(LongConsumer onNext, Consumer<Throwable> onError, Runnable onCompleted) {
		return null;
	}

}
