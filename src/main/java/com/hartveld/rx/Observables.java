package com.hartveld.rx;

import java.util.function.Block;

public class Observables {

	public static <T> IObservable<T> observableOf(T... values) {
		return new IObservable<T>() {
			@Override public AutoCloseable subscribe(Block<T> onNext, Block<Throwable> onError, Procedure onCompleted) {

				for (T value : values) {
					onNext.accept(value);
				}

				onCompleted.procedure();

				return () -> { };
			}
		};
	}

	private Observables() { }

}
