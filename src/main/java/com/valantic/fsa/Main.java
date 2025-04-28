package com.valantic.fsa;

import com.valantic.fsa.model.DefaultReservationRequest;
import com.valantic.fsa.model.ReservationData;
import com.valantic.fsa.model.ReservationRequest;
import com.valantic.fsa.parser.BasicReservationParser;
import com.valantic.fsa.parser.ReservationParser;

public class Main {

	public static void main(String[] args) {
		ReservationParser parser = new BasicReservationParser();
		
		ReservationRequest example1 = new DefaultReservationRequest(
				"Hallo, bitte für zwei Personen einen Tisch am 19.3. um 20:00 Uhr, Vielen Dank Klaus Müller");
		System.out.println(example1.getText());
		
		ReservationData data1 = parser.parse(example1);
		System.out.println(String.format("(%s, %s, %s, %d)\n", 
				data1.getName(), 
				data1.getDate().toString(), 
				data1.getTime().toString(),
				data1.getNumberOfPeople()));

		ReservationRequest example2 = new DefaultReservationRequest(
				"Sehr geehrte Damen Herren, wir würden gern am 9. April 9:45 Uhr mit sechs Leuten zum Brunch kommen, Mit freundlichen Grüßen Maria Meier");
		System.out.println(example2.getText());
		
		ReservationData data2 = parser.parse(example2);
		System.out.println(String.format("(%s, %s, %s, %d)\n", 
				data2.getName(), 
				data2.getDate().toString(), 
				data2.getTime().toString(),
				data2.getNumberOfPeople()));
		
		ReservationRequest example3 = new DefaultReservationRequest(
				"Guten Tag, einen Tisch für 8 Mann am 1.5. 9 Uhr abends, Gruß Franz Schulze");
		System.out.println(example3.getText());
		
		ReservationData data3 = parser.parse(example3);
		System.out.println(String.format("(%s, %s, %s, %d)\n", 
				data3.getName(), 
				data3.getDate().toString(), 
				data3.getTime().toString(),
				data3.getNumberOfPeople()));
		
	}

}
