package org.jsapar.compose.fixed.pad;

import org.junit.Test;

import java.io.IOException;
import java.io.StringWriter;

import static org.junit.Assert.*;

public class PadBothTest {

    @Test
    public void fit_even() throws IOException {
        PadBoth pad = new PadBoth('*', 4);
        StringWriter writer = new StringWriter();
        pad.fit(writer, "123456");
        assertEquals("2345", writer.toString());
    }

    @Test
    public void fit_odd() throws IOException {
        PadBoth pad = new PadBoth('*', 3);
        StringWriter writer = new StringWriter();
        pad.fit(writer, "123456");
        assertEquals("234", writer.toString());
    }

    @Test
    public void pad_even() throws IOException {
        PadBoth pad = new PadBoth('*', 6);
        StringWriter writer = new StringWriter();
        pad.pad(writer, "12");
        assertEquals("**12**", writer.toString());
    }

    @Test
    public void pad_odd() throws IOException {
        PadBoth pad = new PadBoth('*', 5);
        StringWriter writer = new StringWriter();
        pad.pad(writer, "12");
        assertEquals("*12**", writer.toString());
    }

}