package com.hartveld.rx;

import java.util.function.Block;

public class Observables {

	public static <T> IObservable<T> observableOf(T... values) {
		return (Block<T> onNext, Block<Throwable> onError, Runnable onCompleted) -> {

			for (T value : values) {
				onNext.accept(value);
			}

			onCompleted.run();

			return () -> { };
		};
	}

	private Observables() { }

}
