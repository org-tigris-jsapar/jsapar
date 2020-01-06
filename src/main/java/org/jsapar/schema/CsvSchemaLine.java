package org.jsapar.schema;

import java.util.function.Function;

/**
 * Describes the schema for a delimiter separated line. For instance if you want to ignore a header line you
 * can add a SchemaLine instance to your schema with occurs==1 and ignoreRead==true;
 *
 * @see CsvSchema
 * @see CsvSchemaCell
 */
public class CsvSchemaLine extends SchemaLine<CsvSchemaCell> {
    private static final char NO_QUOTING = 0;


    /**
     * Set this to true when the first line of the input text contains the names and order of the cells in the following
     * lines, i.e. the first line is a header line that works as a schema for the rest of the file. In that case this
     * schema line instance will only be used to get
     * formatting instructions and default values, it will not denote the order of the cells. The order is given by the
     * first line of the input instead.
     */
    private boolean firstLineAsSchema = false;

    /**
     * The character sequence that separates each cell. Default is the ';' (semicolon) character.
     */
    private String cellSeparator = ";";

    /**
     * Specifies quote characters used to encapsulate cells. Default is the standard double quote character (").
     * Disable quoting by calling {@link #disableQuoteChar()}.
     * <p>
     * If quoted cells contain cell separator or line separator characters, these will be treated as content of the cell
     * instead.
     * <p>
     * Specify quoting syntax at schema level by calling {@link CsvSchema#setQuoteSyntax(QuoteSyntax)}.
     * <p>
     * Specify the quote behavior for each cell at cell level by calling {@link CsvSchemaCell#setQuoteBehavior(QuoteBehavior)}
     */
    private char quoteChar = '"';

    /**
     * Creates an empty schema line.
     * <p>
     * Deprecated since 2.2. Use builder instead.
     */
    @Deprecated
    public CsvSchemaLine() {
        super();
    }

    /**
     * Creates an empty schema line which occurs nOccurs number of times.
     * <p>
     * Deprecated since 2.2. Use builder instead.
     *
     * @param nOccurs The number of times this type of line occurs in the input.
     */
    @Deprecated
    public CsvSchemaLine(int nOccurs) {
        super(nOccurs);
    }

    /**
     * Creates a CsvSchemaLine with the supplied line type and occurs infinite number of times.
     * <p>
     * Deprecated since 2.2. Use builder instead.
     *
     * @param lineType The type of the line
     */
    @Deprecated
    public CsvSchemaLine(String lineType) {
        super(lineType);
    }

    /**
     * Creates a CsvSchemaLine with the supplied line type and occurs supplied number of times.
     * <p>
     * Deprecated since 2.2. Use builder instead.
     *
     * @param lineType The type of the line
     * @param nOccurs  The number of times this type of line occurs in the input/output.
     */
    @Deprecated
    public CsvSchemaLine(String lineType, int nOccurs) {
        super(lineType, nOccurs);
    }

    private CsvSchemaLine(Builder builder) {
        super(builder);
        this.cellSeparator = builder.cellSeparator;
        this.firstLineAsSchema = builder.firstLineAsSchema;
        this.quoteChar = builder.quoteChar;
    }

    /**
     * Creates a new builder for supplied line type.
     * @param lineType The name of the line type of the new instance to create.
     * @return A builder that builds CsvSchemaLine instances.
     */
    public static Builder builder(String lineType){
        return new Builder(lineType);
    }

    /**
     * Creates a new builder based on a supplied schema line. Can be used instead of clone as a copy constructor.
     * @param lineType       The name of the line type of the new instance to create.
     * @param schemaLine     The schema line to clone all values except the name from.
     * @return A builder that builds CsvSchemaLine instances.
     */
    public static Builder builder(String lineType, CsvSchemaLine schemaLine){
        return new Builder(lineType, schemaLine);
    }

    public static class Builder extends SchemaLine.Builder<CsvSchemaCell, CsvSchemaLine, Builder>{
        private boolean firstLineAsSchema = false;
        private String cellSeparator = ";";
        private char quoteChar = '"';
        private QuoteBehavior defaultQuoteBehavior = QuoteBehavior.AUTOMATIC;


        public Builder(String lineType) {
            super(lineType);
        }

        public Builder(String lineType, CsvSchemaLine schemaLine) {
            super(lineType, schemaLine);
            this.quoteChar = schemaLine.quoteChar;
            this.cellSeparator = schemaLine.cellSeparator;
            this.firstLineAsSchema = schemaLine.firstLineAsSchema;
        }

        /**
         * @param firstLineAsSchema Set this to true when the first line of the input text contains the names and order of the cells in the following
         *       lines, i.e. the first line is a header line that works as a schema for the rest of the file. In that case this
         *       schema line instance will only be used to get
         *       formatting instructions and default values, it will not denote the order of the cells. The order is given by the
         *       first line of the input instead. Default is false
         * @return This builder instance.
         */
        public Builder withFirstLineAsSchema(boolean firstLineAsSchema){
            this.firstLineAsSchema = firstLineAsSchema;
            return this;
        }

        /**
         * @param cellSeparator The character sequence that separates each cell. Default is the ';' (semicolon) character.
         * @return This builder instance.
         */
        public Builder withCellSeparator(String cellSeparator){
            this.cellSeparator = cellSeparator;
            return this;
        }

        /**
         * @param quoteChar Specifies quote character used to encapsulate cells. Default is the standard double quote character (").
         *       Disable quoting by calling {@link #withoutQuoteChar()}.
         * @return This builder instance.
         */
        public Builder withQuoteChar(char quoteChar){
            this.quoteChar = quoteChar;
            return this;
        }

        /**
         * Disables quoting for this line type. Successive call to {@link #withQuoteChar(char)} will again enable quoting.
          * @return This builder instance.
         */
        public Builder withoutQuoteChar() {
            this.quoteChar = NO_QUOTING;
            return this;
        }

        /**
         * Using this method to set a default quote behavior will have no effect unless the defaults are applied to cell
         * builder that is supposed to use this value. The default quote behavior will not be assigned to the schema line but only
         * used during building phase.
         * @param quoteBehavior The default quote behavior for all cells. Default is {@link QuoteBehavior#AUTOMATIC}.
         * @return This builder instance.
         */
        public Builder withDefaultQuoteBehavior(QuoteBehavior quoteBehavior) {
            this.defaultQuoteBehavior = quoteBehavior;
            return this;
        }

        /**
         * Convenience method that creates cell builder, applies defaults, calls the provided function before using that builder to create a schema cell.
         * @param cellName The name of the cell.
         * @param cellBuilderHandler The function that gets called with the created builder.
         * @return This builder instance.
         * @see SchemaLine.Builder#withCell(SchemaCell)
         */
        public Builder withCell(String cellName, Function<CsvSchemaCell.Builder<?>, CsvSchemaCell.Builder<?>> cellBuilderHandler){
            return this.withCell(cellBuilderHandler.apply(CsvSchemaCell.builder(cellName).applyDefaultsFrom(this)).build());
        }

        /**
         * Convenience method that creates a string csv cell with supplied name without any further
         * formatting and adds it to this builder. For more advanced options use {@link SchemaLine.Builder#withCell(SchemaCell)}
         * @param cellName The name of the cell.
         * @return This builder instance.
         * @see SchemaLine.Builder#withCell(SchemaCell)
         */
        public Builder withCell(String cellName){
            return this.withCell(cellName, c->c);
        }

        /**
         * Convenience method that creates a string csv cell with supplied names without any further
         * formatting and adds it to this builder. For more advanced options use {@link SchemaLine.Builder#withCell(SchemaCell)}
         * @param cellNames The names of the cells.
         * @return This builder instance.
         * @see SchemaLine.Builder#withCell(SchemaCell)
         */
        public Builder withCells(String ... cellNames){
            for(String cellName: cellNames){
                withCell(cellName);
            }
            return this;
        }

        @Override
        public CsvSchemaLine build() {
            return new CsvSchemaLine(this);
        }

        public QuoteBehavior getDefaultQuoteBehavior() {
            return defaultQuoteBehavior;
        }
    }


    public String getCellSeparator() {
        return cellSeparator;
    }

    /**
     * Sets the character sequence that separates each cell.
     * <p>
     * In output schemas the non-breaking space character '\u00A0' is not allowed unless quote character is specified
     * since that character is used to replace any occurrence of the separator within each cell.
     *
     * @param cellSeparator the cellSeparator to set
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
        CsvSchemaLine line;
        line = (CsvSchemaLine) super.clone();
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
        return sb.toString();
    }

    public boolean isFirstLineAsSchema() {
        return firstLineAsSchema;
    }

    public void setFirstLineAsSchema(boolean firstLineAsSchema) {
        this.firstLineAsSchema = firstLineAsSchema;
    }

    public char getQuoteChar() {
        return quoteChar;
    }

    /**
     * @return true if quote character is used, false otherwise.
     */
    public boolean isQuoteCharUsed() {
        return this.quoteChar != NO_QUOTING;
    }

    public void disableQuoteChar() {
        this.quoteChar = NO_QUOTING;
    }

    public void setQuoteChar(char quoteChar) {
        this.quoteChar = quoteChar;
    }


}
