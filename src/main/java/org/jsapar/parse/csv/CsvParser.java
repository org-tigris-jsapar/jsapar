package org.jsapar.parse.csv;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

import org.jsapar.JSaParException;
import org.jsapar.parse.*;
import org.jsapar.parse.Parser;
import org.jsapar.parse.fixed.FixedWidthLineParser;
import org.jsapar.schema.CsvSchema;
import org.jsapar.schema.CsvSchemaCell;
import org.jsapar.schema.CsvSchemaLine;

public class CsvParser implements Parser {
    
    private static final String UTF8_BOM_STR = "\ufeff";
    private CsvLineReader lineReader;
    private CsvSchema schema;
    private CsvLineParserFactory lineParserFactory;

    public CsvParser(Reader reader, CsvSchema schema) {
        BufferedLineReader2 bufferedLineReader = new BufferedLineReader2(schema.getLineSeparator(), reader);
        lineReader = new CsvLineReader(bufferedLineReader);
        this.schema = schema;
        this.lineParserFactory = new CsvLineParserFactory(schema);
    }
    

    @Override
    public void parse(LineEventListener listener) throws JSaParException, IOException {
        if(schema.isEmpty())
            return;
        while(true){

            CsvLineParser lineParser = lineParserFactory.makeLineParser(lineReader);
            if(lineParser == null) {
                if(lineReader.eofReached())
                    return;
                handleNoParser(lineReader);
                continue;
            }
            if(!lineParser.parse(lineReader, listener))
                return;

        }

    }

    protected void handleNoParser(CsvLineReader lineReader) throws IOException, ParseException {
        if (lineReader.lastLineWasEmpty())
            return;
        if (schema.isErrorIfUndefinedLineType())
            throw new ParseException("No schema line could be used to parse line number " + lineReader.currentLineNumber());
    }


}
