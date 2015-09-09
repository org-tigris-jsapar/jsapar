package org.jsapar.schema;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;

import org.jsapar.JSaParException;
import org.jsapar.Line;
import org.jsapar.output.OutputException;

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

    /*
     * (non-Javadoc)
     * 
     * @see org.jsapar.schema.CsvSchema#output(org.jsapar.Document, java.io.Writer)
     */
    @Override
    public void output(Iterator<Line> itLines, Writer writer) throws IOException, JSaParException {

        while (itLines.hasNext()) {
            Line line = itLines.next();
            SchemaLine schemaLine = getSchemaLine(line.getLineType());
            if (schemaLine == null)
                throw new JSaParException("Could not find schema line of type " + line.getLineType());
            writeControlCell(writer, schemaLine.getLineTypeControlValue());

            schemaLine.output(line, writer);

            if (itLines.hasNext())
                writer.write(getLineSeparator());
            else
                return;
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

    /*
     * (non-Javadoc)
     * 
     * @see org.jsapar.schema.Schema#output(org.jsapar.Line, int, java.io.Writer)
     */
    @Override
    public boolean outputLine(Line line, long lineNumber, Writer writer) throws IOException, JSaParException {
        SchemaLine schemaLine = getSchemaLine(line.getLineType());
        if (schemaLine == null)
            return false;

        if (lineNumber > 1)
            writer.append(getLineSeparator());
        outputLine(schemaLine, line, writer);
        return true;
    }
    
    

    /* (non-Javadoc)
     * @see org.jsapar.schema.Schema#outputLine(org.jsapar.schema.SchemaLine, org.jsapar.Line, java.io.Writer)
     */
    @Override
    protected void outputLine(SchemaLine schemaLine, Line line, Writer writer) throws IOException, JSaParException {
        writeControlCell(writer, schemaLine.getLineTypeControlValue());
        super.outputLine(schemaLine, line, writer);
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
    public void writeLinePrefix(SchemaLine schemaLine, Writer writer) throws OutputException, IOException {
        writeControlCell(writer, schemaLine.getLineTypeControlValue());
    }
    
}
