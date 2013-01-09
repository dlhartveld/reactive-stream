package com.hartveld.rx;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

public class SubjectObserverTest {

	private static final String hello = "Hello";
	private static final String world = "world";

	boolean gotHello;
	boolean gotWorld;

	boolean completed;

	@Before
	public void setUp() {
		gotHello = false;
		gotWorld = false;

		completed = false;
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
