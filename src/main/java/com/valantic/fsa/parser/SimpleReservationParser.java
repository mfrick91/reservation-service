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

public class SimpleReservationParser implements ReservationParser {
	
	private static final DateTimeFormatter DATE_FORMAT = new DateTimeFormatterBuilder()
			.appendPattern("[d[.]M[.]][d[.] MMMM]").parseDefaulting(ChronoField.YEAR, LocalDate.now().getYear())
			.toFormatter().withLocale(Locale.GERMAN);
    
    private static final DateTimeFormatter TIME_FORMAT = new DateTimeFormatterBuilder()
    		.appendPattern("H[:][mm]").parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0).toFormatter();

    public ReservationData parse(String text) {
        String name = this.extractName(text);
        
        LocalDate date = this.extractDate(text);
        
        LocalTime time = this.extractTime(text);

        int peopleCount = this.extractPeopleCount(text);
        
        return new DefaultReservationData(name, date, time, peopleCount);
    }

    private String extractName(String text) {
        Pattern namePattern = Pattern.compile("(Gruß|Dank|Grüßen|Mit freundlichen Grüßen).*?([A-ZÄÖÜ][a-zäöüß]+\\s[A-ZÄÖÜ][a-zäöüß]+)");
        Matcher matcher = namePattern.matcher(text);
        if (matcher.find()) {
            return matcher.group(2);
        }
        return "Unbekannt";
    }

    private LocalDate extractDate(String text) {
        Pattern datePattern = Pattern.compile("(\\d{1,2}[.]\\d{1,2})|(\\d{1,2}[.]?\\s?[April]+)");
        Matcher matcher = datePattern.matcher(text);
        if (matcher.find()) {
            String rawDate = matcher.group();
            return LocalDate.parse(rawDate, DATE_FORMAT);
        }
        return null;
    }

    private LocalTime extractTime(String text) {
        Pattern timePattern = Pattern.compile("(\\d{1,2}[:\\.]\\d{2})|(\\d{1,2}\\s*Uhr(\\s*(abends|morgens))?)");
        Matcher matcher = timePattern.matcher(text);
        if (matcher.find()) {
            String rawTime = matcher.group().replace(" Uhr", "").replace(".", ":").trim();

            boolean isAbends = rawTime.toLowerCase().contains("abends");
            rawTime = rawTime.replace("abends", "").replace("morgens", "").trim();

            LocalTime parsed = LocalTime.parse(rawTime, TIME_FORMAT);
            if (isAbends && parsed.getHour() < 12) {
                return parsed.plusHours(12);
            }
            return parsed;
        }
        return null;
    }

    private int extractPeopleCount(String text) {
        Pattern numberPattern = Pattern.compile("(\\d+)\\s*(Personen|Leuten|Mann|Gäste)");
        Matcher matcher = numberPattern.matcher(text);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        }

        return this.extractPeopleFromWords(text.toLowerCase());
    }

    private int extractPeopleFromWords(String text) {
        if (text.contains("eins")) return 1;
        if (text.contains("zwei")) return 2;
        if (text.contains("drei")) return 3;
        if (text.contains("vier")) return 4;
        if (text.contains("fünf")) return 5;
        if (text.contains("sechs")) return 6;
        if (text.contains("sieben")) return 7;
        if (text.contains("acht")) return 8;
        if (text.contains("neun")) return 9;
        if (text.contains("zehn")) return 10;
        return -1;
    }
}
