package com.valantic.fsa.parser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class for parsing reservation requests.
 * 
 * @author M. Frick
 */
public class ParserUtils {

	/**
	 * Map of weekdays (e.g., "montag", "mo") to their integer values.
	 */
	private static final Map<String, Integer> WEEKDAYS_MAP = new LinkedHashMap<>();

	/**
	 * Map of months (e.g., "januar", "jan") to their numeric representations.
	 */
	private static final Map<String, String> MONTH_MAP = new LinkedHashMap<>();

	/**
	 * Map of simple numbers (e.g., "eins", "zwei", "drei") to their integer values.
	 */	
	private static final Map<String, Integer> SIMPLE_NUMBERS_MAP = new LinkedHashMap<>();

	/**
	 * Map of tens (e.g., "zwanzig", "dreißig") to their integer values.
	 */
	private static final Map<String, Integer> TENS_MAP = new LinkedHashMap<>();

	/**
	 * Map of scales (e.g., "hundert", "tausend") to their integer values.
	 */	
	private static final Map<String, Integer> SCALES_MAP = new LinkedHashMap<>();

	static {
		// initialize weekday mapping
		WEEKDAYS_MAP.put("montag", 1);
		WEEKDAYS_MAP.put("mo", 1);
		WEEKDAYS_MAP.put("dienstag", 2);
		WEEKDAYS_MAP.put("di", 2);
		WEEKDAYS_MAP.put("mittwoch", 3);
		WEEKDAYS_MAP.put("mi", 3);
		WEEKDAYS_MAP.put("donnerstag", 4);
		WEEKDAYS_MAP.put("do", 4);
		WEEKDAYS_MAP.put("freitag", 5);
		WEEKDAYS_MAP.put("fr", 5);
		WEEKDAYS_MAP.put("samstag", 6);
		WEEKDAYS_MAP.put("sa", 6);
		WEEKDAYS_MAP.put("sonntag", 7);
		WEEKDAYS_MAP.put("so", 7);
		
		// initialize month mapping
		MONTH_MAP.put("januar", "1.");
		MONTH_MAP.put("jan", "1.");
		MONTH_MAP.put("februar", "2.");
		MONTH_MAP.put("feb", "2.");
		MONTH_MAP.put("maerz", "3.");
		MONTH_MAP.put("maer", "3.");
		MONTH_MAP.put("april", "4.");
		MONTH_MAP.put("apr", "4.");
		MONTH_MAP.put("mai", "5.");
		MONTH_MAP.put("juni", "6.");
		MONTH_MAP.put("jun", "6.");
		MONTH_MAP.put("juli", "7.");
		MONTH_MAP.put("jul", "7.");
		MONTH_MAP.put("august", "8.");
		MONTH_MAP.put("aug", "8.");
		MONTH_MAP.put("september", "9.");
		MONTH_MAP.put("sep", "9.");
		MONTH_MAP.put("oktober", "10.");
		MONTH_MAP.put("okt", "10.");
		MONTH_MAP.put("november", "11.");
		MONTH_MAP.put("nov", "11.");
		MONTH_MAP.put("dezember", "12.");
		MONTH_MAP.put("dez", "12.");
		
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

	/**
	 * Converts a weekday string to its corresponding integer value.
	 * 
	 * @param weekday the weekday string to convert
	 * @return the integer value of the weekday
	 */
	public static int weekdayToInteger(String weekday) {
		return WEEKDAYS_MAP.get(weekday);
	}

	/**
	 * Returns an unmodifiable collection of all weekdays.
	 * 
	 * @return a collection of all weekdays
	 */
	public static Collection<String> weekdays() {
		return WEEKDAYS_MAP.keySet();
	}

	/**
	 * Parses a number word to its corresponding integer value.
	 * 
	 * @param numberWord the number word to parse
	 * @return the integer value of the number word
	 */
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

	/**
	 * Normalizes a text string by converting it to lowercase, removing special
	 * characters, and replacing month names with their numeric representations.
	 * 
	 * @param text the text to normalize
	 * @return the normalized text
	 */
	public static String normalizeText(String text) {
		if (text == null) {
			return "";
		}

		// convert to lower case
		String normalizedText = text.trim().toLowerCase(Locale.GERMAN);

		// remove special characters
		normalizedText = normalizedText
				.replace("ä", "ae").replace("ü", "ue")
    			.replace("ö", "oe").replace("ß", "ss"); 
		
		// replace month names with numbers
		for (Map.Entry<String, String> entry : MONTH_MAP.entrySet()) {
			Pattern pattern = Pattern.compile("(\\b" + entry.getKey() + "\\b)|(\\.\\s(" + entry.getKey() + "))");
			Matcher matcher = pattern.matcher(normalizedText);
			StringBuilder sb = new StringBuilder();
			if (matcher.find()) {
				if (matcher.group().startsWith(".")) {
					matcher.appendReplacement(sb, "." + entry.getValue());
				} else {
					matcher.appendReplacement(sb, entry.getValue());
				}
			}
			matcher.appendTail(sb);
			normalizedText = sb.toString();
		}
		
		// replace number words with numbers
		Pattern wordPattern = Pattern.compile("\\b([a-z]+)\\b");
		Matcher matcher = wordPattern.matcher(normalizedText);
		StringBuilder sb = new StringBuilder();
		while (matcher.find()) {
			String word = matcher.group();
			// try number parsing
			try {
				int number = parseToInteger(word);
				matcher.appendReplacement(sb, String.valueOf(number));
			} catch (ParserException e) {
			}
		}
		matcher.appendTail(sb);
		
		return sb.toString();
	}

	/**
	 * Strips quotes from the beginning and end of a text string.
	 * 
	 * @param text the text to strip quotes from
	 * @return the text with quotes removed
	 */
	public static String stripQuotes(String text) {
		while (!text.isEmpty() 
				&& ((text.charAt(0) == '"') 
						|| (text.charAt(0) == '\"') 
						|| (text.charAt(0) == '\''))) {
			text = text.substring(1);
		}
		while (!text.isEmpty() 
				&& ((text.charAt(text.length() - 1) == '"') 
				|| (text.charAt(text.length() - 1) == '\"')
				|| (text.charAt(text.length() - 1) == '\''))) {
			text = text.substring(0, text.length() - 1);
		}
		return text;
	}

	/**
	 * Exception thrown when parsing fails.
	 * 
	 * @author M. Frick
	 */
	public static class ParserException extends RuntimeException {

		private static final long serialVersionUID = -4303036383556218146L;

		/**
		 * Constructs a new ParserException with the specified message.
		 * 
		 * @param message the error message
		 */
		public ParserException(String message) {
			super(message);
		}
	}

}
