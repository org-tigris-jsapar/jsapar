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

}
