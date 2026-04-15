package org.jsapar.parse.cell;

import org.jsapar.model.BigDecimalCell;
import org.jsapar.text.Format;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Locale;

import static org.junit.Assert.assertEquals;

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


}