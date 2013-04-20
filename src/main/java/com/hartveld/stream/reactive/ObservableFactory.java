package com.hartveld.stream.reactive;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.hartveld.stream.reactive.AutoCloseables.noop;
import static com.hartveld.stream.reactive.concurrency.Schedulers.defaultScheduler;

import com.hartveld.stream.reactive.concurrency.Scheduler;
import com.hartveld.stream.reactive.subjects.TaskSubject;
import java.util.concurrent.Callable;

public final class ObservableFactory {

	@SafeVarargs
	public static <T> Observable<T> observableOf(T... values) {
		checkNotNull(values, "values");

		for (T value : values) {
			checkNotNull(value, "at least one value is null");
		}

		return (onNext, onError, onCompleted) -> {
			for (T value : values) {
				onNext.accept(value);
			}

			onCompleted.run();

			return noop();
		};
	}

	public static IntObservable observableOfInts(int... values) {
		return (onNext, onError, onCompleted) -> {
			for (int value : values) {
				onNext.accept(value);
			}

			onCompleted.run();

			return noop();
		};
	}

	public static LongObservable observableOfLongs(long... values) {
		return (onNext, onError, onCompleted) -> {
			for (long value : values) {
				onNext.accept(value);
			}

			onCompleted.run();

			return noop();
		};
	}

	public static DoubleObservable observableOfDoubles(double... values) {
		return (onNext, onError, onCompleted) -> {
			for (double value : values) {
				onNext.accept(value);
			}

			onCompleted.run();

			return noop();
		};
	}

	public static <T> Observable<T> observableOfTask(final Callable<T> task) {
		return new TaskSubject<>(defaultScheduler(), task);
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

			return noop();
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

			return noop();
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
			final AutoCloseable s0 = scheduler.schedule(() -> onNext.accept(element));
			final AutoCloseable s1 = scheduler.schedule(onCompleted);

			return AutoCloseables.composite(s0, s1);
		});
	}

	private ObservableFactory() { }

}
