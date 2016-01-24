/**
 * 
 */
package org.jsapar.parse.fixed;

import java.io.IOException;
import java.io.Reader;

import org.jsapar.JSaParException;
import org.jsapar.parse.LineEventListener;
import org.jsapar.parse.LineReader;
import org.jsapar.parse.ReaderLineReader;
import org.jsapar.parse.SchemaParser;
import org.jsapar.schema.FixedWidthSchema;
import org.jsapar.schema.FixedWidthSchemaLine;

/**
 * @author stejon0
 *
 */
public class FixedWidthParser implements SchemaParser {
    
    private Reader reader;
    private FixedWidthSchema schema;

    /**
     * 
     */
    public FixedWidthParser(Reader reader, FixedWidthSchema schema) {
        this.reader = reader;
        this.schema = schema;
    }

    /* (non-Javadoc)
     * @see org.jsapar.input.parse.SchemaParser#parse(org.jsapar.input.LineEventListener)
     */
    @Override
    public void parse(LineEventListener listener) throws JSaParException, IOException {
        if (schema.getLineSeparator().length() > 0) {
            parseByOccursLinesSeparated(listener);
        } else {
            parseByOccursFlatFile(listener);
        }
    }

    private LineReader makeLineReader() {
        return new ReaderLineReader(schema.getLineSeparator(), reader);
    }

    /**
     * Builds a document from a reader using a schema where the line types are denoted by the occurs
     * field in the schema and the lines are not separated by any line separator character.
     * 
     * @param reader
     *            The reader to parse input from
     * @param listener
     *            The listener which will receive events for each parsed line.
     * @throws org.jsapar.JSaParException
     * @throws java.io.IOException
     */
    private void parseByOccursFlatFile(LineEventListener listener) throws IOException,
            JSaParException {
        long nLineNumber = 0;
        for (FixedWidthSchemaLine lineSchema : schema.getFixedWidthSchemaLines()) {
            FixedWidthLineParser lineParser = new FixedWidthLineParser(reader, lineSchema);
            for (int i = 0; i < lineSchema.getOccurs(); i++) {
                nLineNumber++;
                boolean isLineFound = lineParser.parse(nLineNumber, listener);
                if (!isLineFound) {
                    break; // End of stream.
                }
            }
        }
    }

    /**
     * Builds a document from a reader using a schema where the line types are denoted by the occurs
     * field in the schema and the lines are separated by line separator character.
     * 
     * @param reader
     *            The reader to parse input from
     * @param listener
     *            The listener which will receive events for each parsed line.
     * @throws IOException
     * @throws JSaParException
     */
    private void parseByOccursLinesSeparated(LineEventListener listener)
            throws IOException, JSaParException {
        LineReader lineReader = makeLineReader();

        long nLineNumber = 0; // First line is 1
        for (FixedWidthSchemaLine lineSchema : schema.getFixedWidthSchemaLines()) {
            FixedWidthSeparatedLineParser lineParser = new FixedWidthSeparatedLineParser(lineReader, lineSchema);
            
            for (int i = 0; i < lineSchema.getOccurs(); i++) {
                nLineNumber++;

                boolean isLineFound = lineParser.parse(nLineNumber, listener);
                if (!isLineFound) {
                    return; // End of stream.
                }
            }
        }
    }
    
}
