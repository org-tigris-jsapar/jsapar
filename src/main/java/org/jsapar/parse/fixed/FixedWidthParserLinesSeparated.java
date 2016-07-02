/**
 * 
 */
package org.jsapar.parse.fixed;

import org.jsapar.JSaParException;
import org.jsapar.parse.LineEventListener;
import org.jsapar.parse.LineReader;
import org.jsapar.parse.ParseException;
import org.jsapar.parse.ReaderLineReader;
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

    public FixedWidthParserLinesSeparated(Reader reader, FixedWidthSchema schema) {
        super(schema);
        lineReader = new ReaderLineReader(schema.getLineSeparator(), reader);
    }

    @Override
    public void parse(LineEventListener listener) throws JSaParException, IOException {

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
                    handleNoParser(lineNumber, result.result);
                    if(result.result == LineParserMatcherResult.NOT_MATCHING)
                        continue;
                    else
                        return;
                }
                boolean lineFound = result.lineParser.parse(r, lineNumber, listener);
                if (!lineFound) // Should never occur.
                    throw new ParseException("Unexpected error while parsing line number " + lineNumber);
            }
        }
    }

}
