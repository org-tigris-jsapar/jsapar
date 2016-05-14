/**
 * 
 */
package org.jsapar.parse.fixed;

import org.jsapar.JSaParException;
import org.jsapar.parse.*;
import org.jsapar.schema.FixedWidthSchema;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

/**
 * @author stejon0
 *
 */
public class FixedWidthParserLinesSeparated extends FixedWidthParser {


    public FixedWidthParserLinesSeparated(Reader reader, FixedWidthSchema schema) {
        super(reader, schema);
    }

    private LineReader makeLineReader() {
        return new ReaderLineReader(getSchema().getLineSeparator(), getReader());
    }

    @Override
    public void parse(LineEventListener listener) throws JSaParException, IOException {
        LineReader lineReader = makeLineReader();

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
                FixedWidthLineParser lineParser = getLineParserFactory().makeLineParser(r);
                if (lineParser == null) {
                    handleNoParser(getReader(), lineNumber);
                    continue;
                }
                boolean lineFound = lineParser.parse(r, lineNumber, listener);
                if (!lineFound) // Should never occur.
                    throw new ParseException("Unexpected error while parsing line number " + lineNumber);
            }
        }
    }

}
