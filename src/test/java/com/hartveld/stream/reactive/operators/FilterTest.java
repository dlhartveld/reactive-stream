package com.hartveld.stream.reactive.operators;

import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;

import com.hartveld.stream.reactive.Observable;
import com.hartveld.stream.reactive.ObservableFactory;
import com.hartveld.stream.reactive.Observer;
import com.hartveld.stream.reactive.tests.AbstractSubjectObserverTestBase;
import com.hartveld.stream.reactive.tests.concurrency.DefaultVirtualTimeScheduler;
import com.hartveld.stream.reactive.tests.concurrency.Notification;
import com.hartveld.stream.reactive.tests.concurrency.VirtualTimeScheduler;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

public class FilterTest extends AbstractSubjectObserverTestBase {

	private VirtualTimeScheduler<String> scheduler;

	@Before
	@Override
	public void setUp() {
		super.setUp();

		this.scheduler = new DefaultVirtualTimeScheduler<>();
	}

	@Override
	protected void initializeFor(Observable<String> source, Observer<String> target) {
		source.filter(x -> true).subscribe(target);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testThatAlwaysTrueFilterPassesEverything() {
		final Observable<String> source = ObservableFactory.observableOf(hello, world).observeOn(scheduler);

		final List<Notification<String>> results = this.scheduler.run(source.filter(x -> true));

		assertThat(results, contains(
				Notification.onNext(101, hello),
				Notification.onNext(102, world),
				Notification.onCompleted(103)
		));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testThatAlwaysFalseFilterPassesNothing() {
		final Observable<String> source = ObservableFactory.observableOf(hello, world).observeOn(scheduler);

		final List<Notification<String>> results = this.scheduler.run(source.filter(x -> false));

		assertThat(results, contains(
				Notification.onCompleted(103)
		));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testThatCorrectStringOfTwoIsPassedThrough() {
		final Observable<String> source = ObservableFactory.observableOf(hello, world).observeOn(scheduler);

		final List<Notification<String>> results = this.scheduler.run(source.filter(s -> s.equals(hello)));

		assertThat(results, contains(
				Notification.onNext(101, hello),
				Notification.onCompleted(103)
		));
	}

}
