package org.jsapar.compose.fixed.pad;

import org.junit.Test;

import java.io.IOException;
import java.io.StringWriter;

import static org.junit.Assert.*;

public class PadLeftTest {

    @Test
    public void fit() throws IOException {
        PadLeft pad = new PadLeft('*', 4);
        StringWriter writer = new StringWriter();
        pad.fit(writer, "12345");
        assertEquals("2345", writer.toString());
    }

    @Test
    public void pad() throws IOException {
        PadLeft pad = new PadLeft('*', 4);
        StringWriter writer = new StringWriter();
        pad.pad(writer, "12");
        assertEquals("**12", writer.toString());
    }
}