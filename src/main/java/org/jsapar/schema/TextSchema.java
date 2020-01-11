package org.jsapar.schema;

public abstract class TextSchema<L extends SchemaLine<? extends SchemaCell>> extends Schema<L> {
    @Deprecated
    public TextSchema() {
    }

    <S extends TextSchema<L>, B extends Builder<L, S, B>> TextSchema(Builder<L, S, B> builder) {
        super(builder);
        super.setLineSeparator(builder.lineSeparator);
    }

    @SuppressWarnings("unchecked")
    public abstract static class Builder<L extends SchemaLine<? extends SchemaCell>, S extends TextSchema<L>, B extends TextSchema.Builder<L, S, B>> extends Schema.Builder<L, S, B> {
        private String lineSeparator = System.getProperty("line.separator");
        Builder() {
        }

        Builder(TextSchema<L> schema) {
            super(schema);
            this.lineSeparator = schema.getLineSeparator();
        }

        /**
         * @param lineSeparator  Line separator string. Default value is the system default
         *                       (Retrieved by {@code System.getProperty("line.separator")}).
         * @return This builder instance.
         */
        public B withLineSeparator(String lineSeparator){
            this.lineSeparator = lineSeparator;
            return (B)this;
        }

    }
}
