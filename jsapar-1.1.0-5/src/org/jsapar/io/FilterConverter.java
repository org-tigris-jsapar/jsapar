/**
 * 
 */
package org.jsapar.io;

import java.io.IOException;
import java.io.Writer;

import org.jsapar.JSaParException;
import org.jsapar.Line;
import org.jsapar.input.CellParseError;
import org.jsapar.input.LineParsedEvent;
import org.jsapar.input.ParseSchema;
import org.jsapar.schema.Schema;

/**
 * Reads buffer using an input schema and writes to another buffer using an output schema. By adding
 * a LineManipulator you are able to make modifications of each line before it is written to the
 * output. The method manipulate() of all added LineManipulators are called for each line that are
 * parsed successfully.
 * <p/>
 * 
 * This class differes from the Converter in that for each line, the line type of the parsed line is
 * considered when choosing the line type of the output schema line. This means that lines with a
 * type that does not exist in the output schema will be discarded in the output.
 * <p/>
 * Also it is possible to add your own line filter which can cause lines to be discarded from the
 * output depending of their contents. Add your own implementation of LineFilter in order to be able
 * to discard lines.
 * 
 * @author stejon0
 * 
 */
public class FilterConverter extends Converter {

    private LineFilter lineFilter = new LineFilter() {
        @Override
        public boolean shouldWrite(Line line) throws JSaParException {
            return true;
        }
    };

    /**
     * @param inputSchema
     * @param outputSchema
     */
    public FilterConverter(ParseSchema inputSchema, Schema outputSchema) {
        super(inputSchema, outputSchema);
        // TODO Auto-generated constructor stub
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

        DocumentWriter outputter = new FilterDocumentWriter(writer);
        // TODO Create a DocumentWriter that supports line type by control cell.

        return doConvert(reader, writer, outputter);
    }

    /**
     * @param lineFilter
     *            the lineFilter to set
     */
    public void setLineFilter(LineFilter lineFilter) {
        this.lineFilter = lineFilter;
    }

    /**
     * @return the lineFilter
     */
    public LineFilter getLineFilter() {
        return lineFilter;
    }

    /**
     * Internal class for handling output of one line at a time while receiving parsing events.
     * 
     * @author stejon0
     * 
     */
    protected class FilterDocumentWriter extends DocumentWriter {

        public FilterDocumentWriter(Writer writer) throws JSaParException {
            super(writer);
        }

        @Override
        public void lineParsedEvent(LineParsedEvent event) throws JSaParException {
            try {
                Line line = event.getLine();
                if (!lineFilter.shouldWrite(line)) {
                    return;
                }
                for (LineManipulator manipulator : getManipulators()) {
                    manipulator.manipulate(line);
                }
                getOutputSchema().outputLine(line, getWriter());
            } catch (IOException e) {
                throw new JSaParException("Failed to write to writer", e);
            }
        }
    }

}
