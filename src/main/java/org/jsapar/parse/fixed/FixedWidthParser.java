package org.jsapar.parse.fixed;

import org.jsapar.error.ErrorEventListener;
import org.jsapar.model.Line;
import org.jsapar.parse.LineEventListener;
import org.jsapar.parse.LineParsedEvent;
import org.jsapar.parse.line.ValidationHandler;
import org.jsapar.parse.text.TextParseConfig;
import org.jsapar.parse.text.TextSchemaParser;
import org.jsapar.schema.FixedWidthSchema;
import org.jsapar.schema.FixedWidthSchemaCell;
import org.jsapar.schema.SchemaLine;

import java.io.IOException;
import java.io.Reader;

/**
 * Abstract base class for fixed width text parser based on schema.
 */
public class FixedWidthParser implements TextSchemaParser {
    private FixedWidthSchema schema;
    private TextParseConfig  config;
    private ValidationHandler validationHandler = new ValidationHandler();
    private final ReadBuffer lineReader;
    private final int minLineLength;


    public FixedWidthParser(Reader reader, FixedWidthSchema schema, TextParseConfig config) {
        this.schema = schema;
        this.config = config;
        boolean allowReadAhead = schema.stream().anyMatch(SchemaLine::isOccursInfinitely);
        this.lineReader = new ReadBuffer(schema.getLineSeparator(), reader, config.getMaxLineLength(), (allowReadAhead ? config.getMaxLineLength(): 1));
        minLineLength = schema.stream().mapToInt(sl->sl.stream().mapToInt(FixedWidthSchemaCell::getLength).sum()).min().orElse(1);
    }

    protected void handleNoParser(long lineNumber, LineParserMatcherResult result, ErrorEventListener errorEventListener) {

        // Check if EOF
        if (result == LineParserMatcherResult.NOT_MATCHING)
            this.validationHandler
                    .lineValidation(this, lineNumber, "No schema line could be used to parse line number " + lineNumber,config.getOnUndefinedLineType(), errorEventListener);
    }

    protected FixedWidthSchema getSchema() {
        return schema;
    }


    public TextParseConfig getConfig() {
        return config;
    }

    public void setConfig(TextParseConfig config) {
        this.config = config;
    }

    protected ValidationHandler getValidationHandler() {
        return validationHandler;
    }

    @Override
    public long parse(LineEventListener lineEventListener, ErrorEventListener errorListener) throws IOException {
        FWLineParserFactory lineParserFactory = new FWLineParserFactory(getSchema(), getConfig());
        while(true){
            if(lineParserFactory.isEmpty())
                return lineReader.getLineNumber();
            int lineLength = lineReader.nextLine(minLineLength);
            if (lineLength < 0)
                return lineReader.getLineNumber() - 1; // End of stream.
            if (lineLength == 0)
                continue; // Just ignore empty lines
            FixedWidthLineParser lineParser = lineParserFactory.makeLineParser(lineReader);
            if (lineParser == null) {
                handleNoParser(lineReader.getLineNumber(), lineParserFactory.getLastResult(), errorListener);
                if(lineParserFactory.getLastResult() == LineParserMatcherResult.NOT_MATCHING)
                    continue;
                else
                    return lineReader.getLineNumber()-1;
            }
            Line line = lineParser.parse(lineReader, errorListener);
            if(lineParser.isIgnoreRead())
                continue;
            if (line != null)
                lineEventListener.lineParsedEvent(new LineParsedEvent(this, line));
            else if(lineReader.eofReached())
                return lineReader.getLineNumber()-1; // End of stream.
        }
    }

}
