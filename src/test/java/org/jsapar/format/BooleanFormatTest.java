/**
 * 
 */
package org.jsapar.format;

import static org.junit.Assert.*;

import java.text.ParseException;
import java.text.ParsePosition;

import org.jsapar.format.BooleanFormat;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author stejon0
 *
 */
public class BooleanFormatTest {

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }



    /**
     * Test method for {@link org.jsapar.format.BooleanFormat#format(java.lang.Object, java.lang.StringBuffer, java.text.FieldPosition)}.
     */
    @Test
    public void testFormatObjectStringBufferFieldPosition() {
        BooleanFormat f = new BooleanFormat("ja", "nej");
        assertEquals("nej", f.format(Boolean.FALSE));
        assertEquals("ja", f.format(Boolean.TRUE));
    }

    /**
     * Test method for {@link org.jsapar.format.BooleanFormat#format(boolean)}.
     */
    @Test
    public void testFormatBoolean() {
        BooleanFormat f = new BooleanFormat("ja", "nej");
        assertEquals("nej", f.format(false));
        assertEquals("ja", f.format(true));
    }

    /**
     * Test method for {@link org.jsapar.format.BooleanFormat#parseObject(java.lang.String, java.text.ParsePosition)}.
     * @throws ParseException 
     */
    @Test
    public void testParseObjectString() throws ParseException {
        BooleanFormat f = new BooleanFormat("ja", "nej");
        assertEquals(Boolean.TRUE, f.parseObject("ja"));
        assertEquals(Boolean.TRUE, f.parseObject("JA"));
        assertEquals(Boolean.FALSE, f.parseObject("nej"));
        assertEquals(Boolean.FALSE, f.parseObject("NEJ"));
    }

    /**
     * Test method for {@link org.jsapar.format.BooleanFormat#parseObject(java.lang.String, java.text.ParsePosition)}.
     * @throws ParseException 
     */
    @Test
    public void testParseObjectStringParsePosition() throws ParseException {
        BooleanFormat f = new BooleanFormat("ja", "nej");
        assertEquals(Boolean.TRUE, f.parseObject("   ja", new ParsePosition(3)));
        
        ParsePosition pos = new ParsePosition(3);
        assertEquals(Boolean.TRUE, f.parseObject("   JA  ", pos));
        assertEquals(5, pos.getIndex());
        
        assertEquals(Boolean.FALSE, f.parseObject("   nej ", new ParsePosition(3)));
        assertEquals(Boolean.FALSE, f.parseObject("   NEJ", new ParsePosition(3)));
    }
    
    /**
     * Test method for {@link org.jsapar.format.BooleanFormat#parse(java.lang.String)}.
     */
    @Test
    public void testParse() {
        BooleanFormat f = new BooleanFormat("ja", "nej");
        assertTrue(f.parse("ja"));
        assertTrue(f.parse("JA"));
        assertFalse(f.parse("nej"));
        assertFalse(f.parse("NEJ"));
    }

}
