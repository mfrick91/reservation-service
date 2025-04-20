package com.valantic.fsa.parser;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class ParserUtils {

	private static Map<String, Integer> wordsToSimpleNumbers;
	private static Map<String, Integer> wordsToTens;
	private static Map<String, Integer> wordsToScales;
	
	static  {
		// units
		wordsToSimpleNumbers = new HashMap<>();
		wordsToSimpleNumbers.put("eins", 1);
		wordsToSimpleNumbers.put("ein", 1);
		wordsToSimpleNumbers.put("eine", 1);
		wordsToSimpleNumbers.put("einen", 1);
		wordsToSimpleNumbers.put("zwei", 2);
		wordsToSimpleNumbers.put("drei", 3);
		wordsToSimpleNumbers.put("vier", 4);
		wordsToSimpleNumbers.put("fünf", 5);
		wordsToSimpleNumbers.put("fuenf", 5);
		wordsToSimpleNumbers.put("sechs", 6);
		wordsToSimpleNumbers.put("sieben", 7);
		wordsToSimpleNumbers.put("acht", 8);
		wordsToSimpleNumbers.put("neun", 9);
		wordsToSimpleNumbers.put("zehn", 10);
		wordsToSimpleNumbers.put("elf", 11);
		wordsToSimpleNumbers.put("zwölf", 12);
		wordsToSimpleNumbers.put("zwoelf", 12);
		
		// tens
		wordsToTens = new HashMap<>();
		wordsToTens.put("zwanzig", 20);
		wordsToTens.put("dreißig", 30);
		wordsToTens.put("dreissig", 30);
		wordsToTens.put("vierzig", 40);
		wordsToTens.put("fünfzig", 50);
		wordsToTens.put("fuenfzig", 50);
		wordsToTens.put("sechzig", 60);
		wordsToTens.put("siebzig", 70);
		wordsToTens.put("achtzig", 80);
		wordsToTens.put("neunzig", 90);
		
		// scales 
		wordsToScales = new HashMap<>();
		wordsToScales.put("hundert", 100);
		wordsToScales.put("tausend", 1000);
		wordsToScales.put("million", 1000000);
		wordsToScales.put("millionen", 1000000);
		wordsToScales.put("milliarde", 1000000000);
		wordsToScales.put("milliarden", 1000000000);
	}
	
	public static int parseToInteger(String numberWord) {
		if ((numberWord == null) || numberWord.isEmpty()) {
			throw new ParserException("Input is empty!");
		}
		
		numberWord = numberWord.trim();
		numberWord = numberWord.toLowerCase();
		
		// handle a actual number
		try {
			return Integer.parseInt(numberWord);
		} catch (Exception e) {
		}
		
		// determine sign
		int sign = 1;
		if (numberWord.startsWith("minus")) {
			numberWord = numberWord.substring(5, numberWord.length());
			sign = -1;
		}

		// handle trivial case
		if (numberWord.equals("null")) {
			return sign * 0;
		}
		
		// calculate sum
		int sum = 0;
		int currentValue = 0;
		outer: 
		while (!numberWord.isEmpty()) {
			// replace "und" one at a time to avoid cases like "h-und-ert"
			if (numberWord.startsWith("und")) {
				numberWord = numberWord.substring(3, numberWord.length());
			}

			// handle scales
			for (Entry<String, Integer> wordToScale : wordsToScales.entrySet()) {
				String number = wordToScale.getKey();
				if (numberWord.startsWith(number)) {
					numberWord = numberWord.substring(number.length(), numberWord.length());

					Integer scale = wordToScale.getValue();
					boolean storePartialCalculation = scale > 100;
					if (storePartialCalculation) {
						if (currentValue == 0) {
							sum += scale;
						} else {
							sum += (currentValue * scale);
						}
						currentValue = 0; // re-set current value
					} else {
						if (currentValue == 0) {
							currentValue = scale;
						} else {
							currentValue *= scale;
						}
					}
					continue outer;
				}
			}
			
			// handles tens first to avoid misidentifications, e.g. "fünfzig" vs. "fünf
			for (Entry<String, Integer> wordToTen : wordsToTens.entrySet()) {
				String number = wordToTen.getKey();
				if (numberWord.startsWith(number)) {
					numberWord = numberWord.substring(number.length(), numberWord.length());
					currentValue += wordToTen.getValue();
					continue outer;
				}
			}
			
			// handle simple numbers
			for (Entry<String, Integer> wordToSimpleNumber : wordsToSimpleNumbers.entrySet()) {
				String number = wordToSimpleNumber.getKey();
				if (numberWord.startsWith(number)) {
					numberWord = numberWord.substring(number.length(), numberWord.length());
					currentValue += wordToSimpleNumber.getValue();
					continue outer;
				}
			}
			
			throw new ParserException("Could not parse '" + numberWord + "' to number.");
		}
		return sign * (sum + currentValue);
	}
	
	public static class ParserException extends RuntimeException {
	
		private static final long serialVersionUID = 1L;

		public ParserException(String message) {
			super(message);
		}
	}

}
