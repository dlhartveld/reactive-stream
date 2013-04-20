package com.hartveld.stream.reactive.operators;

import static com.hartveld.stream.reactive.AutoCloseables.noop;
import static com.hartveld.stream.reactive.tests.concurrency.Notification.onCompleted;
import static com.hartveld.stream.reactive.tests.concurrency.Notification.onNext;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import com.hartveld.stream.reactive.AutoCloseables;
import com.hartveld.stream.reactive.Observable;
import com.hartveld.stream.reactive.ObservableFactory;
import com.hartveld.stream.reactive.tests.concurrency.DefaultVirtualTimeScheduler;
import com.hartveld.stream.reactive.tests.concurrency.Notification;
import com.hartveld.stream.reactive.tests.concurrency.TestObservableFactory;
import com.hartveld.stream.reactive.tests.concurrency.VirtualTimeScheduler;
import java.util.List;
import java.util.function.Consumer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SwitchTest {

	private static final Logger LOG = LoggerFactory.getLogger(SwitchTest.class);

	private static final String HELLO = "Hello";
	private static final String WORLD = "world";
	private static final String MY = "My";
	private static final String NAME = "name";
	private static final String IS = "is";
	private static final String JOHN = "John";

	private VirtualTimeScheduler<String> scheduler;

	@Before
	public void setUp() {
		this.scheduler = new DefaultVirtualTimeScheduler<>();
	}

	@Test
	public void testThatSwitchThrowsClassCastExceptionForIncorrectSource() {
		final Observable<String> source = ObservableFactory.observableOf("ABC");

		final Observable<?> switched = source.switchToNext();

		boolean exceptionWasThrown = false;
		try {
			switched.subscribe(el -> LOG.trace("el: {}", el));
		} catch (final ClassCastException e) {
			LOG.trace("Expected exception caught: {}", e.getMessage(), e);
			exceptionWasThrown = true;
		}
		assertThat(exceptionWasThrown, is(true));
	}

	@Test
	public void testThatSwitchWithCorrectObservableTypeDoesNotThrowException() throws Exception {

		abstract class A implements Observable<String> {
			@Override
			public AutoCloseable subscribe(Consumer onNext, Consumer onError, Runnable onCompleted) {
				LOG.trace("A.subscribe()");

				onNext.accept("ABC");

				return AutoCloseables.noop();
			}
		}

		abstract class B implements Observable<Observable<String>> {
			@Override
			public AutoCloseable subscribe(Consumer onNext, Consumer onError, Runnable onCompleted) {
				LOG.trace("B.subscribe()");

				onNext.accept(new A() { });
				onNext.accept(new A() { });

				return noop();
			}
		}

		final Observable<Observable<String>> source = new B() { };
		final Observable<String> switched = (Observable<String>) source.switchToNext();

		switched.subscribe(el -> LOG.info("el: {}", el)).close();
	}

	@Test
	public void testThatSwitchObservesFromLatestObservable() {
		final Observable<String> o1 = TestObservableFactory.observableOf(
				scheduler,
				onNext(1, HELLO),
				onNext(2, WORLD)
		);
		final Observable<String> o2 = TestObservableFactory.observableOf(
				scheduler,
				onNext(1, MY),
				onNext(2, NAME)
		);
		final Observable<String> o3 = TestObservableFactory.observableOf(
				scheduler,
				onNext(1, IS),
				onNext(2, JOHN)
		);

		final Observable<Integer> events = TestObservableFactory.observableOf(
				(VirtualTimeScheduler<Integer>) scheduler,
				onNext(1, 1),
				onNext(2, 2),
				onNext(3, 3)
		);

		final Observable<Observable<String>> mapped = events.map(i -> i == 1 ? o1 : (i == 2 ? o2 : o3));
		final Observable<String> switched = mapped.switchToNext();

		scheduler.run(switched);

		final List<Notification<Object>> notifications = scheduler.run(switched);

		LOG.trace("Notifications: {}", notifications);

		assertThat(notifications, contains(
					onNext(101, o1),
					onNext(101, HELLO),
					onNext(102, WORLD),
					onCompleted(103)
				));

		Assert.fail();
	}

}
