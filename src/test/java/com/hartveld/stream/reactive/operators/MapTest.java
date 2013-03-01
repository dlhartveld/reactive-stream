package com.hartveld.stream.reactive.operators;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import com.hartveld.stream.reactive.Observable;
import com.hartveld.stream.reactive.ObservableFactory;
import com.hartveld.stream.reactive.Observer;
import com.hartveld.stream.reactive.tests.AbstractSubjectObserverTestBase;
import org.junit.Test;

public class MapTest extends AbstractSubjectObserverTestBase {

	@Override
	protected void initializeFor(Observable<String> source, Observer<String> target) {
		source.map(s -> s).subscribe(target);
	}

	@Test
	public void testThatMapOperatorWorks() throws Exception {
		Observable<String> source = ObservableFactory.observableOf(hello, world);

		AutoCloseable subscription = source.map(x -> x.substring(0, 1)).subscribe(
			e -> {
				switch (e.charAt(0)) {
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
		Observable<String> source = ObservableFactory.observableOf(hello, world);

		AutoCloseable subscription = source.map(x -> x.substring(5, 6)).subscribe(
			e -> fail("Got element: " + e),
			e -> gotError = true,
			() -> fail("Got completed")
		);

		subscription.close();

		assertThat("Did not observe error", gotError, is(true));
	}

}
