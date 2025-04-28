package com.valantic.fsa.validator;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalTime;

import org.junit.jupiter.api.Test;

import com.valantic.fsa.model.DefaultReservationData;
import com.valantic.fsa.model.ReservationData;

class ReservationDataValidatorTest {
	
	private static final String NAME = "Klaus Müller";
	private static final LocalDate DATE = LocalDate.of(LocalDate.now().getYear(), 1, 1);
	private static final LocalTime TIME = LocalTime.of(20, 0);
	private static final int NUMBER_OF_PEOPLE = 2;

	@Test
	void testIsNameValid() {
		ReservationData validData = new DefaultReservationData(NAME, DATE, TIME, NUMBER_OF_PEOPLE);
		assertTrue(ReservationDataValidator.isNameValid(validData));

		assertFalse(ReservationDataValidator.isNameValid(null));

		ReservationData nullNameData = new DefaultReservationData(null, DATE, TIME, NUMBER_OF_PEOPLE);
		assertFalse(ReservationDataValidator.isNameValid(nullNameData));

		ReservationData emptyNameData = new DefaultReservationData("", DATE, TIME, NUMBER_OF_PEOPLE);
		assertFalse(ReservationDataValidator.isNameValid(emptyNameData));
	}

	@Test
	void testIsDateValid() {
		ReservationData validData = new DefaultReservationData(NAME, DATE, TIME, NUMBER_OF_PEOPLE);
		assertTrue(ReservationDataValidator.isNameValid(validData));

		assertFalse(ReservationDataValidator.isDateValid(null));

		ReservationData nullDateData = new DefaultReservationData(NAME, null, TIME, NUMBER_OF_PEOPLE);
		assertFalse(ReservationDataValidator.isDateValid(nullDateData));

		ReservationData pastDateData = new DefaultReservationData(NAME, DATE.minusDays(1), TIME, NUMBER_OF_PEOPLE);
		assertFalse(ReservationDataValidator.isDateValid(pastDateData));
	}

	@Test
	void testIsTimeValid() {
		ReservationData validData = new DefaultReservationData(NAME, DATE, TIME, NUMBER_OF_PEOPLE);
		assertTrue(ReservationDataValidator.isNameValid(validData));

		assertFalse(ReservationDataValidator.isTimeValid(null));

		ReservationData nullTimeData = new DefaultReservationData(NAME, DATE, null, NUMBER_OF_PEOPLE);
		assertFalse(ReservationDataValidator.isTimeValid(nullTimeData));
	}

	@Test
	void testIsNumberOfPeopleValid() {
		ReservationData validData = new DefaultReservationData(NAME, DATE, TIME, NUMBER_OF_PEOPLE);
		assertTrue(ReservationDataValidator.isNameValid(validData));

		ReservationData zeroPeopleData = new DefaultReservationData(NAME, DATE, TIME, 0);
		assertTrue(ReservationDataValidator.isNumberOfPeopleValid(zeroPeopleData));

		ReservationData negativePeopleData = new DefaultReservationData(NAME, DATE, TIME, -1);
		assertFalse(ReservationDataValidator.isNumberOfPeopleValid(negativePeopleData));
	}

	@Test
	void testIsValid() {
		ReservationData validData = new DefaultReservationData(NAME, DATE, TIME, NUMBER_OF_PEOPLE);
		assertTrue(ReservationDataValidator.isNameValid(validData));

		assertFalse(ReservationDataValidator.isValid(null));

		ReservationData invalidNameData = new DefaultReservationData("", DATE, TIME, NUMBER_OF_PEOPLE);
		assertFalse(ReservationDataValidator.isValid(invalidNameData));

		ReservationData invalidDateData = new DefaultReservationData(NAME, LocalDate.now().minusDays(1), TIME, NUMBER_OF_PEOPLE);
		assertFalse(ReservationDataValidator.isValid(invalidDateData));

		ReservationData invalidTimeData = new DefaultReservationData(NAME, DATE, null, NUMBER_OF_PEOPLE);
		assertFalse(ReservationDataValidator.isValid(invalidTimeData));

		ReservationData invalidPeopleData = new DefaultReservationData(NAME, DATE, TIME, -1);
		assertFalse(ReservationDataValidator.isValid(invalidPeopleData));
	}
}
