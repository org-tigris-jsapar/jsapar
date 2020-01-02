package org.jsapar.schema;

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

        @Override
        public StringSchema build() {
            return new StringSchema(this);
        }
    }
}
