package com.valantic.fsa.model;

import java.time.LocalDate;
import java.time.LocalTime;

public class DefaultReservationData implements ReservationData {

	private String name;
	private LocalDate date;
	private LocalTime time;
	private int numberOfPeople;
	
	public DefaultReservationData() {
		this("");
	}
	
	public DefaultReservationData(String name) {
		this(name, LocalDate.of(0, 0, 0));
	}
	
	public DefaultReservationData(String name, LocalDate date) {
		this(name, date, LocalTime.of(0, 0, 0, 0));
	}
	
	public DefaultReservationData(String name, LocalDate date, LocalTime time) {
		this(name, date, time, 0);
	}
	
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
