/**
 * 
 */
package org.jsapar.parse.fixed;

import org.jsapar.error.ErrorEventListener;
import org.jsapar.error.JSaParException;
import org.jsapar.model.Line;
import org.jsapar.parse.*;
import org.jsapar.schema.FixedWidthSchema;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

/**
 * Parses fixed with text where lines are separated by a line separator character sequence.
 */
public class FixedWidthParserLinesSeparated extends FixedWidthParser {

    LineReader lineReader;

    /**
     * Creates a parser for fixed with cells where lines are separated.
     * @param reader The reader to read from.
     * @param schema The schema to use.
     * @param config The parse configuration to use.
     */
    public FixedWidthParserLinesSeparated(Reader reader, FixedWidthSchema schema, ParseConfig config) {
        super(schema, config);
        lineReader = new TextLineReader(schema.getLineSeparator(), reader);
    }

    @Override
    public void parse(LineEventListener listener, ErrorEventListener errorListener) throws JSaParException, IOException {

        long lineNumber = 0;
        while(true){
            lineNumber++;
            String sLine = lineReader.readLine();
            if(sLine == null)
                return;
            if(sLine.isEmpty()) // Just ignore empty lines
                continue;

            try(BufferedReader r = new BufferedReader(new StringReader(sLine))) {
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
                Line line = result.lineParser.parse(r, lineNumber, errorListener );
                if (line == null) // Should never occur.
                    throw new AssertionError("Unexpected error while parsing line number " + lineNumber);
                String remaining = r.readLine();
                if(remaining != null && ! remaining.isEmpty()) {
                    if(!getErrorHandler().lineValidationError(this, lineNumber, "Trailing characters found on line",
                            getConfig().getOnLineOverflow(), errorListener))
                        continue; // Ignore the line.
                }
                listener.lineParsedEvent(new LineParsedEvent(this, line, lineNumber));
            }
        }
    }

}
