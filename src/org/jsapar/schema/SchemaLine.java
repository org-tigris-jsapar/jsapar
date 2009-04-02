package org.jsapar.schema;

import java.io.IOException;
import java.io.Writer;

import org.jsapar.Cell;
import org.jsapar.JSaParException;
import org.jsapar.Line;
import org.jsapar.input.LineParsedEvent;
import org.jsapar.input.ParsingEventListener;

public abstract class SchemaLine implements Cloneable {
    private static final int OCCURS_INFINITE = Integer.MAX_VALUE;
    private static final String NOT_SET="";

    private int occurs = OCCURS_INFINITE;

    private String lineType=NOT_SET;

    private String lineTypeControlValue=NOT_SET;

    private boolean ignoreReadEmptyLines = true;

    public SchemaLine() {
        this.occurs = OCCURS_INFINITE;
    }

    /**
     * Creates a SchemaLine which occurs supplied number of times.
     * 
     * @param nOccurs
     *            The number of times that a line of this type occurs in the corresponding buffer.
     */
    public SchemaLine(int nOccurs) {
        this.occurs = nOccurs;
    }

    /**
     * Creates a SchemaLine with the supplied line type.
     * 
     * @param lineType
     *            The name of the type of the line.
     */
    public SchemaLine(String lineType) {
        this.setLineType(lineType);
    }

    /**
     * Creates a SchemaLine with the supplied line type and control value.
     * 
     * @param lineType
     *            The name of the type of the line.
     * @param lineTypeControlValue
     *            The tag that determines which type of line it is.
     */
    public SchemaLine(String lineType, String lineTypeControlValue) {
        this(lineType);
        this.setLineTypeControlValue(lineTypeControlValue);
    }

    /**
     * @return The number of times this type of line occurs in the corresponding buffer.
     */
    public int getOccurs() {
        return occurs;
    }

    /**
     * @param occurs
     *            The number of times this type of line occurs in the corresponding buffer.
     */
    public void setOccurs(int occurs) {
        this.occurs = occurs;
    }

    /**
     * Setts the occurs attribute so that this type of line occurs until the end of the buffer.
     */
    public void setOccursInfinitely() {
        this.occurs = OCCURS_INFINITE;
    }

    /**
     * @return true if this line occurs to the end of the buffer, false otherwise.
     */
    public boolean isOccursInfinitely() {
        return this.occurs == OCCURS_INFINITE;
    }

    /**
     * Finds the cell to use for output. Each cell is identified from the schema by the name of the
     * cell. If the schema-cell has no name, the cell at the same position in the line is used under
     * the condition that it also lacks name.
     * 
     * If the schema-cell has a name the cell with the same name is used. If no such cell is found
     * and the cell att the same position lacks name, it is used instead.
     * 
     * 
     * @param line
     *            The line to find a cell within.
     * @param schemaCell
     *            The schema-cell to use.
     * @param nSchemaCellIndex
     *            The index at wich the schema-cell is found.
     * @return The cell within the supplied line to use for output according to the supplied
     *         schemaCell.
     */
    protected static Cell findCell(Line line, SchemaCell schemaCell, int nSchemaCellIndex) {
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
        if (this.lineTypeControlValue == NOT_SET)
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
        if (this.lineType == NOT_SET)
            this.lineType = lineTypeControlValue;
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
        if (this.lineTypeControlValue != NOT_SET) {
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
     * Writes a line to the writer. Each cell is identified from the schema by the name of the cell.
     * If the schema-cell has no name, the cell at the same position in the line is used under the
     * condition that it also lacks name.
     * 
     * If the schema-cell has a name the cell with the same name is used. If no such cell is found
     * and the cell at the same position lacks name, it is used instead.
     * 
     * If no corresponding cell is found for a schema-cell, the cell is left empty. Sub-class
     * decides how to treat empty cells.
     * 
     * @param line
     * @param writer
     * @throws IOException
     * @throws JSaParException
     */
    abstract public void output(Line line, Writer writer) throws IOException, JSaParException;

    /**
     * Reads characters from the line and generates a LineParsedEvent to the listener when it has
     * been parsed.
     * 
     * @param nLineNumber
     *            The current line number while parsing.
     * @param sLine
     *            The line to parse
     * @param listener
     *            The listener to generate call-backs to.
     * @return true if a line was found. false if there were no cells within this line.
     * @throws IOException
     * @throws JSaParException
     */
    abstract boolean parse(long lineNumber, String line, ParsingEventListener listener) throws JSaParException,
            IOException;

    /**
     * @return the ignoreReadEmptyLines
     */
    public boolean isIgnoreReadEmptyLines() {
        return ignoreReadEmptyLines;
    }

    /**
     * @param ignoreEmptyLines
     */
    public void setIgnoreReadEmptyLines(boolean ignoreEmptyLines) {
        this.ignoreReadEmptyLines = ignoreEmptyLines;
    }

    /**
     * Handles behavior of empty lines
     * 
     * @param lineNumber
     * @param listener
     * @return Returns true (always).
     * @throws JSaParException
     */
    protected boolean handleEmptyLine(long lineNumber, ParsingEventListener listener) throws JSaParException {
        if (!isIgnoreReadEmptyLines()) {
            listener.lineParsedEvent(new LineParsedEvent(this, new Line(getLineType()), lineNumber));
        }
        return true;
    }

}
