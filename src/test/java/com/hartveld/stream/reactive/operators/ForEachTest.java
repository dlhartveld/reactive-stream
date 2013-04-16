package com.hartveld.stream.reactive.operators;

import static org.mockito.Mockito.verify;

import com.hartveld.stream.reactive.Observable;
import com.hartveld.stream.reactive.ObservableFactory;
import java.util.function.Consumer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ForEachTest {

	private static final String HELLO = "hello";
	private static final String WORLD = "world";
	private static final String EXCLAMATION = "!";

	@Mock
	private Consumer<String> target;

	@Test
	public void testThatForEachForwardsSingleElement() {
		final Observable<String> source = ObservableFactory.observableOf(HELLO);

		source.forEach(target);

		verify(target).accept(HELLO);
	}

	@Test
	public void testThatForEachForwardsMultipleElements() {
		final Observable<String> source = ObservableFactory.observableOf(HELLO, WORLD, EXCLAMATION);

		source.forEach(target);

		verify(target).accept(HELLO);
		verify(target).accept(WORLD);
		verify(target).accept(EXCLAMATION);
	}

}
