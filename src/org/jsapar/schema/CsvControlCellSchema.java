package org.jsapar.schema;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Iterator;

import org.jsapar.Document;
import org.jsapar.JSaParException;
import org.jsapar.Line;
import org.jsapar.input.CellParseError;
import org.jsapar.input.LineParsedEvent;
import org.jsapar.input.ParseException;
import org.jsapar.input.ParsingEventListener;
import org.jsapar.output.OutputException;

/**
 * Defines a schema for a fixed position buffer. Each cell is defined by a fixed
 * number of characters. Each line is separated by the line separator defined in
 * the base class {@link Schema}
 * 
 * @author Jonas
 * 
 */
public class CsvControlCellSchema extends CsvSchema {

    /**
     * Regular expression determining the separator between cells within a row.
     */
    private String controlCellSeparator = ";";

    /**
     * @return the controlCellSeparator
     */
    public String getControlCellSeparator() {
	return controlCellSeparator;
    }

    /**
     * Sets the character sequence that separates each cell. This value can be
     * overridden by setting for each line. <br>
     * In output schemas the non-breaking space character '\u00A0' is not
     * allowed since that character is used to replace any occurrence of the
     * separator within each cell.
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
     * @see org.jsapar.schema.CsvSchema#output(org.jsapar.Document,
     * java.io.Writer)
     */
    @Override
    public void output(Document document, Writer writer) throws IOException, JSaParException {
	Iterator<Line> itLines = document.getLineIterator();

	while (itLines.hasNext()) {
	    Line line = itLines.next();
	    CsvSchemaLine schemaLine = getSchemaLineByType(line.getLineType());
	    if (schemaLine == null)
		throw new JSaParException("Could not find schema line of type "
			+ line.getLineType());
	    writeControlCell(writer, schemaLine.getLineTypeControlValue());

	    ((CsvSchemaLine) schemaLine).output(line, writer);

	    if (itLines.hasNext())
		writer.write(getLineSeparator());
	    else
		return;
	}
    }

    /**
     * Writes the control cell to the buffer.
     * @param writer
     * @param controlValue
     * @throws OutputException
     * @throws IOException
     */
    private void writeControlCell(Writer writer, String controlValue) throws OutputException, IOException{
	    writer.append(controlValue);
	    writer.append(this.getControlCellSeparator());
    }

    /**
     * @param sLineTypeControlValue
     * @return A schema line of type FixedWitdthSchemaLine which has the
     *         supplied line type control value.
     */
    public CsvSchemaLine getSchemaLineByControlValue(
	    String sLineTypeControlValue) {
	for (CsvSchemaLine lineSchema : this.getCsvSchemaLines()) {
	    if (lineSchema.getLineTypeControlValue().equals(
		    sLineTypeControlValue))
		return lineSchema;
	}
	return null;
    }

    /**
     * @param sLineType
     * @return A schema line of type FixedWitdthSchemaLine which has the
     *         supplied line type.
     */
    public CsvSchemaLine getSchemaLineByType(String sLineType) {
	for (CsvSchemaLine lineSchema : this.getCsvSchemaLines()) {
	    if (lineSchema.getLineType().equals(sLineType))
		return lineSchema;
	}
	return null;
    }

    /* (non-Javadoc)
     * @see org.jsapar.schema.CsvSchema#parse(java.io.Reader, org.jsapar.input.ParsingEventListener)
     */
    @Override
    public void parse(Reader reader, ParsingEventListener listener)
	    throws JSaParException {
	CsvSchemaLine lineSchema = null;
	long nLineNumber = 0; // First line is 1
	try {
	    do {
		String sControlCell;
		String sLine = parseLine(reader);
		if (sLine.length() == 0)
		    break;

		int nIndex = sLine.indexOf(this.getControlCellSeparator());
		if (nIndex >= 0) {
		    sControlCell = sLine.substring(0, nIndex);
		    sLine = sLine.substring(nIndex
			    + getControlCellSeparator().length(), sLine
			    .length());
		} else { // There is no delimiter, the control cell is the
		    // complete line.
		    sControlCell = sLine;
		    sLine = "";
		}

		if (lineSchema == null
			|| !lineSchema.getLineTypeControlValue().equals(
				sControlCell))
		    lineSchema = getSchemaLineByControlValue(sControlCell);

		if (lineSchema == null) {
		    CellParseError error = new CellParseError(nLineNumber,
			    "Control cell", sControlCell, null,
			    "Invalid Line-type: " + sControlCell);
		    throw new ParseException(error);
		}

		Line line = lineSchema.build(nLineNumber, sLine, listener);
		line.setLineType(lineSchema.getLineType());
		listener.lineParsedEvent(new LineParsedEvent(this, line,
			nLineNumber));
	    } while (true);

	} catch (IOException ex) {
	    throw new JSaParException("Failed to read control cell.", ex);
	}
    }

    /* (non-Javadoc)
     * @see org.jsapar.schema.CsvSchema#clone()
     */
    public CsvControlCellSchema clone() throws CloneNotSupportedException {
	CsvControlCellSchema schema = (CsvControlCellSchema) super.clone();
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
	sb.append(" controlCellSeparator='");
	sb.append(this.controlCellSeparator);
	sb.append("'");
	return sb.toString();
    }

    /* (non-Javadoc)
     * @see org.jsapar.schema.Schema#output(org.jsapar.Line, int, java.io.Writer)
     */
    @Override
    public void outputLine(Line line, long lineNumber, Writer writer) throws IOException, JSaParException {
	SchemaLine schemaLine = getSchemaLineByType(line.getLineType());
	if(schemaLine != null){
	    if(lineNumber > 1)
		writer.append(getLineSeparator());
	    writeControlCell(writer, schemaLine.getLineTypeControlValue());
	    schemaLine.output(line, writer);
	}
    }
}
