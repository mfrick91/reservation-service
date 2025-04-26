package com.valantic.fsa.model;

import java.time.LocalDateTime;

/**
 * Default implementation of the {@code ReservationRequest} interface.
 * 
 * @author M. Frick
 */
public class DefaultReservationRequest implements ReservationRequest {
    
    /**
     * The text of the reservation request.
     */
    private final String text;
    
    /**
     * The timestamp of the reservation request.
     */
    private final LocalDateTime timestamp;
    
    /**
     * Constructs a new {@code DefaultReservationRequest} with the specified text.
     * 
     * @param text the text of the reservation request
     */
    public DefaultReservationRequest(String text) {
    	this(text, LocalDateTime.now());
    }

    /**
     * Constructs a new {@code DefaultReservationRequest} with the specified text and timestamp.
     * 
     * @param text the text of the reservation request
     * @param timestemp the timestamp of the reservation request
     */
    public DefaultReservationRequest(String text, LocalDateTime timestemp) {
        this.text = text;
        this.timestamp = timestemp;
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("DefaultReservationRequest[")
			.append("text='").append(this.getText())
			.append("', timestamp=").append(this.getTimestamp())
			.append("]");
		return sb.toString();
	}
} 