package com.valantic.fsa.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.time.LocalTime;

import org.junit.jupiter.api.Test;

import com.valantic.fsa.model.ReservationData;

class ReservationParserTest {

	private final ReservationParser parser = new SimpleReservationParser();
	
	@Test
	void testExample1() {
        String text = "Hallo, bitte für zwei Personen einen Tisch am 19.3. um 20:00 Uhr, Vielen Dank Klaus Müller";
        ReservationData data = parser.parse(text);
        
        assertEquals("Klaus Müller", data.getName());
        assertEquals(LocalDate.of(LocalDate.now().getYear(), 3, 19), data.getDate());
        assertEquals(LocalTime.of(20, 0), data.getTime());
        assertEquals(2, data.getPeopleCount());
	}
	
    @Test
    void testExample2() {
        String text = "Sehr geehrte Damen Herren, wir würden gern am 9. April 9:45 Uhr mit sechs Leuten zum Brunch kommen, Mit freundlichen Grüßen Maria Meier";
        ReservationData data = parser.parse(text);

        assertEquals("Maria Meier", data.getName());
        assertEquals(LocalDate.of(LocalDate.now().getYear(), 4, 9), data.getDate());
        assertEquals(LocalTime.of(9, 45), data.getTime());
        assertEquals(6, data.getPeopleCount());
    }
    
    @Test
    void testExample3() {
        String text = "Guten Tag, einen Tisch für 8 Mann am 1.5. 9 Uhr abends, Gruß Franz Schulze";
        ReservationData data = parser.parse(text);

        assertEquals("Franz Schulze", data.getName());
        assertEquals(LocalDate.of(LocalDate.now().getYear(), 5, 1), data.getDate());
        assertEquals(LocalTime.of(21, 0), data.getTime());
        assertEquals(8, data.getPeopleCount());
    }

}
