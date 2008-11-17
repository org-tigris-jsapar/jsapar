package org.jsapar.schema;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;

import org.jsapar.Document;
import org.jsapar.JSaParException;
import org.jsapar.Line;
import org.jsapar.input.CellParseError;
import org.jsapar.input.LineParsedEvent;
import org.jsapar.input.ParseException;
import org.jsapar.input.ParsingEventListener;

/**
 * Defines a schema for a fixed position buffer. Each cell is defined by a fixed
 * number of characters. Each line is separated by the line separator defined in
 * the base class {@link Schema}. 
 * 
 * If the end of line is reached before all cells are parsed the remaining cells 
 * will not be set. 
 * 
 * If there are remaining characters when the end of line is reached, those 
 * characters will be omitted.
 * 
 * If the line separator is an empty string, the lines will be separated by the sum of the length of the cells within the 
 * schema.
 * 
 * @author Jonas
 * 
 */
/**
 * @author Jonas Stenberg
 * 
 */
public class FixedWidthSchema extends Schema {

    private java.util.List<FixedWidthSchemaLine> schemaLines = new java.util.LinkedList<FixedWidthSchemaLine>();

    /**
     * @return the schemaLines
     */
    public java.util.List<FixedWidthSchemaLine> getFixedWidthSchemaLines() {
	return schemaLines;
    }

    /**
     * @param schemaLines
     *            the schemaLines to set
     */
    public void setSchemaLines(java.util.List<FixedWidthSchemaLine> schemaLines) {
	this.schemaLines = schemaLines;
    }

    /**
     * @param schemaLine
     *            the schemaLines to set
     */
    public void addSchemaLine(FixedWidthSchemaLine schemaLine) {
	this.schemaLines.add(schemaLine);
    }


    /**
     * Builds a document from a reader using a schema where the line types are
     * denoted by the occurs field in the schema.
     * 
     * @param reader
     * @param listener
     * @throws java.io.IOException
     * @throws JSaParException 
     */
    @Override
    public void parse(java.io.Reader reader,
	    ParsingEventListener listener) throws IOException, JSaParException {
	if (getLineSeparator().length() > 0) {
	    parseByOccursLinesSeparated(reader, listener);
	} else {
	    parseByOccursFlatFile(reader, listener);
	}
    }

    /**
     * Builds a document from a reader using a schema where the line types are
     * denoted by the occurs field in the schema and the lines are not separated
     * by any line separator character.
     * 
     * @param reader
     * @param parseErrors
     * @return
     * @throws org.jsapar.JSaParException
     * @throws java.io.IOException
     * @throws JSaParException 
     */
    protected void parseByOccursFlatFile(java.io.Reader reader,
	    ParsingEventListener listener) throws IOException, JSaParException {
	long nLineNumber = 0;
	for (FixedWidthSchemaLine lineSchema : getFixedWidthSchemaLines()) {
	    for (int i = 0; i < lineSchema.getOccurs(); i++) {
		nLineNumber++;
		Line line = lineSchema.build(nLineNumber, reader, listener);

		if (line != null) {
		    line.setLineType(lineSchema.getLineType());
		    listener.lineParsedEvent(new LineParsedEvent(this, line,
			    nLineNumber));
		} else {
		    break; // End of stream.
		}
	    }
	}
    }

    protected void parseByOccursLinesSeparated(java.io.Reader reader,
	    ParsingEventListener listener) throws IOException, JSaParException {

	long nLineNumber = 0; // First line is 1
	for (FixedWidthSchemaLine lineSchema : getFixedWidthSchemaLines()) {
	    for (int i = 0; i < lineSchema.getOccurs(); i++) {
		nLineNumber++;
		String sLine = parseLine(reader);
		if (sLine.length() == 0) {
		    if (lineSchema.isOccursInfinitely()) {
			break;
		    } else {
			throw new ParseException(
				"Unexpected end of input buffer. Was expecting "
					+ lineSchema.getOccurs()
					+ " lines of this type. Found " + i
					+ " lines");
		    }
		}

		Line line = lineSchema.build(nLineNumber, sLine, listener);
		if (line == null)
		    return;

		line.setLineType(lineSchema.getLineType());
		listener.lineParsedEvent(new LineParsedEvent(this, line,
			nLineNumber));
	    }
	}
    }

    @Override
    public void output(Document document, Writer writer) throws IOException,
	    JSaParException {

	Iterator<Line> itLines = document.getLineIterator();
	for (SchemaLine lineSchema : getFixedWidthSchemaLines()) {
	    for (int i = 0; i < lineSchema.getOccurs(); i++) {
		if (!itLines.hasNext()) {
		    return;
		}
		Line line = itLines.next();
		((FixedWidthSchemaLine) lineSchema).output(line, writer);

		if (itLines.hasNext()) {
		    if (getLineSeparator().length() > 0) {
			writer.write(getLineSeparator());
		    }
		} else {
		    return;
		}
	    }
	}
    }


    public FixedWidthSchema clone() throws CloneNotSupportedException {
	FixedWidthSchema schema = (FixedWidthSchema) super.clone();

	schema.schemaLines = new java.util.LinkedList<FixedWidthSchemaLine>();
	for (FixedWidthSchemaLine line : this.schemaLines) {
	    schema.addSchemaLine(line.clone());
	}
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
	sb.append(" schemaLines=");
	sb.append(this.schemaLines);
	return sb.toString();
    }

    @Override
    public List getSchemaLines() {
	return this.schemaLines;
    }

    @Override
    public void outputAfter(Writer writer)
	    throws IOException, JSaParException {
    }

    @Override
    public void outputBefore(Writer writer)
	    throws IOException, JSaParException {
    }

   
}
