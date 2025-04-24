package com.valantic.fsa.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class ParserUtilsTest {

    @Test
    void testParseSimpleNumbers() {
        assertEquals(1, ParserUtils.parseToInteger("eins"));
        assertEquals(2, ParserUtils.parseToInteger("zwei"));
        assertEquals(3, ParserUtils.parseToInteger("drei"));
        assertEquals(4, ParserUtils.parseToInteger("vier"));
        assertEquals(5, ParserUtils.parseToInteger("fünf"));
        assertEquals(5, ParserUtils.parseToInteger("fuenf"));
        assertEquals(6, ParserUtils.parseToInteger("sechs"));
        assertEquals(7, ParserUtils.parseToInteger("sieben"));
        assertEquals(8, ParserUtils.parseToInteger("acht"));
        assertEquals(9, ParserUtils.parseToInteger("neun"));
        assertEquals(10, ParserUtils.parseToInteger("zehn"));
        assertEquals(11, ParserUtils.parseToInteger("elf"));
        assertEquals(12, ParserUtils.parseToInteger("zwölf"));
        assertEquals(12, ParserUtils.parseToInteger("zwoelf"));
    }

    @Test
    void testParseTens() {
        assertEquals(20, ParserUtils.parseToInteger("zwanzig"));
        assertEquals(30, ParserUtils.parseToInteger("dreißig"));
        assertEquals(30, ParserUtils.parseToInteger("dreissig"));
        assertEquals(40, ParserUtils.parseToInteger("vierzig"));
        assertEquals(50, ParserUtils.parseToInteger("fünfzig"));
        assertEquals(50, ParserUtils.parseToInteger("fuenfzig"));
        assertEquals(60, ParserUtils.parseToInteger("sechzig"));
        assertEquals(70, ParserUtils.parseToInteger("siebzig"));
        assertEquals(80, ParserUtils.parseToInteger("achtzig"));
        assertEquals(90, ParserUtils.parseToInteger("neunzig"));
    }

    @Test
    void testParseComplexNumbers() {
        assertEquals(21, ParserUtils.parseToInteger("einundzwanzig"));
        assertEquals(32, ParserUtils.parseToInteger("zweiunddreißig"));
        assertEquals(32, ParserUtils.parseToInteger("zweiunddreißig"));
        assertEquals(43, ParserUtils.parseToInteger("dreiundvierzig"));
        assertEquals(54, ParserUtils.parseToInteger("vierundfünfzig"));
        assertEquals(65, ParserUtils.parseToInteger("fünfundsechzig"));
        assertEquals(65, ParserUtils.parseToInteger("fuenfundsechzig"));
        assertEquals(76, ParserUtils.parseToInteger("sechsundsiebzig"));
        assertEquals(87, ParserUtils.parseToInteger("siebenundachtzig"));
        assertEquals(98, ParserUtils.parseToInteger("achtundneunzig"));
    }

    @Test
    void testParseHundreds() {
        assertEquals(100, ParserUtils.parseToInteger("hundert"));
        assertEquals(200, ParserUtils.parseToInteger("zweihundert"));
        assertEquals(300, ParserUtils.parseToInteger("dreihundert"));
        assertEquals(400, ParserUtils.parseToInteger("vierhundert"));
        assertEquals(500, ParserUtils.parseToInteger("fünfhundert"));
        assertEquals(600, ParserUtils.parseToInteger("sechshundert"));
        assertEquals(700, ParserUtils.parseToInteger("siebenhundert"));
        assertEquals(800, ParserUtils.parseToInteger("achthundert"));
        assertEquals(900, ParserUtils.parseToInteger("neunhundert"));
    }

    @Test
    void testParseThousands() {
        assertEquals(1000, ParserUtils.parseToInteger("tausend"));
        assertEquals(2000, ParserUtils.parseToInteger("zweitausend"));
        assertEquals(3000, ParserUtils.parseToInteger("dreitausend"));
        assertEquals(4000, ParserUtils.parseToInteger("viertausend"));
        assertEquals(5000, ParserUtils.parseToInteger("fünftausend"));
        assertEquals(6000, ParserUtils.parseToInteger("sechstausend"));
        assertEquals(7000, ParserUtils.parseToInteger("siebentausend"));
        assertEquals(8000, ParserUtils.parseToInteger("achttausend"));
        assertEquals(9000, ParserUtils.parseToInteger("neuntausend"));
    }

    @Test
    void testParseComplexNumbersWithHundredsAndThousands() {
        assertEquals(1001, ParserUtils.parseToInteger("tausendeins"));
        assertEquals(1100, ParserUtils.parseToInteger("tausendeinhundert"));
        assertEquals(1111, ParserUtils.parseToInteger("tausendeinhundertelf"));
        assertEquals(400066, ParserUtils.parseToInteger("vierhunderttausendsechsundsechzig"));
        assertEquals(123456, ParserUtils.parseToInteger("hundertdreiundzwanzigtausendvierhundertsechsundfünfzig"));
    }

    @Test
    void testParseZero() {
        assertEquals(0, ParserUtils.parseToInteger("null"));
    }

    @Test
    void testParseActualNumbers() {
        assertEquals(0, ParserUtils.parseToInteger("0"));
        assertEquals(-1, ParserUtils.parseToInteger("-1"));
        assertEquals(1100, ParserUtils.parseToInteger("1100"));
        assertEquals(400066, ParserUtils.parseToInteger("400066"));
        assertEquals(123456, ParserUtils.parseToInteger("123456"));
    }

    @Test
    void testParseNegativeNumbers() {
        assertEquals(-0, ParserUtils.parseToInteger("minusnull"));
        assertEquals(-1, ParserUtils.parseToInteger("minuseins"));
        assertEquals(-100, ParserUtils.parseToInteger("minushundert"));
        assertEquals(-1000, ParserUtils.parseToInteger("minustausend"));
        assertEquals(-400066, ParserUtils.parseToInteger("minusvierhunderttausendsechsundsechzig"));
    }    @Test
    void testLowerCaseNormalization() {
        assertEquals("hallo welt", ParserUtils.normalizeText("HALLO WELT"));
        assertEquals("hallo welt", ParserUtils.normalizeText("Hallo Welt"));
        assertEquals("hallo welt", ParserUtils.normalizeText("hallo welt"));
    }
    
    @Test
    void testMonthNormalization() {
        assertEquals("am 1. um 20:00", ParserUtils.normalizeText("am Januar um 20:00"));
        assertEquals("am 1. um 20:00", ParserUtils.normalizeText("am JAN um 20:00"));
        assertEquals("am 11.3. um 20:00", ParserUtils.normalizeText("am 11. März um 20:00"));
        assertEquals("am 12.3. um 20:00", ParserUtils.normalizeText("am 12. MAERZ um 20:00"));
        assertEquals("am 23.12. um 20:00", ParserUtils.normalizeText("am 23. Dezember um 20:00"));
        assertEquals("am 24.12. um 20:00", ParserUtils.normalizeText("am 24. DEZ um 20:00"));
    }
    
    @Test
    void testSimpleNumberNormalization() {
        assertEquals("fuer 1 person", ParserUtils.normalizeText("für eine Person"));
        assertEquals("fuer 1 person", ParserUtils.normalizeText("für EINE Person"));
        assertEquals("fuer 2 personen", ParserUtils.normalizeText("für zwei Personen"));
        assertEquals("fuer 3 personen", ParserUtils.normalizeText("für drei Personen"));
        assertEquals("fuer 4 personen", ParserUtils.normalizeText("für vier Personen"));
        assertEquals("fuer 5 personen", ParserUtils.normalizeText("für fünf Personen"));
        assertEquals("fuer 6 personen", ParserUtils.normalizeText("für sechs Personen"));
        assertEquals("fuer 7 personen", ParserUtils.normalizeText("für sieben Personen"));
        assertEquals("fuer 8 personen", ParserUtils.normalizeText("für acht Personen"));
        assertEquals("fuer 9 personen", ParserUtils.normalizeText("für neun Personen"));
        assertEquals("fuer 10 personen", ParserUtils.normalizeText("für zehn Personen"));
        assertEquals("fuer 11 personen", ParserUtils.normalizeText("für elf Personen"));
        assertEquals("fuer 12 personen", ParserUtils.normalizeText("für zwölf Personen"));
    }
    
    @Test
    void testTensNumberNormalization() {
        assertEquals("fuer 20 personen", ParserUtils.normalizeText("für zwanzig Personen"));
        assertEquals("fuer 30 personen", ParserUtils.normalizeText("für dreißig Personen"));
        assertEquals("fuer 40 personen", ParserUtils.normalizeText("für vierzig Personen"));
        assertEquals("fuer 50 personen", ParserUtils.normalizeText("für fünfzig Personen"));
        assertEquals("fuer 60 personen", ParserUtils.normalizeText("für sechzig Personen"));
        assertEquals("fuer 70 personen", ParserUtils.normalizeText("für siebzig Personen"));
        assertEquals("fuer 80 personen", ParserUtils.normalizeText("für achtzig Personen"));
        assertEquals("fuer 90 personen", ParserUtils.normalizeText("für neunzig Personen"));
    }
    
    @Test
    void testScaleNumberNormalization() {
        assertEquals("fuer 100 personen", ParserUtils.normalizeText("für hundert Personen"));
        assertEquals("fuer 1000 personen", ParserUtils.normalizeText("für tausend Personen"));
        assertEquals("fuer 1000000 personen", ParserUtils.normalizeText("für million Personen"));
        assertEquals("fuer 1000000 personen", ParserUtils.normalizeText("für millionen Personen"));
        assertEquals("fuer 1000000000 personen", ParserUtils.normalizeText("für milliarde Personen"));
        assertEquals("fuer 1000000000 personen", ParserUtils.normalizeText("für milliarden Personen"));
    }
    
    @Test
    void testCombinedNumberNormalization() {
        assertEquals("fuer 21 personen", ParserUtils.normalizeText("für einundzwanzig Personen"));
        assertEquals("fuer 32 personen", ParserUtils.normalizeText("für zweiunddreißig Personen"));
        assertEquals("fuer 45 personen", ParserUtils.normalizeText("für fünfundvierzig Personen"));
        assertEquals("fuer 123 personen", ParserUtils.normalizeText("für hundertdreiundzwanzig Personen"));
        assertEquals("fuer 2000 personen", ParserUtils.normalizeText("für zweitausend Personen"));
    }
    
    @Test
    void testSpecialCases() {
        assertEquals("1", ParserUtils.normalizeText("alleine"));
        assertEquals("1", ParserUtils.normalizeText("allein"));
        assertEquals("zu 2", ParserUtils.normalizeText("zu zweit"));
        assertEquals("zu 3", ParserUtils.normalizeText("zu dritt"));
        assertEquals("zu 4", ParserUtils.normalizeText("zu viert"));
        assertEquals("zu 5", ParserUtils.normalizeText("zu fünft"));
        assertEquals("zu 6", ParserUtils.normalizeText("zu sechst"));
        assertEquals("zu 7", ParserUtils.normalizeText("zu siebt"));
        assertEquals("zu 8", ParserUtils.normalizeText("zu acht"));
        assertEquals("zu 9", ParserUtils.normalizeText("zu neunt"));
        assertEquals("zu 10", ParserUtils.normalizeText("zu zehnt"));
        assertEquals("zu 11", ParserUtils.normalizeText("zu elft"));
        assertEquals("zu 12", ParserUtils.normalizeText("zu zwölft"));
    }
    
    @Test
    void testComplexExamples() {
        assertEquals("hallo, bitte fuer 2 personen 1 tisch am 1. um 20:00 uhr, vielen dank klaus mueller",
                ParserUtils.normalizeText("Hallo, bitte für zwei Personen 1 Tisch am Januar um 20:00 Uhr, Vielen Dank Klaus Müller"));
        
        assertEquals("sehr geehrte damen herren, wir wuerden gern uebermorgen um 9:45 uhr mit 6 leuten zum brunch kommen, mit freundlichen gruessen maria meier",
                ParserUtils.normalizeText("Sehr geehrte Damen Herren, wir würden gern übermorgen um 9:45 Uhr mit sechs Leuten zum Brunch kommen, Mit freundlichen Grüßen Maria Meier"));
        
        assertEquals("guten tag, 1 tisch fuer 8 mann fuer naechste woche 9 uhr abends, gruss franz schulze",
                ParserUtils.normalizeText("Guten Tag, einen Tisch für acht Mann für nächste Woche 9 Uhr abends, Gruß Franz Schulze"));
    }
    
    @Test
    void testStripQuotes() {
        assertEquals("foo", ParserUtils.stripQuotes("'foo'"));
        assertEquals("foo", ParserUtils.stripQuotes("\"foo\""));
        assertEquals("foo", ParserUtils.stripQuotes("\'foo\'"));
        assertEquals("foo", ParserUtils.stripQuotes("\"'\'foo\''\""));
    }

    @Test
    void testParseInvalidInputs() {
        assertThrows(ParserUtils.ParserException.class, () -> ParserUtils.parseToInteger(null));
        assertThrows(ParserUtils.ParserException.class, () -> ParserUtils.parseToInteger("invalid"));
        assertThrows(ParserUtils.ParserException.class, () -> ParserUtils.parseToInteger(""));
    }
} 