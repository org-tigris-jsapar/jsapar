package org.jsapar.schema;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;

import org.jsapar.JSaParException;
import org.jsapar.Line;
import org.jsapar.input.CellParseError;
import org.jsapar.input.ParseException;
import org.jsapar.input.ParsingEventListener;
import org.jsapar.output.OutputException;
import org.jsapar.schema.FixedWidthSchemaCell.Alignment;

/**
 * Defines a schema for a fixed position buffer where the type of the line is determined by the
 * leading characters (control cell) at each line. Each cell is defined by a fixed number of
 * characters. Each line is separated by the line separator defined in the base class {@link Schema}
 * .
 * 
 * If the end of line is reached before all cells are parsed the remaining cells will not be set.
 * 
 * If there are remaining characters when the end of line is reached, those characters will be
 * omitted.
 * 
 * If the line separator is an empty string, the lines will be separated by the sum of the length of
 * the cells within the schema.
 * 
 * @author Jonas Stenberg
 * 
 */
public class FixedWidthControlCellSchema extends FixedWidthSchema {

    private int controlCellLength;
    private FixedWidthSchemaCell.Alignment controlCellAlignment = Alignment.LEFT;

    /**
     * 
     */
    public FixedWidthControlCellSchema() {
        this(0, Alignment.LEFT);
    }

    /**
     * Creates a new schema which uses control cell with the supplied length.
     * 
     * @param controlCellLength
     */
    public FixedWidthControlCellSchema(int controlCellLength) {
        this(controlCellLength, Alignment.LEFT);
    }

    /**
     * Creates a new schema which uses control cell with the supplied length and allignment.
     * 
     * @param controlCellLength
     * @param controlCellAlignment
     */
    public FixedWidthControlCellSchema(int controlCellLength, FixedWidthSchemaCell.Alignment controlCellAlignment) {
        this.controlCellLength = controlCellLength;
        this.controlCellAlignment = controlCellAlignment;
    }

    /**
     * @param sLineTypeControlValue
     * @return A schema line of type FixedWitdthSchemaLine which has the supplied line type.
     */
    public FixedWidthSchemaLine getSchemaLineByControlValue(String sLineTypeControlValue) {
        for (FixedWidthSchemaLine lineSchema : getFixedWidthSchemaLines()) {
            if (lineSchema.getLineTypeControlValue().equals(sLineTypeControlValue)) {
                return lineSchema;
            }
        }
        return null;
    }

    /**
     * @param sLineType
     * @return A schema line of type FixedWitdthSchemaLine which has the supplied line type.
     */
    public FixedWidthSchemaLine getSchemaLineByType(String sLineType) {
        for (FixedWidthSchemaLine lineSchema : getFixedWidthSchemaLines()) {
            if (lineSchema.getLineType().equals(sLineType)) {
                return lineSchema;
            }
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jsapar.schema.FixedWidthSchema#parse(java.io.Reader,
     * org.jsapar.input.ParsingEventListener)
     */
    @Override
    public void parse(java.io.Reader reader, ParsingEventListener listener) throws JSaParException {
        char[] lineSeparatorBuffer = new char[getLineSeparator().length()];
        char[] controlCellBuffer = new char[getControlCellLength()];
        FixedWidthSchemaLine lineSchema = null;
        long nLineNumber = 0; // First line is 1
        try {
            do {
                nLineNumber++;
                int nRead = reader.read(controlCellBuffer, 0, getControlCellLength());
                if (nRead < getControlCellLength()) {
                    break; // End of stream.
                }
                String sControlCell = new String(controlCellBuffer);
                if (lineSchema == null || !lineSchema.getLineTypeControlValue().equals(sControlCell)) {
                    lineSchema = getSchemaLineByControlValue(sControlCell);
                }
                if (lineSchema == null) {
                    CellParseError error = new CellParseError(nLineNumber, "Control cell", sControlCell, null,
                            "Invalid Line-type: " + sControlCell);
                    throw new ParseException(error);
                }

                boolean isLineFound = lineSchema.parse(nLineNumber, reader, listener);
                if (!isLineFound) {
                    break; // End of stream.
                }
                if (getLineSeparator().length() > 0) {
                    nRead = reader.read(lineSeparatorBuffer, 0, getLineSeparator().length());
                    if (nRead < getLineSeparator().length()) {
                        break; // End of stream.
                    }
                    String sSeparator = new String(lineSeparatorBuffer);
                    if (!sSeparator.equals(getLineSeparator())) {
                        CellParseError error = new CellParseError(nLineNumber, "End-of-line", sSeparator, null,
                                "Unexpected characters '" + sSeparator + "' found when expecting line separator.");
                        throw new ParseException(error);
                    }
                }

            } while (true);
        } catch (IOException ex) {
            throw new JSaParException("Failed to read control cell.", ex);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jsapar.schema.FixedWidthSchema#output(org.jsapar.Document, java.io.Writer)
     */
    @Override
    public void output(Iterator<Line> itLines, Writer writer) throws IOException, JSaParException {

        while (itLines.hasNext()) {
            Line line = itLines.next();
            FixedWidthSchemaLine schemaLine = getSchemaLineByType(line.getLineType());
            if (schemaLine == null)
                throw new JSaParException("Could not find schema line of type " + line.getLineType());
            writeControlCell(writer, schemaLine.getLineTypeControlValue());
            schemaLine.output(line, writer);

            if (itLines.hasNext() && getLineSeparator().length() > 0) {
                writer.write(getLineSeparator());
            }
        }
    }

    /**
     * Writes the control cell to the buffer.
     * 
     * @param writer
     * @param controlValue
     * @throws OutputException
     * @throws IOException
     */
    private void writeControlCell(Writer writer, String controlValue) throws OutputException, IOException {
        FixedWidthSchemaCell.output(controlValue, writer, ' ', getControlCellLength(), getControlCellAlignment());
    }

    /**
     * @return the controlCellAlignment
     */
    public FixedWidthSchemaCell.Alignment getControlCellAlignment() {
        return controlCellAlignment;
    }

    /**
     * @param controlCellAlignment
     *            the controlCellAlignment to set
     */
    public void setControlCellAlignment(FixedWidthSchemaCell.Alignment controlCellAlignment) {
        this.controlCellAlignment = controlCellAlignment;
    }

    /**
     * @return the controlCellLength
     */
    public int getControlCellLength() {
        return controlCellLength;
    }

    /**
     * @param controlCellLength
     *            the controlCellLength to set
     */
    public void setControlCellLength(int controlCellLength) {
        this.controlCellLength = controlCellLength;
    }

    public FixedWidthControlCellSchema clone() throws CloneNotSupportedException {
        FixedWidthControlCellSchema schema = (FixedWidthControlCellSchema) super.clone();
        return schema;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jsapar.schema.Schema#toString()
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString());

        sb.append(" controlCellAlignment=");
        sb.append(this.controlCellAlignment);
        sb.append(" controlCellLength=");
        sb.append(this.controlCellLength);

        return sb.toString();
    }

    @Override
    public boolean outputLine(Line line, long lineNumber, Writer writer) throws IOException, JSaParException {
        SchemaLine schemaLine = getSchemaLineByType(line.getLineType());
        if (schemaLine == null)
            return false;
        
        if (lineNumber > 1)
            writer.append(getLineSeparator());
        writeControlCell(writer, schemaLine.getLineTypeControlValue());
        schemaLine.output(line, writer);
        return true;
    }
}
