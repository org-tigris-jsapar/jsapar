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
import java.io.StringReader;

/**
 * @author stejon0
 *
 */
public class FixedWidthParserFlat extends FixedWidthParser{


    public FixedWidthParserFlat(Reader reader, FixedWidthSchema schema) {
        super(reader, schema);
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
            FixedWidthLineParser lineParser = getLineParserFactory().makeLineParser(getReader());
            if(lineParser == null) {
                handleNoParser(getReader(), lineNumber);
                return;
            }
            boolean lineFound = lineParser.parse(getReader(), lineNumber, listener);
            if (!lineFound)
                return; // End of stream.
        }
    }

}
