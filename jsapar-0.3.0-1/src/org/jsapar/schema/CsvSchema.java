package org.jsapar.schema;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;

import org.jsapar.Document;
import org.jsapar.JSaParException;
import org.jsapar.Line;
import org.jsapar.input.LineParsedEvent;
import org.jsapar.input.ParseException;
import org.jsapar.input.ParsingEventListener;

/**
 * Defines a schema for a fixed position buffer. Each cell is defined by a fixed
 * number of characters. Each line is separated by the line separator defined in
 * the base class {@link Schema}
 * 
 * @author Jonas
 * 
 */
public class CsvSchema extends Schema {

    private java.util.LinkedList<CsvSchemaLine> schemaLines = new java.util.LinkedList<CsvSchemaLine>();

    /**
     * @return the schemaLines
     */
    public java.util.List<CsvSchemaLine> getCsvSchemaLines() {
	return schemaLines;
    }

    /**
     * @param schemaLine
     *            the schemaLines to set
     */
    public void addSchemaLine(CsvSchemaLine schemaLine) {
	this.schemaLines.add(schemaLine);
    }

    /**
     * Builds a CsvSchemaLine from a header line.
     * 
     * @param sHeaderLine
     * @return
     * @throws CloneNotSupportedException
     * @throws ParseException
     */
    private CsvSchemaLine buildSchemaFromHeader(CsvSchemaLine masterLineSchema,
	    String sHeaderLine) throws CloneNotSupportedException,
	    ParseException {

	CsvSchemaLine schemaLine = masterLineSchema.clone();
	schemaLine.getSchemaCells().clear();

	schemaLine.setOccursInfinitely();

	String[] asCells = schemaLine.split(sHeaderLine);
	for (String sCell : asCells) {
	    schemaLine.addSchemaCell(new CsvSchemaCell(sCell));
	}
	return schemaLine;
    }

    @Override
    public void output(Document document, Writer writer) throws IOException, JSaParException {
	Iterator<Line> itLines = document.getLineIterator();

	for (CsvSchemaLine lineSchema : getCsvSchemaLines()) {
	    if (lineSchema.isFirstLineAsSchema()) {
		this.schemaLines.getFirst().outputHeaderLine(writer);
		if (itLines.hasNext())
		    writer.write(getLineSeparator());
	    }
	    for (int i = 0; i < lineSchema.getOccurs(); i++) {
		if (!itLines.hasNext())
		    return;

		Line line = itLines.next();
		((CsvSchemaLine) lineSchema).output(line, writer);

		if (itLines.hasNext())
		    writer.write(getLineSeparator());
		else
		    return;
	    }
	}
    }

    @Override
    public void parse(java.io.Reader reader,
	    ParsingEventListener listener) throws IOException, JSaParException {

	long nLineNumber = 0; // First line is 1
	for (CsvSchemaLine lineSchema : getCsvSchemaLines()) {

	    if (lineSchema.isFirstLineAsSchema()) {
		try {
		    lineSchema = buildSchemaFromHeader(lineSchema,
			    parseLine(reader));
		} catch (CloneNotSupportedException e) {
		    throw new ParseException("Failed to create header schema.",
			    e);
		}
	    }
	    nLineNumber += parseLinesByOccurs(lineSchema, nLineNumber, reader,
		    listener);
	}
    }

    /**
     * @param doc
     * @param lineSchema
     * @param nLineNumber
     * @param reader
     * @param parseErrors
     * @return Number of lines that were parsed (including failed ones).
     * @throws IOException
     * @throws JSaParException
     */
    private long parseLinesByOccurs(CsvSchemaLine lineSchema, long nLineNumber,
	    Reader reader, ParsingEventListener listener) throws IOException,
	    JSaParException {
	long nStartLine = nLineNumber;
	for (int i = 0; i < lineSchema.getOccurs(); i++) {
	    nLineNumber++;
	    String sLine = parseLine(reader);
	    if (sLine.length() == 0) {
		if (lineSchema.isOccursInfinitely())
		    break;
		else {
		    throw new ParseException(
			    "Unexpected end of input buffer. Was expecting "
				    + lineSchema.getOccurs()
				    + " lines of this type. Found " + i
				    + " lines");
		}
	    }

	    Line line = lineSchema.build(nLineNumber, sLine, listener);
	    line.setLineType(lineSchema.getLineType());
	    listener.lineParsedEvent(new LineParsedEvent(this, line,
		    nLineNumber));
	}

	return nLineNumber - nStartLine;
    }


    public CsvSchema clone() throws CloneNotSupportedException {
	CsvSchema schema = (CsvSchema) super.clone();

	schema.schemaLines = new java.util.LinkedList<CsvSchemaLine>();
	for (CsvSchemaLine line : this.schemaLines) {
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
	sb.append("'");
	sb.append(" schemaLines=");
	sb.append(this.schemaLines);
	return sb.toString();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List getSchemaLines() {
	return this.schemaLines;
    }

    @Override
    public void outputAfter(Writer writer) throws IOException, JSaParException {
    }

    @Override
    public void outputBefore(Writer writer) throws IOException, JSaParException {
    }

}
