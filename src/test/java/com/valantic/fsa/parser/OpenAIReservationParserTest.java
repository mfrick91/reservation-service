package com.valantic.fsa.parser;

import org.junit.jupiter.api.BeforeAll;

class OpenAIReservationParserTest extends AbstractReservationParserTest {

	@BeforeAll
	public static void beforeClass() {
		parser = new OpenAIReservationParser();
	}

}
