package com.hartveld.stream.reactive.operators;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import com.hartveld.stream.reactive.Observable;
import com.hartveld.stream.reactive.ObservableFactory;
import com.hartveld.stream.reactive.Observer;
import com.hartveld.stream.reactive.concurrency.Schedulers;
import com.hartveld.stream.reactive.tests.AbstractSubjectObserverTestBase;
import org.junit.Test;

public class ObserveOnTest extends AbstractSubjectObserverTestBase {

	@Override
	protected void initializeFor(Observable<String> source, Observer<String> target) {
		source.observeOn(Schedulers.IMMEDIATE).subscribe(target);
	}

	@Test
	public void testThatObserverIsNotifiedOnBackgroundThreadWithObserveOn() throws Exception {
		long mainThreadId = Thread.currentThread().getId();
		final Observable<String> source = ObservableFactory.observableOf(hello, world);
		final AutoCloseable subscription = source.observeOn(Schedulers.DEFAULT).subscribe(
			text -> {
				switch(text) {
				case hello:
					long observingThreadId0 = Thread.currentThread().getId();
					assertThat("Observation 'element' is not on other thread: ", observingThreadId0, is(not(mainThreadId)));
					gotHello = true;
					break;
				case world:
					long observingThreadId1 = Thread.currentThread().getId();
					assertThat("Observation 'element' is not on other thread: ", observingThreadId1, is(not(mainThreadId)));
					gotWorld = true;
					break;
				default:
					fail("Got unknown element: " + text);
				}
			},
			e -> fail("Caught exception: " + e.getMessage()),
			() -> {
				long observingThreadId2 = Thread.currentThread().getId();
				assertThat("Observation 'completed' is not on other thread: ", observingThreadId2, is(not(mainThreadId)));
				completed = true;
			}
		);

		Thread.sleep(500);

		subscription.close();

		assertThat("Did not find hello", gotHello, is(true));
		assertThat("Did not find world", gotWorld, is(true));
		assertThat("Did not complete", completed, is(true));
	}

}
