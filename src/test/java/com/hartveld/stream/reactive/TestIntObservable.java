package com.hartveld.stream.reactive;

import java.util.function.Consumer;
import java.util.function.IntConsumer;

public class TestIntObservable implements IntObservable {

	@Override
	public AutoCloseable subscribe(IntConsumer onNext, Consumer<Exception> onError, Runnable onCompleted) {
		return null;
	}

}
