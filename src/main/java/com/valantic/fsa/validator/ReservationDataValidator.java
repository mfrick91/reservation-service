package com.valantic.fsa.validator;

import java.time.LocalDate;

import com.valantic.fsa.model.ReservationData;

/**
 * Validator for {@code ReservationData} objects.
 * 
 * @author M. Frick
 */
public class ReservationDataValidator {

	/**
	 * Validates the name in the reservation data.
	 * 
	 * @param data the reservation data to validate
	 * @return true if the name is valid, false otherwise
	 */
	public static boolean isNameValid(ReservationData data) {
		if (data == null) {
			return false;
		}
		String name = data.getName();
		return (name != null) && !name.trim().isEmpty();
	}

	/**
	 * Validates the date in the reservation data.
	 * 
	 * @param data the reservation data to validate
	 * @return true if the date is valid, false otherwise
	 */
	public static boolean isDateValid(ReservationData data) {
		if (data == null) {
			return false;
		}
		LocalDate date = data.getDate();
		if (date == null) {
			return false;
		}
		// date should not be in the past
		return !date.isBefore(LocalDate.now());
	}

	/**
	 * Validates the time in the reservation data.
	 * 
	 * @param data the reservation data to validate
	 * @return true if the time is valid, false otherwise
	 */
	public static boolean isTimeValid(ReservationData data) {
		if (data == null) {
			return false;
		}
		return data.getTime() != null;
	}

	/**
	 * Validates the number of people in the reservation data.
	 * 
	 * @param data the reservation data to validate
	 * @return true if the number of people is valid, false otherwise
	 */
	public static boolean isNumberOfPeopleValid(ReservationData data) {
		if (data == null) {
			return false;
		}
		return data.getNumberOfPeople() >= 0;
	}

	/**
	 * Validates all fields in the reservation data.
	 * 
	 * @param data the reservation data to validate
	 * @return true if all fields are valid, false otherwise
	 */
	public static boolean isValid(ReservationData data) {
		return isNameValid(data) && 
			   isDateValid(data) && 
			   isTimeValid(data) && 
			   isNumberOfPeopleValid(data);
	}
}
