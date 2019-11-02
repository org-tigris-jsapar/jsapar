package org.jsapar.utils.text;

import org.jsapar.text.ImpliedDecimalFormat;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.BigInteger;

import static org.junit.Assert.*;

public class ImpliedDecimalFormatTest {

    @Test
    public void format() {
        ImpliedDecimalFormat format = new ImpliedDecimalFormat(2);
        assertEquals("0", format.format(0));
        assertEquals("100", format.format(1L));
        assertEquals("314", format.format(new BigDecimal(3.14)));
        assertEquals("100", format.format(new BigInteger("1")));
        assertEquals("314", format.format(3.14D));
        assertEquals("500", format.format("5"));
    }

    @Test
    public void parse() {
        ImpliedDecimalFormat format = new ImpliedDecimalFormat(2);
        assertEquals(new BigDecimal("0.00"), format.parse("0"));
        assertEquals(new BigDecimal("1.00"), format.parse("100"));
        assertEquals(new BigDecimal("3.14"), format.parse("314"));
        assertEquals(new BigDecimal("4711.00"), format.parse("471100"));
    }
}