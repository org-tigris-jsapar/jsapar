package org.jsapar.model;

import org.junit.Test;

import java.math.BigDecimal;
import java.math.BigInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class BigDecimalCellTest {

    @Test
    public void testGetSetBigDecimalValue() {
        BigDecimalCell cell = new BigDecimalCell("test", BigDecimal.ZERO);
        assertEquals("test", cell.getName());
        assertEquals(BigDecimal.ZERO, cell.getBigDecimalValue());
        assertEquals(BigInteger.ZERO, cell.getBigIntegerValue());

    }


    @Test
    public void testGetBigIntegerValue() {
        BigDecimalCell cell = new BigDecimalCell("test", BigInteger.ZERO);
        assertEquals("test", cell.getName());
        assertEquals(BigDecimal.ZERO, cell.getBigDecimalValue());
        assertEquals(BigInteger.ZERO, cell.getBigIntegerValue());

    }


    @Test
    public void testCompareValueTo() {
        assertTrue( new BigDecimalCell("test", BigDecimal.ZERO).compareValueTo(new BigDecimalCell("test", BigDecimal.ONE)) < 0) ;
        assertTrue( new BigDecimalCell("test", BigDecimal.ONE).compareValueTo(new BigDecimalCell("test", BigDecimal.ZERO)) > 0) ;
        assertEquals(0, new BigDecimalCell("test", BigDecimal.ONE).compareValueTo(new BigDecimalCell("test", BigDecimal.ONE)));

    }
}