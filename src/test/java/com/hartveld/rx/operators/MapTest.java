package com.hartveld.rx.operators;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import com.hartveld.rx.IObservable;
import com.hartveld.rx.IObserver;
import com.hartveld.rx.Observables;
import com.hartveld.rx.tests.AbstractSubjectObserverTestBase;
import org.junit.Test;

public class MapTest extends AbstractSubjectObserverTestBase {

	@Override
	protected void initializeFor(IObservable<String> source, IObserver<String> target) {
		source.map(s -> s).subscribe(target);
	}

	@Test
	public void testThatMapOperatorWorks() throws Exception {
		IObservable<String> source = Observables.observableOf(hello, world);

		AutoCloseable subscription = source.map(x -> x.charAt(0)).subscribe(
			e -> {
				switch(e) {
					case 'H':
						gotHello = true;
						break;
					case 'w':
						gotWorld = true;
						break;
					default:
						fail("Got unrecognized character: " + e);
						break;
				}
			},
			e -> fail("Error occurred: " + e.getMessage()),
			() -> completed = true
		);

		subscription.close();

		assertThat("Did not observe 'H'", gotHello, is(true));
		assertThat("Did not observe 'w'", gotWorld, is(true));
		assertThat("Did not complete", completed, is(true));
	}

	@Test
	public void testThatMapOperatorHandlesExceptionCorrectly() throws Exception {
		IObservable<String> source = Observables.observableOf(hello, world);

		AutoCloseable subscription = source.map(x -> x.charAt(5)).subscribe(
			e -> fail("Got element: " + e),
			e -> gotError = true,
			() -> fail("Got completed")
		);

		subscription.close();

		assertThat("Did not observe error", gotError, is(true));
	}

}
