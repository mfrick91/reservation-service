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

abstract class AbstractExamplesTest {

	protected static ReservationParser parser;
    
	private void assertRequestEquals(ReservationRequest request, String expectedName, LocalDate expectedDate,
			LocalTime expectedTime, Integer expectedNumberOfPeople) {
		ReservationData response = parser.parse(request);
		assertNotNull(response);
		assertEquals(expectedName, response.getName());
		assertEquals(expectedDate, response.getDate());
		assertEquals(expectedTime, response.getTime());
		assertEquals(expectedNumberOfPeople, response.getNumberOfPeople());
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
		LocalDateTime today = LocalDateTime.now();
		
		ReservationRequest request = new DefaultReservationRequest(
				"Hallo, bitte für zwei Personen einen Tisch am 19.3. um 20:00 Uhr, Vielen Dank Klaus Müller", today);
		assertRequestEquals(request, "Klaus Müller", LocalDate.of(today.getYear(), 3, 19), LocalTime.of(20, 0), 2);

		request = new DefaultReservationRequest(
				"Sehr geehrte Damen Herren, wir würden gern am 9. April 9:45 Uhr mit sechs Leuten zum Brunch kommen, Mit freundlichen Grüßen Maria Meier", today);
		assertRequestEquals(request, "Maria Meier", LocalDate.of(today.getYear(), 4, 9), LocalTime.of(9, 45), 6);

		request = new DefaultReservationRequest(
				"Guten Tag, einen Tisch für 8 Mann am 1.5. 9 Uhr abends, Gruß Franz Schulze", today);
		assertRequestEquals(request, "Franz Schulze", LocalDate.of(today.getYear(), 5, 1), LocalTime.of(21, 0), 8);
	}

	@Test
	void testEasyExamples() {
		LocalDateTime today = LocalDateTime.now();
		
		ReservationRequest request = new DefaultReservationRequest(
				"Bitte reservieren Sie einen Tisch für zwei Personen am 12.4. um 18:30 Uhr. Danke, Anna Fischer", today);
		assertRequestEquals(request, "Anna Fischer", LocalDate.of(today.getYear(), 4, 12), LocalTime.of(18, 30), 2);
		
		request = new DefaultReservationRequest(
				"Einen Tisch für 4 Personen am 8. Mai um 19 Uhr, Gruß Tom Weber", today);
		assertRequestEquals(request, "Tom Weber", LocalDate.of(today.getYear(), 5, 8), LocalTime.of(19, 0), 4);
		
		request = new DefaultReservationRequest(
				"Reservierung am 3.6. um 20:00 Uhr für 6 Leute. Mit freundlichen Grüßen, Lisa Krause", today);
		assertRequestEquals(request, "Lisa Krause", LocalDate.of(today.getYear(), 6, 3), LocalTime.of(20, 0), 6);
		
		request = new DefaultReservationRequest(
				"Ich hätte gerne einen Tisch am 15.5. um 12:00 für drei Personen. Danke, Jens König", today);
		assertRequestEquals(request, "Jens König", LocalDate.of(today.getYear(), 5, 15), LocalTime.of(12, 0), 3);
		
		request = new DefaultReservationRequest(
				"Guten Tag, wir möchten am 1. Juni um 17:45 zu fünft kommen. Gruß, Sabine Hofmann", today);
		assertRequestEquals(request, "Sabine Hofmann", LocalDate.of(today.getYear(), 6, 1), LocalTime.of(17, 45), 5);
		
		request = new DefaultReservationRequest(
				"Für fünf Gäste bitte ein Tisch am 10.04. um 18 Uhr. Vielen Dank, Peter Wolf", today);
		assertRequestEquals(request, "Peter Wolf", LocalDate.of(today.getYear(), 4, 10), LocalTime.of(18, 0), 5);
		
		request = new DefaultReservationRequest(
				"Sehr geehrte Damen und Herren, bitte reservieren Sie am 22. Mai um 19:15 Uhr für 7 Personen. Grüße, Markus Schäfer", today);
		assertRequestEquals(request, "Markus Schäfer", LocalDate.of(today.getYear(), 5, 22), LocalTime.of(19, 15), 7);
		
		request = new DefaultReservationRequest(
				"Hallo, ein Tisch für 8 Mann am 25.5. um 21:00 Uhr. Beste Grüße, Nora Bauer", today);
		assertRequestEquals(request, "Nora Bauer", LocalDate.of(today.getYear(), 5, 25), LocalTime.of(21, 0), 8);
		
		request = new DefaultReservationRequest(
				"Wir sind vier Leute und möchten am 5. Juni um 20:30 Uhr essen. Viele Grüße, Michael Berger", today);
		assertRequestEquals(request, "Michael Berger", LocalDate.of(today.getYear(), 6, 5), LocalTime.of(20, 30), 4);
		
		request = new DefaultReservationRequest(
				"Für zwei Personen am 30. April um 13 Uhr bitte. Danke, Sophie Neumann", today);
		assertRequestEquals(request, "Sophie Neumann", LocalDate.of(today.getYear(), 4, 30), LocalTime.of(13, 0), 2);
	}

	@Test
	void testMediumExamples() {
		LocalDateTime today = LocalDateTime.now();
		
		ReservationRequest request = new DefaultReservationRequest(
				"Wir würden gern kommenden Dienstag um 18 Uhr mit fünf Leuten bei Ihnen essen. Viele Grüße, Laura Schmitt", today);
		assertRequestEquals(request, "Laura Schmitt", today.toLocalDate().plusDays(daysToAdd(2)), LocalTime.of(18, 0), 5);
		
		request = new DefaultReservationRequest(
				"Einen Tisch bitte übernächsten Freitagabend um 8:30 Uhr für etwa sechs Personen. Danke, Jan Lorenz", today);
		assertRequestEquals(request, "Jan Lorenz", today.toLocalDate().plusDays(daysToAdd(5, true)), LocalTime.of(20, 30), 6);
		
		request = new DefaultReservationRequest(
				"Guten Tag, wir kommen morgen gegen 20 Uhr mit 3-4 Leuten vorbei. Gruß, Nina Thalberg", today);
		assertRequestEquals(request, "Nina Thalberg", today.toLocalDate().plusDays(1), LocalTime.of(20, 0), 4);
		
		request = new DefaultReservationRequest(
				"Ich hätte gern eine Reservierung irgendwann zwischen 18 und 19 Uhr am 10. Mai für vier Personen. Danke, Tobias Frank", today);
		assertRequestEquals(request, "Tobias Frank", LocalDate.of(today.getYear(), 5, 10), LocalTime.of(18, 0), 4);

		request = new DefaultReservationRequest(
				"Können Sie uns bitte für Montagabend einen Tisch für fünf reservieren zwischen 9:30-10:45 Uhr? Vielen Dank, Tim Harms", today);
		assertRequestEquals(request, "Tim Harms", today.toLocalDate().plusDays(daysToAdd(1)), LocalTime.of(21, 30), 5);
		
		request = new DefaultReservationRequest(
				"Hallo, wir planen für den nächsten Mittwoch ein Abendessen, ca. 6 Personen um 6 Uhr. Grüße, Kim Wagner", today);
		assertRequestEquals(request, "Kim Wagner", today.toLocalDate().plusDays(daysToAdd(3)), LocalTime.of(18, 0), 6);
		
		request = new DefaultReservationRequest(
				"Für meine Kollegen und mich (wir sind sieben) bitte einen Tisch am 6.5., so ab 18:30 Uhr. Gruß, Pascal Nowak", today);
		assertRequestEquals(request, "Pascal Nowak", LocalDate.of(today.getYear(), 5, 6), LocalTime.of(18, 30), 7);
		
		request = new DefaultReservationRequest(
				"Einen Tisch für mich und meine Familie (5 Personen) am 1. Juni gegen 17 Uhr. Grüße, Petra Lang", today);
		assertRequestEquals(request, "Petra Lang", LocalDate.of(today.getYear(), 6, 1), LocalTime.of(17, 0), 5);
	}
    
    @Test
    void testHardExamples() {
    	// TODO: implement me
    }

}
