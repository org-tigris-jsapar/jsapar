package org.jsapar.schema;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Defines a schema for a delimited input text. Each cell is delimited by a delimiter character sequence.
 * Lines are separated by the line separator defined by {@link Schema#getLineSeparator()}.
 * @see Schema
 * @see SchemaLine
 * @see CsvSchemaLine
 */
public class CsvSchema extends TextSchema<CsvSchemaLine> implements Cloneable{


    /**
     * Specifies the syntax while parsing and composing of quoted cells. Default is {@link QuoteSyntax#FIRST_LAST}
     */
    private QuoteSyntax quoteSyntax = QuoteSyntax.FIRST_LAST;

    @Deprecated
    public CsvSchema() {
    }

    private CsvSchema(Builder builder) {
        super(builder);
        this.quoteSyntax = builder.quoteSyntax;
    }

    /**
     * @return A new builder that can be used to creat schemas.
     */
    public static Builder builder(){
        return new Builder();
    }

    /**
     * Makes it possible to create a new schema instance that is a copy of an existing schema.
     * @param schema The schema to create a new copy of.
     * @return A new builder that can be used to creat schemas.
     */
    public static Builder builder(CsvSchema schema){
        return new Builder(schema);
    }

    public static class Builder extends TextSchema.Builder<CsvSchemaLine, CsvSchema, Builder>{
        private QuoteSyntax quoteSyntax = QuoteSyntax.FIRST_LAST;

        private Builder(){
        }

        private Builder(CsvSchema schema) {
            super(schema);
            this.quoteSyntax = schema.quoteSyntax;
        }

        /**
         * @param quoteSyntax Specifies the syntax while parsing and composing of quoted cells. Default is {@link QuoteSyntax#FIRST_LAST}
         * @return This builder instance.
         */
        public Builder withQuoteSyntax(QuoteSyntax quoteSyntax){
            this.quoteSyntax = quoteSyntax;
            return this;
        }

        /**
         * Creates a line builder, applies defaults and calls provided function before using that builder to build a csv schema line.
         * @param lineType  The line type of the lines to create.
         * @param lineBuilderHandler The function that gets called for the builder.
         * @return This builder instance.
         */
        public Builder withLine(String lineType, Function<CsvSchemaLine.Builder, CsvSchemaLine.Builder> lineBuilderHandler){
            return this.withLine(lineBuilderHandler.apply(CsvSchemaLine.builder(lineType).applyDefaultsFrom(this)).build());
        }

        @Override
        public CsvSchema build() {
            return new CsvSchema(this);
        }
    }
    

    @Override
    public CsvSchema clone() {
        CsvSchema schema;
        schema = (CsvSchema) super.clone();

        return schema;
    }

    @Override
    public String toString() {
        return "CsvSchema" + super.toString();
    }

    /**
     * @return The syntax while parsing and composing of quoted cells.
     * @see QuoteSyntax
     */
    public QuoteSyntax getQuoteSyntax() {
        return quoteSyntax;
    }

    /**
     * @param quoteSyntax Specifies the syntax while parsing and composing of quoted cells. Default is {@link QuoteSyntax#FIRST_LAST}
     * @see QuoteSyntax
     */
    public void setQuoteSyntax(QuoteSyntax quoteSyntax) {
        this.quoteSyntax = quoteSyntax;
    }
}
