package org.jsapar.text;

import org.junit.Test;

import java.text.ParseException;

import static org.junit.Assert.*;

public class EnumFormatTest {
    private enum TestEnum{
        A, BB, CCC, DDDD;
    }

    @Test
    public void format() {
        EnumFormat enumFormat = new EnumFormat(TestEnum.class);
        assertEquals("A", enumFormat.format(TestEnum.A));
        assertEquals("BB", enumFormat.format(TestEnum.BB));
        assertEquals("CCC", enumFormat.format(TestEnum.CCC));
        assertEquals("DDDD", enumFormat.format(TestEnum.DDDD));
    }

    @Test
    public void format_object() {
        EnumFormat enumFormat = new EnumFormat(TestEnum.class);
        assertEquals("A", enumFormat.format((Object)TestEnum.A));
        assertEquals("BB", enumFormat.format((Object)TestEnum.BB));
        assertEquals("CCC", enumFormat.format((Object)TestEnum.CCC));
        assertEquals("DDDD", enumFormat.format((Object)TestEnum.DDDD));
    }

    @Test(expected = ClassCastException.class)
    public void format_object_invalid_class() {
        EnumFormat enumFormat = new EnumFormat(TestEnum.class);
        enumFormat.format((Object)this);
        fail();
    }

    @Test
    public void parseObject() throws ParseException {
        EnumFormat enumFormat = new EnumFormat(TestEnum.class);
        assertEquals(TestEnum.A, enumFormat.parseObject("A"));
        assertEquals(TestEnum.BB, enumFormat.parseObject("BB"));
        assertEquals(TestEnum.CCC, enumFormat.parseObject("CCC"));
    }

    @Test(expected = ParseException.class)
    public void parseObject_fail() throws ParseException {
        EnumFormat enumFormat = new EnumFormat(TestEnum.class);
        assertEquals(TestEnum.A, enumFormat.parseObject("Q"));
    }

    @Test
    public void parse() throws ParseException {
        EnumFormat enumFormat = new EnumFormat(TestEnum.class);
        assertEquals(TestEnum.A, enumFormat.parse("A"));
        assertEquals(TestEnum.BB, enumFormat.parse("BB"));
        assertEquals(TestEnum.CCC, enumFormat.parse("CCC"));
    }
}