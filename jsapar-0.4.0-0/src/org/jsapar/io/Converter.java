package org.jsapar.io;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.Iterator;
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
    private List<ParseSchema> inputSchemas = new LinkedList<ParseSchema>();
    private List<Schema> outputSchemas = new LinkedList<Schema>();

    /**
     * Creates an empty Converter. Note that you have to add both input and output schemas before
     * calling the convert method.
     */
    public Converter() {
    }

    /**
     * Creates a Converter object with the specified schemas.
     * 
     * @param inputSchema
     * @param outputSchema
     */
    public Converter(ParseSchema inputSchema, Schema outputSchema) {
        this.inputSchemas.add(inputSchema);
        this.outputSchemas.add(outputSchema);
    }

    /**
     * Creates a Converter object with the specified multiple schemas. A file is parsed according to
     * each input schema one at a time. The output is produced according to each output schema one
     * at a time. <br>
     * <br>
     * Multiple input schemas will not work together with control cell schemas. It will continue to
     * use the first input schema for the complete file.<br>
     * Multiple output schemas will step to the next schema when no valid output schmea line is
     * found any more for the current output schema.
     * 
     * @param inputSchemas
     * @param outputSchemas
     */
    public Converter(Collection<Schema> inputSchemas, Collection<Schema> outputSchemas) {
        this.inputSchemas.addAll(inputSchemas);
        this.outputSchemas.addAll(outputSchemas);
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
     * @param schema
     */
    public void addInputSchema(ParseSchema schema) {
        inputSchemas.add(schema);
    }

    /**
     * @param schema
     */
    public void addOutputSchema(Schema schema) {
        outputSchemas.add(schema);
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

        DocumentWriter outputter = new DocumentWriter(outputSchemas, writer);

        outputter.outputBefore(writer);
        for (ParseSchema inputSchema : inputSchemas) {
            inputSchema.parse(reader, outputter);
        }
        outputter.outputAfter(writer);
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
        private Schema currentOutputSchema = null;
        private Iterator<Schema> outputSchemaIter;
        private java.io.Writer writer;
        private long lineNumberWithinSchema = 0;

        public DocumentWriter(Collection<Schema> outputSchemas, Writer writer) throws JSaParException {
            this.outputSchemaIter = outputSchemas.iterator();
            this.writer = writer;
        }

        public void outputBefore(Writer writer) throws IOException, JSaParException {
            incrementOutputSchema();
        }

        public void outputAfter(Writer writer) throws IOException, JSaParException {
            if (currentOutputSchema != null)
                currentOutputSchema.outputAfter(writer);

        }

        /**
         * @throws IOException
         * @throws JSaParException
         */
        private void incrementOutputSchema() throws IOException, JSaParException {
            if (currentOutputSchema != null)
                currentOutputSchema.outputAfter(writer);
            if (outputSchemaIter.hasNext()) {
                if (currentOutputSchema != null)
                    writer.append(currentOutputSchema.getLineSeparator());
                currentOutputSchema = outputSchemaIter.next();
                lineNumberWithinSchema = 0;
                currentOutputSchema.outputBefore(writer);
            } else
                currentOutputSchema = null;
        }

        @Override
        public void lineErrorErrorEvent(LineErrorEvent event) {
            parseErrors.add(event.getCellParseError());
        }

        @Override
        public void lineParsedEvent(LineParsedEvent event) throws JSaParException {
            // No point in processing further if the end of output schemas has been reached.
            if (currentOutputSchema == null)
                return;

            try {
                Line line = event.getLine();
                lineNumberWithinSchema++;
                for (LineManipulator manipulator : manipulators) {
                    manipulator.manipulate(line);
                }
                boolean success = currentOutputSchema.outputLine(line, lineNumberWithinSchema, this.writer);
                if (!success) {
                    // Try again with next output schema.
                    incrementOutputSchema();
                    lineNumberWithinSchema = 1;
                    if (currentOutputSchema != null)
                        currentOutputSchema.outputLine(line, lineNumberWithinSchema, this.writer);
                }
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

}
