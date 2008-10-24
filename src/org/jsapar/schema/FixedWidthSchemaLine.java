package org.jsapar.schema;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;

import org.jsapar.Cell;
import org.jsapar.JSaParException;
import org.jsapar.Line;
import org.jsapar.input.CellParseError;
import org.jsapar.input.ParseException;


public class FixedWidthSchemaLine extends SchemaLine{

    private java.util.List<FixedWidthSchemaCell> schemaCells = new java.util.LinkedList<FixedWidthSchemaCell>();

    public FixedWidthSchemaLine() {
	super();
    }

    public FixedWidthSchemaLine(int nOccurs) {
	super(nOccurs);
    }

    /**
     * @return the cells
     */
    public java.util.List<FixedWidthSchemaCell> getSchemaCells() {
	return schemaCells;
    }

    /**
     * @param cells
     *                the cells to set
     */

    /**
     * Adds a schema cell to this row.
     * 
     * @param cell
     */
    public void addSchemaCell(FixedWidthSchemaCell cell) {
	this.schemaCells.add(cell);
    }

    /**
     * Reads characters from the reader and parses them into a complete line.
     * 
     * @param reader
     *                The reader to read characters from.
     * @param schema
     *                The schema to use while parsing.
     * @param parseErrors
     *                TODO
     * @return
     * @throws IOException
     * @throws JSaParException
     */
    Line build(long nLineNumber, String sLine, FixedWidthSchema schema, List<CellParseError> parseErrors)
	    throws IOException, ParseException {
            java.io.Reader reader = new java.io.StringReader(sLine);
            return build(nLineNumber, reader, schema, parseErrors);
    }
    
     /**
     * Reads characters from the reader and parses them into a complete line.
     * 
     * @param reader
     *                The reader to read characters from.
     * @param schema
     *                The schema to use while parsing.
     * @param parseErrors
     *                TODO
     * @return
     * @throws IOException
     * @throws JSaParException
     */
    Line build(long nLineNumber, Reader reader, FixedWidthSchema schema, List<CellParseError> parseErrors)
	    throws IOException, ParseException {

	Line line = new Line(getSchemaCells().size());
	for (FixedWidthSchemaCell schemaCell : getSchemaCells()) {
	    if (schemaCell.isIgnoreRead()) {
		long nSkipped = reader.skip(schemaCell.getLength());
		if (nSkipped != schemaCell.getLength())
		    break;
	    } else {
		try {
		    Cell cell = schemaCell.build(reader, schema);
		    if (cell == null)
			break;
		    line.addCell(cell);
		} catch (ParseException pe) {
		    pe.getCellParseError().setLineNumber(nLineNumber); 
		    if (parseErrors == null) {
			throw pe;
		    } else {
			parseErrors.add(pe.getCellParseError());
		    }
		}
	    }
	}
	if (line.getNumberOfCells() <= 0)
	    return null;
	else
	    return line;
    }

    /**
     * Writes a line to the writer. Each cell is identified from the schema by
     * the name of the cell. If the schema-cell has no name, the cell at the
     * same position in the line is used under the condition that it also lacks
     * name.
     * 
     * If the schema-cell has a name the cell with the same name is used. If no
     * such cell is found and the cell att the same position lacks name, it is
     * used instead.
     * 
     * If no corresponding cell is found for a schema-cell, the positions are
     * filled with the schema fill character.
     * 
     * @param line
     * @param writer
     * @param schema
     * @throws IOException
     * @throws JSaParException
     */
    void output(Line line, Writer writer, FixedWidthSchema schema)
	    throws IOException, JSaParException {
	Iterator<FixedWidthSchemaCell> iter = getSchemaCells().iterator();

	// Iterate all schema cells.
	for (int i = 0; iter.hasNext(); i++) {
	    FixedWidthSchemaCell schemaCell = iter.next();
	    Cell cell = findCell(line, schemaCell, i);
	    if (cell == null)
		schemaCell.outputEmptyCell(writer, schema);
	    else
		schemaCell.output(cell, writer, schema);
	}
    }

    void outputByIndex(Line line, Writer writer, FixedWidthSchema schema)
	    throws IOException, JSaParException {
	Iterator<FixedWidthSchemaCell> iter = getSchemaCells().iterator();
	for (int i = 0; iter.hasNext(); i++) {
	    iter.next().output(line.getCell(i), writer, schema);
	}
    }

    /* (non-Javadoc)
     * @see java.lang.Object#clone()
     */
    public FixedWidthSchemaLine clone() throws CloneNotSupportedException{
    	FixedWidthSchemaLine line = (FixedWidthSchemaLine)super.clone(); 
    	
		line.schemaCells = new java.util.LinkedList<FixedWidthSchemaCell>();
		for(FixedWidthSchemaCell cell: this.schemaCells){
			line.addSchemaCell(cell.clone());
		}
    	return line;
    }

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(super.toString());
		sb.append(" schemaCells=");
		sb.append(this.schemaCells);
		return sb.toString();
	}
}
