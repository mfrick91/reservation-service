package com.valantic.fsa.parser;

import com.valantic.fsa.model.ReservationData;
import com.valantic.fsa.model.ReservationRequest;

/**
 * Interface for parsing a {@code ReservationData} object from a {@code ReservationRequest} object.
 * 
 * @author M. Frick
 */
public interface ReservationParser {

	/**
	 * Parses the reservation data from a reservation request.
	 * 
	 * @param request the reservation request
	 * @return the data parsed from the request
	 */
	public ReservationData parse(ReservationRequest request);

}
