package com.hartveld.rx.operators;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

import com.hartveld.rx.IObservable;
import com.hartveld.rx.IObserver;
import com.hartveld.rx.Observables;
import com.hartveld.rx.tests.AbstractSubjectObserverTestBase;
import java.util.concurrent.Executor;
import org.mockito.Mock;
import org.junit.Test;

public class SubscribeOnTest extends AbstractSubjectObserverTestBase {

	@Mock
	private Executor executor;

	@Override
	protected void initializeFor(IObservable<String> source, IObserver<String> target) {
		source.subscribeOn(super.syncExecSvc).subscribe(target);
	}

	@Test
	public void testThatSubscriptionAfterSubscribeOnIsExecutedThroughExecutor() throws Exception {
		IObservable<String> source = Observables.observableOf(hello, world);

		AutoCloseable subscription = source.subscribeOn(executor).subscribe(
			e -> { },
			e -> { },
			() -> { }
		);

		subscription.close();

		verify(executor, times(2)).execute(any(Runnable.class));
	}

}
