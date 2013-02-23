package com.hartveld.rx.operators;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import com.hartveld.rx.Observable;
import com.hartveld.rx.ObservableFactory;
import com.hartveld.rx.Observer;
import com.hartveld.rx.tests.AbstractSubjectObserverTestBase;
import java.util.stream.FlatMapper;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FlatMapTest extends AbstractSubjectObserverTestBase {

	private static final Logger LOG = LoggerFactory.getLogger(FlatMapTest.class);

	@Override
	protected void initializeFor(Observable<String> source, Observer<String> target) {
		final FlatMapper<String, String> m = (x, sink) -> {
			for (String s : x.split(",")) {
				sink.accept(s);
			}
		};

		source.flatMap(m).subscribe(target);
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

	private FlatMapper<String, String> split(String regex) {
		return (x, sink) -> {
			LOG.trace("Mapping: {}", x);
			for (String s : x.split(regex)) {
				final String trimmed = s.trim();
				LOG.trace("Flattened: {}", trimmed);
				sink.accept(trimmed);
			}
			LOG.trace("Mapping {} done.", x);
		};
	}

}
