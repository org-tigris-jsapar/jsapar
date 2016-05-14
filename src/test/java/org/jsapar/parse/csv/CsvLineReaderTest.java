package org.jsapar.parse.csv;

import org.junit.Test;

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
        BufferedLineReader2 lineReader = new BufferedLineReader2("|", reader);
        CsvLineReader item = new CsvLineReader(lineReader);
        assertArrayEquals( new String[]{"First", "line"}, item.readLine(";", (char) 0));
        assertArrayEquals( new String[]{"second,'line'"}, item.readLine(";", (char) 0));
        item.reset();
        assertArrayEquals( new String[]{"second", "line"}, item.readLine(",", '\''));
        item.reset();
        assertArrayEquals( new String[]{"second", "line"}, item.readLine(",", '\''));
        assertArrayEquals( new String[]{"third", "line"}, item.readLine(",", '\''));
        assertArrayEquals( new String[]{}, item.readLine(",", '\''));
        assertArrayEquals( new String[]{"fifth;one"}, item.readLine(" ", (char) 0));
        assertTrue(item.eofReached());
        item.reset();
        assertFalse(item.eofReached());
        assertArrayEquals( new String[]{"fifth", "one"}, item.readLine(";", (char) 0));
        assertTrue(item.eofReached());
    }



    @Test
    public void testReadLine() throws Exception {
        Reader reader = new StringReader("First;line|second,'line'|third,line||fifth;one");
        BufferedLineReader2 lineReader = new BufferedLineReader2("|", reader);
        CsvLineReader item = new CsvLineReader(lineReader);
        assertArrayEquals( new String[]{"First", "line"}, item.readLine(";", (char) 0));
        assertArrayEquals( new String[]{"second", "line"}, item.readLine(",", '\''));
        assertArrayEquals( new String[]{"third", "line"}, item.readLine(",", '\''));
        assertArrayEquals( new String[]{}, item.readLine(",", '\''));
        assertArrayEquals( new String[]{"fifth", "one"}, item.readLine(";", (char) 0));

    }
}