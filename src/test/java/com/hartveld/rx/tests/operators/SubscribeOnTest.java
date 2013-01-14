package com.hartveld.rx.tests.operators;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

import com.hartveld.rx.IObservable;
import com.hartveld.rx.Observables;
import com.hartveld.rx.tests.AbstractOperatorTestBase;
import com.hartveld.rx.tests.SynchronousExecutorService;
import java.util.concurrent.Executor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(MockitoJUnitRunner.class)
public class SubscribeOnTest extends AbstractOperatorTestBase {

	@Mock
	private Executor executor;

	@Override
	protected IObservable<String> getTestableObservableFrom(IObservable<String> o) {
		return o.subscribeOn(new SynchronousExecutorService());
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
