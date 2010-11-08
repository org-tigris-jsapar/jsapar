package org.jsapar.schema;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Iterator;

import org.jsapar.Cell;
import org.jsapar.JSaParException;
import org.jsapar.Line;
import org.jsapar.input.CellParseError;
import org.jsapar.input.LineErrorEvent;
import org.jsapar.input.LineParsedEvent;
import org.jsapar.input.ParseException;
import org.jsapar.input.ParsingEventListener;

/**
 * This class represents the schema for a line of a fixed with file. Each cell within the line has a
 * specified size. There are no delimiter characters.
 * 
 * @author stejon0
 * 
 */
public class FixedWidthSchemaLine extends SchemaLine {

    private static final String EMPTY_STRING = "";
    private java.util.List<FixedWidthSchemaCell> schemaCells = new java.util.ArrayList<FixedWidthSchemaCell>();
    private boolean trimFillCharacters = false;
    private char fillCharacter = ' ';

    /**
     * Creates an empty schema line.
     */
    public FixedWidthSchemaLine() {
        super();
    }

    /**
     * Creates an empty schema line which will occur nOccurs times within the file. When the line
     * has occured nOccurs times this schema-line will not be used any more.
     * 
     * @param nOccurs
     *            The number of times this schema line is used while parsing or writing.
     */
    public FixedWidthSchemaLine(int nOccurs) {
        super(nOccurs);
    }

    /**
     * Creates an empty schema line which parses lines of type lineType.
     * 
     * @param lineType
     *            The line type for which this schema line is used. The line type is stored as the
     *            lineType of the generated Line.
     */
    public FixedWidthSchemaLine(String lineType) {
        super(lineType);
    }

    /**
     * Creates a schema line with the supplied line type and control value.
     * 
     * @param lineType
     *            The name of the type of the line.
     * @param lineTypeControlValue
     *            The tag that determines which type of line it is.
     */
    public FixedWidthSchemaLine(String lineType, String lineTypeControlValue) {
        super(lineType, lineTypeControlValue);
    }

    /**
     * @return the cells
     */
    public java.util.List<FixedWidthSchemaCell> getSchemaCells() {
        return schemaCells;
    }

    /**
     * Adds a schema cell to this row.
     * 
     * @param schemaCell
     */
    public void addSchemaCell(FixedWidthSchemaCell schemaCell) {
        this.schemaCells.add(schemaCell);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jsapar.schema.SchemaLine#parse(long, java.lang.String,
     * org.jsapar.input.ParsingEventListener)
     */
    @Override
    boolean parse(long nLineNumber, String sLine, ParsingEventListener listener) throws IOException, JSaParException {
        if (sLine.trim().isEmpty())
            return handleEmptyLine(nLineNumber, listener);
        java.io.Reader reader = new java.io.StringReader(sLine);
        return parse(nLineNumber, reader, listener);
    }

    /**
     * Reads characters from the reader and parses them into a complete line. The line is sent as an
     * event to the supplied event listener.
     * 
     * @param nLineNumber
     *            The current line number while parsing.
     * @param reader
     *            The reader to read characters from.
     * @param listener
     *            The listener to generate call-backs to.
     * @return true if a line was found. false if end-of-file was found while reading the input.
     * @throws IOException
     * @throws JSaParException
     */
    boolean parse(long nLineNumber, Reader reader, ParsingEventListener listener) throws IOException, JSaParException {

        Line line = new Line(getLineType(), getSchemaCells().size());
        boolean setDefaultsOnly = false;
        boolean oneRead = false;

        for (FixedWidthSchemaCell schemaCell : getSchemaCells()) {
            if (setDefaultsOnly) {
                if (schemaCell.getDefaultCell() != null)
                    line.addCell(schemaCell.makeCell(EMPTY_STRING, listener, nLineNumber));
                continue;
            } else if (schemaCell.isIgnoreRead()) {
                long nSkipped = reader.skip(schemaCell.getLength());
                if (nSkipped != schemaCell.getLength()) {
                    if (oneRead)
                        setDefaultsOnly = true;
                    continue;
                }
            } else {
                try {
                    Cell cell = schemaCell.build(reader, isTrimFillCharacters(), getFillCharacter(), listener,
                            nLineNumber);
                    if (cell == null) {
                        if (oneRead) {
                            setDefaultsOnly = true;
                            if (schemaCell.getDefaultCell() != null)
                                line.addCell(schemaCell.makeCell(EMPTY_STRING));
                        }
                        continue;
                    }

                    oneRead = true;
                    line.addCell(cell);
                } catch (ParseException e) {
                    CellParseError cellParseError = e.getCellParseError();
                    cellParseError = new CellParseError(nLineNumber, cellParseError);
                    listener.lineErrorEvent(new LineErrorEvent(this, cellParseError));
                }
            }
        }
        if (line.getNumberOfCells() <= 0)
            return false;

        listener.lineParsedEvent(new LineParsedEvent(this, line, nLineNumber));

        return true;
    }

    /**
     * Writes a line to the writer. Each cell is identified from the schema by the name of the cell.
     * If the schema-cell has no name, the cell at the same position in the line is used under the
     * condition that it also lacks name.
     * 
     * If the schema-cell has a name the cell with the same name is used. If no such cell is found
     * and the cell att the same position lacks name, it is used instead.
     * 
     * If no corresponding cell is found for a schema-cell, the positions are filled with the schema
     * fill character.
     * 
     * @param line
     *            The line to write to the writer
     * @param writer
     *            The writer to write to.
     * @throws IOException
     * @throws JSaParException
     */
    @Override
    public void output(Line line, Writer writer) throws IOException, JSaParException {
        Iterator<FixedWidthSchemaCell> iter = getSchemaCells().iterator();

        // Iterate all schema cells.
        for (int i = 0; iter.hasNext(); i++) {
            FixedWidthSchemaCell schemaCell = iter.next();
            Cell cell = findCell(line, schemaCell, i);
            if (cell == null)
                schemaCell.outputEmptyCell(writer, getFillCharacter());
            else
                schemaCell.output(cell, writer, getFillCharacter());
        }
    }

    /**
     * @param line
     * @param writer
     * @param schema
     * @throws IOException
     * @throws JSaParException
     */
    void outputByIndex(Line line, Writer writer, FixedWidthSchema schema) throws IOException, JSaParException {
        Iterator<FixedWidthSchemaCell> iter = getSchemaCells().iterator();
        for (int i = 0; iter.hasNext(); i++) {
            iter.next().output(line.getCell(i), writer, getFillCharacter());
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#clone()
     */
    public FixedWidthSchemaLine clone() throws CloneNotSupportedException {
        FixedWidthSchemaLine line = (FixedWidthSchemaLine) super.clone();

        line.schemaCells = new java.util.LinkedList<FixedWidthSchemaCell>();
        for (FixedWidthSchemaCell cell : this.schemaCells) {
            line.addSchemaCell(cell.clone());
        }
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
        sb.append(" trimFillCharacters=");
        sb.append(this.trimFillCharacters);
        if (this.trimFillCharacters) {
            sb.append(" fillCharacter='");
            sb.append(this.fillCharacter);
            sb.append("'");
        }
        sb.append(" schemaCells=");
        sb.append(this.schemaCells);
        return sb.toString();
    }

    /**
     * @return the trimFillCharacters
     */
    public boolean isTrimFillCharacters() {
        return trimFillCharacters;
    }

    /**
     * @param trimFillCharacters
     *            the trimFillCharacters to set
     */
    public void setTrimFillCharacters(boolean trimFillCharacters) {
        this.trimFillCharacters = trimFillCharacters;
    }

    /**
     * @return the fillCharacter
     */
    public char getFillCharacter() {
        return fillCharacter;
    }

    /**
     * @param fillCharacter
     *            the fillCharacter to set
     */
    public void setFillCharacter(char fillCharacter) {
        this.fillCharacter = fillCharacter;
    }

    /**
     * Finds the cell's fist and last positions within a line. First position starts with 1.
     * 
     * @param cellName
     *            The name of the cell to find positions for.
     * @return The cell positions for the cell with the supplied name, null if no such cell exists.
     */
    public FixedWidthCellPositions getCellPositions(String cellName) {
        FixedWidthCellPositions pos = new FixedWidthCellPositions();
        for (FixedWidthSchemaCell cell : schemaCells) {
            pos.increment(cell);
            if (cell.getName().equals(cellName))
                return pos;
        }
        return null;
    }

    /**
     * Finds the cell's fist position within a line. First position starts with 1.
     * 
     * @param cellName
     *            The name of the cell to find positions for.
     * @return The cell's first position for the cell with the supplied name, -1 if no such cell
     *         exists.
     */
    public int getCellFirstPosition(String cellName) {
        FixedWidthCellPositions pos = getCellPositions(cellName);
        return pos != null ? pos.getFirst() : -1;
    }

    @Override
    public SchemaCell getSchemaCell(String cellName) {
        for (FixedWidthSchemaCell schemaCell : schemaCells) {
            if (schemaCell.getName().equals(cellName))
                return schemaCell;
        }
        return null;
    }

    @Override
    public int getSchemaCellsCount() {
        return this.schemaCells.size();
    }

    @Override
    public SchemaCell getSchemaCellAt(int index) {
        return this.schemaCells.get(index);
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
        if (!(obj instanceof FixedWidthSchemaLine)) {
            return false;
        }
        return true;
    }

}
