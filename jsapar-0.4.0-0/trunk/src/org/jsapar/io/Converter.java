package org.jsapar.io;

import java.io.IOException;
import java.io.Writer;
import java.util.LinkedList;
import java.util.List;

import org.jsapar.JSaParException;
import org.jsapar.Line;
import org.jsapar.input.CellParseError;
import org.jsapar.input.LineErrorEvent;
import org.jsapar.input.LineParsedEvent;
import org.jsapar.input.ParseSchema;
import org.jsapar.input.ParsingEventListener;
import org.jsapar.schema.Schema;

/**
 * Reads buffer using an input schema and writes to another buffer using an output schema. By adding
 * a LineManipulator you are able to make modifications of each line before it is written to the
 * output. The method manipulate() of all added LineManipulators are called for each line that are
 * parsed successfully.
 * 
 * @author stejon0
 * 
 */
public class Converter {

    private List<LineManipulator> manipulators = new java.util.LinkedList<LineManipulator>();
    private ParseSchema inputSchema;
    private Schema outputSchema;

    /**
     * Creates a Converter object with the specified schemas.
     * 
     * @param inputSchema
     * @param outputSchema
     */
    public Converter(ParseSchema inputSchema, Schema outputSchema) {
        this.inputSchema = inputSchema;
        this.outputSchema = outputSchema;
    }

    /**
     * Adds LineManipulator to this converter. All present line manipulators are executed for each
     * line.
     * 
     * @param manipulator
     */
    public void addLineManipulator(LineManipulator manipulator) {
        manipulators.add(manipulator);
    }

    /**
     * @param reader
     * @param writer
     * @return A list of CellParseErrors or an empty list if there were no errors.
     * @throws IOException
     * @throws JSaParException
     */
    public java.util.List<CellParseError> convert(java.io.Reader reader, java.io.Writer writer) throws IOException,
            JSaParException {

        DocumentWriter outputter = new DocumentWriter(outputSchema, writer);
        // TODO Create a DocumentWriter that supports line type by control cell.

        outputSchema.outputBefore(writer);
        inputSchema.parse(reader, outputter);
        outputSchema.outputAfter(writer);
        return outputter.getParseErrors();

    }

    /**
     * Internal class for handling output of one line at a time while receiving parsing events.
     * 
     * @author stejon0
     * 
     */
    private class DocumentWriter implements ParsingEventListener {
        private List<CellParseError> parseErrors = new LinkedList<CellParseError>();
        private Schema outputSchema;
        private java.io.Writer writer;

        public DocumentWriter(Schema outputSchema, Writer writer) throws JSaParException {
            this.outputSchema = outputSchema;
            this.writer = writer;
        }

        @Override
        public void lineErrorErrorEvent(LineErrorEvent event) {
            parseErrors.add(event.getCellParseError());
        }

        @Override
        public void lineParsedEvent(LineParsedEvent event) throws JSaParException {
            try {
                Line line = event.getLine();
                for (LineManipulator manipulator : manipulators) {
                    manipulator.manipulate(line);
                }
                outputSchema.outputLine(line, event.getLineNumber(), this.writer);
            } catch (IOException e) {
                throw new JSaParException("Failed to write to writer", e);
            }
        }

        /**
         * @return the parseErrors
         */
        public List<CellParseError> getParseErrors() {
            return parseErrors;
        }

    }

    /**
     * @return the inputSchema
     */
    public ParseSchema getInputSchema() {
        return inputSchema;
    }

    /**
     * @param inputSchema
     *            the inputSchema to set
     */
    public void setInputSchema(ParseSchema inputSchema) {
        this.inputSchema = inputSchema;
    }

    /**
     * @return the outputSchema
     */
    public Schema getOutputSchema() {
        return outputSchema;
    }

    /**
     * @param outputSchema
     *            the outputSchema to set
     */
    public void setOutputSchema(Schema outputSchema) {
        this.outputSchema = outputSchema;
    }

}
