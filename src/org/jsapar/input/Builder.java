package org.jsapar.input;

import java.io.IOException;
import java.util.List;

import org.jsapar.JSaParException;


public class Builder {

    /**
     * Reads characters from the reader and parses them into a Document. If
     * there is an error while parsing, this method will throw an exception of
     * type org.jsapar.input.ParseException.
     * 
     * @param reader
     * @param schema
     * @return
     * @throws JSaParException
     */
    public org.jsapar.Document build(java.io.Reader reader,
	    Parser documentBuilder) throws JSaParException {
	return this.build(reader, documentBuilder, null);
    }

    /**
     * Reads characters from the reader and parses them into a Document. If
     * there is an error while parsing a cell, this method will add a
     * CellParseError object to the supplied parseError list. If there is an
     * error reading the input or an error at the structure of the input, a
     * JSaParException will be thrown.
     * 
     * @param reader
     * @param schema
     * @param parseErrors
     * @return
     * @throws JSaParException
     */
    public org.jsapar.Document build(java.io.Reader reader,
	    Parser documentBuilder, List<CellParseError> parseErrors)
	    throws JSaParException {
	try {
	    return documentBuilder.build(reader, parseErrors);
	} catch (IOException e) {
	    throw new ParseException("Failed to read input", e);
	}
    }

}
