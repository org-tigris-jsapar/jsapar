package org.jsapar.parse.csv;

import org.jsapar.error.JSaParException;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;

import static org.junit.Assert.assertArrayEquals;

public class UnquotedCellSplitterTest {

    @Test
    public void testSplit() throws IOException, JSaParException {
        CellSplitter s = new UnquotedCellSplitter(";");
        assertArrayEquals(new String[]{"A", "B", "", "C"}, s.split("A;B;;C", new ArrayList<>()).toArray());
    }

    @Test
    public void testSplit_cellSeparatorThatIsReservedRegexpChars() throws IOException, JSaParException {
        CellSplitter s = new UnquotedCellSplitter("[]");
        assertArrayEquals(new String[]{"A", "B", "", "["}, s.split("A[]B[][][", new ArrayList<>()).toArray());
    }    
    
    @Test
    public void testSplit_lastCellEmpty() throws IOException, JSaParException {
        CellSplitter s = new UnquotedCellSplitter(";");
        assertArrayEquals(new String[]{"A", "B", ""}, s.split("A;B;", new ArrayList<>()).toArray());
    }

    @Test
    public void testSplit_firstCellEmpty() throws IOException, JSaParException {
        CellSplitter s = new UnquotedCellSplitter(";");
        assertArrayEquals(new String[]{"", "A", "B"}, s.split(";A;B", new ArrayList<>()).toArray());
    }

    @Test
    public void testSplit_empty() throws IOException, JSaParException {
        CellSplitter s = new UnquotedCellSplitter(";");
        assertArrayEquals(new String[]{""}, s.split("", new ArrayList<>()).toArray());
    }

}
