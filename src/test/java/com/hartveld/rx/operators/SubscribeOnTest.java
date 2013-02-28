package com.hartveld.rx.operators;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.hartveld.rx.Observable;
import com.hartveld.rx.ObservableFactory;
import com.hartveld.rx.Observer;
import com.hartveld.rx.concurrency.Schedulers;
import com.hartveld.rx.tests.AbstractSubjectObserverTestBase;
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

		verify(scheduler, times(2)).execute(any(Runnable.class));
	}

}
