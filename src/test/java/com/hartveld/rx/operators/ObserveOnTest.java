package com.hartveld.rx.operators;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import com.hartveld.rx.Observable;
import com.hartveld.rx.Observer;
import com.hartveld.rx.ObservableFactory;
import com.hartveld.rx.tests.AbstractSubjectObserverTestBase;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import org.junit.Test;

public class ObserveOnTest extends AbstractSubjectObserverTestBase {

	@Override
	protected void initializeFor(Observable<String> source, Observer<String> target) {
		source.observeOn(super.syncExecSvc).subscribe(target);
	}

	@Test
	public void testThatObserverIsNotifiedOnBackgroundThreadWithObserveOn() throws Exception {
		long mainThreadId = Thread.currentThread().getId();
		ExecutorService svc = Executors.newFixedThreadPool(1);
		Observable<String> source = ObservableFactory.observableOf(hello, world);
		AutoCloseable subscription = source.observeOn(svc).subscribe(
			text -> {
				switch(text) {
				case hello:
					long observingThreadId0 = Thread.currentThread().getId();
					assertThat("Observation 'element' is not on other thread: ", observingThreadId0, is(not(mainThreadId)));
					gotHello = true;
					break;
				case world:
					long observingThreadId1 = Thread.currentThread().getId();
					assertThat("Observation 'element' is not on other thread: ", observingThreadId1, is(not(mainThreadId)));
					gotWorld = true;
					break;
				default:
					fail("Got unknown element: " + text);
				}
			},
			e -> fail("Caught exception: " + e.getMessage()),
			() -> {
				long observingThreadId2 = Thread.currentThread().getId();
				assertThat("Observation 'completed' is not on other thread: ", observingThreadId2, is(not(mainThreadId)));
				completed = true;
			}
		);

		svc.shutdown();
		assertThat("ExecutorService tasks failed to terminate", svc.awaitTermination(1, TimeUnit.SECONDS), is(true));

		subscription.close();

		assertThat("Did not find hello", gotHello, is(true));
		assertThat("Did not find world", gotWorld, is(true));
		assertThat("Did not complete", completed, is(true));
	}

}
