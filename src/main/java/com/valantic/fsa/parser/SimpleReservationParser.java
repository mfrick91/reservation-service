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

public class SimpleReservationParser implements ReservationParser {
	
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
        
        LocalDate date = this.extractDate(text, searchText);
        
        LocalTime time = this.extractTime(searchText);

        int peopleCount = this.extractPeopleCount(searchText);
        
        return new DefaultReservationData(name, date, time, peopleCount);
    }

    private String extractName(String orginal, String searchText) {
    	String[] closings = new String[] { "gruß", "gruss", "grüßen", "grüssen", "grueßen", "gruessen", "dank", "danke" };
    	
        Pattern namePattern = Pattern.compile("(" + String.join("|", closings) + ").*?([a-zäüöß]+\\s[a-zäüöß]+)");
        Matcher matcher = namePattern.matcher(searchText);
        if (matcher.find()) {
        	return orginal.substring(matcher.start(2), matcher.end(2));
        }
        return "Unbekannt";
    }

    private LocalDate extractDate(String orginal, String searchText) {
    	String[] months = new String[] { "januar", "februar", "märz", "maerz", "april", "mai", 
    			"juni", "juli", "august", "september", "oktober", "november", "dezember" };
    	
    	// TODO: what about stuff like "morgen", "übermorgen" "nächste Woche" "übernächste Woche" "in x tagen", "kommenden Dienstag"
    	
        Pattern datePattern = Pattern.compile("(\\d{1,2}[.]\\d{1,2})|(\\d{1,2}[.]?\\s?(" + String.join("|", months) + ")+)");
        Matcher matcher = datePattern.matcher(searchText);
        if (matcher.find()) {
        	String rawDate = orginal.substring(matcher.start(), matcher.end());
            return LocalDate.parse(rawDate, DATE_FORMAT);
        }
        return null;
    }

    private LocalTime extractTime(String searchText) {
    	String morning = "morgens";
    	String evening = "abends";
    	
        Pattern timePattern = Pattern.compile("(\\d{1,2}[:\\.]\\d{2})|(\\d{1,2}\\s*uhr(\\s*(" + morning + "|" + evening + "))?)");
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

    private int extractPeopleCount(String searchText) {
		String[] peopleIdentifiers = new String[] { 
				"person", "personen", "leute", "leuten", "freund", "freunde", "freunden", "kind", "kinder",
				"herr", "herren", "mann", "männer", "maenner", "junge", "jungen",
				"dame", "damen", "frau", "frauen", 
				"gäste", "gaeste", "gästen", "gaesten" };
		String peoplePatternStr = "\\s*(" + String.join("|", peopleIdentifiers) + ")";

		int peopleCount = -1;
		
        // patterns for simple numbers
        Pattern numberPattern = Pattern.compile("(\\d+|[a-zäüöß]+)" + peoplePatternStr);
        Matcher matcher = numberPattern.matcher(searchText);
        while(matcher.find()) {
        	try {
            	peopleCount = ParserUtils.parseToInteger(matcher.group(1));
			} catch (Exception e) {
			}
        }
		
        // pattern for "zwischen x und y"
        Pattern betweenPattern = Pattern.compile("zwischen\\s+(\\d+|[a-zäüöß]+)\\s+und\\s+(\\d+|[a-zäüöß]+)" + peoplePatternStr);
        matcher = betweenPattern.matcher(searchText);
        while(matcher.find()) {
        	try {
	            int min = ParserUtils.parseToInteger(matcher.group(1));
	            int max = ParserUtils.parseToInteger(matcher.group(2));
	            peopleCount = Math.max(peopleCount, Math.max(min, max)); // return the larger value to ensure enough space
			} catch (Exception e) {
			}
        }

        // pattern for "mindestens x"
        Pattern atLeastPattern = Pattern.compile("mindestens\\s+(\\d+|[a-zäüöß]+)" + peoplePatternStr);
        matcher = atLeastPattern.matcher(searchText);
        while(matcher.find()) {
			try {
				peopleCount = Math.max(peopleCount, ParserUtils.parseToInteger(matcher.group(1)));
			} catch (Exception e) {
			}
        }

        // pattern for "bis zu x"
        Pattern upToPattern = Pattern.compile("bis\\s+zu\\s+(\\d+|[a-zäüöß]+)" + peoplePatternStr);
        matcher = upToPattern.matcher(searchText);
        while(matcher.find()) {
			try {
				peopleCount = Math.max(peopleCount, ParserUtils.parseToInteger(matcher.group(1)));
			} catch (Exception e) {
			}
        }

        // pattern for "nicht mehr als x"
        Pattern notMoreThanPattern = Pattern.compile("nicht\\s+mehr\\s+als\\s+(\\d+|[a-zäüöß]+)" + peoplePatternStr);
        matcher = notMoreThanPattern.matcher(searchText);
        while(matcher.find()) {
			try {
				peopleCount = Math.max(peopleCount, ParserUtils.parseToInteger(matcher.group(1)));
			} catch (Exception e) {
			}
        }

        return peopleCount;
    }

}
