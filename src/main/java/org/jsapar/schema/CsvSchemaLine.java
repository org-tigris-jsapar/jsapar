package org.jsapar.schema;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Stream;

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
     */
    public CsvSchemaLine() {
        super();
    }

    /**
     * Creates an empty schema line which occurs nOccurs number of times.
     *
     * @param nOccurs The number of times this type of line occurs in the input.
     */
    public CsvSchemaLine(int nOccurs) {
        super(nOccurs);
    }

    /**
     * Creates a CsvSchemaLine with the supplied line type and occurs infinite number of times.
     *
     * @param lineType The type of the line
     */
    public CsvSchemaLine(String lineType) {
        super(lineType);
    }

    /**
     * Creates a CsvSchemaLine with the supplied line type and occurs supplied number of times.
     *
     * @param lineType The type of the line
     * @param nOccurs  The number of times this type of line occurs in the input/output.
     */
    public CsvSchemaLine(String lineType, int nOccurs) {
        super(lineType, nOccurs);
    }

    public CsvSchemaLine(Builder builder) {
        super(builder);
        this.cellSeparator = builder.cellSeparator;
        this.firstLineAsSchema = builder.firstLineAsSchema;
        this.quoteChar = builder.quoteChar;
    }


    public class Builder extends SchemaLine<CsvSchemaCell>.Builder<CsvSchemaLine, Builder>{
        private boolean firstLineAsSchema = false;
        private String cellSeparator = ";";
        private char quoteChar = '"';


        public Builder(String lineType) {
            super(lineType);
        }

        public Builder withFirstLineAsSchema(boolean firstLineAsSchema){
            this.firstLineAsSchema = firstLineAsSchema;
            return this;
        }

        public Builder withCellSeparator(String cellSeparator){
            this.cellSeparator = cellSeparator;
            return this;
        }

        public Builder withQuoteChar(Character quoteChar){
            this.quoteChar = quoteChar;
            return this;
        }

        @Override
        public CsvSchemaLine build() {
            return new CsvSchemaLine(this);
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
