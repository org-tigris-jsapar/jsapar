package org.jsapar.parse.fixed;

import java.io.IOException;
import java.io.StringReader;

import org.jsapar.JSaParException;
import org.jsapar.parse.LineEventListener;
import org.jsapar.parse.LineParser;
import org.jsapar.parse.LineReader;
import org.jsapar.schema.FixedWidthSchemaLine;

public class FixedWidthSeparatedLineParser implements LineParser {
    
    private LineReader lineReader;
    private FixedWidthSchemaLine lineSchema;

    public FixedWidthSeparatedLineParser(LineReader lineReader, FixedWidthSchemaLine lineSchema) {
        this.lineReader = lineReader;
        this.lineSchema = lineSchema;
    }


    /* (non-Javadoc)
     * @see org.jsapar.input.parse.LineParser#parse(long, org.jsapar.input.LineEventListener)
     */
    @Override
    public boolean parse(long nLineNumber, LineEventListener listener) throws IOException, JSaParException {
        String line = lineReader.readLine();
        if(line == null)
            return false;
        FixedWidthLineParser lineParser = new FixedWidthLineParser(lineSchema);
        return lineParser.parse(new StringReader(line), nLineNumber, listener);
    }


}
