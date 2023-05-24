package org.jsapar.schema;

import org.jsapar.model.CellType;

/**
 * Describes how a cell is represented for a fixed width schema.
 * Create instances by using the builder provided by {@link #builder(String, int)} or {@link #builder(String, int, FixedWidthSchemaCell)}
 */
public class FixedWidthSchemaCell extends SchemaCell {


    /**
     * Describes how a cell is aligned within its allocated space.
     */
    public enum Alignment {

        /**
         * Content of the cell is left aligned and filled/truncated to the right to reach correct size.
         */
        LEFT,
        /**
         * Content of the cell is center aligned and filled/truncated to both left and right to reach correct size.
         */
        CENTER,
        /**
         * Content of the cell is right aligned and filled/truncated to the left to reach correct size.
         */
        RIGHT


    }

    /**
     * The length of the cell.
     */
    private int length;

    /**
     * The alignment of the cell content within the allocated space. Default is Alignment.LEFT.
     */
    private Alignment alignment = Alignment.LEFT;

    /**
     * The pad character to use to fill cell according to alignment to its defined fix size.
     */
    private char padCharacter = ' ';

    /**
     * If set to true, pad characters are trimmed according to alignment while parsing. Default is true.
     */
    private boolean trimPadCharacter = true;

    /**
     * If pad character is not space and this attribute is true then trim leading spaces before trimming pad character.
     * Can be used for numeric cells that may contain space character before any leading zeros or that may contain
     * only spaces as indication of absent value.
     * <p>
     * Default is true.
     */
    private boolean trimLeadingSpaces = true;

    /**
     * Creates a fixed with schema cell with specified name, length and alignment.
     * <p>
     * Deprecated since 2.2. Use builder instead.
     *
     * @param sName        The name of the cell
     * @param nLength      The length of the cell
     * @param alignment    The alignment of the cell content within the allocated space
     * @param padCharacter The pad character to use to fill the cell.
     */
    @Deprecated
    public FixedWidthSchemaCell(String sName, int nLength, Alignment alignment, Character padCharacter) {
        this(sName, nLength);
        this.alignment = alignment;
        this.padCharacter = padCharacter;
    }


    /**
     * Creates a fixed with schema cell with specified name and length.
     * <p>
     * Deprecated since 2.2. Use builder instead.
     *
     * @param sName   The name of the cell
     * @param nLength The length of the cell
     */
    @Deprecated
    public FixedWidthSchemaCell(String sName, int nLength) {
        super(sName);
        this.length = nLength;
    }

    /**
     * Creates a fixed with schema cell with specified name, length and format.
     * <p>
     * Deprecated since 2.2. Use builder instead.
     *
     * @param sName      The name of the cell
     * @param nLength    The length of the cell
     * @param cellFormat The format of the cell
     */
    @Deprecated
    public FixedWidthSchemaCell(String sName, int nLength, SchemaCellFormat cellFormat) {
        super(sName, cellFormat, null);
        this.length = nLength;
    }

    private <T> FixedWidthSchemaCell(Builder<T> builder) {
        super(builder);
        this.length = builder.length;
        this.alignment = builder.alignment != null ? builder.alignment : defaultAlignment(getCellFormat().getCellType());
        this.trimLeadingSpaces = builder.trimLeadingSpaces;
        this.trimPadCharacter = builder.trimPadCharacter;
        this.padCharacter = builder.padCharacter;
    }

    /**
     * Creates a new builder for a schema cell with supplied name and length.
     * @param name  The name of the schema cell
     * @param length The width of the cell.
     * @param <T> The expected value type of the cells produced by this schema cell.
     * @return A builder that builds FixedWidthSchemaCell instances.
     */
    public static <T> Builder<T> builder(String name, int length) {
        return new Builder<>(name, length);
    }

    /**
     * Creates a new builder based on a supplied schema cell. Can be used instead of clone as a copy constructor.
     * @param name       The name of the new instance to create.
     * @param length The width of the cell.
     * @param schemaCell The schema cell to clone all values except the name from.
     * @return A builder that builds FixedWidthSchemaCell instances.
     * @param <T> The expected value type of the cells produced by this schema cell.
     */
    public static <T> Builder<T> builder(String name, int length, FixedWidthSchemaCell schemaCell) {
        return new Builder<>(name, length, schemaCell);
    }

    /**
     * A builder that creates instances of FixedWidthSchemaCell
     */
    public static class Builder<T> extends SchemaCell.Builder<T, FixedWidthSchemaCell, Builder<T>> {
        private final int length;
        private Alignment alignment;
        private char padCharacter = ' ';
        private boolean trimPadCharacter = true;
        private boolean trimLeadingSpaces = true;

        private Builder(String name, int length) {
            super(name);
            this.length = length;
        }

        private Builder(String name, int length, FixedWidthSchemaCell schemaCell) {
            super(name, schemaCell);
            this.length = length;
            this.alignment = schemaCell.alignment;
            this.padCharacter = schemaCell.padCharacter;
            this.trimPadCharacter = schemaCell.trimPadCharacter;
            this.trimLeadingSpaces = schemaCell.trimLeadingSpaces;
        }

        /**
         * @param alignment The alignment of the cell content within the allocated space. Default is Alignment.LEFT for none numeric cells and Alignment.RIGHT for numeric cells.
         * @return This builder instance.
         */
        public Builder<T> withAlignment(Alignment alignment) {
            this.alignment = alignment;
            return this;
        }

        /**
         * @param padCharacter The pad character to use to fill cell according to alignment to its defined fix size.
         * @return This builder instance.
         */
        public Builder<T> withPadCharacter(char padCharacter) {
            this.padCharacter = padCharacter;
            return this;
        }

        /**
         * @param trimPadCharacter If set to true, pad characters are trimmed according to alignment while parsing. Default is true.
         * @return This builder instance.
         */
        public Builder<T> withTrimPadCharacter(boolean trimPadCharacter) {
            this.trimPadCharacter = trimPadCharacter;
            return this;
        }

        /**
         * @param trimLeadingSpaces If pad character is not space and this attribute is true then trim leading spaces before trimming pad character.
         *                          Can be used for numeric cells that may contain space character before any leading zeros or that may contain
         *                          only spaces as indication of absent value.
         *                          <p>
         *                          Default is true.
         * @return This builder instance.
         */
        public Builder<T> withTrimLeadingSpaces(boolean trimLeadingSpaces) {
            this.trimLeadingSpaces = trimLeadingSpaces;
            return this;
        }

        Builder<T> applyDefaultsFrom(FixedWidthSchemaLine.Builder schemaLineBuilder) {
            super.applyDefaultsFrom(schemaLineBuilder);
            return withPadCharacter(schemaLineBuilder.getPadCharacter());
        }

        @Override
        public FixedWidthSchemaCell build() {
            return new FixedWidthSchemaCell(this);
        }
    }

    /**
     * @return the length
     */
    public int getLength() {
        return length;
    }

    /**
     * @param length the length to set
     */
    public void setLength(int length) {
        this.length = length;
    }


    /**
     * The alignment of the cell content within the allocated space. Default is Alignment.LEFT.
     *
     * @return the alignment
     */
    public Alignment getAlignment() {
        return alignment;
    }

    /**
     * The alignment of the cell content within the allocated space. Default is Alignment.LEFT.
     * @param alignment the alignment to set
     */
    public void setAlignment(Alignment alignment) {
        this.alignment = alignment;
    }

    /**
     * Sets alignment for default value according to current cell type. For type INTEGER and DECIMAL, alignment is
     * RIGHT. For all other types, alignment is LEFT.
     */
    @SuppressWarnings("WeakerAccess")
    public void setDefaultAlignmentForType() {
        this.alignment = defaultAlignment(getCellFormat().getCellType());
    }

    private static Alignment defaultAlignment(CellType type) {
        if (type == CellType.INTEGER || type == CellType.DECIMAL)
            return Alignment.RIGHT;
        else
            return Alignment.LEFT;
    }

    public FixedWidthSchemaCell clone() {
        return (FixedWidthSchemaCell) super.clone();
    }

    @Override
    public String toString() {
        return super.toString() +
                " length=" +
                this.length +
                " alignment=" +
                this.alignment;
    }

    /**
     * @return The pad character to use to fill cell according to alignment to its defined fix size.
     */
    public char getPadCharacter() {
        return padCharacter;
    }

    /**
     * @param padCharacter The pad character to use to fill cell according to alignment to its defined fix size.
     */
    public void setPadCharacter(char padCharacter) {
        this.padCharacter = padCharacter;
    }

    /**
     * @return True if pad character should be trimmed while parsing.
     */
    public boolean isTrimPadCharacter() {
        return trimPadCharacter;
    }

    /**
     * @param trimPadCharacter True if pad character should be trimmed while parsing.
     */
    public void setTrimPadCharacter(boolean trimPadCharacter) {
        this.trimPadCharacter = trimPadCharacter;
    }

    /**
     * @return True if leading space characters should be removed while parsing.
     */
    public boolean isTrimLeadingSpaces() {
        return trimLeadingSpaces;
    }

    /**
     * @param trimLeadingSpaces True if leading space characters should be removed while parsing.
     */
    public void setTrimLeadingSpaces(boolean trimLeadingSpaces) {
        this.trimLeadingSpaces = trimLeadingSpaces;
    }

}
