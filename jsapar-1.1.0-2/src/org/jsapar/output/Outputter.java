/** 
 * Copyright: Jonas Stenberg
 */
package org.jsapar.output;

import java.io.IOException;
import java.util.Iterator;

import org.jsapar.Document;
import org.jsapar.JSaParException;
import org.jsapar.Line;
import org.jsapar.schema.CsvSchemaLine;
import org.jsapar.schema.Schema;
import org.jsapar.schema.SchemaLine;

/**
 * This class contains methods for transforming a Document into an output. E.g. if you want to write
 * the Document to a file you should first set the schema, then use a {@link java.io.FileWriter} and
 * call the {@link #output(Document, java.io.Writer)} method.
 * 
 * @author Jonas Stenberg
 * 
 */
public class Outputter {
    private Schema schema;

    /**
     * Creates an Outputter with a schema.
     * 
     * @param schema
     */
    public Outputter(Schema schema) {
        this.schema = schema;
    }

    /**
     * Writes the document to a {@link java.io.Writer} according to the schemas of this outputter.
     * Note that you have to add at least one schema to the instance of Outputter before calling
     * this method.
     * 
     * @param document
     * @param writer
     * @throws JSaParException
     */
    public void output(Document document, java.io.Writer writer) throws JSaParException {
        try {
            Iterator<Line> itLines = document.getLineIterator();
            schema.outputBefore(writer);
            schema.output(itLines, writer);
            schema.outputAfter(writer);
            if (!itLines.hasNext())
                return;
        } catch (IOException e) {
            throw new OutputException("Failed to write to buffert.", e);
        }
    }

    /**
     * Writes the document to a {@link java.io.Writer} according to the supplied schema.
     * 
     * @deprecated Deprecated since release 0.4.0. Use output(Document, Writer) instead.
     * @param document
     * @param schema
     * @param writer
     * @throws JSaParException
     */
    @Deprecated
    public void output(Document document, Schema schema, java.io.Writer writer) throws JSaParException {
        this.schema=schema;
        output(document, writer);
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
     * supplied line.
     * 
     * @param line
     * @param schema
     * @param lineNumberForSchema
     *            The line number for this schema. If you use multiple schemas in sequence, the
     *            number should only count the number of lines written with the supplied schema.
     * @param writer
     * @throws JSaParException
     */
    public void outputLine(Line line, Schema schema, long lineNumberForSchema, java.io.Writer writer)
            throws JSaParException {
        try {
            schema.outputLine(line, lineNumberForSchema, writer);
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
