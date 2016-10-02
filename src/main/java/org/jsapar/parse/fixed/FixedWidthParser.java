/**
 * 
 */
package org.jsapar.parse.fixed;

import org.jsapar.parse.*;
import org.jsapar.schema.FixedWidthSchema;

import java.io.IOException;

/**
 * @author stejon0
 *
 */
public abstract class FixedWidthParser implements SchemaParser {
    private FixedWidthSchema    schema;
    private FWLineParserFactory lineParserFactory;


    public FixedWidthParser(FixedWidthSchema schema) {
        this.schema = schema;
        lineParserFactory = new FWLineParserFactory(schema);
    }

    protected void handleNoParser(long lineNumber, LineParserMatcherResult result) throws IOException, ParseException {
        // Check if EOF
        if (result == LineParserMatcherResult.NOT_MATCHING)
            if (schema.getIfUndefinedLineType())
                throw new ParseException("No schema line could be used to parse line number " + lineNumber);
    }

    protected FixedWidthSchema getSchema() {
        return schema;
    }

    protected FWLineParserFactory getLineParserFactory() {
        return lineParserFactory;
    }
}
