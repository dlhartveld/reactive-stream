package com.hartveld.stream.reactive.factory;

import static org.hamcrest.Matchers.arrayWithSize;
import static org.hamcrest.Matchers.hasItemInArray;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import com.hartveld.stream.reactive.AutoCloseables;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class CompositeAutoCloseableTest {

	@Mock
	private AutoCloseable ac0;
	@Mock
	private AutoCloseable ac1;
	@Mock
	private AutoCloseable ac2;

	@Test
	public void testThatCompositeAutoCloseableClosesAllChildren() throws Exception {
		final AutoCloseable composite = AutoCloseables.composite(ac0, ac1, ac2);

		composite.close();

		verify(ac0).close();
		verify(ac1).close();
		verify(ac2).close();
	}

	@Test
	public void testThatCompositeAutoCloseableForwardsExceptionFromChild() throws Exception {
		final AutoCloseable composite = AutoCloseables.composite(ac0);

		final Exception expected = new Exception();
		doThrow(expected).when(ac0).close();

		Exception caught = new Exception();
		try {
			composite.close();
		} catch (final Exception e) {
			caught = e;
		}

		assertThat(caught, is(notNullValue()));
		assertThat(caught.getSuppressed(), is(arrayWithSize(1)));
		assertThat(caught.getSuppressed(), hasItemInArray((Throwable)expected));
	}

	@Test
	public void testThatCompositeAutoCloseableClosesSecondChildAfterFirstChildThrowsException() throws Exception {
		final AutoCloseable composite = AutoCloseables.composite(ac0, ac1);

		doThrow(new Exception()).when(ac0).close();

		try {
			composite.close();
		} catch (Exception e) { }

		verify(ac1).close();
	}

}
