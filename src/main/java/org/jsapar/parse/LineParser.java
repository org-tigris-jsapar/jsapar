package org.jsapar.parse;

import java.io.BufferedReader;
import java.io.IOException;

import org.jsapar.JSaParException;
import org.jsapar.model.Line;
import org.jsapar.parse.LineParsedEvent;
import org.jsapar.parse.LineEventListener;
import org.jsapar.schema.SchemaLine;

public interface LineParser {

    /**
     * @param nLineNumber
     * @param listener
     * @return
     * @throws IOException 
     * @throws JSaParException 
     */
    boolean parse(long nLineNumber, LineEventListener listener) throws JSaParException, IOException;

}
