package org.jsapar.text;

import org.jsapar.text.BooleanFormat;
import org.junit.Test;

import java.text.ParseException;
import java.text.ParsePosition;

import static org.junit.Assert.*;

public class BooleanFormatTest {


    @Test
    public void testJavadocSample() throws ParseException {
        BooleanFormat format = new BooleanFormat("Y", "N", true);
        assert format.format(true).equals("Y");
        assert format.format(false).equals("N");
        assert format.parse("Y");
        assert !format.parse("N");
    }

    /**
     * Test method for {@link BooleanFormat#format(java.lang.Object)}.
     */
    @Test
    public void testFormatObjectStringBufferFieldPosition() {
        BooleanFormat f = new BooleanFormat("ja", "nej", true);
        assertEquals("nej", f.format(Boolean.FALSE));
        assertEquals("ja", f.format(Boolean.TRUE));
    }

    @Test
    public void testFormatDefault() {
        BooleanFormat f = new BooleanFormat(true);
        assertEquals("false", f.format(Boolean.FALSE));
        assertEquals("true", f.format(Boolean.TRUE));
    }

    /**
     * Test method for {@link BooleanFormat#format(java.lang.Object)}.
     */
    @Test
    public void testFormatObjectStringBufferFieldPosition_empty() {
        BooleanFormat f = new BooleanFormat("ja", "", true);
        assertEquals("", f.format(Boolean.FALSE));
        assertEquals("ja", f.format(Boolean.TRUE));
    }

    /**
     * Test method for {@link BooleanFormat#format(boolean)}.
     */
    @Test
    public void testFormatBoolean() {
        BooleanFormat f = new BooleanFormat("ja", "nej", true);
        assertEquals("nej", f.format(false));
        assertEquals("ja", f.format(true));
    }

    @Test
    public void testParseDefault() throws ParseException {
        BooleanFormat f = new BooleanFormat(true);
        assertEquals(Boolean.TRUE, f.parse("true"));
        assertEquals(Boolean.TRUE, f.parse("on"));
        assertEquals(Boolean.TRUE, f.parse("1"));
        assertEquals(Boolean.TRUE, f.parse("ON"));
        assertEquals(Boolean.TRUE, f.parse("yes"));
        assertEquals(Boolean.TRUE, f.parse("y"));
        assertEquals(Boolean.TRUE, f.parse("Y"));
        assertEquals(Boolean.FALSE, f.parse("false"));
        assertEquals(Boolean.FALSE, f.parse("off"));
        assertEquals(Boolean.FALSE, f.parse("0"));
        assertEquals(Boolean.FALSE, f.parse("OFF"));
        assertEquals(Boolean.FALSE, f.parse("NO"));
        assertEquals(Boolean.FALSE, f.parse("N"));
        assertEquals(Boolean.FALSE, f.parse("n"));
    }

    @Test(expected = ParseException.class)
    public void testParseFailed() throws ParseException {
        BooleanFormat f = new BooleanFormat("true", "false", true);
        assertEquals(null, f.parse("something different"));
    }

    /**
     * Test method for {@link BooleanFormat#parse(java.lang.String)}.
     */
    @Test
    public void testParseObjectString() throws ParseException {
        BooleanFormat f = new BooleanFormat("ja", "nej", true);
        assertEquals(Boolean.TRUE, f.parse("ja"));
        assertEquals(Boolean.TRUE, f.parse("JA"));
        assertEquals(Boolean.FALSE, f.parse("nej"));
        assertEquals(Boolean.FALSE, f.parse("NEJ"));
    }


    /**
     * Test method for {@link BooleanFormat#parse(java.lang.String)}.
     */
    @Test
    public void testParse() throws ParseException {
        BooleanFormat f = new BooleanFormat("ja", "nej", true);
        assertTrue(f.parse("ja"));
        assertTrue(f.parse("JA"));
        assertFalse(f.parse("nej"));
        assertFalse(f.parse("NEJ"));
    }

}
