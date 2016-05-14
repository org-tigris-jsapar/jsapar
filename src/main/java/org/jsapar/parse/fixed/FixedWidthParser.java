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
public abstract class FixedWidthParser implements Parser {
    private BufferedReader      reader;
    private FixedWidthSchema    schema;
    private FWLineParserFactory lineParserFactory;


    public FixedWidthParser(Reader reader, FixedWidthSchema schema) {
        this.reader = new BufferedReader(reader);
        this.schema = schema;
        lineParserFactory = new FWLineParserFactory(schema);
    }



    protected void handleNoParser(BufferedReader reader, long lineNumber) throws IOException, ParseException {
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

    protected BufferedReader getReader() {
        return reader;
    }

    protected FixedWidthSchema getSchema() {
        return schema;
    }

    protected FWLineParserFactory getLineParserFactory() {
        return lineParserFactory;
    }
}
