/**
 * 
 */
package org.jsapar.parse.fixed;

import org.jsapar.JSaParException;
import org.jsapar.parse.*;
import org.jsapar.schema.FixedWidthSchema;
import org.jsapar.schema.FixedWidthSchemaLine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

/**
 * @author stejon0
 *
 */
public class FixedWidthParser implements Parser {
    private BufferedReader      reader;
    private FixedWidthSchema    schema;
    private FWLineParserFactory lineParserFactory;


    public FixedWidthParser(Reader reader, FixedWidthSchema schema) {
        this.reader = new BufferedReader(reader);
        this.schema = schema;
        lineParserFactory = new FWLineParserFactory(schema);
    }

    @Override
    public void parse(LineEventListener listener) throws JSaParException, IOException {
        if (schema.getLineSeparator().length() > 0) {
            parseLinesSeparated(listener);
        } else {
            parseFlatFile(listener);
        }
    }

    private LineReader makeLineReader() {
        return new ReaderLineReader(schema.getLineSeparator(), reader);
    }

    private void parseLinesSeparated(LineEventListener listener) throws IOException, JSaParException {
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
                FixedWidthLineParser lineParser = lineParserFactory.makeLineParser(r);
                if (lineParser == null) {
                    if(schema.isErrorIfUndefinedLineType())
                        throw new ParseException("No schema line could be used to parse line number " + lineNumber);
                    else
                        continue;
                }
                boolean lineFound = lineParser.parse(r, lineNumber, listener);
                if (!lineFound) // Should never occur.
                    throw new ParseException("Unexpected error while parsing line number " + lineNumber);
            }
        }
    }
    /**
     * Sends line parce events to the supplied listener while parsing.
     *
     * @param listener
     *            The listener which will receive events for each parsed line.
     * @throws org.jsapar.JSaParException
     * @throws java.io.IOException
     */
    private void parseFlatFile(LineEventListener listener) throws IOException,
            JSaParException {
        long lineNumber = 0;
        while(true){
            lineNumber++;
            FixedWidthLineParser lineParser = lineParserFactory.makeLineParser(reader);
            if(lineParser == null) {
                handleNoParser(reader, lineNumber);
                return;
            }
            boolean lineFound = lineParser.parse(reader, lineNumber, listener);
            if (!lineFound)
                return; // End of stream.
        }
    }


    private void handleNoParser(BufferedReader reader, long lineNumber) throws IOException, ParseException {
        reader.mark(10);
        try {
            // Check if EOF
            if (reader.read() != -1)
                if(schema.isErrorIfUndefinedLineType())
                    throw new ParseException("No schema line could be used to parse line number " + lineNumber);
        }finally{
            reader.reset();
        }
    }

}
