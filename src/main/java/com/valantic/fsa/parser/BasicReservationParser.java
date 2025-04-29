package com.valantic.fsa.parser;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.regex.Matcher;

import com.valantic.fsa.model.DefaultReservationData;
import com.valantic.fsa.model.ReservationData;
import com.valantic.fsa.model.ReservationRequest;
import com.valantic.fsa.util.Formatters;
import com.valantic.fsa.util.ParserUtils;
import com.valantic.fsa.util.Patterns;

/**
 * Basic implementation of the {@code ReservationParser} interface.
 *	
 * @author M. Frick
 */
public class BasicReservationParser implements ReservationParser {

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
        Matcher namerMatcher = Patterns.NAME.matcher(text.toLowerCase());
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
    	Matcher particalDateMatcher = Patterns.PARTIAL_DATE.matcher(normalizedText);
		if (particalDateMatcher.find()) {
			Matcher dateMatcher = Patterns.DATE.matcher(normalizedText);
			if (dateMatcher.find()) {
				return LocalDate.parse(dateMatcher.group(), Formatters.DATE);
			}
			return LocalDate.parse(particalDateMatcher.group(), Formatters.PARTIAL_DATE);
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
    	Matcher inMatcher = Patterns.RELATIVE_DATE_IN.matcher(text);
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
		Matcher nextMatcher = Patterns.RELATIVE_DATE_NEXT.matcher(text);
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
	 * @param text the text to parse the weekday from
	 * @param timestamp the timestamp of the reservation request
	 * @return the weekday
	 */
	private LocalDate parseWeekday(String text, LocalDate timestamp) {
		Matcher weekdayMatcher = Patterns.WEEKDAY.matcher(text);
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
			Matcher timeMatcher = Patterns.TIME.matcher(normalizedText);
			if (timeMatcher.find()) {
				time = LocalTime.parse(timeMatcher.group().replace("uhr", "").trim(), Formatters.TIME);
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
		Matcher rangeMatcher = Patterns.TIME_RANGE.matcher(text);
		if (rangeMatcher.find()) {
			LocalTime time1 = LocalTime.parse(rangeMatcher.group(1), Formatters.TIME);
			LocalTime time2 = LocalTime.parse(rangeMatcher.group(3), Formatters.TIME);
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
		Matcher simplePeopleCountMatcher = Patterns.SIMPLE_PEOPLE_COUNT.matcher(normalizedText);
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
    	Matcher peopleCountMatcher = Patterns.PEOPLE_COUNT.matcher(text);
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
		Matcher peopleRangeMatcher = Patterns.PEOPLE_RANGE.matcher(text);
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
