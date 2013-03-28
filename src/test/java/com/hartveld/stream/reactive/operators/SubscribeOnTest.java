package com.hartveld.stream.reactive.operators;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.hartveld.stream.reactive.Observable;
import com.hartveld.stream.reactive.ObservableFactory;
import com.hartveld.stream.reactive.Observer;
import com.hartveld.stream.reactive.concurrency.Schedulers;
import com.hartveld.stream.reactive.tests.AbstractSubjectObserverTestBase;
import org.junit.Test;

public class SubscribeOnTest extends AbstractSubjectObserverTestBase {

	@Override
	protected void initializeFor(Observable<String> source, Observer<String> target) {
		source.subscribeOn(Schedulers.IMMEDIATE).subscribe(target);
	}

	@Test
	public void testThatSubscriptionAfterSubscribeOnIsExecutedThroughExecutor() throws Exception {
		Observable<String> source = ObservableFactory.observableOf(hello, world);

		AutoCloseable subscription = source.subscribeOn(scheduler).subscribe(
			e -> { },
			e -> { },
			() -> { }
		);

		subscription.close();

		verify(scheduler, times(2)).schedule(any(Runnable.class));
	}

}
