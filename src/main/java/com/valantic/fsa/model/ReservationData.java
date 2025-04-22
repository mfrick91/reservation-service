package com.valantic.fsa.model;

import java.time.LocalDate;
import java.time.LocalTime;

public interface ReservationData {
	
	public String getName();
	
	public LocalDate getDate();
	
	public LocalTime getTime();
	
	public Integer getPeopleCount();

}
