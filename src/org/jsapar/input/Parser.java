package org.jsapar.input;

import java.io.IOException;
import java.util.List;

import org.jsapar.Document;
import org.jsapar.JSaParException;


public interface Parser {

    /**
     * This method should only be called by a Builder class. Don't use this
     * directly in your code. Use a Builder instead.
     * 
     * @param reader
     * @param parseErrors
     * @return
     * @throws IOException
     * @throws JSaParException
     */
    public abstract Document build(java.io.Reader reader, List<CellParseError> parseErrors)
	    throws IOException, JSaParException;

}
