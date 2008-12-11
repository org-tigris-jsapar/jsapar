/** 
 * Copyright: Jonas Stenberg
 */
package org.jsapar.output;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jsapar.Cell;
import org.jsapar.Document;
import org.jsapar.JSaParException;
import org.jsapar.Line;
import org.jsapar.schema.CsvSchemaLine;
import org.jsapar.schema.Schema;
import org.jsapar.schema.SchemaLine;

/**
 * This class contains methods for transforming a Document into an output. E.g. if you want to write
 * the Document to a file you should use a {@link java.io.FileWriter} together with a Schema and
 * call the {@link #output(Document, Schema, java.io.Writer)} method.
 * 
 * @author Jonas Stenberg
 * 
 */
public class Outputter {

    /**
     * Writes the document to a {@link java.io.Writer} according to the supplied schema.
     * 
     * @param document
     * @param schema
     * @param writer
     * @throws JSaParException
     */
    public void output(Document document, Schema schema, java.io.Writer writer) throws JSaParException {
	try {
	    schema.outputBefore(writer);
	    schema.output(document, writer);
	    schema.outputAfter(writer);
	} catch (IOException e) {
	    throw new OutputException("Failed to write to buffert.", e);
	}
    }

    /**
     * Writes the single line to a {@link java.io.Writer} according to the supplied schema line.
     * 
     * @param line
     * @param schemaLine
     * @param writer
     * @throws JSaParException
     */
    public void outputLine(Line line, SchemaLine schemaLine, java.io.Writer writer) throws JSaParException {
	try {
	    schemaLine.output(line, writer);
	} catch (IOException e) {
	    throw new OutputException("Failed to write to buffert.", e);
	}
    }

    /**
     * Writes the single line to a {@link java.io.Writer} according to the supplied schema for the
     * supplied line number.
     * 
     * @param line
     * @param schema
     * @param lineNumber
     * @param writer
     * @throws JSaParException
     */
    public void outputLine(Line line, Schema schema, long lineNumber, java.io.Writer writer) throws JSaParException {
	try {
	    schema.outputLine(line, lineNumber, writer);
	} catch (IOException e) {
	    throw new OutputException("Failed to write to buffert.", e);
	}
    }

    /**
     * Writes the header line if the first line is schema.
     * 
     * @param schemaLine
     * @param writer
     * @throws JSaParException
     */
    public void outputCsvHeaderLine(CsvSchemaLine schemaLine, java.io.Writer writer) throws JSaParException {
	if (!schemaLine.isFirstLineAsSchema())
	    throw new JSaParException("The schema line is not of type where first line is schema.");

	try {
	    schemaLine.outputHeaderLine(writer);
	} catch (IOException e) {
	    throw new OutputException("Failed to write to buffert.", e);
	}
    }

}
