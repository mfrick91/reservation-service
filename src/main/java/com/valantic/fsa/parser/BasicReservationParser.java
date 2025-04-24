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

public class BasicReservationParser implements ReservationParser {
	
	private static final DateTimeFormatter DATE_FORMAT = new DateTimeFormatterBuilder()
			.appendPattern("[d[.]M[.]][d[.] MMMM]")
			.parseDefaulting(ChronoField.YEAR, LocalDate.now().getYear())
			.toFormatter()
			.withLocale(Locale.GERMAN);
    
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

    protected String extractName(String text) {
    	// match closing pattern
    	String[] closings = new String[] { 
   			 "grüßen", "grüssen", "grueßen", "gruessen", "grüße", "grueße", "grüsse", "gruesse", "gruß", "gruss", "dank", "danke" };
        Matcher matcher = Pattern.compile("(" + String.join("|", closings) + ").*?([a-zäüöß]+\\s[a-zäüöß]+)")
        		.matcher(text.toLowerCase());
        if (matcher.find()) {
        	return text.substring(matcher.start(2), matcher.end(2));
        }
        return "";
    }
    
    protected LocalDate extractDate(String normalizedText, LocalDate timestamp) {
    	// match date pattern, e.g., 12.04
    	Matcher dateMatcher = Pattern.compile("(\\d{1,2}[.]\\d{1,2})[.]").matcher(normalizedText);
        if (dateMatcher.find()) {
            return LocalDate.parse(dateMatcher.group(), DATE_FORMAT);
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
    	
        return timestamp;
    }
    
    private LocalDate parseRelativeDate(String text, LocalDate timestemp) {
    	// match in x pattern
    	Matcher inPattern = Pattern.compile("in\\s+(\\d+)\\s+(tagen|wochen|monaten|jahren)").matcher(text);
    	LocalDate date = timestemp;
        while (inPattern.find()) {
            try {
                int valueToAdd = Integer.parseInt(inPattern.group(1));
                switch (inPattern.group(2)) {
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
		Matcher nextMatcher = Pattern.compile("(naechste|naechsten|naechstes|kommende|kommenden|kommendes)\\s+("
				+ "tage|woche|monat|jahr)").matcher(text);
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
    
	private LocalDate parseWeekday(String text, LocalDate timestamp) {
		Matcher weekdayMatcher = Pattern.compile("(|naechste|naechsten|naechstes|kommende|kommenden|kommendes)\\s+("
				+ String.join("|", ParserUtils.weekdays()) + ")").matcher(text);
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
    
    protected LocalTime extractTime(String normalizedText) {
    	// match time range pattern
		LocalTime time = this.parseTimeRange(normalizedText);
		if (time == null) {
			// match time pattern
			Matcher timeMatcher = Pattern.compile("(\\d{1,2}:\\d{2})|(\\d{1,2})\\s*uhr").matcher(normalizedText);
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
        
        return LocalTime.of(0, 0, 0, 0);
    }
    
	private LocalTime parseTimeRange(String text) {
		Matcher rangeMatcher = Pattern
				.compile("zwischen\\s+(\\d{1,2}:\\d{2}|\\d{1,2})\\s*(und|bis|-)\\s*(\\d{1,2}:\\d{2}|\\d{1,2})\\s*uhr")
				.matcher(text);
		if (rangeMatcher.find()) {
			LocalTime time1 = LocalTime.parse(rangeMatcher.group(1), TIME_FORMAT);
			LocalTime time2 = LocalTime.parse(rangeMatcher.group(3), TIME_FORMAT);
			return (time1.compareTo(time2) == -1) ? time1 : time2;
		}
		return null;
	}
    
    private boolean isMorningTime(String normalizedText) {
    	boolean isMorning = false;
    	String[] morningMarkers = new String []{ "morgens", "fruehstueck" };
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
    
    private boolean isEveningTime(String normalizedText) {
    	boolean isEvening = false;
    	String[] eveningMarkers = new String []{ "abends", "abendessen" };
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

    protected int extractNumberOfPeople(String normalizedText) {
       String[] peopleMarkers = new String[] { 
                "person", "personen", "leute", "leuten", "freund", "freunde", "freunden", "kind", "kinder",
                "herr", "herren", "mann", "maenner", "junge", "jungen",
                "dame", "damen", "frau", "frauen", "maedchen", 
                "gaeste", "gaesten" };
		Matcher peopleMatcher = Pattern.compile("(\\d+)\\s+(" + String.join("|", peopleMarkers) + ")").matcher(normalizedText);
		int numberOfPeople = 0;
		while (peopleMatcher.find()) {
			numberOfPeople = Math.max(numberOfPeople, Integer.parseInt(peopleMatcher.group(1)));
		}
		if (numberOfPeople > 0) {
			return numberOfPeople;
		}

		numberOfPeople = this.parsePeopleRange(normalizedText, peopleMarkers);
		if (numberOfPeople > 0) {
			return numberOfPeople;
		}
		
		numberOfPeople = this.parseQuantities(normalizedText, peopleMarkers);
		if (numberOfPeople > 0) {
			return numberOfPeople;
		}
		
        return numberOfPeople;
    }
    
    private int parsePeopleRange(String text, String[] markers) {
		Matcher rangeMatcher = Pattern.compile("(zwischen|mit)\\s+(\\d+)\\s*(und|bis|-)\\s*(\\d+)\\s+(" 
				+ String.join("|", markers) + ")").matcher(text);
		int numberOfPeople = 0;
		while (rangeMatcher.find()) {
        	try {
        		boolean isTimePattern = text.substring(rangeMatcher.end(4)).trim().startsWith("uhr");
        		if (!isTimePattern) {
        			Integer amount1 = Integer.parseInt(rangeMatcher.group(2));
					Integer amount2 = Integer.parseInt(rangeMatcher.group(4));
					numberOfPeople = Math.max(numberOfPeople, Math.max(amount1, amount2));
        		}
			} catch (Exception e) {
				// fail gracefully
			}
        }
		return numberOfPeople;
    }
    
    private int parseQuantities(String text, String[] markers) {
    	Matcher quantitiesMatcher = Pattern.compile("(zu|sind|fuer|mindestens|bis\\s*zu|nicht\\s*mehr\\s*als)\\s+(\\d+)(\\s+" 
        		+ String.join("|", markers) + ")?").matcher(text);
		int numberOfPeople = 0;
        while(quantitiesMatcher.find()) {
        	try {
        		boolean isTimePattern = text.substring(quantitiesMatcher.end(2)).trim().startsWith("uhr");
				if (!isTimePattern) {
	        		numberOfPeople = Math.max(numberOfPeople, Integer.parseInt(quantitiesMatcher.group(2)));
				}
			} catch (Exception e) {
				// fail gracefully
			}
        }
        return numberOfPeople;
    }

}
