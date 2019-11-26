package org.jsapar.compose.fixed.pad;

import org.junit.Test;

import java.io.IOException;
import java.io.StringWriter;

import static org.junit.Assert.*;

public class FillerTest {

    @Test
    public void fill() throws IOException {
        Filler filler = new Filler('*', 4);
        StringWriter writer = new StringWriter();
        filler.fill(writer, 4);
        assertEquals("****", writer.toString());
    }

    @Test
    public void filler_zero() throws IOException {
        Filler filler = new Filler('*', 0);
        StringWriter writer = new StringWriter();
        filler.fill(writer, 0);
        assertEquals("", writer.toString());
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void filler_ioobe() throws IOException {
        Filler filler = new Filler('*', 4);
        StringWriter writer = new StringWriter();
        filler.fill(writer, 5);
    }

}