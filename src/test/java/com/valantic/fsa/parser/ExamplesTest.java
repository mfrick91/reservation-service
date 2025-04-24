package com.valantic.fsa.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.junit.jupiter.api.Test;

import com.valantic.fsa.model.DefaultReservationRequest;
import com.valantic.fsa.model.ReservationData;
import com.valantic.fsa.model.ReservationRequest;

class ExamplesTest {

	private static final ReservationParser PARSER = new BasicReservationParser();
    
	private void assertRequest(ReservationRequest request, String expectedName, LocalDate expectedDate,
			LocalTime expectedTime, Integer expectedNumberOfPeople) {
		ReservationData response = PARSER.parse(request);
		assertNotNull(response);
		assertEquals(expectedName, response.getName());
		assertEquals(expectedDate, response.getDate());
		assertEquals(expectedTime, response.getTime());
		assertEquals(expectedNumberOfPeople, response.getNumberOfPeople());
	}

	private int year() {
		return LocalDate.now().getYear();
	}

	private int daysToAdd(int targetDayIndex) {
		return daysToAdd(targetDayIndex, false);
	}

	private int daysToAdd(int targetDayIndex, boolean isWeekAfterNext) {
		LocalDateTime now = LocalDateTime.now();
		int daysToAdd = (targetDayIndex - now.getDayOfWeek().getValue() + 7) % 7;
		if (daysToAdd == 0) {
			daysToAdd = 7;
		}
		if (isWeekAfterNext) {
			daysToAdd += 7;
		}
		return daysToAdd;
	}
	
	@Test
	void testExample() {
		ReservationRequest request = new DefaultReservationRequest(
				"Hallo, bitte für zwei Personen einen Tisch am 19.3. um 20:00 Uhr, Vielen Dank Klaus Müller");
		assertRequest(request, "Klaus Müller", LocalDate.of(year(), 3, 19), LocalTime.of(20, 0), 2);

		request = new DefaultReservationRequest(
				"Sehr geehrte Damen Herren, wir würden gern am 9. April 9:45 Uhr mit sechs Leuten zum Brunch kommen, Mit freundlichen Grüßen Maria Meier");
		assertRequest(request, "Maria Meier", LocalDate.of(year(), 4, 9), LocalTime.of(9, 45), 6);

		request = new DefaultReservationRequest(
				"Guten Tag, einen Tisch für 8 Mann am 1.5. 9 Uhr abends, Gruß Franz Schulze");
		assertRequest(request, "Franz Schulze", LocalDate.of(year(), 5, 1), LocalTime.of(21, 0), 8);
	}

	@Test
	void testEasyExamples() {
		ReservationRequest request = new DefaultReservationRequest(
				"Bitte reservieren Sie einen Tisch für zwei Personen am 12.4. um 18:30 Uhr. Danke, Anna Fischer");
		assertRequest(request, "Anna Fischer", LocalDate.of(year(), 4, 12), LocalTime.of(18, 30), 2);
		
		request = new DefaultReservationRequest(
				"Einen Tisch für 4 Personen am 8. Mai um 19 Uhr, Gruß Tom Weber");
		assertRequest(request, "Tom Weber", LocalDate.of(year(), 5, 8), LocalTime.of(19, 0), 4);
		
		request = new DefaultReservationRequest(
				"Reservierung am 3.6. um 20:00 Uhr für 6 Leute. Mit freundlichen Grüßen, Lisa Krause");
		assertRequest(request, "Lisa Krause", LocalDate.of(year(), 6, 3), LocalTime.of(20, 0), 6);
		
		request = new DefaultReservationRequest(
				"Ich hätte gerne einen Tisch am 15.5. um 12:00 für drei Personen. Danke, Jens König");
		assertRequest(request, "Jens König", LocalDate.of(year(), 5, 15), LocalTime.of(12, 0), 3);
		
		request = new DefaultReservationRequest(
				"Guten Tag, wir möchten am 1. Juni um 17:45 zu fünft kommen. Gruß, Sabine Hofmann");
		assertRequest(request, "Sabine Hofmann", LocalDate.of(year(), 6, 1), LocalTime.of(17, 45), 5);
		
		request = new DefaultReservationRequest(
				"Für fünf Gäste bitte ein Tisch am 10.04. um 18 Uhr. Vielen Dank, Peter Wolf");
		assertRequest(request, "Peter Wolf", LocalDate.of(year(), 4, 10), LocalTime.of(18, 0), 5);
		
		request = new DefaultReservationRequest(
				"Sehr geehrte Damen und Herren, bitte reservieren Sie am 22. Mai um 19:15 Uhr für 7 Personen. Grüße, Markus Schäfer");
		assertRequest(request, "Markus Schäfer", LocalDate.of(year(), 5, 22), LocalTime.of(19, 15), 7);
		
		request = new DefaultReservationRequest(
				"Hallo, ein Tisch für 8 Mann am 25.5. um 21:00 Uhr. Beste Grüße, Nora Bauer");
		assertRequest(request, "Nora Bauer", LocalDate.of(year(), 5, 25), LocalTime.of(21, 0), 8);
		
		request = new DefaultReservationRequest(
				"Wir sind vier Leute und möchten am 5. Juni um 20:30 Uhr essen. Viele Grüße, Michael Berger");
		assertRequest(request, "Michael Berger", LocalDate.of(year(), 6, 5), LocalTime.of(20, 30), 4);
		
		request = new DefaultReservationRequest(
				"Für zwei Personen am 30. April um 13 Uhr bitte. Danke, Sophie Neumann");
		assertRequest(request, "Sophie Neumann", LocalDate.of(year(), 4, 30), LocalTime.of(13, 0), 2);
	}

	@Test
	void testMediumExamples() {
		ReservationRequest request = new DefaultReservationRequest(
				"Wir würden gern kommenden Dienstag um 18 Uhr mit fünf Leuten bei Ihnen essen. Viele Grüße, Laura Schmitt");
		assertRequest(request, "Laura Schmitt", LocalDate.now().plusDays(daysToAdd(2)), LocalTime.of(18, 0), 5);
		
		request = new DefaultReservationRequest(
				"Einen Tisch bitte übernächsten Freitagabend um 8:30 Uhr für etwa sechs Personen. Danke, Jan Lorenz");
		assertRequest(request, "Jan Lorenz", LocalDate.now().plusDays(daysToAdd(5, true)), LocalTime.of(20, 30), 6);
		
		request = new DefaultReservationRequest(
				"Guten Tag, wir kommen morgen gegen 20 Uhr mit 3-4 Leuten vorbei. Gruß, Nina Thalberg");
		assertRequest(request, "Nina Thalberg", LocalDate.now().plusDays(1), LocalTime.of(20, 0), 4);
		
		request = new DefaultReservationRequest(
				"Ich hätte gern eine Reservierung irgendwann zwischen 18 und 19 Uhr am 10. Mai für vier Personen. Danke, Tobias Frank");
		assertRequest(request, "Tobias Frank", LocalDate.of(year(), 5, 10), LocalTime.of(18, 0), 4);

		request = new DefaultReservationRequest(
				"Können Sie uns bitte für Montagabend einen Tisch für fünf reservieren zwischen 9:30-10:45 Uhr? Vielen Dank, Tim Harms");
		assertRequest(request, "Tim Harms", LocalDate.now().plusDays(daysToAdd(1)), LocalTime.of(21, 30), 5);
		
		request = new DefaultReservationRequest(
				"Hallo, wir planen für den nächsten Mittwoch ein Abendessen, ca. 6 Personen um 6 Uhr. Grüße, Kim Wagner");
		assertRequest(request, "Kim Wagner", LocalDate.now().plusDays(daysToAdd(3)), LocalTime.of(18, 0), 6);
		
		request = new DefaultReservationRequest(
				"Für meine Kollegen und mich (wir sind sieben) bitte einen Tisch am 6.5., so ab 18:30 Uhr. Gruß, Pascal Nowak");
		assertRequest(request, "Pascal Nowak", LocalDate.of(year(), 5, 6), LocalTime.of(18, 30), 7);
		
		request = new DefaultReservationRequest(
				"Einen Tisch für mich und meine Familie (5 Personen) am 1. Juni gegen 17 Uhr. Grüße, Petra Lang");
		assertRequest(request, "Petra Lang", LocalDate.of(year(), 6, 1), LocalTime.of(17, 0), 5);
	}
//    
//    @Test
//    void testHardExamples() {
//        assertParsed("Wir würden am Wochenende zu mehreren kommen, so zwischen fünf und neun Personen. Vielleicht gegen Abend. Viele Grüße, Sarah Ullmann",
//                "Sarah Ullmann", null, null, 5); // TODO: Wochenende, Intervall, Abend
//
//        assertParsed("Reservierung für irgendwann nächsten Donnerstag, 3 Personen zum Lunch. Mit Dank, Benjamin Scholz",
//                "Benjamin Scholz", null, null, 3); // TODO: Lunch = ca. 12:00
//
//        assertParsed("Wir sind ein kleines Team (etwa sechs Leute), möchten aber flexibel am 8. Mai zwischen 18 und 20 Uhr kommen. Gruß, Diana Klein",
//                "Diana Klein", LocalDate.of(year(), 5, 8), null, 6);
//
//        assertParsed("Tisch für ein paar Leute am 10.5., Uhrzeit noch nicht ganz klar – aber abends. Danke, Manuel Fischer",
//                "Manuel Fischer", LocalDate.of(year(), 5, 10), null, -1); // TODO: "ein paar" interpretieren
//
//        assertParsed("Ich komme mit einer Gruppe, Größe variiert, sagen wir fünf bis acht. Irgendwann am 15. Mai. Grüße, Hanna Busch",
//                "Hanna Busch", LocalDate.of(year(), 5, 15), null, 5); // Intervall
//
//        assertParsed("Hi, wir sind vielleicht sechs, vielleicht auch sieben. Abendessen am 3. Juni, Uhrzeit flexibel. Beste Grüße, Erik Roth",
//                "Erik Roth", LocalDate.of(year(), 6, 3), null, 6);
//
//        assertParsed("Ich möchte einen Tisch reservieren für Dienstag in zwei Wochen, gegen 19 Uhr. Für fünf Personen. Gruß, Nicole Weber",
//                "Nicole Weber", null, LocalTime.of(19, 0), 5); // TODO: „Dienstag in zwei Wochen“
//
//        assertParsed("Bitte reservieren Sie einen Tisch für unser Team-Event (6–9 Personen) irgendwann nächste Woche abends. Danke! – Lena Weiß",
//                "Lena Weiß", null, null, 6);
//
//        assertParsed("Wir würden gern spontan am Feiertag (1. Mai) abends vorbeikommen – etwa zu acht. Gruß, David Sturm",
//                "David Sturm", LocalDate.of(year(), 5, 1), null, 8);
//
//        assertParsed("Reservierung für den Abend des 12. Juni. Zwischen sechs und zehn Personen. Uhrzeit zwischen 19 und 21 Uhr. Viele Grüße, Carolin Otto",
//                "Carolin Otto", LocalDate.of(year(), 6, 12), null, 6);
//    }

}
