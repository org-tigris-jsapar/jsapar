package org.jsapar.text;

import org.jsapar.text.EnumFormat;
import org.junit.Test;

import java.text.ParseException;
import java.text.ParsePosition;

import static org.junit.Assert.*;

public class EnumFormatTest {
    private enum TestEnum{
        A, BB, CCC, DDDD
    }

    @Test
    public void format() {
        EnumFormat<TestEnum> enumFormat = EnumFormat.builder(TestEnum.class).build();
        assertEquals("A", enumFormat.format(TestEnum.A));
        assertEquals("BB", enumFormat.format(TestEnum.BB));
        assertEquals("CCC", enumFormat.format(TestEnum.CCC));
        assertEquals("DDDD", enumFormat.format(TestEnum.DDDD));
    }

    @Test
    public void format_object() {
        EnumFormat<TestEnum> enumFormat = EnumFormat.builder(TestEnum.class)
                .withValue("xxxx", TestEnum.DDDD)
                .build();
        assertEquals("A", enumFormat.format(TestEnum.A));
        assertEquals("BB", enumFormat.format(TestEnum.BB));
        assertEquals("CCC", enumFormat.format(TestEnum.CCC));
        assertEquals("xxxx", enumFormat.format(TestEnum.DDDD));
    }

    @Test(expected = IllegalArgumentException.class)
    public void format_object_invalid_class() {
        EnumFormat<TestEnum> enumFormat = EnumFormat.builder(TestEnum.class).build();
        enumFormat.format(this);
        fail();
    }

    @Test(expected = ParseException.class)
    public void parse_fail() throws ParseException {
        EnumFormat<TestEnum> enumFormat = EnumFormat.builder(TestEnum.class).build();
        assertEquals(TestEnum.A, enumFormat.parse("Q"));
    }

    @Test
    public void parse() throws ParseException {
        EnumFormat<TestEnum> enumFormat = EnumFormat.builder(TestEnum.class).withIgnoreCase(true).build();
        assertEquals(TestEnum.A, enumFormat.parse("A"));
        assertEquals(TestEnum.BB, enumFormat.parse("bB"));
        assertEquals(TestEnum.CCC, enumFormat.parse("ccc"));
    }
}