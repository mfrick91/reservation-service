package com.valantic.fsa.model;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Interface representing the structured data extracted from a reservation request.
 * 
 * @author M. Frick
 */
public interface ReservationData {

	/**
	 * Returns the name of the person making the reservation.
	 * @return the customer's name, or an empty string if not specified
	 */
	public String getName();

	/**
	 * Returns the date of the reservation.
	 * @return the reservation date
	 */
	public LocalDate getDate();

	/**
	 * Returns the time of the reservation.
	 * @return the reservation time
	 */
	public LocalTime getTime();

	/**
	 * Returns the number of people for the reservation.
	 * @return the number of people, or -1 if not specified
	 */
	public int getNumberOfPeople();

}
