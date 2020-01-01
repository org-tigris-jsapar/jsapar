package org.jsapar.schema;

import org.jsapar.parse.csv.CsvParser;
import org.jsapar.text.TextParseConfig;
import org.jsapar.parse.text.TextSchemaParser;

import java.io.Reader;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Defines a schema for a delimited input text. Each cell is delimited by a delimiter character sequence.
 * Lines are separated by the line separator defined by {@link Schema#getLineSeparator()}.
 * @see Schema
 * @see SchemaLine
 * @see CsvSchemaLine
 */
public class CsvSchema extends Schema<CsvSchemaLine> implements Cloneable{


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

    public static Builder builder(){
        return new Builder();
    }

    public static class Builder extends Schema.Builder<CsvSchemaLine, CsvSchema, Builder>{
        private QuoteSyntax quoteSyntax = QuoteSyntax.FIRST_LAST;

        private Builder(){
        }

        /**
         * @param quoteSyntax Specifies the syntax while parsing and composing of quoted cells. Default is {@link QuoteSyntax#FIRST_LAST}
         * @return This builder instance.
         */
        public Builder withQuoteSyntax(QuoteSyntax quoteSyntax){
            this.quoteSyntax = quoteSyntax;
            return this;
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

    /*
     * (non-Javadoc)
     * 
     * @see org.jsapar.schema.Schema#toString()
     */
    @Override
    public String toString() {
        return "CsvSchema" + super.toString();
    }

    public QuoteSyntax getQuoteSyntax() {
        return quoteSyntax;
    }

    public void setQuoteSyntax(QuoteSyntax quoteSyntax) {
        this.quoteSyntax = quoteSyntax;
    }
}
