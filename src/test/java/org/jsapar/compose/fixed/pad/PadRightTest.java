package org.jsapar.compose.fixed.pad;

import org.junit.Test;

import java.io.IOException;
import java.io.StringWriter;

import static org.junit.Assert.*;

public class PadRightTest {

    @Test
    public void fit() throws IOException {
        PadRight pad = new PadRight('*', 4);
        StringWriter writer = new StringWriter();
        pad.fit(writer, "12345");
        assertEquals("1234", writer.toString());
    }

    @Test
    public void pad() throws IOException {
        PadRight pad = new PadRight('*', 4);
        StringWriter writer = new StringWriter();
        pad.pad(writer, "12");
        assertEquals("12**", writer.toString());
    }
}