package org.jsapar.parse.csv.states;

import org.junit.Test;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import static org.junit.Assert.*;

public class CsvLineReaderStatesTest {

    @Test
    public void testReset() throws IOException
    {
        doTestReset(true);
    }

    @Test
    public void testReset_no_read_ahead() throws IOException
    {
        doTestReset(false);
    }

    private void doTestReset(boolean allowReadAhead) throws IOException {
        Reader reader = new StringReader("First;line|second,'line'|third,line||fifth;one");
        CsvLineReaderStates item = new CsvLineReaderStates("|", reader, allowReadAhead, 64);
        assertArrayEquals(new String[]{"First", "line"}, item.readLine(";", (char) 0).toArray());
        assertArrayEquals(new String[]{"second,'line'"}, item.readLine(";", (char) 0).toArray());
        item.reset();
        assertArrayEquals(new String[]{"second", "line"}, item.readLine(",", '\'').toArray());
        item.reset();
        assertArrayEquals(new String[]{"second", "line"}, item.readLine(",", '\'').toArray());
        assertArrayEquals(new String[]{"third", "line"}, item.readLine(",", '\'').toArray());
        assertArrayEquals(new String[]{}, item.readLine(",", '\'').toArray());
        assertArrayEquals(new String[]{"fifth;one"}, item.readLine(" ", (char) 0).toArray());
        assertTrue(item.eofReached());
        item.reset();
        assertFalse(item.eofReached());
        assertArrayEquals(new String[]{"fifth", "one"}, item.readLine(";", (char) 0).toArray());
        assertTrue(item.eofReached());
    }


    @Test
    public void testReadLine() throws IOException{
        Reader reader = new StringReader("First;line|second,'line'|third,line||fifth;one");
        CsvLineReaderStates item = new CsvLineReaderStates("|", reader, true, 32);
        assertArrayEquals( new String[]{"First", "line"}, item.readLine(";", (char) 0).toArray());
        assertArrayEquals( new String[]{"second", "line"}, item.readLine(",", '\'').toArray());
        assertArrayEquals( new String[]{"third", "line"}, item.readLine(",", '\'').toArray());
        assertArrayEquals( new String[]{}, item.readLine(",", '\'').toArray());
        assertArrayEquals( new String[]{"fifth", "one"}, item.readLine(";", (char) 0).toArray());
    }

    @Test
    public void testReadLine_first_line_empty() throws IOException{
        Reader reader = new StringReader("|First;line|second,'line'");
        CsvLineReaderStates item = new CsvLineReaderStates("|", reader, true, 64);
        assertArrayEquals( new String[]{}, item.readLine(",", '\'').toArray());
        assertArrayEquals( new String[]{"First", "line"}, item.readLine(";", (char) 0).toArray());
        assertArrayEquals( new String[]{"second", "line"}, item.readLine(",", '\'').toArray());
    }


    @Test
    public void testReadLine_unquoted() throws IOException {
        Reader reader = new StringReader("A;B;;C");
        CsvLineReaderStates lineReader = new CsvLineReaderStates("|", reader, true, 64);
        assertArrayEquals(new String[]{"A", "B", "", "C"}, lineReader.readLine(";", '"').toArray());
    }

    @Test
    public void testReadLine_unquoted_last_char_matches_cell_break() throws IOException {
        Reader reader = new StringReader("A;=B;=C=;=D");
        CsvLineReaderStates lineReader = new CsvLineReaderStates("|", reader, true, 64);
        assertArrayEquals(new String[]{"A", "B", "C=", "D"}, lineReader.readLine(";=", '"').toArray());
    }

    @Test
    public void testReadLine_unquoted_first_char_matches_last_char_of_cell_break() throws IOException {
        Reader reader = new StringReader("=A;%=B;%=C=C;%==");
        CsvLineReaderStates lineReader = new CsvLineReaderStates("|", reader, true, 64);
        assertArrayEquals(new String[]{"=A", "B", "C=C", "="}, lineReader.readLine(";%=", '"').toArray());
    }

    @Test
    public void testReadLine_unquoted_first_char_matches_last_char_of_line_break() throws IOException {
        Reader reader = new StringReader("A;B|;C;D%|a;b;c;d");
        CsvLineReaderStates lineReader = new CsvLineReaderStates("%|", reader, true, 64);
        assertArrayEquals(new String[]{"A", "B|", "C", "D"}, lineReader.readLine(";", '"').toArray());
    }

    @Test
    public void testSplit_cellSeparatorThatIsReservedRegexpChars() throws IOException {
        Reader reader = new StringReader("A[|]B[|][|][");
        CsvLineReaderStates lineReader = new CsvLineReaderStates("\n", reader, true, 64);
        assertArrayEquals(new String[]{"A", "B", "", "["}, lineReader.readLine("[|]", '"').toArray());
    }

    @Test
    public void testSplit_lastCellEmpty() throws IOException {
        Reader reader = new StringReader("A;B;");
        CsvLineReaderStates lineReader = new CsvLineReaderStates("|", reader, true, 64);
        assertArrayEquals(new String[]{"A", "B", ""}, lineReader.readLine(";", '"').toArray());
    }

    @Test
    public void testSplit_firstCellEmpty() throws IOException {
        Reader reader = new StringReader(";A;B");
        CsvLineReaderStates lineReader = new CsvLineReaderStates("|", reader, true, 64);
        assertArrayEquals(new String[]{"", "A", "B"}, lineReader.readLine(";", '"').toArray());
    }

    @Test
    public void testSplit_quoted() throws IOException {
        Reader reader = new StringReader("A;/B/;;C\nA;/B/;//;/C/\r\n/A/;/B/;;/C/\n/A/;B;//;/C/\r\n/A/;/B/;//;C");
        CsvLineReaderStates lineReader = new CsvLineReaderStates("\n", reader, true, 64);
        assertArrayEquals(new String[]{"A", "B", "", "C"}, lineReader.readLine(";", '/').toArray());
        assertArrayEquals(new String[]{"A", "B", "", "C"}, lineReader.readLine(";", '/').toArray());
        assertArrayEquals(new String[]{"A", "B", "", "C"}, lineReader.readLine(";", '/').toArray());
        assertArrayEquals(new String[]{"A", "B", "", "C"}, lineReader.readLine(";", '/').toArray());
        assertArrayEquals(new String[]{"A", "B", "", "C"}, lineReader.readLine(";", '/').toArray());
        assertArrayEquals(new String[0], lineReader.readLine(";", '/').toArray());
        assertTrue(lineReader.eofReached());
    }

    @Test
    public void testSplit_quoted_multi_line_separator() throws IOException {
        Reader reader = new StringReader("A;/B/;;C|+A;/B/;//;/C/C|+/A/;/B/;;/C/|+/A/;B;//;/C/|+/A/;/B/;//;C|+/A/;./B/;.//;.C");
        CsvLineReaderStates lineReader = new CsvLineReaderStates("|+", reader, true, 64);
        assertArrayEquals(new String[]{"A", "B", "", "C"}, lineReader.readLine(";", '/').toArray());
        assertArrayEquals(new String[]{"A", "B", "", "/C/C"}, lineReader.readLine(";", '/').toArray());
        assertArrayEquals(new String[]{"A", "B", "", "C"}, lineReader.readLine(";", '/').toArray());
        assertArrayEquals(new String[]{"A", "B", "", "C"}, lineReader.readLine(";", '/').toArray());
        assertArrayEquals(new String[]{"A", "B", "", "C"}, lineReader.readLine(";", '/').toArray());
        assertArrayEquals(new String[]{"A", "B", "", "C"}, lineReader.readLine(";.", '/').toArray());
        assertArrayEquals(new String[0], lineReader.readLine(";", '/').toArray());
        assertTrue(lineReader.eofReached());
    }

    @Test
    public void testSplit_quotedCellSeparator() throws IOException {
        Reader reader = new StringReader("A;/B;B/;;C");
        CsvLineReaderStates lineReader = new CsvLineReaderStates("|", reader, true, 64);
        assertArrayEquals(new String[]{"A", "B;B", "", "C"}, lineReader.readLine(";", '/').toArray());
        assertArrayEquals(new String[0], lineReader.readLine(";", '/').toArray());
        assertTrue(lineReader.eofReached());
    }

    @Test
    public void testSplit_quote_not_firstCharacter() throws IOException {
        Reader reader = new StringReader("A; /B;B/;;C");
        CsvLineReaderStates lineReader = new CsvLineReaderStates("|", reader, true, 64);
        assertArrayEquals(new String[]{"A", " /B","B/", "", "C"}, lineReader.readLine(";", '/').toArray());
        assertArrayEquals(new String[0], lineReader.readLine(";", '/').toArray());
        assertTrue(lineReader.eofReached());
    }

    @Test
    public void testSplit_quote_not_last_character() throws IOException {
        Reader reader = new StringReader("A;/B/ ;;C|A;//ABC;;C|A;//ABC//;;C");
        CsvLineReaderStates lineReader = new CsvLineReaderStates("|", reader, true, 64);
        assertArrayEquals(new String[]{"A", "/B/ ", "", "C"}, lineReader.readLine(";", '/').toArray());
        assertArrayEquals(new String[]{"A", "//ABC", "", "C"}, lineReader.readLine(";", '/').toArray());
        assertArrayEquals(new String[]{"A", "/ABC/", "", "C"}, lineReader.readLine(";", '/').toArray());
        assertTrue(lineReader.eofReached());
    }


    @Test
    public void testSplit_multiLineCell() throws IOException {
        Reader reader = new StringReader("A;/BB;;C|Second;S;S|Third;T/;T|Fourth");
        CsvLineReaderStates lineReader = new CsvLineReaderStates("|", reader, true, 64);
        assertArrayEquals(new String[]{"A", "BB;;C|Second;S;S|Third;T", "T"}, lineReader.readLine(";", '/').toArray());
        assertArrayEquals(new String[]{"Fourth"}, lineReader.readLine(";", '/').toArray());
        assertArrayEquals(new String[0], lineReader.readLine(";", '/').toArray());
        assertTrue(lineReader.eofReached());
    }

    @Test
    public void testSplit_multiLineCellWithLineBreakFirst() throws IOException {
        Reader reader = new StringReader("A;B;/|Second;S;S|Third;T/;T|Fourth");
        CsvLineReaderStates s = new CsvLineReaderStates("|", reader, true, 64);
        String[] result = s.readLine(";", '/').toArray(new String[0]);
        assertArrayEquals(new String[]{"A", "B", "|Second;S;S|Third;T", "T"}, result);
    }

    @Test
    public void testReadLine_endQuoteWithinCell() throws IOException {
        Reader reader = new StringReader("A;/B/B;;C|A;//B;//;/C/|/A/;/B/;;/C/C|A;;/B/B;/C/|/A/;/B;/B;C");
        CsvLineReaderStates lineReader = new CsvLineReaderStates("|", reader, true, 64);
        assertArrayEquals(new String[]{"A", "/B/B", "", "C"}, lineReader.readLine(";", '/').toArray());
        assertArrayEquals(new String[]{"A", "//B", "", "C"}, lineReader.readLine(";", '/').toArray());
        assertArrayEquals(new String[]{"A", "B", "", "/C/C"}, lineReader.readLine(";", '/').toArray());
        assertArrayEquals(new String[]{"A", "", "/B/B", "C"}, lineReader.readLine(";", '/').toArray());
        assertArrayEquals(new String[]{"A", "/B;/B", "C"}, lineReader.readLine(";", '/').toArray());
        assertArrayEquals(new String[0], lineReader.readLine(";", '/').toArray());
        assertTrue(lineReader.eofReached());
    }

    @Test()
    public void testReadLine_missingEndQuote() throws IOException {
        Reader reader = new StringReader("A;/B;;C");
        CsvLineReaderStates lineReader = new CsvLineReaderStates("|", reader, true, 64);
        assertArrayEquals(new String[]{"A", "/B", "", "C"}, lineReader.readLine(";", '/').toArray());
    }


    
}