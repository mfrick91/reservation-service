package com.valantic.fsa.parser;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.Locale;

import com.valantic.fsa.llm.OpenAIClient;
import com.valantic.fsa.model.DefaultReservationData;
import com.valantic.fsa.model.ReservationData;
import com.valantic.fsa.model.ReservationRequest;

/**
 * A parser that uses the OpenAI API to parse reservation requests.
 * 
 * @author M. Frick
 */
public class OpenAIReservationParser implements ReservationParser {

	/**
     * The date formatter.
     */
	private static final DateTimeFormatter DATE_FORMAT = new DateTimeFormatterBuilder()
			.appendPattern("d[.]M[.]uuuu")
			.parseDefaulting(ChronoField.YEAR, LocalDate.now().getYear())
			.toFormatter()
			.withLocale(Locale.GERMAN);

    /**
     * The time formatter.
     */
    private static final DateTimeFormatter TIME_FORMAT = new DateTimeFormatterBuilder()
    		.appendPattern("H[:]mm")
    		.parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
    		.toFormatter();

	/**
	 * The OpenAI client.
	 */
	private OpenAIClient openAi;

	/**
	 * Constructs a new OpenAI reservation parser.
	 */
	public OpenAIReservationParser() {
		this.openAi = new OpenAIClient();
	}

	@Override
	public ReservationData parse(ReservationRequest request) {
        String timestamp = request.getTimestamp().toLocalDate().toString();
        String text = request.getText();

		String prompt = String.format(
            "Extrahiere aus folgendem Text die folgenden Informationen im Format: (Name, Datum, Uhrzeit, Personen)\n" +
            "Name in dem Format wie der Name im Text vorkommt (z. B. ist der Name hochgestellt im Text, dann den Name auch hochgestellt lassen).\n" +
    		"Datum als TT.MM.2025 (z. B. 01.05.2025).\n" +
            "Uhrzeit im Format HH:mm (z. B. 09:45).\n"+ 
    		"Personen als Integer.\n" +
            "Relative Datumsangaben immer vom Zeitpunkt %s berechnen.\n" +
            "Angaben wie \"übernächste X\" berechnet sich als %s + 2 X, wobei X ein Tag, eine Woche, ein Monat ein Jahr oder ein Wochentag sein kann.\n" +
            "Bei Zeitangaben wie z. B. \"zwischen 18 und 19 Uhr \" immer die kleinere Zeit nehmen.\n" +
            "Bei Personenangaben wie z. B. \"vier bis sechs Personen\" immer die größere Personenanzahl nehmen.\n" +
            "Fehlende Names-, Datums- und Zeitangaben als leerer String \"\". Fehlende Personenangaben als -1.\n\n" +
//            "Beispiele:\n" +
//            "\"Guten Tag, einen Tisch für 8 Mann am 1.5. 9 Uhr abends, Gruß Franz Schulze\"-> (Franz Schulze, 01.05.2025, 21:00, 8)\n" +
//            "\"Guten Tag, einen Tisch für vier Personen am kommenden Montagabend um 20 Uhr, Gruß Franz Schulze\"-> (Franz Schulze, 28.04.2025, 20:00, 4)\n" +
//            "\"Guten Tag, einen Tisch für sieben für den übernächsten Freitagmittag um 12 Uhr, Gruß Franz Schulze\"-> (Franz Schulze, 02.05.2025, 12:00, 7)\n" +
//            "\"Guten Tag, wir sind 9 und brauchen einen Tisch für den übernächsten Montag um 22 Uhr, Gruß Franz Schulze\"-> (Franz Schulze, 05.05.2025, 22:00, 9)\n" +
//            "\"Guten Tag, einen Tisch für vier Personen für den übernächsten Monat um 18 Uhr, Gruß Franz Schulze\"-> (Franz Schulze, 24.06.2025, 18:00, 4)\n" +
            "Text:\n \"%s\"", timestamp, timestamp, text
        );
        String response = openAi.ask(prompt);
        return parseResponse(response, request);
    }

	/**
	 * Parses the response from the OpenAI API.
	 * 
	 * @param response the response from the OpenAI API
	 * @param request  the reservation request
	 * @return the reservation data
	 */
	protected ReservationData parseResponse(String response, ReservationRequest request) {
		if ((response != null) && !response.isEmpty()) {
			String[] parts = response.replace("(", "").replace(")", "").split(",");
			if (parts.length == 4) {

				String name = this.parseName(parts[0]);

				LocalDate date = this.parseDate(parts[1], request);

				LocalTime time = this.parseTime(parts[2]);

				int numberOfPeople = this.parseNumberOfPeople(parts[3]);

				return new DefaultReservationData(name, date, time, numberOfPeople);
			}
		}
		return new DefaultReservationData();
	}

	/**
	 * Parses the name from the response.
	 * 
	 * @param namePart the name part
	 * @return the name
	 */
	private String parseName(String namePart) {
		if (namePart.contains("Name:")) {
			namePart = namePart.split("Name:")[1];
		}
		namePart = ParserUtils.stripQuotes(namePart.trim());
		if (!namePart.isEmpty()) {
			return namePart;
		}
		return null;
	}

	/**
	 * Parses the date from the response.
	 * 
	 * @param datePart the date part
	 * @param request the reservation request
	 * @return the date
	 */
	private LocalDate parseDate(String datePart, ReservationRequest request) {
		if (datePart.contains("Datum:")) {
			datePart = datePart.split("Datum:")[1];
		}
		datePart = ParserUtils.stripQuotes(datePart.trim());
		if (!datePart.isEmpty()) {
			return LocalDate.parse(datePart, DATE_FORMAT);
		}
		return null;
	}

	/**
	 * Parses the time from the response.
	 * 
	 * @param timePart the time part
	 * @return the time
	 */
	private LocalTime parseTime(String timePart) {
		if (timePart.contains("Uhrzeit:")) {
			timePart = timePart.split("Uhrzeit:")[1];
		}
		timePart = ParserUtils.stripQuotes(timePart.trim());
		if (!timePart.isEmpty()) {
			return LocalTime.parse(timePart.trim(), TIME_FORMAT);
		}
		return null;
	}

	/**
	 * Parses the number of people from the response.
	 * 
	 * @param numberOfPeopleString the number of people string
	 * @return the number of people
	 */
	private int parseNumberOfPeople(String numberOfPeopleString) {
		try {
			if (numberOfPeopleString.contains("Personen:")) {
				numberOfPeopleString = numberOfPeopleString.split("Personen:")[1];
			}
			String numberString = ParserUtils.stripQuotes(numberOfPeopleString.trim());
			return Integer.parseInt(numberString);
		} catch (Exception e) {
		}
		return -1;
	}

}
