package org.jsapar.input.parse.fixed;

import java.io.IOException;
import java.io.StringReader;

import org.jsapar.JSaParException;
import org.jsapar.input.LineEventListener;
import org.jsapar.input.parse.LineReader;
import org.jsapar.input.parse.SchemaLineParser;
import org.jsapar.schema.FixedWidthSchemaLine;

public class FixedWidthSeparatedLineParser extends SchemaLineParser {
    
    private LineReader lineReader;
    private FixedWidthSchemaLine lineSchema;

    public FixedWidthSeparatedLineParser(LineReader lineReader, FixedWidthSchemaLine lineSchema) {
        this.lineReader = lineReader;
        this.lineSchema = lineSchema;
    }


    /* (non-Javadoc)
     * @see org.jsapar.input.parse.SchemaLineParser#parse(long, org.jsapar.input.LineEventListener)
     */
    @Override
    public boolean parse(long nLineNumber, LineEventListener listener) throws IOException, JSaParException {
        String line = lineReader.readLine();
        if(line == null)
            return false;
        FixedWidthLineParser lineParser = new FixedWidthLineParser(new StringReader(line), lineSchema);
        return lineParser.parse(nLineNumber, listener);
    }


}
