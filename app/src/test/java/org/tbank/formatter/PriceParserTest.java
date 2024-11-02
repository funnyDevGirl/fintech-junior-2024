package org.tbank.formatter;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class PriceParserTest {

    @Test
    void testParsePrice_WithSpaces() {
        String input = "Пример текста с числом 1 234 и другими словами";

        BigDecimal expectedNumber = new BigDecimal("1234.00");
        BigDecimal actualNumber = PriceParser.parsePrice(input);

        assertEquals(expectedNumber, actualNumber);
    }

    @Test
    void testParsePrice_NoNumber() {
        String input = "Нет чисел в этом тексте";

        BigDecimal actualNumber = PriceParser.parsePrice(input);

        assertEquals(BigDecimal.ZERO, actualNumber);
    }

    @Test
    void testParsePrice_EmptyInput() {
        String input = "";

        BigDecimal actualNumber = PriceParser.parsePrice(input);

        assertEquals(BigDecimal.ZERO, actualNumber);
    }

    @Test
    void testParsePrice_PriceWithSigh() {
        String input = "Цена - 1 234 RUB";

        BigDecimal expectedNumber = new BigDecimal("1234.00");
        BigDecimal actualNumber = PriceParser.parsePrice(input);

        assertEquals(expectedNumber, actualNumber);
    }
}