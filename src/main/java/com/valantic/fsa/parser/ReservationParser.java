package com.valantic.fsa.parser;

import com.valantic.fsa.model.ReservationData;

public interface ReservationParser {

	public ReservationData parse(String text);
}
