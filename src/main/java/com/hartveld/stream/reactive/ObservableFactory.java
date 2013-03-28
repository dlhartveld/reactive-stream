package com.hartveld.stream.reactive;

import static com.google.common.base.Preconditions.checkNotNull;

import com.hartveld.stream.reactive.concurrency.Scheduler;
import com.hartveld.stream.reactive.concurrency.Schedulers;
import com.hartveld.stream.reactive.subjects.TaskSubject;
import java.util.concurrent.Callable;

public class ObservableFactory {

	@SafeVarargs
	public static <T> Observable<T> observableOf(T... values) {
		for (T value : values) {
			checkNotNull(value, "at least one value is null");
		}

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

	/**
	 * Returns a new, empty observable sequence.
	 *
	 * @param <T> The type of the new {@link Observable}. This type is usually inferred by the compiler from the
	 *               assignment target type.
	 *
	 * @return The new observable sequence.
	 */
	public static <T> Observable<T> emptyObservable() {
		return (onNext, onError, onCompleted) -> {
			onCompleted.run();

			return () -> { };
		};
	}

	/**
	 * Returns a new observable sequence that, on subscription, immediately throws the given exception.
	 *
	 * @param <T>       The type of the new {@link Observable}. This type is usually inferred by the compiler from the
	 *                     assignment target type.
	 *
	 * @param exception The {@link Exception} that will be thrown.
	 *
	 * @return A new {@link Observable} that only returns an exception.
	 */
	public static <T> Observable<T> throwsObservable(final Exception exception) {
		checkNotNull(exception, "exception");

		return (onNext, onError, onCompleted) -> {
			onError.accept(exception);

			return () -> { };
		};
	}

	/**
	 * Return a new observable sequence that contains a single element.
	 *
	 * @param <T>     The type of the new {@link Observable}. This type is usually inferred by the compiler from the
	 *                   assignment target type.
	 *
	 * @param element The single element to return.
	 *
	 * @return A new {@link Observable} that returns a single element and then completes.
	 */
	public static <T> Observable<T> return_(final T element) {
		checkNotNull(element, "element");

		return observableOf(element);
	}

	/**
	 * Return a new observable sequence that contains a single element. Execution is scheduled on the scheduler.
	 *
	 * @param <T>       The type of the new {@link Observable}. This type is usually inferred by the compiler from the
	 *                     assignment target type.
	 *
	 * @param <A>	    The scheduler absolute time type.
	 * @param <R>	    The scheduler relative time type.
	 *
	 * @param element   The single element to return.
	 * @param scheduler The scheduler on which to execute the notifications.
	 *
	 * @return A new {@link Observable} that returns a single element and then completes.
	 */
	public static <T, A, R> Observable<T> return_(final T element, final Scheduler<A, R> scheduler) {
		checkNotNull(element, "element");

		return ((Observable<T>) (onNext, onError, onCompleted) -> {
			scheduler.schedule(() -> onNext.accept(element));
			scheduler.schedule(onCompleted);

			return () -> { };
		});
	}

	private ObservableFactory() {
	}

}
