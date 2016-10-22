/**
 * 
 */
package org.jsapar.parse.fixed;

import org.jsapar.error.ErrorEventListener;
import org.jsapar.parse.ErrorHandler;
import org.jsapar.parse.ParseConfig;
import org.jsapar.parse.SchemaParser;
import org.jsapar.schema.FixedWidthSchema;

import java.io.IOException;

/**
 * @author stejon0
 *
 */
public abstract class FixedWidthParser implements SchemaParser {
    private FixedWidthSchema    schema;
    private FWLineParserFactory lineParserFactory;
    private ParseConfig config;
    private ErrorHandler errorHandler = new ErrorHandler();


    public FixedWidthParser(FixedWidthSchema schema, ParseConfig config) {
        this.schema = schema;
        this.config = config;
        lineParserFactory = new FWLineParserFactory(schema);
    }

    protected void handleNoParser(long lineNumber, LineParserMatcherResult result, ErrorEventListener errorEventListener) throws IOException {

        // Check if EOF
        if (result == LineParserMatcherResult.NOT_MATCHING)
            this.errorHandler.lineValidationError(this, lineNumber, "No schema line could be used to parse line number " + lineNumber,config.getOnUndefinedLineType(), errorEventListener);
    }

    protected FixedWidthSchema getSchema() {
        return schema;
    }

    protected FWLineParserFactory getLineParserFactory() {
        return lineParserFactory;
    }

    public ParseConfig getConfig() {
        return config;
    }

    public void setConfig(ParseConfig config) {
        this.config = config;
    }
}
