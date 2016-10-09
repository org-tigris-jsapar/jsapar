package org.jsapar.parse.csv;

import java.io.IOException;
import java.io.Reader;

import org.jsapar.JSaParException;
import org.jsapar.error.ErrorEventListener;
import org.jsapar.error.ErrorHandler;
import org.jsapar.parse.*;
import org.jsapar.parse.SchemaParser;
import org.jsapar.schema.CsvSchema;

public class CsvParser implements SchemaParser {
    
    private static final String UTF8_BOM_STR = "\ufeff";
    private CsvLineReader lineReader;
    private CsvSchema schema;
    private CsvLineParserFactory lineParserFactory;
    private ParseConfig parseConfig;
    private ErrorHandler errorHandler = new ErrorHandler();

    public CsvParser(Reader reader, CsvSchema schema, ParseConfig parseConfig) {
        this.parseConfig = parseConfig;
        lineReader = new CsvLineReader(schema.getLineSeparator(), reader);
        this.schema = schema;
        this.lineParserFactory = new CsvLineParserFactory(schema);
    }
    

    @Override
    public void parse(LineEventListener listener, ErrorEventListener errorListener) throws JSaParException, IOException {
        if(schema.isEmpty())
            return;
        while(true){

            CsvLineParser lineParser = lineParserFactory.makeLineParser(lineReader);
            if(lineParser == null) {
                if(lineParserFactory.isEmpty())
                    return; // No more parsers. We should not read any more. Leave rest of input as is.
                if(lineReader.eofReached())
                    return;
                handleNoParser(lineReader, errorListener);
                continue;
            }
            if(!lineParser.parse(lineReader, listener, errorListener))
                return;

        }

    }

    protected void handleNoParser(CsvLineReader lineReader, ErrorEventListener errorEventListener) throws IOException, ParseException {
        if (lineReader.lastLineWasEmpty())
            return;
        errorHandler.lineValidationError(this, lineReader.currentLineNumber(), "No schema line could be used to parse line number " + lineReader.currentLineNumber(), parseConfig.getOnUndefinedLineType(), errorEventListener);
    }


}
