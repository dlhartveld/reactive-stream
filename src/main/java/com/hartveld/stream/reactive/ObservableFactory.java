package com.hartveld.stream.reactive;

import com.hartveld.stream.reactive.concurrency.Schedulers;
import com.hartveld.stream.reactive.subjects.TaskSubject;
import java.util.concurrent.Callable;

public class ObservableFactory {

	@SafeVarargs
	public static <T> Observable<T> observableOf(T... values) {
		return (onNext, onError, onCompleted) -> {
			for (T value : values) {
				onNext.accept(value);
			}

			onCompleted.run();

			return () -> { };
		};
	}

	public static IntObservable observableOfInts(int... values) {
		return (onNext, onError, onCompleted) -> {
			for (int value : values) {
				onNext.accept(value);
			}

			onCompleted.run();

			return () -> { };
		};
	}

	public static LongObservable observableOfLongs(long... values) {
		return (onNext, onError, onCompleted) -> {
			for (long value : values) {
				onNext.accept(value);
			}

			onCompleted.run();

			return () -> { };
		};
	}

	public static DoubleObservable observableOfDoubles(double... values) {
		return (onNext, onError, onCompleted) -> {
			for (double value : values) {
				onNext.accept(value);
			}

			onCompleted.run();

			return () -> { };
		};
	}

	public static <T> Observable<T> observableOfTask(final Callable<T> task) {
		return new TaskSubject<>(Schedulers.DEFAULT, task);
	}

	private ObservableFactory() {
	}

}
