package com.valantic.fsa.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.junit.jupiter.api.Test;

import com.valantic.fsa.model.DefaultReservationRequest;
import com.valantic.fsa.model.ReservationData;

class ReservationParserTest {

	private final ReservationParser parser = new BasicReservationParser();
	
    @Test
    void testUpperCase() {
		assertParsed("Hallo, bitte für zwei Personen einen Tisch am 19.3. um 20:00 Uhr, Vielen Dank Klaus Müller".toUpperCase(),
				"Klaus Müller".toUpperCase(), LocalDate.of(year(), 3, 19), LocalTime.of(20, 0), 2);
    }
	
    @Test
    void testNullCases() {
		assertParsed("Hallo, bitte für zwei Personen einen Tisch am 19.3. um 20:00 Uhr, Vielen Dank",
				null, LocalDate.of(year(), 3, 19), LocalTime.of(20, 0), 2);
		
		assertParsed("Sehr geehrte Damen Herren, wir würden um 9:45 Uhr mit sechs Leuten zum Brunch kommen, Mit freundlichen Grüßen Maria Meier",
				"Maria Meier", null, LocalTime.of(9, 45), 6);
		
		assertParsed("Guten Tag, einen Tisch für 8 Mann am 1.5., Gruß Franz Schulze",
				"Franz Schulze", LocalDate.of(year(), 5, 1), null, 8);
		
		assertParsed("Guten Tag, einen Tisch am 1.5. 9 Uhr abends, Gruß Franz Schulze",
				"Franz Schulze", LocalDate.of(year(), 5, 1), LocalTime.of(21, 0), null);
    }
    
    @Test
    void testRelativeDates() {
        LocalDateTime now = LocalDateTime.now();
        
        assertParsed("Hallo, bitte für zwei Personen einen Tisch für morgen um 20:00 Uhr, Vielen Dank Klaus Müller",
                "Klaus Müller", now.toLocalDate().plusDays(1), LocalTime.of(20, 0), 2);
        
        assertParsed("Sehr geehrte Damen Herren, wir würden gern übermorgen um 9:45 Uhr mit sechs Leuten zum Brunch kommen, Mit freundlichen Grüßen Maria Meier",
                "Maria Meier", now.toLocalDate().plusDays(2), LocalTime.of(9, 45), 6);
        
        assertParsed("Guten Tag, einen Tisch für 8 Mann für nächste Woche 9 Uhr abends, Gruß Franz Schulze",
                "Franz Schulze", now.toLocalDate().plusWeeks(1), LocalTime.of(21, 0), 8);
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
				
				// go to next week
				int daysToAdd = (dayIndex - currentDayIndex + 7) % 7;
				if (daysToAdd == 0) {
					daysToAdd = 7;
				}
				// go to week after next
				if (qualifier.contains("übernächsten")) {
					daysToAdd += 7;
				}
				assertParsed(text, "Klaus Müller", now.toLocalDate().plusDays(daysToAdd), LocalTime.of(20, 0), 2);
				dayIndex++;
			}
		}
	}

	@Test
	void testInXDaysWeeksMonthsYears() {
		LocalDateTime now = LocalDateTime.now();

		assertParsed("Hallo, bitte für zwei Personen einen Tisch in fünf Tagen um 20:00 Uhr, Vielen Dank Klaus Müller",
				"Klaus Müller", now.toLocalDate().plusDays(5), LocalTime.of(20, 0), 2);

		assertParsed("Hallo, bitte für zwei Personen einen Tisch in zwei Wochen um 20:00 Uhr, Vielen Dank Klaus Müller",
				"Klaus Müller", now.toLocalDate().plusWeeks(2), LocalTime.of(20, 0), 2);
		
		assertParsed("Hallo, bitte für zwei Personen einen Tisch in 28 Monaten um 20:00 Uhr, Vielen Dank Klaus Müller",
				"Klaus Müller", now.toLocalDate().plusMonths(28), LocalTime.of(20, 0), 2);

		assertParsed("Hallo, bitte für zwei Personen einen Tisch in 28 Jahren um 20:00 Uhr, Vielen Dank Klaus Müller",
				"Klaus Müller", now.toLocalDate().plusYears(28), LocalTime.of(20, 0), 2);

		assertParsed("Hallo, bitte für zwei Personen einen Tisch in 4 Tagen, drei Wochen, neun Monaten und 2 Jahren um 20:00 Uhr, Vielen Dank Klaus Müller",
				"Klaus Müller", now.toLocalDate().plusDays(4).plusWeeks(3).plusMonths(9).plusYears(2), LocalTime.of(20, 0), 2);
	}
    
    @Test
    void testPeopleCount() {
        assertParsed("Guten Tag, wir sind zu 4 und kommen am 1.5. 9 Uhr abends zum Essen, Gruß Franz Schulze",
                "Franz Schulze", LocalDate.of(year(), 5, 1), LocalTime.of(21, 0), 4);
        
        assertParsed("Guten Tag, wir würden zu zwölft am 1.5. 9 Uhr abends kommen, Gruß Franz Schulze",
                "Franz Schulze", LocalDate.of(year(), 5, 1), LocalTime.of(21, 0), 12);
    }
    
    @Test
    void testAtLeastPeopleCount() {
        assertParsed("Guten Tag, ich möchte einen Tisch für mindestens 4 Personen am 1.5. 9 Uhr abends, Gruß Franz Schulze",
                "Franz Schulze", LocalDate.of(year(), 5, 1), LocalTime.of(21, 0), 4);

        assertParsed("Hallo, wir sind mindestens acht Leute und möchten am 19.3. um 20:00 Uhr kommen, Gruß Franz Schulze",
                "Franz Schulze", LocalDate.of(year(), 3, 19), LocalTime.of(20, 0), 8);
    }

    @Test
    void testUpToPeopleCount() {
        assertParsed("Guten Tag, ich möchte einen Tisch für bis zu 4 Personen am 1.5. 9 Uhr abends, Gruß Franz Schulze",
                "Franz Schulze", LocalDate.of(year(), 5, 1), LocalTime.of(21, 0), 4);

        assertParsed("Hallo, wir sind bis zu acht Leute und möchten am 19.3. um 20:00 Uhr kommen, Gruß Franz Schulze",
                "Franz Schulze", LocalDate.of(year(), 3, 19), LocalTime.of(20, 0), 8);
    }

    @Test
    void testNotMoreThanPeopleCount() {
        assertParsed("Guten Tag, ich möchte einen Tisch für nicht mehr als 4 Personen am 1.5. 9 Uhr abends, Gruß Franz Schulze",
                "Franz Schulze", LocalDate.of(year(), 5, 1), LocalTime.of(21, 0), 4);

        assertParsed("Hallo, wir sind nicht mehr als acht Leute und möchten am 19.3. um 20:00 Uhr kommen, Gruß Franz Schulze",
                "Franz Schulze", LocalDate.of(year(), 3, 19), LocalTime.of(20, 0), 8);
    }
    
    @Test
    void testBetweenPeopleCount() {
        assertParsed("Guten Tag, ich möchte einen Tisch für zwischen 4 und 6 Personen am 1.5. 9 Uhr abends, Gruß Franz Schulze",
                "Franz Schulze", LocalDate.of(year(), 5, 1), LocalTime.of(21, 0), 6);
        
        assertParsed("Guten Tag, ich möchte einen Tisch für 4-6 Personen am 1.5. 9 Uhr abends, Gruß Franz Schulze",
                "Franz Schulze", LocalDate.of(year(), 5, 1), LocalTime.of(21, 0), 6);

        assertParsed("Hallo, wir sind zwischen 12 und 8 Leute und möchten am 19.3. um 20:00 Uhr kommen, Gruß Franz Schulze",
                "Franz Schulze", LocalDate.of(year(), 3, 19), LocalTime.of(20, 0), 12);
        
        assertParsed("Hallo, wir sind zwischen acht und 12 Leute und möchten am 19.3. um 20:00 Uhr kommen, Gruß Franz Schulze",
                "Franz Schulze", LocalDate.of(year(), 3, 19), LocalTime.of(20, 0), 12);
        
        assertParsed("Hallo, wir sind zwischen 8 und zwölf Leute und möchten am 19.3. um 20:00 Uhr kommen, Gruß Franz Schulze",
                "Franz Schulze", LocalDate.of(year(), 3, 19), LocalTime.of(20, 0), 12);
        
        assertParsed("Hallo, wir sind zwischen zwölf und acht Leute und möchten am 19.3. um 20:00 Uhr kommen, Gruß Franz Schulze",
                "Franz Schulze", LocalDate.of(year(), 3, 19), LocalTime.of(20, 0), 12);
    }

    @Test
    void testMixedPeopleCount() {
        assertParsed("Guten Tag, ich möchte einen Tisch für mindestens 4, aber nicht mehr als 6 Personen am 1.5. 9 Uhr abends, Gruß Franz Schulze",
                "Franz Schulze", LocalDate.of(year(), 5, 1), LocalTime.of(21, 0), 6);

        assertParsed("Hallo, wir sind mindestens vier, aber nicht mehr als sechs Leute, und möchten am 19.3. um 20:00 Uhr kommen, Gruß Franz Schulze",
                "Franz Schulze", LocalDate.of(year(), 3, 19), LocalTime.of(20, 0), 6);
    }

    private void assertParsed(String text, String expectedName, 
    		LocalDate expectedDate, LocalTime expectedTime, Integer expectedPeopleCount) {
    	
        ReservationData data = parser.parse(new DefaultReservationRequest(text));
        assertEquals(expectedName, data.getName());
    	assertEquals(expectedDate, data.getDate());
    	assertEquals(expectedTime, data.getTime());
    	assertEquals(expectedPeopleCount, data.getPeopleCount());
    }

    private int year() {
        return LocalDate.now().getYear();
    }
    
}
