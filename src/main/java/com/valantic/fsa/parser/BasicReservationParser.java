package com.valantic.fsa.parser;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.valantic.fsa.model.DefaultReservationData;
import com.valantic.fsa.model.ReservationData;
import com.valantic.fsa.model.ReservationRequest;

/**
 * Basic implementation of the {@code ReservationParser} interface.
 *	
 * @author M. Frick
 */
public class BasicReservationParser implements ReservationParser {

	/**
	 * Pattern to match names in the text.
	 */
	private static final Pattern NAME_PATTERN = Pattern.compile(
			"(grüßen|grüssen|grueßen|gruessen|grüße|grueße|grüsse|gruesse|gruß|gruss|dank|danke).*?([a-zäüöß]+\\s[a-zäüöß]+)");

	/**
	 * Pattern to match partial dates in the text.
	 */
	private static final Pattern PARTIAL_DATE_PATTERN = Pattern.compile(
			"(\\d{1,2}[.]\\d{1,2})(|[.])");
	
	/**
	 * Pattern to match full dates in the text.
	 */
	private static final Pattern DATE_PATTEN = Pattern.compile(
			"(\\d{1,2}[.]\\d{1,2}[.]\\d{2,4})");
	
	/**
	 * Pattern to match relative dates using "in X days/weeks/months/years".
	 */
	private static final Pattern IN_PATTERN = Pattern.compile(
			"in\\s+(\\d+)\\s+(tagen|wochen|monaten|jahren)");

	/**
	 * Pattern to match relative dates using "next" or "coming".
	 */
	private static final Pattern NEXT_PATTERN = Pattern.compile(
			"(naechste|naechsten|naechstes|kommende|kommenden|kommendes)\\s+(tage|woche|monat|jahr)");

	/**
	 * Pattern to match weekdays in the text with optional "next" or "coming" prefix.
	 */
	private static final Pattern WEEKDAY_PATTERN = Pattern.compile(
			"(|naechste|naechsten|naechstes|kommende|kommenden|kommendes)\\s+(" + String.join("|", ParserUtils.weekdays()) + ")");

	/**
	 * Pattern to match times in the text.
	 */
	private static final Pattern TIME_PATTERN = Pattern.compile(
			"(\\d{1,2}:\\d{2})|(\\d{1,2})\\s*uhr");

	/**
	 * Pattern to match time ranges in the text using "zwischen", "und", "bis", or "-".
	 */
	private static final Pattern TIME_RANGE_PATTERN = Pattern.compile(
			"zwischen\\s+(\\d{1,2}:\\d{2}|\\d{1,2})\\s*(und|bis|-)\\s*(\\d{1,2}:\\d{2}|\\d{1,2})\\s*uhr");
	
	/**
	 * Array of words that can indicate a number of people in a reservation.
	 */
	private static final String[] PEOPLE = new String[] { 
		"person", "personen", "leute", "leuten", "freund", "freunde", "freunden", "kind", "kinder",
		"herr", "herren", "mann", "maenner", "junge", "jungen",
		"dame", "damen", "frau", "frauen", "maedchen", 
		"gaeste", "gaesten" };

	/**
	 * Pattern to match simple people counts in the text.
	 */
	private static final Pattern SIMPLE_PEOPLE_COUNT_PATTERN = Pattern.compile(
			"(\\d+)\\s+(" + String.join("|", PEOPLE) + ")");

	/**
	 * Pattern to match people counts using various phrases.
	 */
	private static final Pattern PEOPLE_COUNT_PATTERN = Pattern.compile(
			"(zu|sind|fuer|mindestens|bis\\s*zu|nicht\\s*mehr\\s*als)\\s+(\\d+)(\\s+" + String.join("|", PEOPLE) + ")?");

	/**
	 * Pattern to match ranges of people in the text using "zwischen", "mit", "und", "bis", or "-".
	 */
	private static final Pattern PEOPLE_RANGE_PATTERN = Pattern.compile(
			"(zwischen|mit)\\s+(\\d+)\\s*(und|bis|-)\\s*(\\d+)\\s+(" + String.join("|", PEOPLE) + ")");

	/**
	 * The date formatter without the year.
	 */
	private static final DateTimeFormatter PARTIAL_DATE_FORMAT = new DateTimeFormatterBuilder()
			.appendPattern("[d[.]M[.]][d[.] MMMM][d[.]M[.]uuuu]")
			.parseDefaulting(ChronoField.YEAR, LocalDate.now().getYear())
			.toFormatter()
			.withLocale(Locale.GERMAN);
	
	/**
	 * The date formatter including the year.
	 */
	private static final DateTimeFormatter DATE_FORMAT = new DateTimeFormatterBuilder()
			.appendPattern("[d[.]M[.]uuuu][d[.]M[.]uu]")
			.toFormatter()
			.withLocale(Locale.GERMAN);

	/**
	 * The time formatter for the time.
	 */
	private static final DateTimeFormatter TIME_FORMAT = new DateTimeFormatterBuilder()
    		.appendPattern("H[:][mm]")
    		.parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
    		.toFormatter();
	
    @Override
    public ReservationData parse(ReservationRequest request) {
        String text = request.getText();
        
    	text = text.trim();
        String name = this.extractName(text);

    	String normalizedText = ParserUtils.normalizeText(text);
        
        LocalDate date = this.extractDate(normalizedText, request.getTimestamp().toLocalDate());
        
        LocalTime time = this.extractTime(normalizedText);

        int peopleCount = this.extractNumberOfPeople(normalizedText);
        
        return new DefaultReservationData(name, date, time, peopleCount);
    }

    /**
     * Extracts the name of the person making the reservation from the text.
     * 
     * @param text the text to extract the name from
     * @return the name of the person making the reservation
     */
    protected String extractName(String text) {
    	// match name pattern
        Matcher namerMatcher = NAME_PATTERN.matcher(text.toLowerCase());
		if (namerMatcher.find()) {
			return text.substring(namerMatcher.start(2), namerMatcher.end(2));
		}
		return null;
	}

    /**
     * Extracts the date of the reservation from the text.
     * 
     * @param normalizedText the normalized text to extract the date from
     * @param timestamp the timestamp of the reservation request
     * @return the date of the reservation
     */
    protected LocalDate extractDate(String normalizedText, LocalDate timestamp) {
    	// match date pattern, e.g., 12.04, 12 May 2024
    	Matcher particalDateMatcher = PARTIAL_DATE_PATTERN.matcher(normalizedText);
		if (particalDateMatcher.find()) {
			Matcher dateMatcher = DATE_PATTEN.matcher(normalizedText);
			if (dateMatcher.find()) {
				return LocalDate.parse(dateMatcher.group(), DATE_FORMAT);
			}
			return LocalDate.parse(particalDateMatcher.group(), PARTIAL_DATE_FORMAT);
		}

		// special cases
		if (normalizedText.contains("morgen")) {
			if (normalizedText.contains("uebermorgen")) {
				return timestamp.plusDays(2);
			}
			return timestamp.plusDays(1);
		}

		LocalDate date = this.parseRelativeDate(normalizedText, timestamp);
		if (date != null) {
			return date;
		}

		date = this.parseWeekday(normalizedText, timestamp);
		if (date != null) {
			return date;
		}

		return null;
	}

    /**
     * Parses a relative date from the text.
     * 
     * @param text the text to parse the relative date from
     * @param timestemp the timestamp of the reservation request
     * @return the relative date
     */
    private LocalDate parseRelativeDate(String text, LocalDate timestemp) {
    	// match in x pattern
    	Matcher inMatcher = IN_PATTERN.matcher(text);
		LocalDate date = timestemp;
		while (inMatcher.find()) {
			try {
				int valueToAdd = Integer.parseInt(inMatcher.group(1));
				switch (inMatcher.group(2)) {
					case "tagen":
						date = date.plusDays(valueToAdd);
						break;
					case "wochen":
						date = date.plusWeeks(valueToAdd);
						break;
					case "monaten":
						date = date.plusMonths(valueToAdd);
						break;
					case "jahren":
						date = date.plusYears(valueToAdd);
						break;
				}
			} catch (Exception e) {
				// fail gracefully
			}
		}
		if (date != timestemp) {
			return date;
		}

    	// match next day/week/month/year pattern
		Matcher nextMatcher = NEXT_PATTERN.matcher(text);
		if (nextMatcher.find()) {
			int amount = 1;
			if (text.substring(nextMatcher.start() - 5).startsWith("ueber")) {
				amount = 2;
			}
			switch (nextMatcher.group(2)) {
			case "tage":
				return timestemp.plusDays(amount);
			case "woche":
				return timestemp.plusWeeks(amount);
			case "monat":
				return timestemp.plusMonths(amount);
			case "jahr":
				return timestemp.plusYears(amount);
			}
		}

		return null;
	}

	/**
	 * Parses a weekday from the text.
	 * 
	 * @param text      the text to parse the weekday from
	 * @param timestamp the timestamp of the reservation request
	 * @return the weekday
	 */
	private LocalDate parseWeekday(String text, LocalDate timestamp) {
		Matcher weekdayMatcher = WEEKDAY_PATTERN.matcher(text);
		if (weekdayMatcher.find()) {
			int weekday = ParserUtils.weekdayToInteger(weekdayMatcher.group(2));
			int currentDay = timestamp.getDayOfWeek().getValue();
			int daysToAdd = (weekday - currentDay + 7) % 7;
			if (daysToAdd == 0) {
				daysToAdd = 7; // go to next week
			}
			if (text.substring(weekdayMatcher.start() - 5).startsWith("ueber")) {
				daysToAdd += 7; // go to week after next
			}
			return timestamp.plusDays(daysToAdd);
		}
		return null;
	}

	/**
	 * Extracts the time of the reservation from the text.
	 * 
	 * @param normalizedText the normalized text to extract the time from
	 * @return the time of the reservation
	 */
	protected LocalTime extractTime(String normalizedText) {
		// match time range pattern
		LocalTime time = this.parseTimeRange(normalizedText);
		if (time == null) {
			// match time pattern
			Matcher timeMatcher = TIME_PATTERN.matcher(normalizedText);
			if (timeMatcher.find()) {
				time = LocalTime.parse(timeMatcher.group().replace("uhr", "").trim(), TIME_FORMAT);
			}
		}

		if (time != null) {
			// apply hourly offsets
			if (this.isMorningTime(normalizedText) && time.getHour() > 12) {
				return time.minusHours(12);
			}

			if (this.isEveningTime(normalizedText) && time.getHour() < 12) {
				return time.plusHours(12);
			}

			return time;
		}

		return null;
	}

	/**
	 * Parses a time range from the text.
	 * 
	 * @param text the text to parse the time range from
	 * @return the time range
	 */
	private LocalTime parseTimeRange(String text) {
		Matcher rangeMatcher = TIME_RANGE_PATTERN.matcher(text);
		if (rangeMatcher.find()) {
			LocalTime time1 = LocalTime.parse(rangeMatcher.group(1), TIME_FORMAT);
			LocalTime time2 = LocalTime.parse(rangeMatcher.group(3), TIME_FORMAT);
			return (time1.compareTo(time2) == -1) ? time1 : time2;
		}
		return null;
	}

	/**
	 * Checks if the time is morning.
	 * 
	 * @param normalizedText the normalized text to check the time from
	 * @return true if the time is morning, false otherwise
	 */
	private boolean isMorningTime(String normalizedText) {
		boolean isMorning = false;
		String[] morningMarkers = new String[] { "morgens", "fruehstueck" };
		for (String morningMarker : morningMarkers) {
			isMorning = normalizedText.contains(morningMarker);
			if (isMorning) {
				return isMorning;
			}
		}
		for (String weekday : ParserUtils.weekdays()) {
			isMorning = normalizedText.contains(weekday + "morgen");
			if (isMorning) {
				return isMorning;
			}
		}
		return isMorning;
	}

	/**
	 * Checks if the time is evening.
	 * 
	 * @param normalizedText the normalized text to check the time from
	 * @return true if the time is evening, false otherwise
	 */
	private boolean isEveningTime(String normalizedText) {
		boolean isEvening = false;
		String[] eveningMarkers = new String[] { "abends", "abendessen" };
		for (String eveningMarker : eveningMarkers) {
			isEvening = normalizedText.contains(eveningMarker);
			if (isEvening) {
				return isEvening;
			}
		}
		for (String weekday : ParserUtils.weekdays()) {
			isEvening = normalizedText.contains(weekday + "abend");
			if (isEvening) {
				return isEvening;
			}
		}
		return isEvening;
	}

    /**
     * Extracts the number of people for the reservation from the text.
     * 
     * @param normalizedText the normalized text to extract the number of people from
     * @return the number of people for the reservation
     */
    protected int extractNumberOfPeople(String normalizedText) {
		Matcher simplePeopleCountMatcher = SIMPLE_PEOPLE_COUNT_PATTERN.matcher(normalizedText);
		int numberOfPeople = -1;
		while (simplePeopleCountMatcher.find()) {
			numberOfPeople = Math.max(numberOfPeople, Integer.parseInt(simplePeopleCountMatcher.group(1)));
		}
		if (numberOfPeople >= 0) {
			return numberOfPeople;
		}

		numberOfPeople = this.parsePeopleCount(normalizedText);
		if (numberOfPeople >= 0) {
			return numberOfPeople;
		}

		numberOfPeople = this.parsePeopleRange(normalizedText);
		if (numberOfPeople >= 0) {
			return numberOfPeople;
		}

		return -1;
    }

	/**
	 * Parses the number of people from the text.
	 * 
	 * @param text the text to parse the people count from
	 * @return the number of people for the reservation
	 */
    private int parsePeopleCount(String text) {
    	Matcher peopleCountMatcher = PEOPLE_COUNT_PATTERN.matcher(text);
		int numberOfPeople = -1;
		while (peopleCountMatcher.find()) {
			try {
				boolean isTimePattern = text.substring(peopleCountMatcher.end(2)).trim().startsWith("uhr");
				if (!isTimePattern) {
					numberOfPeople = Math.max(numberOfPeople, Integer.parseInt(peopleCountMatcher.group(2)));
				}
			} catch (Exception e) {
				// fail gracefully
			}
		}
		return numberOfPeople;
	}
    
    /**
     * Parses a people range from the text.
     * 
     * @param text the text to parse the people range from
     * @return the number of people for the reservation
     */
	private int parsePeopleRange(String text) {
		Matcher peopleRangeMatcher = PEOPLE_RANGE_PATTERN.matcher(text);
		int numberOfPeople = -1;
		while (peopleRangeMatcher.find()) {
			try {
				boolean isTimePattern = text.substring(peopleRangeMatcher.end(4)).trim().startsWith("uhr");
				if (!isTimePattern) {
					Integer amount1 = Integer.parseInt(peopleRangeMatcher.group(2));
					Integer amount2 = Integer.parseInt(peopleRangeMatcher.group(4));
					numberOfPeople = Math.max(numberOfPeople, Math.max(amount1, amount2));
				}
			} catch (Exception e) {
				// fail gracefully
			}
		}
		return numberOfPeople;
	}

}
