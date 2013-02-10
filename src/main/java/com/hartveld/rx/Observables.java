package com.hartveld.rx;

import java.util.function.Consumer;

public class Observables {

	@SafeVarargs
	public static <T> IObservable<T> observableOf(T... values) {
		return (Consumer<T> onNext, Consumer<Throwable> onError, Runnable onCompleted) -> {

			for (T value : values) {
				onNext.accept(value);
			}

			onCompleted.run();

			return () -> { };
		};
	}

	private Observables() {
	}

}
