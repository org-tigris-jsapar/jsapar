package org.jsapar.parse;

import java.io.IOException;

import org.jsapar.error.JSaParException;

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
