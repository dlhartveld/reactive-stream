package com.hartveld.rx;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import com.hartveld.rx.IObservable;
import com.hartveld.rx.IObserver;
import com.hartveld.rx.Observables;
import com.hartveld.rx.tests.AbstractSubjectObserverTestBase;
import org.junit.Test;

public class IObservableTest extends AbstractSubjectObserverTestBase {

	@Override
	protected void initializeFor(IObservable<String> source, IObserver<String> target) {
		source.id().subscribe(target);
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
