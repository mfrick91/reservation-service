package com.valantic.fsa.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDate;
import java.time.LocalTime;

import org.junit.jupiter.api.Test;

import com.valantic.fsa.model.DefaultReservationRequest;
import com.valantic.fsa.model.ReservationData;
import com.valantic.fsa.model.ReservationRequest;

class ReservationParserTest {

	private static final ReservationParser PARSER = new BasicReservationParser();

	private static final String NAME = "Klaus Müller";
	private static final LocalDate DATE = LocalDate.of(LocalDate.now().getYear(), 1, 1);
	private static final LocalTime TIME = LocalTime.of(20, 0);
	private static final int NUMBER_OF_PEOPLE = 2;

	private void assertRequest(ReservationRequest request, String expectedName, LocalDate expectedDate,
			LocalTime expectedTime, Integer expectedNumberOfPeople) {
		ReservationData response = PARSER.parse(request);
		assertNotNull(response);
		assertEquals(expectedName, response.getName());
		assertEquals(expectedDate, response.getDate());
		assertEquals(expectedTime, response.getTime());
		assertEquals(expectedNumberOfPeople, response.getNumberOfPeople());
	}

	@Test
	void testUpperCase() {
		ReservationRequest request = new DefaultReservationRequest(
				"Hallo, bitte für 2 Personen einen Tisch am 1. Januar um 20:00 Uhr, vielen Dank Klaus Müller".toUpperCase());
		assertRequest(request, NAME.toUpperCase(), DATE, TIME, NUMBER_OF_PEOPLE);
	}
	
	@Test
	void testNestedStrings() {
		ReservationRequest request = new DefaultReservationRequest(
				"\"Hallo, bitte für 2 Personen einen Tisch am 1. Januar um 20:00 Uhr, vielen Dank Klaus Müller\"");
		assertRequest(request, NAME, DATE, TIME, NUMBER_OF_PEOPLE);
		
		request = new DefaultReservationRequest(
				"'Hallo, bitte für 2 Personen einen Tisch am 1. Januar um 20:00 Uhr, vielen Dank Klaus Müller'");
		assertRequest(request, NAME, DATE, TIME, NUMBER_OF_PEOPLE);
	}
	
	@Test
	void testNullCases() {
		ReservationRequest request = new DefaultReservationRequest(
				"Hallo, bitte für 2 Personen einen Tisch am 1. Januar um 20:00 Uhr, vielen Dank");
		assertRequest(request, "", DATE, TIME, NUMBER_OF_PEOPLE);
		
		request = new DefaultReservationRequest(
				"Hallo, bitte für 2 Personen einen Tisch um 20:00 Uhr, vielen Dank Klaus Müller");
		assertRequest(request, NAME, request.getTimestamp().toLocalDate(), TIME, NUMBER_OF_PEOPLE);

		request = new DefaultReservationRequest(
				"Hallo, bitte für 2 Personen einen Tisch am 1. Januar, vielen Dank Klaus Müller");
		assertRequest(request, NAME, DATE, LocalTime.of(0, 0, 0, 0), NUMBER_OF_PEOPLE);
		
		request = new DefaultReservationRequest(
				"Hallo, bitte einen Tisch am 1. Januar um 20:00 Uhr, vielen Dank Klaus Müller");
		assertRequest(request, NAME, DATE, TIME, 0);
	}

	@Test
	void testNumberOfPeopleCount() {
		ReservationRequest request = new DefaultReservationRequest(
	            "Hallo, bitte für zwei Personen einen Tisch am 1. Januar um 20:00 Uhr, vielen Dank Klaus Müller");
		assertRequest(request, NAME, DATE, TIME, NUMBER_OF_PEOPLE);
		
		request = new DefaultReservationRequest(
	            "Hallo, bitte einen Tisch zu zwölft für den 1. Januar um 20:00 Uhr, vielen Dank Klaus Müller");
		assertRequest(request, NAME, DATE, TIME, 12);
	}

	@Test
	void testRelativeDates() {
		LocalDate today = LocalDate.now();
		LocalDate tomorrow = today.plusDays(1);
		LocalDate nextWeek = today.plusWeeks(1);
		LocalDate nextMonth = today.plusMonths(2);

		ReservationRequest request = new DefaultReservationRequest(
            "Hallo, bitte für zwei Personen einen Tisch für morgen um 20:00 Uhr, vielen Dank Klaus Müller");
		assertRequest(request, NAME, tomorrow, TIME, NUMBER_OF_PEOPLE);

		request = new DefaultReservationRequest(
            "Hallo, bitte für zwei Personen einen Tisch für nächste Woche um 20:00 Uhr, vielen Dank Klaus Müller");
		assertRequest(request, NAME, nextWeek, TIME, NUMBER_OF_PEOPLE);

		request = new DefaultReservationRequest(
            "Hallo, bitte für zwei Personen einen Tisch für übernächsten Monat um 20:00 Uhr, vielen Dank Klaus Müller");
		assertRequest(request, NAME, nextMonth, TIME, NUMBER_OF_PEOPLE);
		
		request = new DefaultReservationRequest(
	            "Hallo, bitte für zwei Personen einen Tisch für nächste Woche um 8 Uhr abends, vielen Dank Klaus Müller");
		assertRequest(request, NAME, nextWeek, TIME, NUMBER_OF_PEOPLE);
	}

	@Test
	void testWeekdays() {
		LocalDate today = LocalDate.now();
		LocalDate nextMonday = today.plusDays(1);
		while (nextMonday.getDayOfWeek().getValue() != 1) {
			nextMonday = nextMonday.plusDays(1);
		}

		ReservationRequest request = new DefaultReservationRequest(
            "Hallo, bitte für zwei Personen einen Tisch für nächsten Montag um 20:00 Uhr, vielen Dank Klaus Müller");
		assertRequest(request, NAME, nextMonday, TIME, NUMBER_OF_PEOPLE);
		
		LocalDate nextNextMonday = nextMonday.plusDays(1);
		while (nextNextMonday.getDayOfWeek().getValue() != 1) {
			nextNextMonday = nextNextMonday.plusDays(1);
		}

		request = new DefaultReservationRequest(
            "Hallo, bitte für zwei Personen einen Tisch für übernächsten Montag um 20:00 Uhr, vielen Dank Klaus Müller");
		assertRequest(request, NAME, nextNextMonday, TIME, NUMBER_OF_PEOPLE);
	}

	@Test
	void testInXDaysWeeksMonthsYears() {
		LocalDate today = LocalDate.now();
		LocalDate in2Days = today.plusDays(2);
		LocalDate in3Weeks = today.plusWeeks(3);
		LocalDate in4Months = today.plusMonths(4);
		LocalDate in5Years = today.plusYears(5);

		ReservationRequest request = new DefaultReservationRequest(
            "Hallo, bitte für zwei Personen einen Tisch in 2 Tagen um 20:00 Uhr, vielen Dank Klaus Müller");
		assertRequest(request, NAME, in2Days, TIME, NUMBER_OF_PEOPLE);

		request = new DefaultReservationRequest(
            "Hallo, bitte für zwei Personen einen Tisch in 3 Wochen um 20:00 Uhr, vielen Dank Klaus Müller");
		assertRequest(request, NAME, in3Weeks, TIME, NUMBER_OF_PEOPLE);

		request = new DefaultReservationRequest(
            "Hallo, bitte für zwei Personen einen Tisch in vier Monaten um 20:00 Uhr, vielen Dank Klaus Müller");
		assertRequest(request, NAME, in4Months, TIME, NUMBER_OF_PEOPLE);

		request = new DefaultReservationRequest(
            "Hallo, bitte für zwei Personen einen Tisch in fünf Jahren um 20:00 Uhr, vielen Dank Klaus Müller");
		assertRequest(request, NAME, in5Years, TIME, NUMBER_OF_PEOPLE);
	}

	@Test
	void testBetweenXAndYPeople() {
		ReservationRequest request = new DefaultReservationRequest(
            "Hallo, bitte für zwischen 2 und 4 Personen einen Tisch am 1. Januar um 20:00 Uhr, vielen Dank Klaus Müller");
		assertRequest(request, NAME, DATE, TIME, 4);

		request = new DefaultReservationRequest(
            "Hallo, bitte für 2 bis 4 Personen einen Tisch am 1. Januar um 20:00 Uhr, vielen Dank Klaus Müller");
		assertRequest(request, NAME, DATE, TIME, 4);

		request = new DefaultReservationRequest(
            "Hallo, bitte für 2-4 Personen einen Tisch am 1. Januar um 20:00 Uhr, vielen Dank Klaus Müller");
		assertRequest(request, NAME, DATE, TIME, 4);
	}

	@Test
	void testAtLeastXPeople() {
		ReservationRequest request = new DefaultReservationRequest(
            "Hallo, bitte für mindestens 2 Personen einen Tisch am 1. Januar um 20:00 Uhr, vielen Dank Klaus Müller");
		assertRequest(request, NAME, DATE, TIME, 2);

		request = new DefaultReservationRequest(
            "Hallo, bitte für ab 2 Personen einen Tisch am 1. Januar um 20:00 Uhr, vielen Dank Klaus Müller");
		assertRequest(request, NAME, DATE, TIME, 2);
	}

	@Test
	void testUpToXPeople() {
		ReservationRequest request = new DefaultReservationRequest(
            "Hallo, bitte für bis zu 4 Personen einen Tisch am 1. Januar um 20:00 Uhr, vielen Dank Klaus Müller");
		assertRequest(request, NAME, DATE, TIME, 4);

		request = new DefaultReservationRequest(
            "Hallo, bitte für maximal 4 Personen einen Tisch am 1. Januar um 20:00 Uhr, vielen Dank Klaus Müller");
		assertRequest(request, NAME, DATE, TIME, 4);
	}

	@Test
	void testNotMoreThanXPeople() {
		ReservationRequest request = new DefaultReservationRequest(
            "Hallo, bitte für nicht mehr als 4 Personen einen Tisch am 1. Januar um 20:00 Uhr, vielen Dank Klaus Müller");
		assertRequest(request, NAME, DATE, TIME, 4);
	}

	@Test
	void testMixedCounts() {
		ReservationRequest request = new DefaultReservationRequest(
            "Hallo, bitte für 2 bis 4 Personen, mindestens 2, maximal 4 Personen einen Tisch am 1. Januar um 20:00 Uhr, vielen Dank Klaus Müller");
		assertRequest(request, NAME, DATE, TIME, 4);
	}
	
} 