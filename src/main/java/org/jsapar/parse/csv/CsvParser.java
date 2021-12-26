package org.jsapar.parse.csv;

import org.jsapar.error.JSaParException;
import org.jsapar.model.Line;
import org.jsapar.parse.line.ValidationHandler;
import org.jsapar.parse.text.TextSchemaParser;
import org.jsapar.schema.CsvSchema;
import org.jsapar.schema.SchemaLine;
import org.jsapar.text.TextParseConfig;

import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Internal class for parsing CSV input.
 */
public class CsvParser implements TextSchemaParser {
    
    private CsvLineReader lineReader;
    private CsvSchema            schema;
    private CsvLineParserFactory lineParserFactory;
    private TextParseConfig      parseConfig;
    private final ValidationHandler validationHandler = new ValidationHandler();

    CsvParser(Reader reader, CsvSchema schema) {
        this(reader, schema, new TextParseConfig());
    }


    public CsvParser(Reader reader, CsvSchema schema, TextParseConfig parseConfig) {
        this.parseConfig = parseConfig;
        lineReader = new CsvLineReaderStates(schema.getLineSeparator(), reader, schema.stream().anyMatch(SchemaLine::isOccursInfinitely), parseConfig.getMaxLineLength(), schema.getQuoteSyntax());
        this.schema = schema;
        this.lineParserFactory = new CsvLineParserFactory(schema, parseConfig);
    }
    

    @Override
    public long parse(Consumer<Line> listener, Consumer<JSaParException> errorListener) throws IOException {
        if(schema.isEmpty()) {
            return 0;
        }
        long lineNumber = 0;
        while(true){
            CsvLineParser lineParser = lineParserFactory.makeLineParser(lineReader);
            if(lineParser == null) {
                if(lineParserFactory.isEmpty())
                    return lineNumber; // No more parsers. We should not read any more. Leave rest of input as is.
                if(lineReader.eofReached())
                    return lineNumber;
                handleNoParser(lineReader, errorListener);
                continue;
            }
            if(!lineParser.parse(lineReader, listener, errorListener))
                return lineNumber;
            if(!lineReader.lastLineWasEmpty())
                lineNumber++;
        }

    }

    public Stream<Line> stream(boolean parallel, Consumer<JSaParException> errorListener) throws IOException {
        if(schema.isEmpty()) {
            return Stream.empty();
        }
        try {
            Spliterator<Line> spliterator = new Spliterator<>() {
                @Override
                public boolean tryAdvance(Consumer<? super Line> action) {
                    try {
                        CsvLineParser lineParser = lineParserFactory.makeLineParser(lineReader);
                        if (lineParser == null) {
                            if (lineParserFactory.isEmpty() || lineReader.eofReached())
                                return false;
                            handleNoParser(lineReader, errorListener);
                            return true;
                        }
                        return lineParser.parse(lineReader, action, errorListener);
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                }

                @Override
                public Spliterator<Line> trySplit() {
                    return null;
                }

                @Override
                public long estimateSize() {
                    return 0;
                }

                @Override
                public int characteristics() {
                    return 0;
                }
            };

            return StreamSupport.stream(spliterator, parallel);
        }catch (UncheckedIOException e){
            throw e.getCause();
        }

    }
    private void handleNoParser(CsvLineReader lineReader, Consumer<JSaParException> errorEventListener)
            throws IOException {
        if (lineReader.lastLineWasEmpty())
            return;
        validationHandler.lineValidation(
                lineReader.currentLineNumber(), parseConfig.getOnUndefinedLineType(), errorEventListener,
                ()->"No schema line could be used to parse line number " + lineReader.currentLineNumber());
        lineReader.skipLine();
    }


}
