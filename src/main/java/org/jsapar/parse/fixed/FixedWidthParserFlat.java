/**
 * 
 */
package org.jsapar.parse.fixed;

import org.jsapar.error.ErrorEventListener;
import org.jsapar.error.JSaParException;
import org.jsapar.model.Line;
import org.jsapar.parse.LineEventListener;
import org.jsapar.parse.LineParsedEvent;
import org.jsapar.parse.ParseConfig;
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
    public FixedWidthParserFlat(Reader reader, FixedWidthSchema schema) {
        this(reader, schema, new ParseConfig());
    }

    /**
     * Creates a fixed width parser for flat files without line separators.
     * @param reader The reader to use.
     * @param schema The schema to use.
     * @param config Configuration while parsing.
     */
    public FixedWidthParserFlat(Reader reader, FixedWidthSchema schema, ParseConfig config) {
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
     * @throws JSaParException
     * @throws java.io.IOException
     */
    @Override
    public void parse(LineEventListener lineEventListener, ErrorEventListener errorListener) throws JSaParException, IOException {
        long lineNumber = 0;
        while(true){
            lineNumber++;
            if(getLineParserFactory().isEmpty())
                return;
            FWLineParserFactory.LineParserResult result = getLineParserFactory().makeLineParser(reader);
            if (result.result != LineParserMatcherResult.SUCCESS) {
                handleNoParser(lineNumber, result.result, errorListener);
                if(result.result == LineParserMatcherResult.NOT_MATCHING)
                    continue;
                else
                    return;
            }
            Line line = result.lineParser.parse(reader, lineNumber, errorListener);
            if (line != null)
                lineEventListener.lineParsedEvent(new LineParsedEvent(this, line));
            else
                return; // End of stream.
        }
    }

}
