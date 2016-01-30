package org.jsapar;

import java.io.IOException;
import java.io.Writer;
import java.util.LinkedList;
import java.util.List;

import org.jsapar.convert.LineManipulator;
import org.jsapar.convert.MaxErrorsExceededException;
import org.jsapar.parse.*;
import org.jsapar.model.Line;
import org.jsapar.parse.LineEventListener;
import org.jsapar.parse.SchemaParserFactory;
import org.jsapar.schema.Schema;

/**
 * Reads buffer using an input schema and writes to another buffer using an output schema. By adding
 * a LineManipulator you are able to make modifications of each line before it is written to the
 * output. The method manipulate() of all added LineManipulators are called for each line that are
 * parsed successfully.
 * 
 * @see FilterConverter
 * 
 * @author stejon0
 * 
 */
public class Converter {

    private List<LineManipulator> manipulators = new java.util.LinkedList<LineManipulator>();
    private ParseSchema           inputSchema;
    private Schema                outputSchema;
    private int                   maxNumberOfErrors = Integer.MAX_VALUE;
    private SchemaParserFactory   parserFactory     = new SchemaParserFactory();

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

        DocumentWriter outputter = new DocumentWriter(writer);

        return doConvert(reader, writer, outputter);

    }

    /**
     * @param reader
     * @param writer
     * @param outputter
     * @return A list of errors that occurred during conversion.
     * @throws IOException
     * @throws JSaParException
     */
    protected java.util.List<CellParseError> doConvert(java.io.Reader reader,
                                                       java.io.Writer writer,
                                                       DocumentWriter outputter) throws IOException, JSaParException {
        outputSchema.writeBefore(writer);
        parserFactory .makeParser(inputSchema, reader).parse(outputter);
        outputSchema.writeAfter(writer);
        return outputter.getParseErrors();
    }

    /**
     * Internal class for handling output of one line at a time while receiving parsing events.
     * 
     * @author stejon0
     * 
     */
    protected class DocumentWriter implements LineEventListener {
        private List<CellParseError> parseErrors = new LinkedList<CellParseError>();
        private java.io.Writer       writer;

        public DocumentWriter(Writer writer) throws JSaParException {
            this.writer = writer;
        }

        @Override
        public void lineErrorEvent(LineErrorEvent event) throws MaxErrorsExceededException {
            parseErrors.add(event.getCellParseError());
            if (parseErrors.size() > maxNumberOfErrors)
                throw new MaxErrorsExceededException(parseErrors);
        }

        @Override
        public void lineParsedEvent(LineParsedEvent event) throws JSaParException {
            try {
                Line line = event.getLine();
                for (LineManipulator manipulator : manipulators) {
                    manipulator.manipulate(line);
                }
                outputSchema.writeLine(line, event.getLineNumber(), this.writer);
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

        /**
         * @return the writer
         */
        protected java.io.Writer getWriter() {
            return writer;
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



    /**
     * @return the manipulators
     */
    protected List<LineManipulator> getManipulators() {
        return manipulators;
    }

    /**
     * When the number of errors exceeds maxNumberOfErrors, an MaxErrorsExceededException exception
     * is thrown.
     * 
     * @param maxNumberOfErrors
     *            the maxNumberOfErrors to set
     */
    public void setMaxNumberOfErrors(int maxNumberOfErrors) {
        this.maxNumberOfErrors = maxNumberOfErrors;
    }

    /**
     * @return the maxNumberOfErrors
     */
    public int getMaxNumberOfErrors() {
        return maxNumberOfErrors;
    }

    public SchemaParserFactory getParserFactory() {
        return parserFactory;
    }

    public void setParserFactory(SchemaParserFactory parserFactory) {
        this.parserFactory = parserFactory;
    }

}
