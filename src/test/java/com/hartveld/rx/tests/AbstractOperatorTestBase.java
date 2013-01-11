package com.hartveld.rx.tests;

import org.junit.Before;

public abstract class AbstractOperatorTestBase {

	protected static final String hello = "Hello";
	protected static final String world = "world";

	protected boolean gotHello;
	protected boolean gotWorld;

	protected boolean gotError;
	protected boolean completed;

	@Before
	public void setUp() {
		gotHello = false;
		gotWorld = false;

		gotError = false;
		completed = false;
	}

}
