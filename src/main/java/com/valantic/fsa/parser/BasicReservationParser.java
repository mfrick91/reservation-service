package com.valantic.fsa.parser;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.valantic.fsa.model.DefaultReservationData;
import com.valantic.fsa.model.ReservationData;
import com.valantic.fsa.model.ReservationRequest;

public class BasicReservationParser implements ReservationParser {
	
	private static final String[] CLOSINGS = new String[] { 
			"gruß", "gruss", "grüßen", "grüssen", "grueßen", "gruessen", "dank", "danke" };
    
    private static final String[] WEEKDAYS = new String[] { 
            "montag", "dienstag", "mittwoch", "donnerstag", "freitag", "samstag", "sonntag",
            "mo", "di", "mi", "do", "fr", "sa", "so" };
    
    private static final String[] PEOPLE_IDENTIFIERS = new String[] { 
            "person", "personen", "leute", "leuten", "freund", "freunde", "freunden", "kind", "kinder",
            "herr", "herren", "mann", "maenner", "junge", "jungen",
            "dame", "damen", "frau", "frauen", 
            "gaeste", "gaesten" };
	
	private static final DateTimeFormatter DATE_FORMAT = new DateTimeFormatterBuilder()
			.appendPattern("[d[.]M[.]][d[.] MMMM]").parseDefaulting(ChronoField.YEAR, LocalDate.now().getYear())
			.toFormatter().withLocale(Locale.GERMAN);
    
    private static final DateTimeFormatter TIME_FORMAT = new DateTimeFormatterBuilder()
    		.appendPattern("H[:][mm]").parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0).toFormatter();

    @Override
    public ReservationData parse(ReservationRequest request) {
        String text = request.getText();
        
    	text = text.trim();
        String name = this.extractName(text);

    	String normalizedText = ParserUtils.normalizeText(text);
        
        LocalDate date = this.extractDate(request, normalizedText);
        
        LocalTime time = this.extractTime(normalizedText);

        Integer peopleCount = this.extractPeopleCount(normalizedText);
        
        return new DefaultReservationData(name, date, time, peopleCount);
    }

    protected String extractName(String text) {
        Pattern namePattern = Pattern.compile("(" + String.join("|", CLOSINGS) + ").*?([a-zäüöß]+\\s[a-zäüöß]+)");
        Matcher matcher = namePattern.matcher(text.toLowerCase());
        if (matcher.find()) {
        	return text.substring(matcher.start(2), matcher.end(2));
        }
        return null;
    }

    protected LocalDate extractDate(ReservationRequest request, String normalizedText) {
        Pattern datePattern = Pattern.compile("(\\d{1,2}[.]\\d{1,2})[.]");
        Matcher matcher = datePattern.matcher(normalizedText);
        if (matcher.find()) {
            return LocalDate.parse(matcher.group(), DATE_FORMAT);
        }
        
        // match relative dates
        LocalDateTime timestamp = request.getTimestamp();
        LocalDate today = timestamp.toLocalDate();
        
    	// special cases
    	if (normalizedText.contains("uebermorgen")) {
    		return today.plusDays(2);
    	} 
    	if (normalizedText.contains("morgen")) {
    		return today.plusDays(1);
    	}
    	
        // match "in X tagen/wochen/monaten/jahren"
    	Pattern durationPattern = Pattern.compile("\\s+(\\d+|[a-z]+)\\s+(tagen|wochen|monaten|jahren)");
        matcher = durationPattern.matcher(normalizedText);
    	LocalDate targetDate = today;
        while (matcher.find()) {
            try {
                int durationTime = ParserUtils.parseToInteger(matcher.group(1));
                String duration = matcher.group(2);
                if (duration.startsWith("tagen")) {
                	targetDate = targetDate.plusDays(durationTime);
                } else if (duration.startsWith("wochen")) {
                	targetDate = targetDate.plusWeeks(durationTime);
                } else if (duration.startsWith("monaten")) {
                	targetDate = targetDate.plusMonths(durationTime);
                } else if (duration.startsWith("jahren")) {
                	targetDate = targetDate.plusYears(durationTime);
                }
            } catch (Exception e) {
                // fail gracefully
            }
        }
    	if (targetDate != today) {
    		return targetDate;
    	}
    	
        // determine offset
        Map<String, Long> qualifiersToOffset = new LinkedHashMap<>();
        qualifiersToOffset.put("naechste", 1l);
        qualifiersToOffset.put("uebernaechste", 2l);
        qualifiersToOffset.put("kommende", 1l);
        long offset = 0;
        for (Entry<String, Long> qualifierToOffset : qualifiersToOffset.entrySet()) {
			String qualifier = qualifierToOffset.getKey();
			if (normalizedText.contains(qualifier)) {
				offset = qualifierToOffset.getValue();
				
		        // match "woche"
		        Pattern weekPattern = Pattern.compile(qualifier +"(n|s)?\\s+woche");
		        matcher = weekPattern.matcher(normalizedText);
		        if (matcher.find()) {
		        	return today.plusWeeks(offset);
		        }
		        // match "monat"
		        Pattern	monthPattern = Pattern.compile(qualifier +"(n|s)?\\s+monat");
		        matcher = monthPattern.matcher(normalizedText);
		        if (matcher.find()) {
		        	return today.plusMonths(offset);
		        }
		        // match "jahr"
		        Pattern	yearPattern = Pattern.compile(qualifier +"(n|s)?\\s+jahr");
		        matcher = yearPattern.matcher(normalizedText);
		        if (matcher.find()) {
		        	return today.plusYears(offset);
		        }
			}
		}
        
		// match weekdays
		int dayIndex = 1;
		for (String weekday : WEEKDAYS) {
			if (normalizedText.contains(" " + weekday + " ")) {
				int currentDayIndex = today.getDayOfWeek().getValue();
				int daysToAdd = (dayIndex - currentDayIndex + 7) % 7;
				// go to next week
				if (daysToAdd == 0) {
					daysToAdd = 7;
				}
				// go to week after next
				if (offset > 1) {
					daysToAdd += 7;
				}
				return today.plusDays(daysToAdd);
			}
			dayIndex++;
		}
        
        return null;
    }

    protected LocalTime extractTime(String normalizedText) {
        Pattern timePattern = Pattern.compile("(\\d{1,2}:\\d{2})|(\\d{1,2}\\s*uhr)");
        Matcher matcher = timePattern.matcher(normalizedText);
        if (matcher.find()) {
            String rawTime = matcher.group();
            rawTime = rawTime.replace(" uhr", "").replace(".", ":").trim();
            LocalTime parsed = LocalTime.parse(rawTime, TIME_FORMAT);
            
            if (this.isMorningTime(normalizedText) && parsed.getHour() > 12) {
                return parsed.minusHours(12);
            }

            if (this.isEveningTime(normalizedText) && parsed.getHour() < 12) {
                return parsed.plusHours(12);
            }
            return parsed;
        }
        return null;
    }
    
    private boolean isMorningTime(String normalizedText) {
    	boolean isMorning = false;
    	for (String morningMarker : new String []{ "morgens", "fruehstueck" }) {
			isMorning = normalizedText.contains(morningMarker);
			if (isMorning) {
				return isMorning;
			}
		}
		for (String weekday : WEEKDAYS) {
			isMorning = normalizedText.contains(weekday + "morgen");
			if (isMorning) {
				return isMorning;
			}
		}
		return isMorning;
    }
    
    private boolean isEveningTime(String normalizedText) {
    	boolean isEvening = false;
    	for (String eveningMarker : new String []{ "abends", "abendessen" }) {
			isEvening = normalizedText.contains(eveningMarker);
			if (isEvening) {
				return isEvening;
			}
		}
		for (String weekday : WEEKDAYS) {
			isEvening = normalizedText.contains(weekday + "abend");
			if (isEvening) {
				return isEvening;
			}
		}
		return isEvening;
    }

    protected Integer extractPeopleCount(String normalizedText) {
    	String peoplePatternStr = "\\s*(" + String.join("|", PEOPLE_IDENTIFIERS) + ")";
		String[] patternStrings = new String[] {
				// default pattern
				"(\\d+)" + peoplePatternStr, 
				// "zu x" pattern
				"zu\\s*(\\d+)",
				// "mindestens x" pattern
				"mindestens\\s+(\\d+)" + peoplePatternStr, 
				// "bis zu x" pattern
				"bis\\s+zu\\s+(\\d+)" + peoplePatternStr, 
				// "nicht mehr als x" pattern
				"nicht\\s+mehr\\s+als\\s+(\\d+)" + peoplePatternStr, 
		};

		Integer peopleCount = null;
		for (String patternStr : patternStrings) {
	        Pattern pattern = Pattern.compile(patternStr);
	        Matcher matcher = pattern.matcher(normalizedText);
	        while(matcher.find()) {
	        	try {
	        		int value = Integer.valueOf(matcher.group(1));
	        		if (peopleCount == null) {
	        			peopleCount = value;
	        		} else {
		            	peopleCount = Math.max(peopleCount, value);
	        		}
				} catch (Exception e) {
	                // fail gracefully
				}
	        }
		}
    	
        // match "zwischen x und y"
        Pattern betweenPattern = Pattern.compile("zwischen\\s+(\\d+)\\s+und\\s+(\\d+)|(\\d+)\\s*-\\s*(\\d+)" + peoplePatternStr);
        Matcher matcher = betweenPattern.matcher(normalizedText);
        while(matcher.find()) {
        	try {
				// return the larger value to ensure enough space
				int maximum = Math.max(Integer.valueOf(matcher.group(1)), Integer.valueOf(matcher.group(2)));
				if (peopleCount == null) {
					peopleCount = maximum;
				} else {
					peopleCount = Math.max(peopleCount, maximum);
				}
			} catch (Exception e) {
                // fail gracefully
			}
        }

        return peopleCount;
    }

}
