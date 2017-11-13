package org.jsapar.parse.csv;

import org.jsapar.error.ErrorEventListener;
import org.jsapar.parse.LineEventListener;
import org.jsapar.parse.ValidationHandler;
import org.jsapar.parse.text.TextParseConfig;
import org.jsapar.parse.text.TextSchemaParser;
import org.jsapar.schema.CsvSchema;

import java.io.IOException;
import java.io.Reader;

public class CsvParser implements TextSchemaParser {
    
    private CsvLineReader        lineReader;
    private CsvSchema            schema;
    private CsvLineParserFactory lineParserFactory;
    private TextParseConfig      parseConfig;
    private ValidationHandler validationHandler = new ValidationHandler();

    public CsvParser(Reader reader, CsvSchema schema) {
        this(reader, schema, new TextParseConfig());
    }


    public CsvParser(Reader reader, CsvSchema schema, TextParseConfig parseConfig) {
        this.parseConfig = parseConfig;
        lineReader = new CsvLineReader(schema.getLineSeparator(), reader);
        this.schema = schema;
        this.lineParserFactory = new CsvLineParserFactory(schema, parseConfig);
    }
    

    @Override
    public long parse(LineEventListener listener, ErrorEventListener errorListener) throws IOException {
        long lineNumber = 0;
        if(schema.isEmpty())
            return lineNumber-1;
        while(true){

            CsvLineParser lineParser = lineParserFactory.makeLineParser(lineReader);
            if(lineParser == null) {
                if(lineParserFactory.isEmpty())
                    return lineNumber-1; // No more parsers. We should not read any more. Leave rest of input as is.
                if(lineReader.eofReached())
                    return lineNumber-1;
                handleNoParser(lineReader, errorListener);
                continue;
            }
            if(!lineParser.parse(lineReader, listener, errorListener))
                return lineNumber-1;

        }

    }

    protected void handleNoParser(CsvLineReader lineReader, ErrorEventListener errorEventListener) throws IOException {
        if (lineReader.lastLineWasEmpty())
            return;
        validationHandler.lineValidation(this, lineReader.currentLineNumber(), "No schema line could be used to parse line number " + lineReader.currentLineNumber(), parseConfig.getOnUndefinedLineType(), errorEventListener);
    }


}
