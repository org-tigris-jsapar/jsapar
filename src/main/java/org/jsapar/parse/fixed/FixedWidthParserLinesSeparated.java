/**
 * 
 */
package org.jsapar.parse.fixed;

import org.jsapar.error.JSaParException;
import org.jsapar.error.ErrorEventListener;
import org.jsapar.parse.*;
import org.jsapar.schema.FixedWidthSchema;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

/**
 * Parses fixed with text where lines are separated by a line separator character sequence.
 * @author stejon0
 *
 */
public class FixedWidthParserLinesSeparated extends FixedWidthParser {

    LineReader lineReader;

    /**
     * Mainly for testing
     * @param reader The reader to read from.
     * @param schema The schema to use.
     */
    public FixedWidthParserLinesSeparated(Reader reader, FixedWidthSchema schema) {
        this(reader, schema, new ParseConfig());
    }

    /**
     * Creates a parser for fixed with cells where lines are separated.
     * @param reader The reader to read from.
     * @param schema The schema to use.
     * @param config The parse configuration to use.
     */
    public FixedWidthParserLinesSeparated(Reader reader, FixedWidthSchema schema, ParseConfig config) {
        super(schema, config);
        lineReader = new ReaderLineReader(schema.getLineSeparator(), reader);
    }

    @Override
    public void parse(LineEventListener listener, ErrorEventListener errorListener) throws JSaParException, IOException {

        long lineNumber = 0;
        while(true){
            lineNumber++;
            String line = lineReader.readLine();
            if(line == null)
                return;
            if(line.isEmpty()) // Just ignore empty lines
                continue;

            try(BufferedReader r = new BufferedReader(new StringReader(line))) {
                if(getLineParserFactory().isEmpty())
                    return;
                FWLineParserFactory.LineParserResult result = getLineParserFactory().makeLineParser(r);
                if (result.result != LineParserMatcherResult.SUCCESS) {
                    handleNoParser(lineNumber, result.result, errorListener);
                    if(result.result == LineParserMatcherResult.NOT_MATCHING)
                        continue;
                    else
                        return;
                }
                boolean lineFound = result.lineParser.parse(r, lineNumber, listener, errorListener );
                if (!lineFound) // Should never occur.
                    throw new AssertionError("Unexpected error while parsing line number " + lineNumber);
            }
        }
    }

}
