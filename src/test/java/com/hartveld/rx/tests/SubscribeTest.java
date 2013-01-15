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
		return o.id();
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
