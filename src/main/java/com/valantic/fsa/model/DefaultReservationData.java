package com.valantic.fsa.model;

import java.time.LocalDate;
import java.time.LocalTime;

public class DefaultReservationData implements ReservationData {

	private String name;
	private LocalDate date;
	private LocalTime time;
	private int peopleCount;
	
	public DefaultReservationData() {
		
	}

	public DefaultReservationData(String name, LocalDate date, LocalTime time, int peopleCount) {
		this.name = name;
		this.date = date;
		this.time = time;
		this.peopleCount = peopleCount;
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
	public int getPeopleCount() {
		return peopleCount;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("DefaultReservationDate[")
			.append("name='").append(this.getName())
			.append("', date=").append(this.getDate())
			.append(", time=").append(this.getTime())
			.append(", people=").append(this.getPeopleCount());
		return sb.toString();
	}

}
