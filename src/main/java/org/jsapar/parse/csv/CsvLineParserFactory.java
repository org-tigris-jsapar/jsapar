package org.jsapar.parse.csv;

import org.jsapar.JSaParException;
import org.jsapar.parse.fixed.FWLineParserMatcher;
import org.jsapar.parse.fixed.FixedWidthLineParser;
import org.jsapar.schema.CsvSchema;
import org.jsapar.schema.CsvSchemaLine;
import org.jsapar.schema.FixedWidthSchemaLine;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by stejon0 on 2016-04-17.
 */
public class CsvLineParserFactory {

    private CsvSchema                  schema;
    private List<CsvLineParserMatcher> lineParserMatchers;

    public CsvLineParserFactory(CsvSchema schema) {
        this.schema = schema;
        lineParserMatchers = new LinkedList<>();
        for (CsvSchemaLine schemaLine : schema.getCsvSchemaLines()) {
            lineParserMatchers.add(new CsvLineParserMatcher(schemaLine));
        }
    }

    public CsvLineParser makeLineParser(CsvLineReader lineReader) throws IOException, JSaParException {
        if (lineParserMatchers.isEmpty())
            return null;
        Iterator<CsvLineParserMatcher> iter = lineParserMatchers.iterator();
        boolean first = true;
        while (iter.hasNext()) {
            CsvLineParserMatcher currentMatcher = iter.next();
            CsvLineParser lineParser = currentMatcher.makeLineParserIfMatching(lineReader);
            if (lineParser == null) {
                if (lineReader.eofReached())
                    return null;
            }
            else {
                if (!currentMatcher.isOccursLeft())
                    // No longer needed
                    iter.remove();
                else if (!first) {
                    // Move current matching line first in list so that it is tested first next time.
                    iter.remove();
                    lineParserMatchers.add(0, currentMatcher);
                }
                return lineParser;
            }
            first = false;
        }
        return null;
    }

}
