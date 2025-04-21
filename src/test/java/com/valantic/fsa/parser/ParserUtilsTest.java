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
    }

    @Test
    void testParseInvalidInputs() {
        assertThrows(ParserUtils.ParserException.class, () -> ParserUtils.parseToInteger(null));
        assertThrows(ParserUtils.ParserException.class, () -> ParserUtils.parseToInteger("invalid"));
        assertThrows(ParserUtils.ParserException.class, () -> ParserUtils.parseToInteger(""));
    }
} 