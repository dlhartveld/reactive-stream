package com.hartveld.stream.reactive.operators;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

import com.hartveld.stream.reactive.Observable;
import com.hartveld.stream.reactive.ObservableFactory;
import com.hartveld.stream.reactive.concurrency.Schedulers;
import java.util.List;
import org.junit.Test;

public class CollectTest {

	@Test
	public void testCollect() {
		final Observable<String> source = ObservableFactory
				.observableOf("Hello", "World", "!")
				.observeOn(Schedulers.defaultScheduler());

		final List<String> list = source.collect(toList());

		assertThat(list, hasSize(3));
		assertThat(list, contains("Hello", "World", "!"));
	}

}
