package org.jsapar.parse.cell;

import org.jsapar.model.BigDecimalCell;
import org.jsapar.text.Format;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Locale;

import static org.junit.Assert.assertEquals;

public class BigDecimalCellFactoryTest {

    BigDecimalCellFactory cellFactory = new BigDecimalCellFactory();

    @Test
    public void testSetValue() throws Exception {
        BigDecimalCell cell;
        Format format = Format.ofDecimalInstance("#.#",Locale.GERMAN);
        cell = (BigDecimalCell) cellFactory.makeCell("test", "3,14", format);
        assertEquals(new BigDecimal("3.14"), cell.getValue());
    }


}