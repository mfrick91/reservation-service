package com.valantic.fsa.model;

import java.time.LocalDateTime;

/**
 * Interface representing a reservation request that needs to be parsed.
 * 
 * @author M.Frick
 */
public interface ReservationRequest {

	/**
	 * Returns the original text of the reservation request.
	 * @return the raw reservation request text
	 */
	public String getText();

	/**
	 * Returns the timestamp when the reservation request was received.
	 * @return the request timestamp
	 */
	public LocalDateTime getTimestamp();
	
}
