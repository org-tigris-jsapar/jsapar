package org.jsapar.input.parse;

import static org.junit.Assert.*;

import java.io.IOException;

import org.jsapar.JSaParException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SimpleCellSplitterTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testSplit() throws IOException, JSaParException {
        CellSplitter s = new SimpleCellSplitter(";");
        assertArrayEquals(new String[]{"A", "B", "", "C"}, s.split("A;B;;C"));
    }

    @Test
    public void testSplit_cellSeparatorThatIsReservedRegexpChars() throws IOException, JSaParException {
        CellSplitter s = new SimpleCellSplitter("[]");
        assertArrayEquals(new String[]{"A", "B", "", "["}, s.split("A[]B[][]["));
    }    
    
    @Test
    public void testSplit_lastCellEmpty() throws IOException, JSaParException {
        CellSplitter s = new SimpleCellSplitter(";");
        assertArrayEquals(new String[]{"A", "B", ""}, s.split("A;B;"));
    }

    @Test
    public void testSplit_firstCellEmpty() throws IOException, JSaParException {
        CellSplitter s = new SimpleCellSplitter(";");
        assertArrayEquals(new String[]{"", "A", "B"}, s.split(";A;B"));
    }
    
}
