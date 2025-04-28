package com.valantic.fsa.parser;

import org.junit.jupiter.api.BeforeAll;

class BasicReservationParerTest extends AbstractReservationParserTest {

	@BeforeAll
	public static void beforeClass() {
		parser = new BasicReservationParser();
	}

}
