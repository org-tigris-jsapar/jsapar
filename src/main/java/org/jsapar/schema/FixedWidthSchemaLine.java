package org.jsapar.schema;

/**
 * This class represents the schema for a line of a fixed with file. Each cell within the line has a
 * specified size. There are no delimiter characters.
 * 
 * @author stejon0
 * 
 */
public class FixedWidthSchemaLine extends SchemaLine {

    private java.util.List<FixedWidthSchemaCell> schemaCells = new java.util.ArrayList<>();
    private boolean trimFillCharacters = true;
    private char fillCharacter = ' ';
    private int minLength = -1;

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
     * @return the cells
     */
    public java.util.List<FixedWidthSchemaCell> getSchemaCells() {
        return schemaCells;
    }

    /**
     * Adds a schema cell to this row.
     * 
     * @param schemaCell The schema cell to add
     */
    public void addSchemaCell(FixedWidthSchemaCell schemaCell) {
        if(schemaCell == null)
            throw new IllegalArgumentException("Cell schema cannot be null.");
        this.schemaCells.add(schemaCell);
    }





    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#clone()
     */
    public FixedWidthSchemaLine clone(){
        FixedWidthSchemaLine line;
        line = (FixedWidthSchemaLine) super.clone();

        line.schemaCells = new java.util.LinkedList<>();
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
    @SuppressWarnings("WeakerAccess")
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
    @SuppressWarnings("WeakerAccess")
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
    @SuppressWarnings("WeakerAccess")
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

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        return super.equals(obj) && obj instanceof FixedWidthSchemaLine;
    }

    /**
     * @return the minimal length of a line to generate. If the sum of all cells' length do not reach the length of a
     *         line, the line will be filled with the fill character.
     */
    public int getMinLength() {
        return minLength;
    }

    /**
     * The minimal length of a line to generate. If the sum of all cells' length do not reach the length of a line, the
     * line will be filled with the fill character. Use the method addFillerCellToReachMinLength() to make sure that
     * minLength is also used while reading.
     * 
     * @param length
     *            the length to set
     */
    public void setMinLength(int length) {
        this.minLength = length;
    }
    
    /**
     * Adds a schema cell at the end of the line to make sure that the total length generated/parsed will always be at
     * least the minLength of the line. The name of the added cell will be _fillToMinLength_. The length of the added
     * cell will be the difference between all minLength of the line and the cells added so far. Adding more schema
     * cells after calling this method will add those cells after the filler cell which will probably lead to unexpected
     * behavior.
     */
    void addFillerCellToReachMinLength() {
        if (getMinLength() <= 0)
            return;

        int diff = getMinLength() - getTotalCellLength();
        if (diff > 0) {
            addSchemaCell(new FixedWidthSchemaCell("_fillToMinLength_", diff));
        }

    }

    /**
     * @return The sum of the length of all cells.
     */
    int getTotalCellLength(){
        int sum = 0;
        for (FixedWidthSchemaCell schemaCell : schemaCells) {
            sum += schemaCell.getLength();
        }
        return sum;
    }

}
