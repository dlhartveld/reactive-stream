package com.hartveld.rx;

public class Observables {

	public static <T> IObservable<T> observableOf(T... values) {
		return new IObservable<T>() {
			@Override public AutoCloseable subscribe(Procedure1<T> onNext, Procedure1<Throwable> onError, Procedure onCompleted) {

				for (T value : values) {
					onNext.procedure(value);
				}

				onCompleted.procedure();

				return () -> { };
			}
		};
	}

	private Observables() { }

}
