package org.jsapar.schema;

import java.util.function.Function;

/**
 * This class represents the schema for a line of a fixed with file. Each cell within the line has a
 * specified size. There are no delimiter characters.
 */
public class FixedWidthSchemaLine extends SchemaLine<FixedWidthSchemaCell> {

    private char padCharacter = ' ';
    private int minLength = -1;

    /**
     * Creates an empty schema line.
     * <p>
     * Deprecated since 2.2. Use builder instead.
     */
    @Deprecated
    public FixedWidthSchemaLine() {
        super();
    }

    /**
     * Creates an empty schema line which will occur nOccurs times within the file. When the line
     * has occured nOccurs times this schema-line will not be used any more.
     *
     * <p>
     * Deprecated since 2.2. Use builder instead.
     * @param nOccurs The number of times this schema line is used while parsing or writing.
     */
    @Deprecated
    public FixedWidthSchemaLine(int nOccurs) {
        super(nOccurs);
    }

    /**
     * Creates an empty schema line which parses lines of type lineType.
     *
     * <p>
     * Deprecated since 2.2. Use builder instead.
     * @param lineType The line type for which this schema line is used. The line type is stored as the
     *                 lineType of the generated Line.
     */
    @Deprecated
    public FixedWidthSchemaLine(String lineType) {
        super(lineType);
    }


    /**
     * Creates a fixed width schema line with the supplied line type and specified to occur supplied number of times.
     *
     * <p>
     * Deprecated since 2.2. Use builder instead.
     * @param lineType The line type of this schema line.
     * @param nOccurs  The number of times it should occur.
     */
    @Deprecated
    public FixedWidthSchemaLine(String lineType, int nOccurs) {
        super(lineType, nOccurs);
    }

    private FixedWidthSchemaLine(Builder builder) {
        super(builder);
        this.minLength = builder.minLength;
        this.padCharacter = builder.padCharacter;
        if(builder.minLength > 0) {
            addFillerCellToReachMinLength();
        }
    }

    /**
     * Creates a new builder for supplied line type.
     * @param lineType The name of the line type of the new instance to create.
     * @return A builder that builds FixedWithSchemaLine instances.
     */
    public static Builder builder(String lineType) {
        return new Builder(lineType);
    }

    /**
     * Creates a new builder based on a supplied schema line. Can be used instead of clone as a copy constructor.
     * @param lineType       The name of the line type of the new instance to create.
     * @param schemaLine     The schema line to clone all values except the name from.
     * @return A builder that builds FixedWithSchemaLine instances.
     */
    public static Builder builder(String lineType, FixedWidthSchemaLine schemaLine)
    {
        return new Builder(lineType, schemaLine);
    }

    public static class Builder extends SchemaLine.Builder<FixedWidthSchemaCell, FixedWidthSchemaLine, Builder> {
        private char padCharacter = ' ';
        private int minLength = -1;


        public Builder(String lineType) {
            super(lineType);
        }

        public Builder(String lineType, FixedWidthSchemaLine schemaLine) {
            super(lineType, schemaLine);
            this.padCharacter = schemaLine.padCharacter;
            this.minLength = schemaLine.minLength;
        }

        /**
         * @param padCharacter The pad character to use when padding line. Default is space.
         * @return This builder instance.
         */
        public Builder withPadCharacter(char padCharacter) {
            this.padCharacter = padCharacter;
            return this;
        }

        /**
         * @param minLength The minimal length of a line to generate. If the sum of all cells' length do not reach the length of a line, the
         *      line will be filled with the pad character. When parsing, the parse configuration determines the behavior if this length is not reached.
         * @return This builder instance.
         */
        public Builder withMinLength(int minLength) {
            this.minLength = minLength;
            return this;
        }

        /**
         * Convenience method that creates cell builder, applies defaults, calls the provided function before using that builder to create a schema cell.
         *
         * @param name               The name of the cell.
         * @param length             The width of the cell.
         * @param cellBuilderHandler The function that gets called with the created builder.
         * @return This builder instance.
         * @see SchemaLine.Builder#withCell(SchemaCell)
         */
        public FixedWidthSchemaLine.Builder withCell(String name, int length, Function<FixedWidthSchemaCell.Builder<?>, FixedWidthSchemaCell.Builder<?>> cellBuilderHandler) {
            return this.withCell(cellBuilderHandler.apply(FixedWidthSchemaCell.builder(name, length).applyDefaultsFrom(this)).build());
        }

        /**
         * Convenience method that creates a string csv cell with supplied name and length without any further
         * formatting and adds it to this builder. For more advanced options use {@link SchemaLine.Builder#withCell(SchemaCell)}
         *
         * @param cellName   The name of the cell.
         * @param cellLength The length of the cell.
         * @return This builder instance.
         * @see SchemaLine.Builder#withCell(SchemaCell)
         */
        public Builder withCell(String cellName, int cellLength) {
            return this.withCell(cellName, cellLength, c->c);
        }

        @Override
        public FixedWidthSchemaLine build() {
            return new FixedWidthSchemaLine(this);
        }

        public char getPadCharacter() {
            return this.padCharacter;
        }
    }


    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#clone()
     */
    public FixedWidthSchemaLine clone() {
        FixedWidthSchemaLine line;
        line = (FixedWidthSchemaLine) super.clone();
        return line;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return super.toString() + " padCharacter='" + this.padCharacter;
    }

    /**
     * @return the padCharacter
     */
    public char getPadCharacter() {
        return padCharacter;
    }

    /**
     * @param padCharacter the padCharacter to set
     */
    @SuppressWarnings("WeakerAccess")
    public void setPadCharacter(char padCharacter) {
        this.padCharacter = padCharacter;
    }

    /**
     * Finds the cell's fist and last positions within a line. First position starts with 1.
     *
     * @param cellName The name of the cell to find positions for.
     * @return The cell positions for the cell with the supplied name, null if no such cell exists.
     */
    @SuppressWarnings("WeakerAccess")
    public FixedWidthCellPositions getCellPositions(String cellName) {
        FixedWidthCellPositions pos = new FixedWidthCellPositions();
        for (FixedWidthSchemaCell cell : this) {
            pos.increment(cell);
            if (cell.getName().equals(cellName))
                return pos;
        }
        return null;
    }

    /**
     * Finds the cell's fist position within a line. First position starts with 1.
     *
     * @param cellName The name of the cell to find positions for.
     * @return The cell's first position for the cell with the supplied name, -1 if no such cell
     * exists.
     */
    @SuppressWarnings("WeakerAccess")
    public int getCellFirstPosition(String cellName) {
        FixedWidthCellPositions pos = getCellPositions(cellName);
        return pos != null ? pos.getFirst() : -1;
    }

    /**
     * @return the minimal length of a line to generate. If the sum of all cells' length do not reach the length of a
     * line, the line will be filled with the fill character.
     */
    public int getMinLength() {
        return minLength;
    }

    /**
     * The minimal length of a line to generate. If the sum of all cells' length do not reach the length of a line, the
     * line will be filled with the fill character. Use the method addFillerCellToReachMinLength() to make sure that
     * minLength is also used while reading.
     *
     * @param length the length to set
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
            addSchemaCell( FixedWidthSchemaCell.builder("_fillToMinLength_", diff).build());
        }

    }

    /**
     * @return The sum of the length of all cells.
     */
    public int getTotalCellLength() {
        return stream().mapToInt(FixedWidthSchemaCell::getLength).sum();
    }

}
