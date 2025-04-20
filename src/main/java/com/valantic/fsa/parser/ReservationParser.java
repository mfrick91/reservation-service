package com.valantic.fsa.parser;

import com.valantic.fsa.model.ReservationData;
import com.valantic.fsa.model.ReservationRequest;

public interface ReservationParser {

	public ReservationData parse(ReservationRequest request);
}
