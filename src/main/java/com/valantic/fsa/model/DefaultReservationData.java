package com.valantic.fsa.model;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Default implementation of the {@code ReservationData} interface.
 * 
 * @author M. Frick
 */
public class DefaultReservationData implements ReservationData {

	/**
	 * The name of the person making the reservation.
	 */
	private String name;

	/**
	 * The date of the reservation.
	 */
	private LocalDate date;

	/**
	 * The time of the reservation.
	 */
	private LocalTime time;

	/**
	 * The number of people for the reservation.
	 */
	private int numberOfPeople;

	/**
	 * Constructs a new {@code DefaultReservationData} with the specified name.
	 * 
	 * @param name the name of the person making the reservation
	 */
	public DefaultReservationData() {
		this(null);
	}

	/**
	 * Constructs a new {@code DefaultReservationData} with the specified name and date.
	 * 
	 * @param name the name of the person making the reservation
	 * @param date the date of the reservation
	 */
	public DefaultReservationData(String name) {
		this(name, null);
	}

	/**
	 * Constructs a new {@code DefaultReservationData} with the specified name and date.
	 * 
	 * @param name the name of the person making the reservation
	 * @param date the date of the reservation
	 */
	public DefaultReservationData(String name, LocalDate date) {
		this(name, date, null);
	}

	/**
	 * Constructs a new {@code DefaultReservationData} with the specified name, date, and time.
	 * 
	 * @param name the name of the person making the reservation
	 * @param date the date of the reservation
	 * @param time the time of the reservation
	 */
	public DefaultReservationData(String name, LocalDate date, LocalTime time) {
		this(name, date, time, -1);
	}

	/**
	 * Constructs a new {@code DefaultReservationData} with the specified name, date, time, and number of people.
	 * 
	 * @param name the name of the person making the reservation
	 * @param date the date of the reservation
	 * @param time the time of the reservation
	 * @param numberOfPeople the number of people for the reservation
	 */
	public DefaultReservationData(String name, LocalDate date, LocalTime time, int numberOfPeople) {
		this.name = name;
		this.date = date;
		this.time = time;
		this.numberOfPeople = numberOfPeople;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public LocalDate getDate() {
		return date;
	}

	@Override
	public LocalTime getTime() {
		return time;
	}

	@Override
	public int getNumberOfPeople() {
		return numberOfPeople;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("DefaultReservationDate[")
			.append("name='").append(this.getName())
			.append("', date=").append(this.getDate())
			.append(", time=").append(this.getTime())
			.append(", numberOfPeople=").append(this.getNumberOfPeople())
			.append("]");
		return sb.toString();
	}

}
