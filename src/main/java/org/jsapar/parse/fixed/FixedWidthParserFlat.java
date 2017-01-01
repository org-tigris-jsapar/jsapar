package org.jsapar.parse.fixed;

import org.jsapar.error.ErrorEventListener;
import org.jsapar.model.Line;
import org.jsapar.parse.LineEventListener;
import org.jsapar.parse.LineParsedEvent;
import org.jsapar.parse.text.TextParseConfig;
import org.jsapar.schema.FixedWidthSchema;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

/**
 * Parses fixed width text input with no line separator characters based on provided schema.
 */
public class FixedWidthParserFlat extends FixedWidthParser{

    private BufferedReader reader;

    /**
     * Mainly for testing, using default parse config.
     * @param reader The reader to use.
     * @param schema The schema to use.
     */
    FixedWidthParserFlat(Reader reader, FixedWidthSchema schema) {
        this(reader, schema, new TextParseConfig());
    }

    /**
     * Creates a fixed width parser for flat files without line separators.
     * @param reader The reader to use.
     * @param schema The schema to use.
     * @param config Configuration while parsing.
     */
    public FixedWidthParserFlat(Reader reader, FixedWidthSchema schema, TextParseConfig config) {
        super(schema, config);
        this.reader = new BufferedReader(reader);
    }

    /**
     * Sends line parce events to the supplied lineEventListener while parsing.
     *
     * @param lineEventListener
     *            The {@link LineEventListener} which will receive events for each parsed line.
     * @param errorListener
     * The {@link ErrorEventListener} that will receive events for each error.
     *
     * @throws java.io.IOException If there is an error reading from the input reader.
     */
    @Override
    public void parse(LineEventListener lineEventListener, ErrorEventListener errorListener) throws IOException {
        long lineNumber = 0;
        FWLineParserFactory lineParserFactory = new FWLineParserFactory(getSchema(), getConfig());
        while(true){
            lineNumber++;
            if(lineParserFactory.isEmpty())
                return;
            FixedWidthLineParser lineParser = lineParserFactory.makeLineParser(reader);
            if (lineParser == null) {
                handleNoParser(lineNumber, lineParserFactory.getLastResult(), errorListener);
                if(lineParserFactory.getLastResult() == LineParserMatcherResult.NOT_MATCHING)
                    continue;
                else
                    return;
            }
            Line line = lineParser.parse(reader, lineNumber, errorListener);
            if (line != null)
                lineEventListener.lineParsedEvent(new LineParsedEvent(this, line));
            else
                return; // End of stream.
        }
    }

}
