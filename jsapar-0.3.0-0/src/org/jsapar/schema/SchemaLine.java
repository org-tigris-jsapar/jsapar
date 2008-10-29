package org.jsapar.schema;

import java.io.IOException;
import java.io.Writer;

import org.jsapar.Cell;
import org.jsapar.JSaParException;
import org.jsapar.Line;

public abstract class SchemaLine implements Cloneable {
    private static final int OCCURS_INFINITE = Integer.MAX_VALUE;

    private int occurs = 1;

    private String lineType;

    private String lineTypeControlValue;

    public SchemaLine() {
	this.occurs = OCCURS_INFINITE;
    }

    public SchemaLine(int nOccurs) {
	this.occurs = nOccurs;
    }

    /**
     * @return the occurs
     */
    public int getOccurs() {
	return occurs;
    }

    /**
     * @param occurs
     *            the occurs to set
     */
    public void setOccurs(int occurs) {
	this.occurs = occurs;
    }

    public void setOccursInfinitely() {
	this.occurs = OCCURS_INFINITE;
    }

    public boolean isOccursInfinitely() {
	return this.occurs == OCCURS_INFINITE;
    }

    /**
     * Finds the cell to use for output. Each cell is identified from the schema
     * by the name of the cell. If the schema-cell has no name, the cell at the
     * same position in the line is used under the condition that it also lacks
     * name.
     * 
     * If the schema-cell has a name the cell with the same name is used. If no
     * such cell is found and the cell att the same position lacks name, it is
     * used instead.
     * 
     * 
     * @param line
     *            The line to find a cell within.
     * @param schemaCell
     *            The schema-cell to use.
     * @param nSchemaCellIndex
     *            The index at wich the schema-cell is found.
     * @return
     */
    protected static Cell findCell(Line line, SchemaCell schemaCell,
	    int nSchemaCellIndex) {
	Cell cellByIndex = null;
	if (nSchemaCellIndex < line.getNumberOfCells())
	    cellByIndex = line.getCell(nSchemaCellIndex); // Use optimistic
	// matching.
	if (null != cellByIndex && null == schemaCell.getName()) {
	    // cell = line.getCell(i);
	    if (null == cellByIndex.getName()) {
		// If both cell and schema cell names are null then it is ok
		// to use cell by index.
		return cellByIndex;
	    } else {
		return null;
	    }
	} else {
	    if (null != cellByIndex && null != cellByIndex.getName()
		    && schemaCell.getName().equals(cellByIndex.getName())) {
		// We were lucky.
		return cellByIndex;
	    } else {
		// The optimistic match failed. We take the penalty.
		Cell cellByName = line.getCell(schemaCell.getName());
		if (null != cellByName) {
		    return cellByName;
		} else {
		    if (null == cellByIndex) {
			return null;
		    } else if (cellByIndex.getName() == null) {
			// If it was not found by name we fall back to the
			// cell with correct index.
			return cellByIndex;
		    } else {
			return null;
		    }
		}
	    }
	}
    }

    /**
     * @return the lineType
     */
    public String getLineType() {
	return lineType;
    }

    /**
     * @param lineType
     *            the lineType to set
     */
    public void setLineType(String lineType) {
	this.lineType = lineType;
	if (this.lineTypeControlValue == null)
	    this.lineTypeControlValue = lineType;
    }

    /**
     * @return the lineTypeControlValue
     */
    public String getLineTypeControlValue() {
	return lineTypeControlValue;
    }

    /**
     * @param lineTypeControlValue
     *            the lineTypeControlValue to set
     */
    public void setLineTypeControlValue(String lineTypeControlValue) {
	this.lineTypeControlValue = lineTypeControlValue;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
	StringBuilder sb = new StringBuilder();
	sb.append("SchemaLine lineType=");
	sb.append(this.lineType);
	if (this.lineTypeControlValue != null) {
	    sb.append(" lineTypeControlValue=");
	    sb.append(this.lineTypeControlValue);
	}
	sb.append(" occurs=");
	if (isOccursInfinitely())
	    sb.append("INFINITE");
	else
	    sb.append(this.occurs);
	return sb.toString();
    }

    /**
     * Writes a line to the writer. Each cell is identified from the schema by
     * the name of the cell. If the schema-cell has no name, the cell at the
     * same position in the line is used under the condition that it also lacks
     * name.
     * 
     * If the schema-cell has a name the cell with the same name is used. If no
     * such cell is found and the cell at the same position lacks name, it is
     * used instead.
     * 
     * If no corresponding cell is found for a schema-cell, the cell is left
     * empty. Sub-class decides how to treat empty cells.
     * 
     * @param line
     * @param writer
     * @throws IOException
     * @throws JSaParException
     */
    abstract public void output(Line line, Writer writer) throws IOException,
	    JSaParException;
}
