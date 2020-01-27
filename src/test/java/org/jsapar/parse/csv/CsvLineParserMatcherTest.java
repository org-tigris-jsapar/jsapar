package org.jsapar.parse.csv;

import org.jsapar.schema.CsvSchemaLine;
import org.jsapar.text.TextParseConfig;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class CsvLineParserMatcherTest {

    @Test
    public void makeLineParserIfMatching_lineCondition() throws IOException {
        CsvSchemaLine schemaLine = CsvSchemaLine.builder("A")
                .withCell("type" , c->c.withLineCondition(v->v.equals("aa")))
                .withCells("b", "c", "d")
                .build();
        CsvLineParserMatcher lineParserMatcher = new CsvLineParserMatcher(schemaLine, new TextParseConfig());

        CsvLineReader lineReaderMock1 = new CsvLineReaderMock(Arrays.asList("aa", "bb", "cc"));
        assertNotNull( lineParserMatcher.makeLineParserIfMatching(lineReaderMock1));

        CsvLineReader lineReaderMock2 = new CsvLineReaderMock(Arrays.asList("xx", "yy", "zz"));
        assertNull( lineParserMatcher.makeLineParserIfMatching(lineReaderMock2));

        CsvLineReader lineReaderMock3 = new CsvLineReaderMock(Arrays.asList("aa", "bb", "cc", "dd", "ee"));
        assertNotNull( lineParserMatcher.makeLineParserIfMatching(lineReaderMock3));

    }

    @Test
    public void makeLineParserIfMatching_withoutLineCondition() throws IOException {
        CsvSchemaLine schemaLine = CsvSchemaLine.builder("A")
                .withCells("a", "b", "c", "d")
                .build();
        CsvLineParserMatcher lineParserMatcher = new CsvLineParserMatcher(schemaLine, new TextParseConfig());

        CsvLineReader lineReaderMock1 = new CsvLineReaderMock(Arrays.asList("aa", "bb", "cc"));
        assertNotNull( lineParserMatcher.makeLineParserIfMatching(lineReaderMock1));

        CsvLineReader lineReaderMock2 = new CsvLineReaderMock(Arrays.asList("xx", "yy", "zz"));
        assertNotNull( lineParserMatcher.makeLineParserIfMatching(lineReaderMock2));
    }



    private static class CsvLineReaderMock implements CsvLineReader {

        private final List<String> nextLine;

        private CsvLineReaderMock(List<String> nextLine) {
            this.nextLine = nextLine;
        }

        @Override
        public void reset() {

        }

        @Override
        public List<String> readLine(String cellSeparator, char quoteChar) {
            return nextLine;
        }

        @Override
        public boolean eofReached() {
            return false;
        }

        @Override
        public long currentLineNumber() {
            return 0;
        }

        @Override
        public boolean lastLineWasEmpty() {
            return false;
        }

        @Override
        public void skipLine() {
        }
    }
}