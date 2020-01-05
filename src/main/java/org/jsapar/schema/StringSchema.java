package org.jsapar.schema;

import java.util.function.Function;

/**
 * A string schema can be used to describe the format of each individual cell so that they are converted correctly into
 * strings. This schema cannot be used to parse a text input or create a text output.
 * @see org.jsapar.Text2StringConverter
 * @see org.jsapar.compose.string.StringComposer
 * @see org.jsapar.compose.string.StringComposerNullOnEmptyCell
 */
public class StringSchema extends Schema<StringSchemaLine> {

    private StringSchema(Builder builder) {
        super(builder);
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
    public static Builder builder(StringSchema schema){
        return new Builder(schema);
    }

    public static class Builder extends Schema.Builder<StringSchemaLine, StringSchema, Builder>{

        private Builder(){
        }

        public Builder(StringSchema schema) {
            super(schema);
        }

        /**
         * Creates a line builder and calls provided function before using that builder to build a csv schema line.
         * @param lineType  The line type of the lines to create.
         * @param lineBuilderHandler The function that gets called for the builder.
         * @return This builder instance.
         */
        public StringSchema.Builder withLine(String lineType, Function<StringSchemaLine.Builder, StringSchemaLine.Builder> lineBuilderHandler){
            StringSchemaLine.Builder builder = StringSchemaLine.builder(lineType);
            this.withLine(lineBuilderHandler.apply(builder).build());
            return this;
        }

        @Override
        public StringSchema build() {
            return new StringSchema(this);
        }
    }
}
