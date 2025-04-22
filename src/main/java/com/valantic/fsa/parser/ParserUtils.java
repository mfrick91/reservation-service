package com.valantic.fsa.parser;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

public class ParserUtils {

	private static final Map<String, Integer> SIMPLE_NUMBERS_MAP = new LinkedHashMap<>();
	private static final Map<String, Integer> TENS_MAP = new LinkedHashMap<>();
	private static final Map<String, Integer> SCALES_MAP = new LinkedHashMap<>();
	
	static  {
		// initialize simple number mapping
		SIMPLE_NUMBERS_MAP.put("einen", 1);
		SIMPLE_NUMBERS_MAP.put("eins", 1);
		SIMPLE_NUMBERS_MAP.put("eine", 1);
		SIMPLE_NUMBERS_MAP.put("ein", 1);
		SIMPLE_NUMBERS_MAP.put("alleine", 1);
		SIMPLE_NUMBERS_MAP.put("allein", 1);
		SIMPLE_NUMBERS_MAP.put("zweit", 2);
		SIMPLE_NUMBERS_MAP.put("zwei", 2);
		SIMPLE_NUMBERS_MAP.put("drei", 3);
		SIMPLE_NUMBERS_MAP.put("dritt", 3);
		SIMPLE_NUMBERS_MAP.put("viert", 4);
		SIMPLE_NUMBERS_MAP.put("vier", 4);
		SIMPLE_NUMBERS_MAP.put("fünft", 5);
		SIMPLE_NUMBERS_MAP.put("fünf", 5);
		SIMPLE_NUMBERS_MAP.put("fuenft", 5);
		SIMPLE_NUMBERS_MAP.put("fuenf", 5);
		SIMPLE_NUMBERS_MAP.put("sechst", 6);
		SIMPLE_NUMBERS_MAP.put("sechs", 6);
		SIMPLE_NUMBERS_MAP.put("sieben", 7);
		SIMPLE_NUMBERS_MAP.put("siebt", 7);
		SIMPLE_NUMBERS_MAP.put("acht", 8);
		SIMPLE_NUMBERS_MAP.put("neunt", 9);
		SIMPLE_NUMBERS_MAP.put("neun", 9);
		SIMPLE_NUMBERS_MAP.put("zehnt", 10);
		SIMPLE_NUMBERS_MAP.put("zehn", 10);
		SIMPLE_NUMBERS_MAP.put("elft", 11);
		SIMPLE_NUMBERS_MAP.put("elf", 11);
		SIMPLE_NUMBERS_MAP.put("zwölft", 12);
		SIMPLE_NUMBERS_MAP.put("zwölf", 12);
		SIMPLE_NUMBERS_MAP.put("zwoelft", 12);
		SIMPLE_NUMBERS_MAP.put("zwoelf", 12);
		
		// initialize tens mapping
		TENS_MAP.put("zwanzig", 20);
		TENS_MAP.put("dreißig", 30);
		TENS_MAP.put("dreissig", 30);
		TENS_MAP.put("vierzig", 40);
		TENS_MAP.put("fünfzig", 50);
		TENS_MAP.put("fuenfzig", 50);
		TENS_MAP.put("sechzig", 60);
		TENS_MAP.put("siebzig", 70);
		TENS_MAP.put("achtzig", 80);
		TENS_MAP.put("neunzig", 90);
		
		// initialize scales mapping
		SCALES_MAP.put("hundert", 100);
		SCALES_MAP.put("tausend", 1000);
		SCALES_MAP.put("millionen", 1000000);
		SCALES_MAP.put("million", 1000000);
		SCALES_MAP.put("milliarden", 1000000000);
		SCALES_MAP.put("milliarde", 1000000000);
	}
	
	public static int parseToInteger(String numberWord) {
		if ((numberWord == null) || numberWord.isEmpty()) {
			throw new ParserException("Input is empty!");
		}
		
		numberWord = numberWord.trim();
		numberWord = numberWord.toLowerCase(Locale.GERMAN);
		
		// handle actual number
		try {
			return Integer.parseInt(numberWord);
		} catch (Exception e) {
		}
		
		// determine sign
		int sign = 1;
		if (numberWord.startsWith("minus")) {
			numberWord = numberWord.substring(5);
			sign = -1;
		}

		// handle trivial case
		if (numberWord.equals("null")) {
			return sign * 0;
		}
		
		// try to parse the number recursively
		List<Integer> results = new ArrayList<>();
		parseNumberRecursive(numberWord, 0, 0, results);
		if (results.isEmpty()) {
			throw new ParserException("Could not parse '" + numberWord + "' to number.");
		}
		
		return sign * results.get(0);
	}

	private static void parseNumberRecursive(String numberWord, 
			int sum, int currentValue, List<Integer> results) {
		if (numberWord.isEmpty()) {
			results.add(sum + currentValue);
			return;
		}
		
		// remove "und" if present
		if (numberWord.startsWith("und")) {
			numberWord = numberWord.substring(3);
		}
		
		// try to match scales first (as they have highest priority)
		for (Entry<String, Integer> scale : SCALES_MAP.entrySet()) {
			if (numberWord.startsWith(scale.getKey())) {
				String remaining = numberWord.substring(scale.getKey().length());
				
                Integer scaleValue = scale.getValue();
                boolean storePartialCalculation = scaleValue > 100;
                if (storePartialCalculation) {
                    if (currentValue == 0) {
                        sum += scaleValue;
                    } else {
                        sum += (currentValue * scaleValue);
                    }
                    currentValue = 0; // re-set current value
                } else {
                    if (currentValue == 0) {
                        currentValue = scaleValue;
                    } else {
                        currentValue *= scaleValue;
                    }
                }
				parseNumberRecursive(remaining, sum, currentValue, results);
			}
		}
		
		// try to match tens
		for (Entry<String, Integer> ten : TENS_MAP.entrySet()) {
			if (numberWord.startsWith(ten.getKey())) {
				String remaining = numberWord.substring(ten.getKey().length());
				parseNumberRecursive(remaining, sum, currentValue + ten.getValue(), results);
			}
		}
		
		// try to match simple numbers
		for (Entry<String, Integer> simple : SIMPLE_NUMBERS_MAP.entrySet()) {
			if (numberWord.startsWith(simple.getKey())) {
				String remaining = numberWord.substring(simple.getKey().length());
				parseNumberRecursive(remaining, sum, currentValue + simple.getValue(), results);
			}
		}
	}
	
	public static class ParserException extends RuntimeException {
	
		private static final long serialVersionUID = 1L;

		public ParserException(String message) {
			super(message);
		}
	}

}
