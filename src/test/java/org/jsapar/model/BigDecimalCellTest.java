package org.jsapar.model;

import org.junit.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class BigDecimalCellTest {

    @Test
    public void testGetSetBigDecimalValue() throws Exception {
        BigDecimalCell cell = new BigDecimalCell("test", BigDecimal.ZERO);
        assertEquals("test", cell.getName());
        assertEquals(BigDecimal.ZERO, cell.getBigDecimalValue());
        assertEquals(BigInteger.ZERO, cell.getBigIntegerValue());
        cell.setBigDecimalValue(BigDecimal.ONE);
        assertEquals(BigDecimal.ONE, cell.getBigDecimalValue());
        assertEquals(BigInteger.ONE, cell.getBigIntegerValue());

    }


    @Test
    public void testGetBigIntegerValue() throws Exception {
        BigDecimalCell cell = new BigDecimalCell("test", BigInteger.ZERO);
        assertEquals("test", cell.getName());
        assertEquals(BigDecimal.ZERO, cell.getBigDecimalValue());
        assertEquals(BigInteger.ZERO, cell.getBigIntegerValue());
        cell.setBigIntegerValue(BigInteger.ONE);
        assertEquals(BigDecimal.ONE, cell.getBigDecimalValue());
        assertEquals(BigInteger.ONE, cell.getBigIntegerValue());

    }

    @Test
    public void testSetValue() throws Exception {
        BigDecimalCell cell = new BigDecimalCell("test", BigDecimal.ZERO);
        DecimalFormat format = new DecimalFormat("#.#", DecimalFormatSymbols.getInstance(Locale.GERMAN));
        format.setParseBigDecimal(true);
        cell.setValue("3,14", format);
        assertEquals(new BigDecimal("3.14"), cell.getBigDecimalValue());
    }

    @Test
    public void testCompareValueTo() throws Exception {
        assertTrue( new BigDecimalCell("test", BigDecimal.ZERO).compareValueTo(new BigDecimalCell("test", BigDecimal.ONE)) < 0) ;
        assertTrue( new BigDecimalCell("test", BigDecimal.ONE).compareValueTo(new BigDecimalCell("test", BigDecimal.ZERO)) > 0) ;
        assertTrue( new BigDecimalCell("test", BigDecimal.ONE).compareValueTo(new BigDecimalCell("test", BigDecimal.ONE)) == 0) ;

    }
}