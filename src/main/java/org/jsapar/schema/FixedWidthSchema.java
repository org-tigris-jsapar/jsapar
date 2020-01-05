package org.jsapar.schema;

import org.jsapar.parse.fixed.FixedWidthParser;
import org.jsapar.parse.text.TextSchemaParser;
import org.jsapar.text.TextParseConfig;

import java.io.Reader;
import java.util.function.Function;

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

    /**
     * @return A new builder instance that can be used to create fixed width schemas.
     */
    public static Builder builder(){
        return new Builder();
    }

    /**
     * Makes it possible to create a new schema instance that is a copy of an existing schema.
     * @param schema The schema to create a new copy of.
     * @return A new builder that can be used to creat schemas.
     */
    public static Builder builder(FixedWidthSchema schema){
        return new Builder(schema);
    }

    public static class Builder extends Schema.Builder<FixedWidthSchemaLine, FixedWidthSchema, Builder>{

        private Builder(){
        }

        public Builder(FixedWidthSchema schema) {
            super(schema);
        }

        /**
         * Creates a line builder and calls provided function before using that builder to build a csv schema line.
         * @param lineType  The line type of the lines to create.
         * @param lineBuilderHandler The function that gets called for the builder.
         * @return This builder instance.
         */
        public FixedWidthSchema.Builder withLine(String lineType, Function<FixedWidthSchemaLine.Builder, FixedWidthSchemaLine.Builder> lineBuilderHandler){
            FixedWidthSchemaLine.Builder builder = FixedWidthSchemaLine.builder(lineType);
            this.withLine(lineBuilderHandler.apply(builder).build());
            return this;
        }


        @Override
        public FixedWidthSchema build() {
            return new FixedWidthSchema(this);
        }
    }

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
     *
     * Deprecated. This will be done automatically while using builders.
     */
    @Deprecated
    void addFillerCellsToReachLineMinLength() {
        stream().forEach(FixedWidthSchemaLine::addFillerCellToReachMinLength);
    }

}
