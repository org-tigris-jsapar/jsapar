package org.jsapar.schema;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;

import org.jsapar.JSaParException;
import org.jsapar.model.Line;
import org.jsapar.compose.ComposeException;
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
    
    private boolean errorIfUndefinedLineType = true;

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
        final String trimmedControlValue = sLineTypeControlValue.trim();
        for (FixedWidthSchemaLine lineSchema : getFixedWidthSchemaLines()) {
            if (lineSchema.getLineTypeControlValue().equals(trimmedControlValue)) {
                return lineSchema;
            }
        }
        return null;
    }




 
    /*
     * (non-Javadoc)
     * 
     * @see org.jsapar.schema.FixedWidthSchema#output(org.jsapar.model.Document, java.io.Writer)
     */
    public void write(Iterator<Line> itLines, Writer writer) throws IOException, JSaParException {

        while (itLines.hasNext()) {
            Line line = itLines.next();
            SchemaLine schemaLine = getSchemaLine(line.getLineType());
            if (schemaLine == null)
                throw new JSaParException("Could not find schema line of type " + line.getLineType());
            writeLine(schemaLine, line, writer);

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
     * @throws ComposeException
     * @throws IOException
     */
    private boolean writeControlCell(Writer writer, String controlValue) throws ComposeException, IOException {
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
     * @see org.jsapar.schema.Schema#outputLine(org.jsapar.schema.SchemaLine, org.jsapar.model.Line, java.io.Writer)
     */
    @Override
    protected void writeLine(SchemaLine schemaLine, Line line, Writer writer) throws IOException, JSaParException {
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
    public void writeLinePrefix(SchemaLine schemaLine, Writer writer) throws ComposeException, IOException {
        writeControlCell(writer, schemaLine.getLineTypeControlValue());
    }

    /* (non-Javadoc)
     * @see org.jsapar.schema.FixedWidthSchema#addFillerCellsToReachLineMinLength()
     */
    @Override
    public void addFillerCellsToReachLineMinLength() {
        for (FixedWidthSchemaLine lineSchema : getFixedWidthSchemaLines()) {
            lineSchema.addFillerCellToReachMinLength(this.controlCellLength);
        }
    }

    /**
     * @return  true if there will be an error while parsing and the control cell does not match any defined line type.
     * false if undefined line types are silently ignored.
     */
    public boolean isErrorIfUndefinedLineType() {
        return errorIfUndefinedLineType;
    }

    /**
     * Set to true if there should be an error while parsing and the control cell does not match any defined line type.
     * Set to false if undefined line types should be silently ignored.
     * @param errorIfUndefinedLineType
     */
    public void setErrorIfUndefinedLineType(boolean errorIfUndefinedLineType) {
        this.errorIfUndefinedLineType = errorIfUndefinedLineType;
    }
    

    
}
