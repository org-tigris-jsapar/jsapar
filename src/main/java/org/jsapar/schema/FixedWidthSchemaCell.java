package org.jsapar.schema;

import org.jsapar.model.CellType;

/**
 * Describes how a cell is represented for a fixed width schema.
 */
public class FixedWidthSchemaCell extends SchemaCell {


    /**
     * Describes how a cell is aligned within its allocated space.
     * 
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
    private int       length;

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
     *
     * Deprecated since 2.2. Use builder instead.
     * @param sName
     *            The name of the cell
     * @param nLength
     *            The length of the cell
     * @param alignment
     *            The alignment of the cell content within the allocated space
     * @param padCharacter
     *            The pad character to use to fill the cell.
     */
    @Deprecated
    public FixedWidthSchemaCell(String sName, int nLength, Alignment alignment, Character padCharacter) {
        this(sName, nLength);
        this.alignment = alignment;
        this.padCharacter = padCharacter;
    }


    /**
     * Creates a fixed with schema cell with specified name and length.
     *
     * Deprecated since 2.2. Use builder instead.
     * @param sName
     *            The name of the cell
     * @param nLength
     *            The length of the cell
     */
    @Deprecated
    public FixedWidthSchemaCell(String sName, int nLength) {
        super(sName);
        this.length = nLength;
    }

    /**
     * Creates a fixed with schema cell with specified name, length and format.
     *
     * Deprecated since 2.2. Use builder instead.
     * @param sName
     *            The name of the cell
     * @param nLength
     *            The length of the cell
     * @param cellFormat
     *            The format of the cell
     */
    @Deprecated
    public FixedWidthSchemaCell(String sName, int nLength, SchemaCellFormat cellFormat) {
        super(sName, cellFormat, null);
        this.length = nLength;
    }

    private <T> FixedWidthSchemaCell(Builder<T> builder){
        super(builder);
        this.length = builder.length;
        this.alignment = builder.alignment != null ? builder.alignment : defaultAlignment(getCellFormat().getCellType());
        this.trimLeadingSpaces = builder.trimLeadingSpaces;
        this.trimPadCharacter = builder.trimPadCharacter;
        this.padCharacter = builder.padCharacter;
    }

    public static <T> Builder<T> builder(String name, int length){
        return new Builder<>(name, length);
    }

    public static class Builder<T> extends SchemaCell.Builder<T, FixedWidthSchemaCell, Builder<T>>{
        private final int length;
        private Alignment alignment;
        private char padCharacter = ' ';
        private boolean trimPadCharacter = true;
        private boolean trimLeadingSpaces = true;

        private Builder(String name, int length) {
            super(name);
            this.length = length;
        }

        public Builder<T> withAlignment(Alignment alignment) {
            this.alignment = alignment;
            return this;
        }

        public Builder<T> withPadCharacter(char padCharacter){
            this.padCharacter = padCharacter;
            return this;
        }

        public Builder<T> withTrimPadCharacter(boolean trimPadCharacter) {
            this.trimPadCharacter = trimPadCharacter;
            return this;
        }

        public Builder<T> withTrimLeadingSpaces(boolean trimLeadingSpaces) {
            this.trimLeadingSpaces = trimLeadingSpaces;
            return this;
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
     * @param length
     *            the length to set
     */
    public void setLength(int length) {
        this.length = length;
    }


    /**
     * The alignment of the cell content within the allocated space. Default is Alignment.LEFT.
     * @return the alignment
     */
    public Alignment getAlignment() {
        return alignment;
    }

    /**
     * The alignment of the cell content within the allocated space. Default is Alignment.LEFT.
     * @param alignment
     *            the alignment to set
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

    private static Alignment defaultAlignment(CellType type){
        if(type == CellType.INTEGER || type == CellType.DECIMAL )
            return Alignment.RIGHT;
        else
            return Alignment.LEFT;
    }

    public FixedWidthSchemaCell clone(){
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

    public char getPadCharacter() {
        return padCharacter;
    }

    public void setPadCharacter(char padCharacter) {
        this.padCharacter = padCharacter;
    }

    public boolean isTrimPadCharacter() {
        return trimPadCharacter;
    }

    public void setTrimPadCharacter(boolean trimPadCharacter) {
        this.trimPadCharacter = trimPadCharacter;
    }

    public boolean isTrimLeadingSpaces() {
        return trimLeadingSpaces;
    }

    public void setTrimLeadingSpaces(boolean trimLeadingSpaces) {
        this.trimLeadingSpaces = trimLeadingSpaces;
    }

}
