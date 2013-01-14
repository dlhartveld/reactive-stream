package com.hartveld.rx.tests.operators;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import com.hartveld.rx.IObservable;
import com.hartveld.rx.Observables;
import com.hartveld.rx.tests.AbstractOperatorTestBase;
import org.junit.Test;

public class SelectOperatorTest extends AbstractOperatorTestBase {

	@Test
	public void testThatSelectOperatorWorks() throws Exception {
		IObservable<String> source = Observables.observableOf(hello, world);

		AutoCloseable subscription = source.select(x -> x.charAt(0)).subscribe(
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
	public void testThatSelectOperatorHandlesExceptionCorrectly() throws Exception {
		IObservable<String> source = Observables.observableOf(hello, world);

		AutoCloseable subscription = source.select(x -> x.charAt(5)).subscribe(
			e -> fail("Got element: " + e),
			e -> gotError = true,
			() -> fail("Got completed")
		);

		subscription.close();

		assertThat("Did not observe error", gotError, is(true));
	}

}
