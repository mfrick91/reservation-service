package com.valantic.fsa.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.Locale;

public class Formatters {

	/**
	 * The time formatter for the time.
	 */
	public static final DateTimeFormatter TIME = new DateTimeFormatterBuilder()
    		.appendPattern("H[:][mm]")
    		.parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
    		.toFormatter();
	
	/**
	 * The date formatter including the year.
	 */
	public static final DateTimeFormatter DATE = new DateTimeFormatterBuilder()
			.appendPattern("[d[.]M[.]uuuu][d[.]M[.]uu]")
			.parseDefaulting(ChronoField.YEAR, LocalDate.now().getYear())
			.toFormatter()
			.withLocale(Locale.GERMAN);
	
	/**
	 * The date formatter without the year.
	 */
	public static final DateTimeFormatter PARTIAL_DATE = new DateTimeFormatterBuilder()
			.appendPattern("[d[.]M[.]][d[.] MMMM]")
			.parseDefaulting(ChronoField.YEAR, LocalDate.now().getYear())
			.toFormatter()
			.withLocale(Locale.GERMAN);
}
