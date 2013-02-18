package com.hartveld.rx.operators;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

import com.hartveld.rx.Observable;
import com.hartveld.rx.Observer;
import com.hartveld.rx.ObservableFactory;
import com.hartveld.rx.tests.AbstractSubjectObserverTestBase;
import java.util.concurrent.Executor;
import org.mockito.Mock;
import org.junit.Test;

public class SubscribeOnTest extends AbstractSubjectObserverTestBase {

	@Mock
	private Executor executor;

	@Override
	protected void initializeFor(Observable<String> source, Observer<String> target) {
		source.subscribeOn(super.syncExecSvc).subscribe(target);
	}

	@Test
	public void testThatSubscriptionAfterSubscribeOnIsExecutedThroughExecutor() throws Exception {
		Observable<String> source = ObservableFactory.observableOf(hello, world);

		AutoCloseable subscription = source.subscribeOn(executor).subscribe(
			e -> { },
			e -> { },
			() -> { }
		);

		subscription.close();

		verify(executor, times(2)).execute(any(Runnable.class));
	}

}
