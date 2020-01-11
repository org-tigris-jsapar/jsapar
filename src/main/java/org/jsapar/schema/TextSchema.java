package org.jsapar.schema;

import org.jsapar.utils.StringUtils;

public abstract class TextSchema<L extends SchemaLine<? extends SchemaCell>> extends Schema<L> {
    private String lineSeparator = System.getProperty("line.separator");

    @Deprecated
    public TextSchema() {
    }

    <S extends TextSchema<L>, B extends Builder<L, S, B>> TextSchema(Builder<L, S, B> builder) {
        super(builder);
        this.lineSeparator = builder.lineSeparator;
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

    /**
     * Line separator string. Default value is the system default (Retrieved by
     * {@code System.getProperty("line.separator")}).
     * @return the lineSeparator
     */
    public String getLineSeparator() {
        return lineSeparator;
    }

    /**
     * Sets the line separator string. Default value is the system default (Retrieved by
     * {@code System.getProperty("line.separator")}).
     *
     * @param lineSeparator
     *            the lineSeparator to set.
     */
    public void setLineSeparator(String lineSeparator) {
        this.lineSeparator = lineSeparator;
    }

    @Override
    public String toString() {
        return " lineSeparator='" + StringUtils.replaceJava2Escapes(this.lineSeparator) + "'" + super.toString();
    }
}
