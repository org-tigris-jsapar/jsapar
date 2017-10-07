package org.jsapar.input.parse;

import static org.junit.Assert.assertArrayEquals;

import java.io.IOException;
import java.io.StringReader;

import org.jsapar.JSaParException;
import org.jsapar.input.parse.csv.CellSplitter;
import org.jsapar.input.parse.csv.QuotedCellSplitter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class QuotedCellSplitterTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testSplit() throws IOException, JSaParException {
        CellSplitter s = new QuotedCellSplitter(";", '"');
        assertArrayEquals(new String[]{"A", "B", "", "C"}, s.split("A;B;;C"));
    }

    @Test
    public void testSplit_cellSeparatorThatIsReservedRegexpChars() throws IOException, JSaParException {
        CellSplitter s = new QuotedCellSplitter("[|]", '"');
        assertArrayEquals(new String[]{"A", "B", "", "["}, s.split("A[|]B[|][|]["));
    }    
    
    @Test
    public void testSplit_lastCellEmpty() throws IOException, JSaParException {
        CellSplitter s = new QuotedCellSplitter(";", '"');
        assertArrayEquals(new String[]{"A", "B", ""}, s.split("A;B;"));
    }

    @Test
    public void testSplit_firstCellEmpty() throws IOException, JSaParException {
        CellSplitter s = new QuotedCellSplitter(";", '"');
        assertArrayEquals(new String[]{"", "A", "B"}, s.split(";A;B"));
    }
    
    @Test
    public void testSplit_quoted() throws IOException, JSaParException {
        CellSplitter s = new QuotedCellSplitter(";", '/');
        assertArrayEquals(new String[]{"A", "B", "", "C"}, s.split("A;/B/;;C"));
    }
    
    @Test
    public void testSplit_quotedCellSeparator() throws IOException, JSaParException {
        CellSplitter s = new QuotedCellSplitter(";", '/');
        assertArrayEquals(new String[]{"A", "B;B", "", "C"}, s.split("A;/B;B/;;C"));
    }

    @Test
    public void testSplit_quote_not_firstCharacter() throws IOException, JSaParException {
        CellSplitter s = new QuotedCellSplitter(";", '/');
        assertArrayEquals(new String[]{"A", " /B","B/", "", "C"}, s.split("A; /B;B/;;C"));
    }

    @Test
    public void testSplit_multiLineCell() throws IOException, JSaParException {
        LineReader lineReader = new ReaderLineReader("|", new StringReader("Second;S;S|Third;T/;T|Fourth"));
        CellSplitter s = new QuotedCellSplitter(";", '/', lineReader);
        String[] result = s.split("A;/BB;;C");
        assertArrayEquals(new String[]{"A", "BB;;C|Second;S;S|Third;T", "T"}, result);
    }

    @Test
    public void testSplit_multiLineCellWithLineBreakFirst() throws IOException, JSaParException {
        LineReader lineReader = new ReaderLineReader("|", new StringReader("Second;S;S|Third;T/;T|Fourth"));
        CellSplitter s = new QuotedCellSplitter(";", '/', lineReader);
        String[] result = s.split("A;B;/");
        assertArrayEquals(new String[]{"A", "B", "|Second;S;S|Third;T", "T"}, result);
    }

    @Test
    public void testSplit_endQuoteWithinCell() throws IOException, JSaParException {
        LineReader lineReader = new ReaderLineReader("|", new StringReader("No end quote"));
        CellSplitter s = new QuotedCellSplitter(";", '/', lineReader);
        assertArrayEquals(new String[]{"A", "/B/B", "", "C"}, s.split("A;/B/B;;C"));
        assertArrayEquals(new String[]{"A", "//B", "", "C"}, s.split("A;//B;;C"));
        assertArrayEquals(new String[]{"A", "", "C", "/B/B"}, s.split("A;;C;/B/B"));
        assertArrayEquals(new String[]{"A", "", "C", "/B/B"}, s.split("A;;C;/B/B"));
        assertArrayEquals(new String[]{"A", "", "B/B;/C"}, s.split("A;;/B/B;/C/"));
    }

    @Test(expected=JSaParException.class)
    public void testSplit_missingEndQuote() throws IOException, JSaParException {
        LineReader lineReader = new ReaderLineReader("|", new StringReader("Second;S;S|Third;T;T|Fourth"));
        CellSplitter s = new QuotedCellSplitter(";", '/', lineReader);
        String[] result = s.split("A;/BB;;C");
        assertArrayEquals(new String[]{"A", "BB;;C|Second;S;S|Third;T", "T"}, result);
    }
    
}