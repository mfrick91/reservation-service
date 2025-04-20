package com.valantic.fsa.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import com.valantic.fsa.model.DefaultReservationRequest;
import com.valantic.fsa.model.ReservationData;
import com.valantic.fsa.model.ReservationRequest;

class ReservationParserTest {

	private final ReservationParser parser = new SimpleReservationParser();
	
	@Test
	void testExample1() {
        String text = "Hallo, bitte für zwei Personen einen Tisch am 19.3. um 20:00 Uhr, Vielen Dank Klaus Müller";
        ReservationRequest request = new DefaultReservationRequest(text);
        ReservationData data = parser.parse(request);
        
        assertEquals("Klaus Müller", data.getName());
        assertEquals(LocalDate.of(LocalDate.now().getYear(), 3, 19), data.getDate());
        assertEquals(LocalTime.of(20, 0), data.getTime());
        assertEquals(2, data.getPeopleCount());
	}
	
    @Test
    void testExample2() {
        String text = "Sehr geehrte Damen Herren, wir würden gern am 9. April 9:45 Uhr mit sechs Leuten zum Brunch kommen, Mit freundlichen Grüßen Maria Meier";
        ReservationRequest request = new DefaultReservationRequest(text);
        ReservationData data = parser.parse(request);

        assertEquals("Maria Meier", data.getName());
        assertEquals(LocalDate.of(LocalDate.now().getYear(), 4, 9), data.getDate());
        assertEquals(LocalTime.of(9, 45), data.getTime());
        assertEquals(6, data.getPeopleCount());
    }
    
    @Test
    void testExample3() {
        String text = "Guten Tag, einen Tisch für 8 Mann am 1.5. 9 Uhr abends, Gruß Franz Schulze";
        ReservationRequest request = new DefaultReservationRequest(text);
        ReservationData data = parser.parse(request);

        assertEquals("Franz Schulze", data.getName());
        assertEquals(LocalDate.of(LocalDate.now().getYear(), 5, 1), data.getDate());
        assertEquals(LocalTime.of(21, 0), data.getTime());
        assertEquals(8, data.getPeopleCount());
    }
    
    @Test
    void testExample3UpperCase() {
        String text = "Guten Tag, einen Tisch für 8 Mann am 1.5. 9 Uhr abends, Gruß Franz Schulze".toUpperCase();
        ReservationRequest request = new DefaultReservationRequest(text);
        ReservationData data = parser.parse(request);

        assertEquals("Franz Schulze".toUpperCase(), data.getName());
        assertEquals(LocalDate.of(LocalDate.now().getYear(), 5, 1), data.getDate());
        assertEquals(LocalTime.of(21, 0), data.getTime());
        assertEquals(8, data.getPeopleCount());
    }
    
    @Test
    void testRelativeDates() {
        LocalDateTime now = LocalDateTime.now();
        
        String text = "Hallo, bitte für zwei Personen einen Tisch für morgen um 20:00 Uhr, Vielen Dank Klaus Müller";
        ReservationRequest request = new DefaultReservationRequest(text, now);
        ReservationData data = parser.parse(request);
        assertEquals(now.toLocalDate().plusDays(1), data.getDate());
        
        text = "Sehr geehrte Damen Herren, wir würden gern übermorgen um 9:45 Uhr mit sechs Leuten zum Brunch kommen, Mit freundlichen Grüßen Maria Meier";
        request = new DefaultReservationRequest(text, now);
        data = parser.parse(request);
        assertEquals(now.toLocalDate().plusDays(2), data.getDate());
        
        text = "Guten Tag, einen Tisch für 8 Mann für nächste Woche 9 Uhr abends, Gruß Franz Schulze";
        request = new DefaultReservationRequest(text, now);
        data = parser.parse(request);
        assertEquals(now.toLocalDate().plusWeeks(1), data.getDate());
    }
    
    @Test
    void testWeekdays() {
        LocalDateTime now = LocalDateTime.now();
        int currentDayIndex = now.getDayOfWeek().getValue();

        String[] qualifiers = new String[] { "", "kommenden ", "nächsten ", "übernächsten " };
        String[] weekdays = new String[] { "Montag", "Dienstag", "Mittwoch", "Donnerstag", "Fr", "Sa", "So" };
		for (String qualifier : qualifiers) {
			int dayIndex = 1;
			for (String weekday : weekdays) {
				String text = String.format(
						"Hallo, bitte für zwei Personen einen Tisch am %s%s um 20:00 Uhr, Vielen Dank Klaus Müller",
						qualifier, weekday);
				
				ReservationRequest request = new DefaultReservationRequest(text);
				ReservationData data = parser.parse(request);

				// go to next week
				int daysToAdd = (dayIndex - currentDayIndex + 7) % 7;
				if (daysToAdd == 0) {
					daysToAdd = 7;
				}
				// go to week after next
				if (qualifier.contains("übernächsten")) {
					daysToAdd += 7;
				}
				
				assertEquals(now.toLocalDate().plusDays(daysToAdd), data.getDate());
				dayIndex++;
			}
		}
	}
    
    @Test
    void testBetweenPeopleCount() {
        String text = "Guten Tag, ich möchte einen Tisch für zwischen 4 und 6 Personen am 1.5. 9 Uhr abends, Gruß Franz Schulze";
        ReservationRequest request = new DefaultReservationRequest(text);
        ReservationData data = parser.parse(request);
        assertEquals(6, data.getPeopleCount());

        text = "Hallo, wir sind zwischen 12 und 8 Leute und möchten am 19.3. um 20:00 Uhr kommen, Gruß Franz Schulze";
        request = new DefaultReservationRequest(text);
        data = parser.parse(request);
        assertEquals(12, data.getPeopleCount());
        
        text = "Hallo, wir sind zwischen acht und 12 Leute und möchten am 19.3. um 20:00 Uhr kommen, Gruß Franz Schulze";
        request = new DefaultReservationRequest(text);
        data = parser.parse(request);
        assertEquals(12, data.getPeopleCount());
        
        text = "Hallo, wir sind zwischen 8 und zwölf Leute und möchten am 19.3. um 20:00 Uhr kommen, Gruß Franz Schulze";
        request = new DefaultReservationRequest(text);
        data = parser.parse(request);
        assertEquals(12, data.getPeopleCount());
        
        text = "Hallo, wir sind zwischen zwölf und acht Leute und möchten am 19.3. um 20:00 Uhr kommen, Gruß Franz Schulze";
        request = new DefaultReservationRequest(text);
        data = parser.parse(request);
        assertEquals(12, data.getPeopleCount());
    }

    @Test
    void testAtLeastPeopleCount() {
        String text = "Guten Tag, ich möchte einen Tisch für mindestens 4 Personen am 1.5. 9 Uhr abends, Gruß Franz Schulze";
        ReservationRequest request = new DefaultReservationRequest(text);
        ReservationData data = parser.parse(request);
        assertEquals(4, data.getPeopleCount());

        text = "Hallo, wir sind mindestens acht Leute und möchten am 19.3. um 20:00 Uhr kommen, Gruß Franz Schulze";
        request = new DefaultReservationRequest(text);
        data = parser.parse(request);
        assertEquals(8, data.getPeopleCount());
    }

    @Test
    void testUpToPeopleCount() {
        String text = "Guten Tag, ich möchte einen Tisch für bis zu 4 Personen am 1.5. 9 Uhr abends, Gruß Franz Schulze";
        ReservationRequest request = new DefaultReservationRequest(text);
        ReservationData data = parser.parse(request);
        assertEquals(4, data.getPeopleCount());

        text = "Hallo, wir sind bis zu acht Leute und möchten am 19.3. um 20:00 Uhr kommen, Gruß Franz Schulze";
        request = new DefaultReservationRequest(text);
        data = parser.parse(request);
        assertEquals(8, data.getPeopleCount());
    }

    @Test
    void testNotMoreThanPeopleCount() {
        String text = "Guten Tag, ich möchte einen Tisch für nicht mehr als 4 Personen am 1.5. 9 Uhr abends, Gruß Franz Schulze";
        ReservationRequest request = new DefaultReservationRequest(text);
        ReservationData data = parser.parse(request);
        assertEquals(4, data.getPeopleCount());

        text = "Hallo, wir sind nicht mehr als acht Leute und möchten am 19.3. um 20:00 Uhr kommen, Gruß Franz Schulze";
        request = new DefaultReservationRequest(text);
        data = parser.parse(request);
        assertEquals(8, data.getPeopleCount());
    }

    @Test
    void testMixedPeopleCount() {
        String text = "Guten Tag, ich möchte einen Tisch für mindestens 4, aber nicht mehr als 6 Personen am 1.5. 9 Uhr abends, Gruß Franz Schulze";
        ReservationRequest request = new DefaultReservationRequest(text);
        ReservationData data = parser.parse(request);
        assertEquals(6, data.getPeopleCount());

        text = "Hallo, wir sind mindestens vier, aber nicht mehr als sechs Leute, und möchten am 19.3. um 20:00 Uhr kommen, Gruß Franz Schulze";
        request = new DefaultReservationRequest(text);
        data = parser.parse(request);
        assertEquals(6, data.getPeopleCount());
    }
    
}
