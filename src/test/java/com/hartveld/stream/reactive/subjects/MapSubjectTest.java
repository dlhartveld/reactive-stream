package com.hartveld.stream.reactive.subjects;

import com.hartveld.stream.reactive.Observable;
import com.hartveld.stream.reactive.Observer;
import com.hartveld.stream.reactive.tests.AbstractSubjectObserverTestBase;
import org.junit.Before;

public class MapSubjectTest extends AbstractSubjectObserverTestBase {

	private Subject<String, String> subject;

	@Before
	@Override
	public void setUp() {
		super.setUp();

		this.subject = new MapSubject<>(s -> s);
	}

	@Override
	protected void initializeFor(final Observable<String> source, final Observer<String> target) {
		subject.subscribe(target);
		source.subscribe(this.subject);
	}

}
