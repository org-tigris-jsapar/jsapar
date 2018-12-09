package org.jsapar.parse.csv;

import org.jsapar.error.JSaParException;
import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;

public class QuotedCellSplitterTest {

    @Test
    public void testSplit() throws IOException, JSaParException {
        CellSplitter s = new QuotedCellSplitter(";", '"');
        assertArrayEquals(new String[]{"A", "B", "", "C"}, s.split("A;B;;C", new ArrayList<>()).toArray());
    }

    @Test
    public void testSplit_cellSeparatorThatIsReservedRegexpChars() throws IOException, JSaParException {
        CellSplitter s = new QuotedCellSplitter("[|]", '"');
        assertArrayEquals(new String[]{"A", "B", "", "["}, s.split("A[|]B[|][|][", new ArrayList<>()).toArray());
    }

    @Test
    public void testSplit_lastCellEmpty() throws IOException, JSaParException {
        CellSplitter s = new QuotedCellSplitter(";", '"');
        assertArrayEquals(new String[]{"A", "B", ""}, s.split("A;B;", new ArrayList<>()).toArray());
    }

    @Test
    public void testSplit_firstCellEmpty() throws IOException, JSaParException {
        CellSplitter s = new QuotedCellSplitter(";", '"');
        assertArrayEquals(new String[]{"", "A", "B"}, s.split(";A;B", new ArrayList<>()).toArray());
    }

    @Test
    public void testSplit_quoted() throws IOException, JSaParException {
        CellSplitter s = new QuotedCellSplitter(";", '/');
        assertArrayEquals(new String[]{"A", "B", "", "C"}, s.split("A;/B/;;C", new ArrayList<>()).toArray());
        assertArrayEquals(new String[]{"A", "B", "", "C"}, s.split("A;/B/;//;/C/", new ArrayList<>()).toArray());
        assertArrayEquals(new String[]{"A", "B", "", "C"}, s.split("/A/;/B/;;/C/", new ArrayList<>()).toArray());
        assertArrayEquals(new String[]{"A", "B", "", "C"}, s.split("/A/;B;//;/C/", new ArrayList<>()).toArray());
        assertArrayEquals(new String[]{"A", "B", "", "C"}, s.split("/A/;/B/;//;C", new ArrayList<>()).toArray());
    }

    @Test
    public void testSplit_quotedCellSeparator() throws IOException, JSaParException {
        CellSplitter s = new QuotedCellSplitter(";", '/');
        assertArrayEquals(new String[]{"A", "B;B", "", "C"}, s.split("A;/B;B/;;C", new ArrayList<>()).toArray());
    }

    @Test
    public void testSplit_quote_not_firstCharacter() throws IOException, JSaParException {
        CellSplitter s = new QuotedCellSplitter(";", '/');
        assertArrayEquals(new String[]{"A", " /B","B/", "", "C"}, s.split("A; /B;B/;;C", new ArrayList<>()).toArray());
    }

    @Test
    public void testSplit_multiLineCell() throws IOException, JSaParException {
        BufferedLineReader lineReader = new BufferedLineReader("|", new StringReader("Second;S;S|Third;T/;T|Fourth"));
        CellSplitter s = new QuotedCellSplitter(";", '/', lineReader, 5);
        String[] result = s.split("A;/BB;;C", new ArrayList<>()).toArray(new String[0]);
        assertArrayEquals(new String[]{"A", "BB;;C|Second;S;S|Third;T", "T"}, result);
    }

    @Test
    public void testSplit_multiLineCellWithLineBreakFirst() throws IOException, JSaParException {
        BufferedLineReader lineReader = new BufferedLineReader("|", new StringReader("Second;S;S|Third;T/;T|Fourth"));
        CellSplitter s = new QuotedCellSplitter(";", '/', lineReader, 5);
        String[] result = s.split("A;B;/", new ArrayList<>()).toArray(new String[0]);
        assertArrayEquals(new String[]{"A", "B", "|Second;S;S|Third;T", "T"}, result);
    }

    @Test
    public void testSplit_endQuoteWithinCell() throws IOException, JSaParException {
        BufferedLineReader lineReader = new BufferedLineReader("|", new StringReader("Second;S;S|Third;T/;T|Fourth"));
        CellSplitter s = new QuotedCellSplitter(";", '/', lineReader, 5);
        assertArrayEquals(new String[]{"A", "/B/B", "", "C"}, s.split("A;/B/B;;C", new ArrayList<>()).toArray());
        assertArrayEquals(new String[]{"A", "//B", "", "C"}, s.split("A;//B;;C", new ArrayList<>()).toArray());
        assertArrayEquals(new String[]{"A", "", "C", "/B/B"}, s.split("A;;C;/B/B", new ArrayList<>()).toArray());
        assertArrayEquals(new String[]{"A", "", "B/B;/C"}, s.split("A;;/B/B;/C/", new ArrayList<>()).toArray());
        assertArrayEquals(new String[]{"A", "/B;/B", "C"}, s.split("A;/B;/B;C", new ArrayList<>()).toArray());
    }

    @Test(expected=JSaParException.class)
    public void testSplit_missingEndQuote() throws IOException, JSaParException {
        BufferedLineReader lineReader = new BufferedLineReader("|", new StringReader("No end quote"));
        CellSplitter s = new QuotedCellSplitter(";", '/', lineReader, 5);
        String[] result = s.split("A;/BB;;C", new ArrayList<>()).toArray(new String[0]);
        assertArrayEquals(new String[]{"A", "BB;;C|Second;S;S|Third;T", "T"}, result);
    }
    
}
