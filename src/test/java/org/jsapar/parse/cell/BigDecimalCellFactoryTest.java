package org.jsapar.parse.cell;

import org.jsapar.model.BigDecimalCell;
import org.jsapar.text.Format;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BigDecimalCellFactoryTest {

    final BigDecimalCellFactory cellFactory = new BigDecimalCellFactory();

    @Test
    public void testSetValue() throws Exception {
        BigDecimalCell cell;
        @SuppressWarnings("unchecked")
        Format<Number> format = (Format<Number>)(Format<?>) Format.ofDecimalInstance("#.#",Locale.GERMAN);
        cell = (BigDecimalCell) cellFactory.makeCell("test", "3,14", format);
        assertEquals(new BigDecimal("3.14"), cell.getValue());
    }

    @Test
    public void testNaN_isParseError() {
        Format<Number> format = cellFactory.makeFormat(Locale.US);
        assertThrows(ParseException.class, () -> cellFactory.makeCell("test", "NaN", format));
        assertThrows(ParseException.class, () -> cellFactory.makeCell("test", "\u221E", format));
        assertThrows(ParseException.class, () -> cellFactory.makeCell("test", "-\u221E", format));
    }

    @Test
    public void testCustomFormatReturningOtherNumberTypes() throws ParseException {
        assertEquals(new BigDecimal("2.5"), cellFactory.makeCell("test", "x", formatReturning(2.5d)).getValue());
        assertEquals(new BigDecimal("42"), cellFactory.makeCell("test", "x", formatReturning(42L)).getValue());
        assertEquals(new BigDecimal("7"), cellFactory.makeCell("test", "x", formatReturning(java.math.BigInteger.valueOf(7))).getValue());
        assertThrows(ParseException.class, () -> cellFactory.makeCell("test", "x", formatReturning(Double.NaN)));
    }

    private static Format<Number> formatReturning(Number value) {
        return new Format<>() {
            @Override
            public org.jsapar.model.CellType cellType() {
                return org.jsapar.model.CellType.DECIMAL;
            }

            @Override
            public Number parse(String stringValue) {
                return value;
            }

            @Override
            public String format(Object o) {
                return String.valueOf(o);
            }
        };
    }
}
