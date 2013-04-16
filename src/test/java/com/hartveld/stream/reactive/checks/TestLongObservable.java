package com.hartveld.stream.reactive.checks;

import com.hartveld.stream.reactive.LongObservable;
import java.util.function.Consumer;
import java.util.function.LongConsumer;

public class TestLongObservable implements LongObservable {

	@Override
	public AutoCloseable subscribe(LongConsumer onNext, Consumer<Exception> onError, Runnable onCompleted) {
		return null;
	}

}
