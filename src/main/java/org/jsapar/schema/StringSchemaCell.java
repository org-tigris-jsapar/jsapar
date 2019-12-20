package org.jsapar.schema;

/**
 * Can be used to format string values without any additional transformation. Useful for testing.
 */
public class StringSchemaCell extends SchemaCell {

    protected <T> StringSchemaCell(Builder<T> builder) {
        super(builder);
    }

    /**
     * Builder class. Created by static method {@link #builder(String)}
     * @param <T>
     */
    public static class Builder<T> extends SchemaCell.Builder<T, StringSchemaCell, StringSchemaCell.Builder<T>> {

        Builder(String name) {
            super(name);
        }

        @Override
        public StringSchemaCell build() {
            return new StringSchemaCell(this);
        }
    }

    /**
     * Creates a builder for a {@link StringSchemaCell} with a name.
     * @param name  The name of the schema cell to build.
     * @param <T> The type of the cell.
     * @return A builder that can be used to build {@link StringSchemaCell} instances.
     */
    public static <T> Builder<T> builder(String name) {
        return new Builder<>(name);
    }

}
