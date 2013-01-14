package com.hartveld.rx.tests;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import com.hartveld.rx.IObservable;
import com.hartveld.rx.Observables;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.Test;

public class SubscribeTest extends AbstractOperatorTestBase {

	@Override
	protected IObservable<String> getTestableObservableFrom(IObservable<String> o) {
		return (onNext, onError, onCompleted) -> {
			AtomicBoolean stopped = new AtomicBoolean(false);
			AutoCloseable ac = o.subscribe(
				el -> {
					if (stopped.get()) return;
					onNext.procedure(el);
				},
				ex -> {
					if (stopped.get()) return;
					stopped.set(true);
					onError.procedure(ex);
				},
				() -> {
					if (stopped.get()) return;
					stopped.set(true);
					onCompleted.procedure();
				}
			);
			return () -> ac.close();
		};
	}

	@Test
	public void testThatObserverIsNotifiedBySubjectOnSubscription() throws Exception {
		IObservable<String> source = Observables.observableOf(hello, world);
		AutoCloseable subscription = source.subscribe(
			text -> {
				switch(text) {
				case hello:
					gotHello = true;
					break;
				case world:
					gotWorld = true;
					break;
				default:
					fail("Got unknown element: " + text);
				}
			},
			e -> fail("Caught exception: " + e.getMessage()),
			() -> { completed = true; }
		);

		subscription.close();

		assertThat("Did not find hello", gotHello, is(true));
		assertThat("Did not find world", gotWorld, is(true));
		assertThat("Did not complete", completed, is(true));
	}

}
