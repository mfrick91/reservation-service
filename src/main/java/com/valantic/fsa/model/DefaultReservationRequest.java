package com.valantic.fsa.model;

import java.time.LocalDateTime;

public class DefaultReservationRequest implements ReservationRequest {
    
    private final String text;
    private final LocalDateTime timestamp;
    
    public DefaultReservationRequest(String text) {
    	this(text, LocalDateTime.now());
    }

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
} 