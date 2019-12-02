package org.jsapar.parse.fixed;

import org.jsapar.error.JSaParException;
import org.jsapar.model.Line;
import org.jsapar.parse.line.ValidationHandler;
import org.jsapar.parse.text.TextSchemaParser;
import org.jsapar.schema.FixedWidthSchema;
import org.jsapar.schema.FixedWidthSchemaCell;
import org.jsapar.schema.SchemaLine;
import org.jsapar.text.TextParseConfig;

import java.io.IOException;
import java.io.Reader;
import java.util.function.Consumer;

/**
 * Abstract base class for fixed width text parser based on schema.
 */
public class FixedWidthParser implements TextSchemaParser {
    private final FixedWidthSchema schema;
    private final TextParseConfig  config;
    private final ValidationHandler validationHandler = new ValidationHandler();
    private final ReadBuffer lineReader;
    private final int minLineLength;


    public FixedWidthParser(Reader reader, FixedWidthSchema schema, TextParseConfig config) {
        this.schema = schema;
        this.config = config;
        boolean allowReadAhead = schema.stream().anyMatch(SchemaLine::isOccursInfinitely);
        this.lineReader = new ReadBuffer(schema.getLineSeparator(), reader, config.getMaxLineLength(), (allowReadAhead ? config.getMaxLineLength(): 1));
        minLineLength = schema.stream().mapToInt(sl->sl.stream().mapToInt(FixedWidthSchemaCell::getLength).sum()).min().orElse(1);
    }

    private void handleNoParser(long lineNumber, LineParserMatcherResult result, Consumer<JSaParException> errorEventListener) {

        // Check if EOF
        if (result == LineParserMatcherResult.NOT_MATCHING)
            this.validationHandler
                    .lineValidation(this, lineNumber, ()->"No schema line could be used to parse line number " + lineNumber,config.getOnUndefinedLineType(), errorEventListener);
    }

    protected FixedWidthSchema getSchema() {
        return schema;
    }


    @Override
    public long parse(Consumer<Line> lineEventListener, Consumer<JSaParException> errorListener) throws IOException {
        FWLineParserFactory lineParserFactory = new FWLineParserFactory(getSchema(), config);
        while(true){
            if(lineParserFactory.isEmpty())
                return lineReader.getLineNumber();
            int lineLength = lineReader.nextLine(minLineLength);
            if (lineLength < 0)
                return lineReader.getLineNumber(); // End of stream.
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
                lineEventListener.accept( line );
            else if(lineReader.eofReached())
                return lineReader.getLineNumber()-1; // End of stream.
        }
    }

}
