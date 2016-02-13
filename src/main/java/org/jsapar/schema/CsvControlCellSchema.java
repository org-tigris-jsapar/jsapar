package org.jsapar.schema;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;

import org.jsapar.JSaParException;
import org.jsapar.model.Line;
import org.jsapar.compose.ComposeException;

/**
 * Defines a schema for a fixed position buffer. Each cell is defined by a fixed number of
 * characters. Each line is separated by the line separator defined in the base class {@link Schema}
 * 
 * @author Jonas
 * 
 */
public class CsvControlCellSchema extends CsvSchema {

    /**
     * Regular expression determining the separator between cells within a row.
     */
    private String controlCellSeparator = ";";
    private boolean writeControlCell = true;

    /**
     * @return the controlCellSeparator
     */
    public String getControlCellSeparator() {
        return controlCellSeparator;
    }

    /**
     * Sets the character sequence that separates each cell. This value can be overridden by setting
     * for each line. <br>
     * In output schemas the non-breaking space character '\u00A0' is not allowed since that
     * character is used to replace any occurrence of the separator within each cell.
     * 
     * @param controlCellSeparator
     *            the controlCellSeparator to set
     */
    public void setControlCellSeparator(String controlCellSeparator) {
        this.controlCellSeparator = controlCellSeparator;
    }

    /**
     * Writes the control cell to the buffer.
     * 
     * @param writer
     * @param controlValue
     * @throws ComposeException
     * @throws IOException
     */
    private void writeControlCell(Writer writer, String controlValue) throws ComposeException, IOException {
        if (writeControlCell) {
            writer.append(controlValue);
            writer.append(this.getControlCellSeparator());
        }
    }
    
    

    /**
     * @param sLineTypeControlValue
     * @return A schema line of type FixedWitdthSchemaLine which has the supplied line type control
     *         value.
     */
    public CsvSchemaLine getSchemaLineByControlValue(String sLineTypeControlValue) {
        for (CsvSchemaLine lineSchema : this.getCsvSchemaLines()) {
            if (lineSchema.getLineTypeControlValue().equals(sLineTypeControlValue))
                return lineSchema;
        }
        return null;
    }



    /*
     * (non-Javadoc)
     * 
     * @see org.jsapar.schema.CsvSchema#clone()
     */
    @Override
    public CsvControlCellSchema clone() {
        return (CsvControlCellSchema) super.clone();
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
        sb.append(" controlCellSeparator='");
        sb.append(this.controlCellSeparator);
        sb.append("'");
        return sb.toString();
    }

    

    /**
     * @return the writeControlCell
     */
    public boolean isWriteControlCell() {
        return writeControlCell;
    }

    /**
     * @param writeControlCell
     *            the writeControlCell to set
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
    
}
