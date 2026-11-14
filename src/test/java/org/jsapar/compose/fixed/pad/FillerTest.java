package org.jsapar.compose.fixed.pad;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.*;

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

    @Test
    public void filler_ioobe() throws IOException {
        assertThrows(IndexOutOfBoundsException.class, () -> {
            Filler filler = new Filler('*', 4);
            StringWriter writer = new StringWriter();
            filler.fill(writer, 5);
        });
    }

}