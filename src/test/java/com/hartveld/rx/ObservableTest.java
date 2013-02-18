package com.hartveld.rx;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import com.hartveld.rx.Observable;
import com.hartveld.rx.Observer;
import com.hartveld.rx.ObservableFactory;
import com.hartveld.rx.tests.AbstractSubjectObserverTestBase;
import org.junit.Test;

public class ObservableTest extends AbstractSubjectObserverTestBase {

	@Override
	protected void initializeFor(Observable<String> source, Observer<String> target) {
		source.id().subscribe(target);
	}

	@Test
	public void testThatObserverIsNotifiedBySubjectOnSubscription() throws Exception {
		Observable<String> source = ObservableFactory.observableOf(hello, world);
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
