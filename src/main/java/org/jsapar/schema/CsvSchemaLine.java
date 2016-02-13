package org.jsapar.schema;

import java.io.IOException;
import java.io.Writer;
import java.util.*;

import org.jsapar.model.Cell;
import org.jsapar.model.CellType;
import org.jsapar.JSaParException;
import org.jsapar.model.Line;
import org.jsapar.model.StringCell;
import org.jsapar.parse.LineReader;
import org.jsapar.parse.csv.CellSplitter;
import org.jsapar.parse.csv.QuotedCellSplitter;
import org.jsapar.parse.csv.SimpleCellSplitter;

/**
 * Describes the schema how to parse or write a comma separated line.
 * 
 * @author stejon0
 * 
 */
public class CsvSchemaLine extends SchemaLine {

    private Map<String, CsvSchemaCell> schemaCells       = new LinkedHashMap<>();
    private boolean                    firstLineAsSchema = false;

    private String                        cellSeparator     = ";";

    /**
     * Specifies quote characters used to encapsulate cells. Numerical value 0 indicates that quotes are not used.
     */
    private char                          quoteChar         = 0;

    /**
     * Creates an empty schema line.
     */
    public CsvSchemaLine() {
        super();
    }

    /**
     * Creates an empty schema line which occurs nOccurs number of times.
     * 
     * @param nOccurs
     */
    public CsvSchemaLine(int nOccurs) {
        super(nOccurs);
    }

    /**
     * The type of the line. You could say that this is the class of the line.
     * 
     * @param lineType
     */
    public CsvSchemaLine(String lineType) {
        super(lineType);
    }

    public CsvSchemaLine(String lineType, String lineTypeControlValue) {
        super(lineType, lineTypeControlValue);
    }

    /**
     * @return the cells
     */
    public Collection<CsvSchemaCell> getSchemaCells() {
        return schemaCells.values();
    }

    /**
     * Adds a schema cell to this row.
     * 
     * @param cell
     */
    public void addSchemaCell(CsvSchemaCell cell) {
        this.schemaCells.put(cell.getName(), cell);
    }


    /**
     * @param lineReader
     * @return An array of all cells found on the line.
     */
    CellSplitter makeCellSplitter(LineReader lineReader) {
        if (quoteChar == 0)
            return new SimpleCellSplitter(cellSeparator);
        return new QuotedCellSplitter(cellSeparator, quoteChar, lineReader);
    }


    /**
     * @return the cellSeparator
     */
    public String getCellSeparator() {
        return cellSeparator;
    }

    /**
     * Sets the character sequence that separates each cell. This value overrides setting for the schema. <br>
     * In output schemas the non-breaking space character '\u00A0' is not allowed since that character is used to
     * replace any occurrence of the separator within each cell.
     * 
     * @param cellSeparator
     *            the cellSeparator to set
     */
    public void setCellSeparator(String cellSeparator) {
        this.cellSeparator = cellSeparator;
    }


    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#clone()
     */
    @Override
    public CsvSchemaLine clone() {
        CsvSchemaLine line = cloneWithoutCells();

        for (CsvSchemaCell cell : this.schemaCells.values()) {
            line.addSchemaCell(cell.clone());
        }
        return line;
    }

    protected CsvSchemaLine cloneWithoutCells() {
        CsvSchemaLine line;
        line = (CsvSchemaLine) super.clone();
        line.schemaCells = new LinkedHashMap<>();

        return line;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString());
        sb.append(" cellSeparator='");
        sb.append(this.cellSeparator);
        sb.append("'");
        sb.append(" firstLineAsSchema=");
        sb.append(this.firstLineAsSchema);
        if (this.quoteChar != 0) {
            sb.append(" quoteChar=");
            sb.append(this.quoteChar);
        }
        sb.append(" schemaCells=");
        sb.append(this.schemaCells);
        return sb.toString();
    }

    /**
     * @return the firstLineAsSchema
     */
    public boolean isFirstLineAsSchema() {
        return firstLineAsSchema;
    }

    /**
     * @param firstLineAsSchema
     *            the firstLineAsSchema to set
     */
    public void setFirstLineAsSchema(boolean firstLineAsSchema) {
        this.firstLineAsSchema = firstLineAsSchema;
    }

    /**
     * @return the quotePattern
     */
    public char getQuoteChar() {
        return quoteChar;
    }

    /**
     * @return true if quote character is used, false otherwise.
     */
    public boolean isQuoteCharUsed() {
        return this.quoteChar == 0 ? false : true;
    }

    /**
     * @param quoteChar
     *            the quote character to set
     */
    public void setQuoteChar(char quoteChar) {
        this.quoteChar = quoteChar;
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.jsapar.schema.SchemaLine#getSchemaCell(java.lang.String)
     */
    @Override
    public SchemaCell getSchemaCell(String cellName) {
        return getCsvSchemaCell(cellName);
    }

    /**
     * @param cellName
     * @return CsvSchemaCell with specified name that is attached to this line or null if no such cell exist.
     */
    public CsvSchemaCell getCsvSchemaCell(String cellName) {
        for (CsvSchemaCell schemaCell : this.getSchemaCells()) {
            if (schemaCell.getName().equals(cellName))
                return schemaCell;
        }
        return null;
    }

    @Override
    public int getSchemaCellsCount() {
        return this.schemaCells.size();
    }


    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) {
            return false;
        }
        if (!(obj instanceof CsvSchemaLine)) {
            return false;
        }
        return true;
    }


}
