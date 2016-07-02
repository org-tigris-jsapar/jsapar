/**
 * 
 */
package org.jsapar.parse.fixed;

import org.jsapar.JSaParException;
import org.jsapar.parse.*;
import org.jsapar.schema.FixedWidthSchema;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

/**
 * @author stejon0
 *
 */
public class FixedWidthParserFlat extends FixedWidthParser{

    private BufferedReader reader;


    public FixedWidthParserFlat(Reader reader, FixedWidthSchema schema) {
        super(schema);
        this.reader = new BufferedReader(reader);
    }

    /**
     * Sends line parce events to the supplied listener while parsing.
     *
     * @param listener
     *            The listener which will receive events for each parsed line.
     * @throws org.jsapar.JSaParException
     * @throws java.io.IOException
     */
    @Override
    public void parse(LineEventListener listener) throws JSaParException, IOException {
        long lineNumber = 0;
        while(true){
            lineNumber++;
            if(getLineParserFactory().isEmpty())
                return;
            FWLineParserFactory.LineParserResult result = getLineParserFactory().makeLineParser(reader);
            if (result.result != LineParserMatcherResult.SUCCESS) {
                handleNoParser(lineNumber, result.result);
                if(result.result == LineParserMatcherResult.NOT_MATCHING)
                    continue;
                else
                    return;
            }
            boolean lineFound = result.lineParser.parse(reader, lineNumber, listener);
            if (!lineFound)
                return; // End of stream.
        }
    }

}
