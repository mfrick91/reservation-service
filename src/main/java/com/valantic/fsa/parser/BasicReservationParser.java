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
    
    private static final String[] MONTH_IDENTIFIERS = new String[] { 
            "januar", "februar", "märz", "maerz", "april", "mai", "juni", 
            "juli", "august", "september", "oktober", "november", "dezember",

            "jan", "feb", "mär", "maer", "apr", "mai", "jun", 
            "jul", "aug", "sep", "okt", "nov", "dez" };

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
    	String searchText = text.toLowerCase(Locale.GERMAN);
    	
        String name = this.extractName(text, searchText);
        
        LocalDate date = this.extractDate(request, text, searchText);
        
        LocalTime time = this.extractTime(searchText);

        Integer peopleCount = this.extractPeopleCount(searchText);
        
        return new DefaultReservationData(name, date, time, peopleCount);
    }

    private String extractName(String orginal, String searchText) {
        Pattern namePattern = Pattern.compile("(" + String.join("|", CLOSINGS) + ").*?([a-zäüöß]+\\s[a-zäüöß]+)");
        Matcher matcher = namePattern.matcher(searchText);
        if (matcher.find()) {
        	return orginal.substring(matcher.start(2), matcher.end(2));
        }
        return null;
    }

    private LocalDate extractDate(ReservationRequest request, String orginal, String searchText) {
        // default match
        Pattern datePattern = Pattern.compile("(\\d{1,2}[.]\\d{1,2})|(\\d{1,2}[.]?\\s?(" + String.join("|", MONTH_IDENTIFIERS) + ")+)");
        Matcher matcher = datePattern.matcher(searchText);
        if (matcher.find()) {
            String rawDate = orginal.substring(matcher.start(), matcher.end());
            return LocalDate.parse(rawDate, DATE_FORMAT);
        }
        
        // TODO: handle stuff like "in 3 wochen"
        
        // match relative dates
        LocalDateTime timestamp = request.getTimestamp();
        LocalDate today = timestamp.toLocalDate();
        
        // remove special characters
    	searchText = searchText.replace("ä", "ae")
    			.replace("ü", "ue").replace("ö", "oe").replace("ß", "ss"); 
    	
    	// special cases
    	if (searchText.contains("uebermorgen")) {
    		return today.plusDays(2);
    	} 
    	if (searchText.contains("morgen")) {
    		return today.plusDays(1);
    	}
    	
        // determine offset
        Map<String, Long> identifiersToOffset = new LinkedHashMap<>();
        identifiersToOffset.put("naechste", 1l);
        identifiersToOffset.put("uebernaechste", 2l);
        identifiersToOffset.put("kommende", 1l);
        long offset = 0;
        for (Entry<String, Long> identifierToOffset : identifiersToOffset.entrySet()) {
			if (searchText.contains(identifierToOffset.getKey())) {
				offset = identifierToOffset.getValue();
			}
		}
        
        // match "woche"
        if (searchText.contains("woche")) {
        	return today.plusWeeks(offset);
        }
        // match "monat"
        if (searchText.contains("monat")) {
        	return today.plusMonths(offset);
        }
        // match "jahr"
        if (searchText.contains("jahr")) {
        	return today.plusYears(offset);
        }
        
		// match weekdays
		int dayIndex = 1;
		for (String weekday : WEEKDAYS) {
			if (searchText.contains(" " + weekday + " ")) {
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

    private LocalTime extractTime(String searchText) {
    	String morning = "morgens";
    	String evening = "abends";
    	
        Pattern timePattern = Pattern.compile("(\\d{1,2}:\\d{2})|(\\d{1,2}\\s*uhr(\\s*(" + morning + "|" + evening + "))?)");
        Matcher matcher = timePattern.matcher(searchText);
        if (matcher.find()) {
            String rawTime = matcher.group();

            boolean isEvening = rawTime.contains(evening);
            rawTime = rawTime.replace(morning, "").replace(evening, "").trim();
            rawTime = rawTime.replace(" uhr", "").replace(".", ":").trim();

            LocalTime parsed = LocalTime.parse(rawTime, TIME_FORMAT);
            if (isEvening && parsed.getHour() < 12) {
                return parsed.plusHours(12);
            }
            return parsed;
        }
        return null;
    }

    private Integer extractPeopleCount(String searchText) {
    	// remove special characters
    	searchText = searchText.replace("ä", "ae").replace("ü", "ue")
    			.replace("ö", "oe").replace("ß", "ss"); 
		
        // default match
    	String peoplePatternStr = "\\s*(" + String.join("|", PEOPLE_IDENTIFIERS) + ")";
        Pattern numberPattern = Pattern.compile("(\\d+|[a-zäüöß]+)" + peoplePatternStr);
        Matcher matcher = numberPattern.matcher(searchText);
        Integer peopleCount = null;
        while(matcher.find()) {
        	try {
            	peopleCount = ParserUtils.parseToInteger(matcher.group(1));
			} catch (Exception e) {
			}
        }
		
        // match "zwischen x und y"
        Pattern betweenPattern = Pattern.compile("zwischen\\s+(\\d+|[a-z]+)\\s+und\\s+(\\d+|[a-z]+)" + peoplePatternStr);
        matcher = betweenPattern.matcher(searchText);
        while(matcher.find()) {
        	try {
	            int number1 = ParserUtils.parseToInteger(matcher.group(1));
	            int number2 = ParserUtils.parseToInteger(matcher.group(2));
	            int maximum = Math.max(number1, number2); // return the larger value to ensure enough space
				if (peopleCount == null) {
					peopleCount = maximum;
				} else {
					peopleCount = Math.max(peopleCount, maximum);
				}
			} catch (Exception e) {
			}
        }

        // match "mindestens x"
        Pattern atLeastPattern = Pattern.compile("mindestens\\s+(\\d+|[a-z]+)" + peoplePatternStr);
        matcher = atLeastPattern.matcher(searchText);
        while(matcher.find()) {
			try {
				int number = ParserUtils.parseToInteger(matcher.group(1));
				if (peopleCount == null) {
					peopleCount = number;
				} else {
					peopleCount = Math.max(peopleCount, number);
				}
			} catch (Exception e) {
			}
        }

        // match "bis zu x"
        Pattern upToPattern = Pattern.compile("bis\\s+zu\\s+(\\d+|[a-z]+)" + peoplePatternStr);
        matcher = upToPattern.matcher(searchText);
        while(matcher.find()) {
			try {
				int number = ParserUtils.parseToInteger(matcher.group(1));
				if (peopleCount == null) {
					peopleCount = number;
				} else {
					peopleCount = Math.max(peopleCount, number);
				}
			} catch (Exception e) {
			}
        }

        // match "nicht mehr als x"
        Pattern notMoreThanPattern = Pattern.compile("nicht\\s+mehr\\s+als\\s+(\\d+|[a-z]+)" + peoplePatternStr);
        matcher = notMoreThanPattern.matcher(searchText);
        while(matcher.find()) {
			try {
				int number = ParserUtils.parseToInteger(matcher.group(1));
				if (peopleCount == null) {
					peopleCount = number;
				} else {
					peopleCount = Math.max(peopleCount, number);
				}
			} catch (Exception e) {
			}
        }

        return peopleCount;
    }

}
