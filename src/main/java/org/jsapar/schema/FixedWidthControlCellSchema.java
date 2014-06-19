package org.jsapar.schema;

import java.io.IOException;
import java.io.Reader;
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
    private boolean writeControlCell = true;

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
     * @param reader
     * @return The controll cell value that was read.
     * @throws JSaParException
     */
    private String readControlCell(Reader reader) throws JSaParException {
        try {
            char[] controlCellBuffer = new char[getControlCellLength()];
            int nRead = reader.read(controlCellBuffer, 0, getControlCellLength());
            if (nRead < getControlCellLength()) {
                return null; // End of stream.
            }
            return new String(controlCellBuffer);
        } catch (IOException ex) {
            throw new JSaParException("Failed to read control cell.", ex);
        }
    }
    
    /**
     * @param reader
     * @param nLineNumber
     * @return a line schema that matches the control value at the beginning of the line. Returns null if end of stream is reached.
     * @throws JSaParException if no matching schema is found.
     */
    private FixedWidthSchemaLine findSchemaLine(Reader reader, long nLineNumber) throws JSaParException{
        String sControlCell = readControlCell(reader);
        if(sControlCell == null)
            return null;

        FixedWidthSchemaLine lineSchema = getSchemaLineByControlValue(sControlCell);
        if (lineSchema == null) {
            CellParseError error = new CellParseError(nLineNumber, "Control cell", sControlCell, null,
                    "Invalid Line-type: " + sControlCell);
            throw new ParseException(error);
        }
        return lineSchema;
    }

    /* (non-Javadoc)
     * @see org.jsapar.schema.FixedWidthSchema#parseByOccursFlatFile(java.io.Reader, org.jsapar.input.ParsingEventListener)
     */
    @Override
    protected void parseByOccursFlatFile(Reader reader, ParsingEventListener listener) throws IOException,
            JSaParException {

        long nLineNumber = 0; // First line is 1
        while (true) {
            nLineNumber++;

            FixedWidthSchemaLine lineSchema = findSchemaLine(reader, nLineNumber);
            if (lineSchema == null)
                return;
            boolean isLineFound = lineSchema.parse(nLineNumber, reader, listener);
            if (!isLineFound) {
                return; // End of stream.
            }

        }
    }

    /* (non-Javadoc)
     * @see org.jsapar.schema.FixedWidthSchema#parseByOccursLinesSeparated(java.io.Reader, org.jsapar.input.ParsingEventListener)
     */
    @Override
    protected void parseByOccursLinesSeparated(Reader reader, ParsingEventListener listener) throws IOException,
            JSaParException {
        long nLineNumber = 0; // First line is 1
        while (true) {
            nLineNumber++;
            FixedWidthSchemaLine lineSchema = findSchemaLine(reader, nLineNumber);
            if (lineSchema == null)
                return;

            String sLine = parseLine(reader);
            if (sLine == null)
                return; // End of buffer
            boolean isLineFound = lineSchema.parse(nLineNumber, sLine, listener);
            if (!isLineFound) {
                return; // End of stream.
            }

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
            SchemaLine schemaLine = getSchemaLine(line.getLineType());
            if (schemaLine == null)
                throw new JSaParException("Could not find schema line of type " + line.getLineType());
            outputLine(schemaLine, line, writer);

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
    private boolean writeControlCell(Writer writer, String controlValue) throws OutputException, IOException {
        if(writeControlCell){
            FixedWidthSchemaCell.output(controlValue, writer, ' ', getControlCellLength(), getControlCellAlignment());
            return true;
        }
        return false;
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

    public FixedWidthControlCellSchema clone()  {
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

    
    /* (non-Javadoc)
     * @see org.jsapar.schema.Schema#outputLine(org.jsapar.schema.SchemaLine, org.jsapar.Line, java.io.Writer)
     */
    @Override
    protected void outputLine(SchemaLine schemaLine, Line line, Writer writer) throws IOException, JSaParException {
        int offset=0;
        if(writeControlCell(writer, schemaLine.getLineTypeControlValue())){
            offset = controlCellLength;
        }
        ((FixedWidthSchemaLine)schemaLine).output(line, writer, offset);
    }

    /**
     * @return the writeControlCell
     */
    public boolean isWriteControlCell() {
        return writeControlCell;
    }

    /**
     * @param writeControlCell the writeControlCell to set
     */
    public void setWriteControlCell(boolean writeControlCell) {
        this.writeControlCell = writeControlCell;
    }

    /* (non-Javadoc)
     * @see org.jsapar.schema.Schema#writeLinePrefix(org.jsapar.schema.SchemaLine, java.io.Writer)
     */
    @Override
    public void writeLinePrefix(SchemaLine schemaLine, Writer writer) throws OutputException, IOException {
        writeControlCell(writer, schemaLine.getLineTypeControlValue());
    }
    
    
}
