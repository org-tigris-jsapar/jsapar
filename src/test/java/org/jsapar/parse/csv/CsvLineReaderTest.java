package org.jsapar.parse.csv;

import org.jsapar.error.JSaParException;
import org.junit.Test;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import static org.junit.Assert.*;

/**
 * Created by stejon0 on 2016-05-01.
 */
public class CsvLineReaderTest {

    @Test
    public void testReset() throws Exception {
        Reader reader = new StringReader("First;line|second,'line'|third,line||fifth;one");
        CsvLineReader item = new CsvLineReader("|", reader);
        assertArrayEquals( new String[]{"First", "line"}, item.readLine(";", (char) 0).toArray());
        assertArrayEquals( new String[]{"second,'line'"}, item.readLine(";", (char) 0).toArray());
        item.reset();
        assertArrayEquals( new String[]{"second", "line"}, item.readLine(",", '\'').toArray());
        item.reset();
        assertArrayEquals( new String[]{"second", "line"}, item.readLine(",", '\'').toArray());
        assertArrayEquals( new String[]{"third", "line"}, item.readLine(",", '\'').toArray());
        assertArrayEquals( new String[]{}, item.readLine(",", '\'').toArray());
        assertArrayEquals( new String[]{"fifth;one"}, item.readLine(" ", (char) 0).toArray());
        assertTrue(item.eofReached());
        item.reset();
        assertFalse(item.eofReached());
        assertArrayEquals( new String[]{"fifth", "one"}, item.readLine(";", (char) 0).toArray());
        assertTrue(item.eofReached());
    }



    @Test
    public void testReadLine() throws Exception {
        Reader reader = new StringReader("First;line|second,'line'|third,line||fifth;one");
        CsvLineReader item = new CsvLineReader("|", reader);
        assertArrayEquals( new String[]{"First", "line"}, item.readLine(";", (char) 0).toArray());
        assertArrayEquals( new String[]{"second", "line"}, item.readLine(",", '\'').toArray());
        assertArrayEquals( new String[]{"third", "line"}, item.readLine(",", '\'').toArray());
        assertArrayEquals( new String[]{}, item.readLine(",", '\'').toArray());
        assertArrayEquals( new String[]{"fifth", "one"}, item.readLine(";", (char) 0).toArray());

    }

    @Test
    public  void testLastLineWasEmpty() throws IOException, JSaParException {
        Reader reader = new StringReader("First;line||third,line|");
        CsvLineReader item = new CsvLineReader("|", reader);
        assertArrayEquals( new String[]{"First", "line"}, item.readLine(";", (char) 0).toArray());
        assertFalse(item.lastLineWasEmpty());
        assertArrayEquals( new String[0], item.readLine(";", (char) 0).toArray());
        assertTrue(item.lastLineWasEmpty());
        assertArrayEquals( new String[]{"third", "line"}, item.readLine(",", '\'').toArray());
        assertFalse(item.lastLineWasEmpty());
        assertFalse(item.eofReached());
        assertArrayEquals( new String[0], item.readLine(",", '\'').toArray());
        assertTrue(item.lastLineWasEmpty());
        assertTrue(item.eofReached());

    }
}