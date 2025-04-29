package com.valantic.fsa.util;

import java.util.regex.Pattern;

public class Patterns {

	/**
	 * Array of words that can indicate a number of people in a reservation.
	 */
	private static final String[] PEOPLE = new String[] { 
		"person", "personen", "leute", "leuten", "freund", "freunde", "freunden", "kind", "kinder",
		"herr", "herren", "mann", "maenner", "junge", "jungen",
		"dame", "damen", "frau", "frauen", "maedchen", 
		"gaeste", "gaesten" };
	
	// name pattern
	public static final Pattern NAME = Pattern.compile(
			"(grüßen|grüssen|grueßen|gruessen|grüße|grueße|grüsse|gruesse|gruß|gruss|dank|danke).*?([a-zäüöß]+\\s[a-zäüöß]+)");

	// date patterns
	public static final Pattern DATE = Pattern.compile(
			"(\\d{1,2}[.]\\d{1,2}[.]\\d{2,4})");
	
	public static final Pattern PARTIAL_DATE = Pattern.compile(
			"(\\d{1,2}[.]\\d{1,2})(|[.])");
	
	public static final Pattern RELATIVE_DATE_IN = Pattern.compile(
			"in\\s+(\\d+)\\s+(tagen|wochen|monaten|jahren)");
	
	public static final Pattern RELATIVE_DATE_NEXT = Pattern.compile(
			"(naechste|naechsten|naechstes|kommende|kommenden|kommendes)\\s+(tage|woche|monat|jahr)");
	
	public static final Pattern WEEKDAY = Pattern.compile(
			"(|naechste|naechsten|naechstes|kommende|kommenden|kommendes)\\s+(" + String.join("|", ParserUtils.weekdays()) + ")");

	// time patterns
	public static final Pattern TIME = Pattern.compile(
			"(\\d{1,2}:\\d{2})|(\\d{1,2})\\s*uhr");
	
	public static final Pattern TIME_RANGE = Pattern.compile(
			"zwischen\\s+(\\d{1,2}:\\d{2}|\\d{1,2})\\s*(und|bis|-)\\s*(\\d{1,2}:\\d{2}|\\d{1,2})\\s*uhr");
	
	// people count patterns
	public static final Pattern SIMPLE_PEOPLE_COUNT = Pattern.compile(
			"(\\d+)\\s+(" + String.join("|", PEOPLE) + ")");
	
	public static final Pattern PEOPLE_COUNT = Pattern.compile(
			"(zu|sind|fuer|mindestens|bis\\s*zu|nicht\\s*mehr\\s*als)\\s+(\\d+)(\\s+" + String.join("|", PEOPLE) + ")?");
	
	public static final Pattern PEOPLE_RANGE = Pattern.compile(
			"(zwischen|mit)\\s+(\\d+)\\s*(und|bis|-)\\s*(\\d+)\\s+(" + String.join("|", PEOPLE) + ")");
}
