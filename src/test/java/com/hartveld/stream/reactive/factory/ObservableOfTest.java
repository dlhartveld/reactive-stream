package com.hartveld.stream.reactive.factory;

import static com.hartveld.stream.reactive.tests.concurrency.Notification.onCompleted;
import static com.hartveld.stream.reactive.tests.concurrency.Notification.onNext;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertThat;

import com.hartveld.stream.reactive.Observable;
import com.hartveld.stream.reactive.ObservableFactory;
import com.hartveld.stream.reactive.tests.concurrency.Notification;
import com.hartveld.stream.reactive.tests.concurrency.VirtualTimeScheduler;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

public class ObservableOfTest {

	private static final String HELLO = "hello";
	private static final String WORLD = "world";
	private static final String MY = "my";
	private static final String NAME = "name";
	private static final String IS = "is";
	private static final String MR = "mr";
	private static final String X = "x";

	private VirtualTimeScheduler scheduler;

	private Observable<String> source;

	@Before
	public void setUp() {
		this.scheduler = new VirtualTimeScheduler();

		this.source = ObservableFactory.observableOf(HELLO, WORLD, MY, NAME, IS, MR, X);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testThatObservableOfSchedulesCorrectly() {
		final List<Notification<String>> results = this.scheduler.run(this.source.observeOn(this.scheduler));

		System.out.println("Results: " + results);

		assertThat(results, containsInAnyOrder(
				onNext(101, HELLO),
				onNext(101, WORLD),
				onNext(101, MY),
				onNext(101, NAME),
				onNext(101, IS),
				onNext(101, MR),
				onNext(101, X),
				onCompleted(101)
		));
	}

}
