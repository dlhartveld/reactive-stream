package com.hartveld.stream.reactive.operators;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import com.hartveld.stream.reactive.Observable;
import com.hartveld.stream.reactive.ObservableFactory;
import com.hartveld.stream.reactive.Observer;
import com.hartveld.stream.reactive.tests.AbstractSubjectObserverTestBase;
import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Stream;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FlatMapTest extends AbstractSubjectObserverTestBase {

	private static final Logger LOG = LoggerFactory.getLogger(FlatMapTest.class);

	@Override
	protected void initializeFor(Observable<String> source, Observer<String> target) {
		source.flatMap(split(",")).subscribe(target);
	}

	@Test
	public void testThatFlatMapBasicsWork() {
		final Observable<String> source = ObservableFactory.observableOf("Hello world", "My name is David");

		source.flatMap(split(" ")).subscribe(target);

		verify(target).onNext("Hello");
		verify(target).onNext("world");
		verify(target).onNext("My");
		verify(target).onNext("name");
		verify(target).onNext("is");
		verify(target).onNext("David");
		verify(target).onCompleted();
		verifyNoMoreInteractions(target);
	}

	private static Function<String, Stream<String>> split(String regex) {
		return x -> Arrays.stream(x.split(regex));
	}

}
