package com.valantic.fsa.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.junit.jupiter.api.Test;

import com.valantic.fsa.model.DefaultReservationRequest;
import com.valantic.fsa.model.ReservationData;
import com.valantic.fsa.model.ReservationRequest;

abstract class AbstractReservationParserTest {

	protected static ReservationParser parser;

	private static final String NAME = "Klaus Müller";
	private static final LocalDate DATE = LocalDate.of(LocalDate.now().getYear(), 1, 1);
	private static final LocalTime TIME = LocalTime.of(20, 0);
	private static final int NUMBER_OF_PEOPLE = 2;

	private void assertRequestEquals(ReservationRequest request, String expectedName, LocalDate expectedDate,
			LocalTime expectedTime, int expectedNumberOfPeople) {
		ReservationData response = parser.parse(request);
		assertNotNull(response);
		assertEquals(expectedName, response.getName());
		assertEquals(expectedDate, response.getDate());
		assertEquals(expectedTime, response.getTime());
		assertEquals(expectedNumberOfPeople, response.getNumberOfPeople());
	}

	@Test
	void testParseUpperCase() {
		ReservationRequest upperCaseRequest = new DefaultReservationRequest(
				"Hallo, bitte für zwei Personen einen Tisch am 1. Januar um 20:00 Uhr, Vielen Dank Klaus Müller".toUpperCase());
		assertRequestEquals(upperCaseRequest, NAME.toUpperCase(), DATE, TIME, NUMBER_OF_PEOPLE);
	}

	@Test
	void testParseNumberOfPeoples() {
		ReservationRequest forTwoRequest = new DefaultReservationRequest(
	            "Hallo, bitte für zwei Personen einen Tisch am 1. Januar um 20:00 Uhr, vielen Dank Klaus Müller");
		assertRequestEquals(forTwoRequest, NAME, DATE, TIME, NUMBER_OF_PEOPLE);
		
		ReservationRequest twelvePeopleRequest = new DefaultReservationRequest(
	            "Hallo, bitte einen Tisch zu zwölft für den 1. Januar um 20:00 Uhr, vielen Dank Klaus Müller");
		assertRequestEquals(twelvePeopleRequest, NAME, DATE, TIME, 12);
	}

	@Test
	void testParseRelativeDates() {
		LocalDateTime today = LocalDateTime.now();
		LocalDate tomorrow = today.plusDays(1).toLocalDate();
		LocalDate nextWeek = today.plusWeeks(1).toLocalDate();
		LocalDate nextMonth = today.plusMonths(2).toLocalDate();

		ReservationRequest tomorrowRequest = new DefaultReservationRequest(
            "Hallo, bitte für zwei Personen einen Tisch für morgen um 20:00 Uhr, vielen Dank Klaus Müller", today);
		assertRequestEquals(tomorrowRequest, NAME, tomorrow, TIME, NUMBER_OF_PEOPLE);
		
		ReservationRequest nextWeekRequest = new DefaultReservationRequest(
	            "Hallo, bitte für zwei Personen einen Tisch für nächste Woche um 8 Uhr abends, vielen Dank Klaus Müller", today);
		assertRequestEquals(nextWeekRequest, NAME, nextWeek, TIME, NUMBER_OF_PEOPLE);
		
		ReservationRequest nextMonthRequest = new DefaultReservationRequest(
            "Hallo, bitte für zwei Personen einen Tisch für übernächsten Monat um 20:00 Uhr, vielen Dank Klaus Müller", today);
		assertRequestEquals(nextMonthRequest, NAME, nextMonth, TIME, NUMBER_OF_PEOPLE);
	}

	@Test
	void testParseRelativeWeekdays() {
		LocalDateTime today = LocalDateTime.now();
		LocalDate nextMonday = today.plusDays(1).toLocalDate();
		while (nextMonday.getDayOfWeek().getValue() != 1) {
			nextMonday = nextMonday.plusDays(1);
		}
		
		LocalDate nextNextMonday = nextMonday.plusDays(1);
		while (nextNextMonday.getDayOfWeek().getValue() != 1) {
			nextNextMonday = nextNextMonday.plusDays(1);
		}
		ReservationRequest nextMondyRequest = new DefaultReservationRequest(
            "Hallo, bitte für zwei Personen einen Tisch für nächsten Montag um 20:00 Uhr, vielen Dank Klaus Müller", today);
		assertRequestEquals(nextMondyRequest, NAME, nextMonday, TIME, NUMBER_OF_PEOPLE);

		ReservationRequest nextNextMondayRequest = new DefaultReservationRequest(
            "Hallo, bitte für zwei Personen einen Tisch für übernächsten Montag um 20:00 Uhr, vielen Dank Klaus Müller", today);
		assertRequestEquals(nextNextMondayRequest, NAME, nextNextMonday, TIME, NUMBER_OF_PEOPLE);
	}

	@Test
	void testParseRelativeDaysWeeksMonthsYears() {
		LocalDateTime today = LocalDateTime.now();
		LocalDate inTwoDays = today.plusDays(2).toLocalDate();
		LocalDate inThreeWeeks = today.plusWeeks(3).toLocalDate();
		LocalDate inFourMonths = today.plusMonths(4).toLocalDate();
		LocalDate inFiveYears = today.plusYears(5).toLocalDate();

		ReservationRequest inTwoDaysRequest = new DefaultReservationRequest(
            "Hallo, bitte für zwei Personen einen Tisch in 2 Tagen um 20:00 Uhr, vielen Dank Klaus Müller", today);
		assertRequestEquals(inTwoDaysRequest, NAME, inTwoDays, TIME, NUMBER_OF_PEOPLE);

		ReservationRequest inThreeWeeksRequest = new DefaultReservationRequest(
            "Hallo, bitte für zwei Personen einen Tisch in 3 Wochen um 20:00 Uhr, vielen Dank Klaus Müller", today);
		assertRequestEquals(inThreeWeeksRequest, NAME, inThreeWeeks, TIME, NUMBER_OF_PEOPLE);

		ReservationRequest inFourMonthsRequest = new DefaultReservationRequest(
            "Hallo, bitte für zwei Personen einen Tisch in vier Monaten um 20:00 Uhr, vielen Dank Klaus Müller", today);
		assertRequestEquals(inFourMonthsRequest, NAME, inFourMonths, TIME, NUMBER_OF_PEOPLE);

		ReservationRequest inFiveYearsRequest = new DefaultReservationRequest(
            "Hallo, bitte für zwei Personen einen Tisch in fünf Jahren um 20:00 Uhr, vielen Dank Klaus Müller", today);
		assertRequestEquals(inFiveYearsRequest, NAME, inFiveYears, TIME, NUMBER_OF_PEOPLE);
	}

	@Test
	void testParsePeopleRange() {
		ReservationRequest betweenRequest = new DefaultReservationRequest(
            "Hallo, bitte für zwischen 2 und 4 Personen einen Tisch am 1. Januar um 20:00 Uhr, vielen Dank Klaus Müller");
		assertRequestEquals(betweenRequest, NAME, DATE, TIME, 4);

		ReservationRequest forRequest = new DefaultReservationRequest(
            "Hallo, bitte für 2 bis 4 Personen einen Tisch am 1. Januar um 20:00 Uhr, vielen Dank Klaus Müller");
		assertRequestEquals(forRequest, NAME, DATE, TIME, 4);

		ReservationRequest rangeRequest = new DefaultReservationRequest(
            "Hallo, bitte für 2-4 Personen einen Tisch am 1. Januar um 20:00 Uhr, vielen Dank Klaus Müller");
		assertRequestEquals(rangeRequest, NAME, DATE, TIME, 4);
	}

	@Test
	void testParseAtLeastPeople() {
		ReservationRequest atLeastTwoRequest = new DefaultReservationRequest(
            "Hallo, bitte für mindestens 2 Personen einen Tisch am 1. Januar um 20:00 Uhr, vielen Dank Klaus Müller");
		assertRequestEquals(atLeastTwoRequest, NAME, DATE, TIME, 2);

		ReservationRequest atLeastFourRequest = new DefaultReservationRequest(
            "Hallo, bitte für ab 4 Personen einen Tisch am 1. Januar um 20:00 Uhr, vielen Dank Klaus Müller");
		assertRequestEquals(atLeastFourRequest, NAME, DATE, TIME, 4);
	}

	@Test
	void testParseUpToPeople() {
		ReservationRequest upToFourRequest = new DefaultReservationRequest(
            "Hallo, bitte für bis zu 4 Personen einen Tisch am 1. Januar um 20:00 Uhr, vielen Dank Klaus Müller");
		assertRequestEquals(upToFourRequest, NAME, DATE, TIME, 4);

		ReservationRequest upToEightRequest = new DefaultReservationRequest(
            "Hallo, bitte für maximal 8 Personen einen Tisch am 1. Januar um 20:00 Uhr, vielen Dank Klaus Müller");
		assertRequestEquals(upToEightRequest, NAME, DATE, TIME, 8);
	}

	@Test
	void testParseNotMoreThanPeople() {
		ReservationRequest notMoreThanFiveRequest = new DefaultReservationRequest(
            "Hallo, bitte für nicht mehr als 5 Personen einen Tisch am 1. Januar um 20:00 Uhr, vielen Dank Klaus Müller");
		assertRequestEquals(notMoreThanFiveRequest, NAME, DATE, TIME, 5);
	}

	@Test
	void testParseMixedNumberOfPeoples() {
		ReservationRequest mixNumberRequest = new DefaultReservationRequest(
            "Hallo, bitte für 2 bis 4 Personen, mindestens 2, maximal 4 Personen einen Tisch am 1. Januar um 20:00 Uhr, vielen Dank Klaus Müller");
		assertRequestEquals(mixNumberRequest, NAME, DATE, TIME, 4);
	}
	
	@Test
	void testParseNestedStrings() {
		ReservationRequest nestedRequest = new DefaultReservationRequest(
				"'Hallo, bitte für zwei Personen einen Tisch am 1. Januar. um 20:00 Uhr, Vielen Dank Klaus Müller'");
		assertRequestEquals(nestedRequest, NAME, DATE, TIME, NUMBER_OF_PEOPLE);

		ReservationRequest nestedPeopleRequest = new DefaultReservationRequest(
				"\"Hallo, bitte für 'zwei' Personen einen Tisch am 1. Januar um 20:00 Uhr, Vielen Dank Klaus Müller\"");
		assertRequestEquals(nestedPeopleRequest, NAME, DATE, TIME, NUMBER_OF_PEOPLE);
		
		ReservationRequest nestedDateRequest = new DefaultReservationRequest(
				"\"'Hallo, bitte für zwei Personen einen Tisch am '1.' \"Januar\" um 20:00 Uhr, vielen Dank 'Klaus Müller''\"");
		assertRequestEquals(nestedDateRequest, NAME, DATE, TIME, NUMBER_OF_PEOPLE);
	}
	
	@Test
	void testParseTypos() {
		ReservationRequest thanksTypoRequst = new DefaultReservationRequest(
				"Hallo, bitte für zwei Personen einen Tisch am 1. Januar um 20:00 Uhr, Vielen Danck Klaus Müller");
		assertRequestEquals(thanksTypoRequst, NAME, DATE, TIME, NUMBER_OF_PEOPLE);
		
		ReservationRequest personTypoRequest = new DefaultReservationRequest(
				"Hallo, bitte für zwei Persoen einen Tisch am 1. Januar um 20:00 Uhr, Vielen Dank Klaus Müller");
		assertRequestEquals(personTypoRequest, NAME, DATE, TIME, NUMBER_OF_PEOPLE);
		
		ReservationRequest timeTypoRequest = new DefaultReservationRequest(
				"Hallo, bitte für zwei Personen einen Tisch am 1. Januar um 20:00 Ur, Vielen Dank Klaus Müller");
		assertRequestEquals(timeTypoRequest, NAME, DATE, TIME, NUMBER_OF_PEOPLE);
	}
	
	@Test
	void testParseNullCases() {
		ReservationRequest nullNameRequest = new DefaultReservationRequest(
				"Hallo, bitte für 2 Personen einen Tisch am 1. Januar um 20:00 Uhr, vielen Dank");
		assertRequestEquals(nullNameRequest, null, DATE, TIME, NUMBER_OF_PEOPLE);
		
		ReservationRequest nullDateRequest = new DefaultReservationRequest(
				"Hallo, bitte für 2 Personen einen Tisch um 20:00 Uhr, vielen Dank Klaus Müller");
		assertRequestEquals(nullDateRequest, NAME, null, TIME, NUMBER_OF_PEOPLE);

		ReservationRequest nullTimeRequest = new DefaultReservationRequest(
				"Hallo, bitte für 2 Personen einen Tisch am 1. Januar, vielen Dank Klaus Müller");
		assertRequestEquals(nullTimeRequest, NAME, DATE, null, NUMBER_OF_PEOPLE);
		
		ReservationRequest noPeopleRequest = new DefaultReservationRequest(
				"Hallo, bitte einen Tisch am 1. Januar um 20:00 Uhr, vielen Dank Klaus Müller");
		assertRequestEquals(noPeopleRequest, NAME, DATE, TIME, -1);
	}
	
} 