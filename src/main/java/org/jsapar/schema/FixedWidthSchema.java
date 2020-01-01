package org.jsapar.schema;

import org.jsapar.parse.fixed.FixedWidthParser;
import org.jsapar.parse.text.TextSchemaParser;
import org.jsapar.text.TextParseConfig;

import java.io.Reader;

/**
 * Defines a schema for a fixed position buffer. Each cell is defined by a fixed number of
 * characters. Each line is separated by the line separator defined in the base class {@link Schema}
 * .
 * <p>
 * If the end of line is reached before all cells are parsed the remaining cells will not be set.
 * <p>
 * If there are remaining characters when the end of line is reached, those characters will be
 * omitted.
 * <p>
 * If the line separator is an empty string, the lines will be separated by the sum of the length of
 * the cells within the schema.
 */
public class FixedWidthSchema extends Schema<FixedWidthSchemaLine> {

    @Deprecated
    public FixedWidthSchema() {
    }

    private FixedWidthSchema(Builder builder) {
        super(builder);
    }

    public static Builder builder(){
        return new Builder();
    }

    public static class Builder extends Schema.Builder<FixedWidthSchemaLine, FixedWidthSchema, Builder>{
        private QuoteSyntax quoteSyntax = QuoteSyntax.FIRST_LAST;

        private Builder(){
        }


        @Override
        public FixedWidthSchema build() {
            return new FixedWidthSchema(this);
        }
    }

    /*
         * (non-Javadoc)
         *
         * @see org.jsapar.schema.Schema#clone()
         */
    @Override
    public FixedWidthSchema clone() {
        FixedWidthSchema schema = (FixedWidthSchema) super.clone();

        return schema;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jsapar.schema.Schema#toString()
     */
    @Override
    public String toString() {
        return "FixedWidthSchema" + super.toString();
    }

    /**
     * Adds a schema cell at the end of each line to make sure that the total length generated/parsed will always be at
     * least the minLength of the line. The name of the added cell will be _fillToMinLength_. The length of the added
     * cell will be the difference between all minLength of the line and the cells added so far. Adding more schema
     * cells after calling this method will add those cells after the filler cell which will probably lead to unexpected
     * behavior.
     */
    void addFillerCellsToReachLineMinLength() {
        stream().forEach(FixedWidthSchemaLine::addFillerCellToReachMinLength);
    }

}
