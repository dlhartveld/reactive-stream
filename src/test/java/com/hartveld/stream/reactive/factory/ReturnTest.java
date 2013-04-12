package com.hartveld.stream.reactive.factory;

import static com.hartveld.stream.reactive.tests.concurrency.Notification.onCompleted;
import static com.hartveld.stream.reactive.tests.concurrency.Notification.onNext;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import com.hartveld.stream.reactive.Observable;
import com.hartveld.stream.reactive.ObservableFactory;
import com.hartveld.stream.reactive.tests.concurrency.DefaultVirtualTimeScheduler;
import com.hartveld.stream.reactive.tests.concurrency.Notification;
import com.hartveld.stream.reactive.tests.concurrency.VirtualTimeScheduler;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

public class ReturnTest {

	private static final String hello = "hello";

	private VirtualTimeScheduler<String> scheduler;

	private Observable<String> source;

	@Before
	public void setUp() {
		scheduler = new DefaultVirtualTimeScheduler<>();
		source = ObservableFactory.return_(hello, scheduler);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testThatReturnWorks() throws Exception {
		final List<Notification<String>> results = scheduler.run(source);

		assertThat(results, contains(
				onNext(101, hello),
				onCompleted(101)
		));
	}

	@Test
	public void testThatCancellingReturnSubscriptionBeforeNotificationsStopsNotifications() {
		scheduler.subscribe(source, 100);
		scheduler.closeSubscription(100);

		final List<Notification<String>> results = scheduler.run();

		assertThat(results, is(empty()));
	}

}
