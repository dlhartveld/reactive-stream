package com.hartveld.stream.reactive.checks;

import com.hartveld.stream.reactive.DoubleObservable;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;

public class TestDoubleObservable implements DoubleObservable {

	@Override
	public AutoCloseable subscribe(DoubleConsumer onNext, Consumer<Exception> onError, Runnable onCompleted) {
		return null;
	}

}
