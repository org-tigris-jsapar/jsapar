package org.jsapar.input.parse;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import org.jsapar.JSaParException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ReaderLineReaderTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testReaderLineReader() {
        String lineSeparator = "|";
        Reader reader = new StringReader("FirstLine|SecondLine");
        ReaderLineReader r = new ReaderLineReader(lineSeparator, reader);
        assertSame(lineSeparator, r.getLineSeparator());
        assertSame(reader, r.getReader());
    }

    @Test
    public void testReadLine() throws IOException, JSaParException {
        String lineSeparator = "|";
        Reader reader = new StringReader("FirstLine|SecondLine");
        ReaderLineReader r = new ReaderLineReader(lineSeparator, reader);
        assertEquals("FirstLine", r.readLine());
        assertEquals("SecondLine", r.readLine());
        assertNull(r.readLine());
    }

    @Test
    public void testReadLine_emptyLine() throws IOException, JSaParException {
        String lineSeparator = "|";
        Reader reader = new StringReader("FirstLine||ThirdLine");
        ReaderLineReader r = new ReaderLineReader(lineSeparator, reader);
        assertEquals("FirstLine", r.readLine());
        assertEquals("", r.readLine());
        assertEquals("ThirdLine", r.readLine());
        assertNull(r.readLine());
    }
    
    @Test
    public void testReadLine_emptyLineLast() throws IOException, JSaParException {
        String lineSeparator = "|";
        Reader reader = new StringReader("FirstLine|SecondLine|");
        ReaderLineReader r = new ReaderLineReader(lineSeparator, reader);
        assertEquals("FirstLine", r.readLine());
        assertEquals("SecondLine", r.readLine());
        assertEquals("", r.readLine());
        assertNull(r.readLine());
    }
}
