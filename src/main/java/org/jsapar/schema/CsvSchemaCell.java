package org.jsapar.schema;

import org.jsapar.model.CellType;

import java.util.Locale;

/**
 * Describes the schema for a specific csv cell.
 * Create instances by using the builder provided by {@link #builder(String)} or {@link #builder(String, CsvSchemaCell)}
 */
@SuppressWarnings("WeakerAccess")
public class CsvSchemaCell extends SchemaCell {

    /**
     * The quote behavior for the cell when composing. Default is {@link QuoteBehavior#AUTOMATIC}. Not used while parsing.
     */
    private QuoteBehavior quoteBehavior = QuoteBehavior.AUTOMATIC;

    /**
     * The maximum number of characters that are read or written to/from the cell. Input and output
     * value will be silently truncated to this length. If you want to get an error when field is to
     * long, use the format regexp pattern instead.
     * <p>
     * A negative value indicates that max length will not be checked.
     */
    private int maxLength = -1;

    /**
     * Creates a CSV string cell with specified name. The format can be added after creation by using the {@link #setCellFormat(CellType, String)} method.
     * <p>
     * Deprecated since 2.2. Use builder instead.
     *
     * @param sName The name of the cell.
     */
    @Deprecated
    public CsvSchemaCell(String sName) {
        super(sName);
    }

    /**
     * Creates a CSV schema cell with the specified name and format parameters.
     * <p>
     * Deprecated since 2.2. Use builder instead.
     *
     * @param sName   The name of the cell.
     * @param type    The type of the cell.
     * @param pattern The pattern to use while formatting and parsing. The pattern has different meaning depending on the type of the cell.
     * @param locale  The locale to use while formatting and parsing dates and numbers that are locale specific. If null, US locale is used.
     */
    @Deprecated
    public CsvSchemaCell(String sName, CellType type, String pattern, Locale locale) {
        super(sName, type, pattern, locale);
    }

    @Deprecated
    public CsvSchemaCell(String name, CellType type) {
        super(name, type);
    }

    private <T> CsvSchemaCell(Builder<T> builder) {
        super(builder);
        this.maxLength = builder.maxLength;
        this.quoteBehavior = builder.quoteBehavior;
    }

    /**
     * Creates a new builder for a schema cell with supplied name.
     * @param name The name of the schema cell.
     * @param <T> The expected value type of the cells produced by this schema cell.
     * @return A builder that builds CsvSchemaCell instances.
     */
    public static <T> Builder<T> builder(String name) {
        return new Builder<>(name);
    }

    /**
     * Creates a new builder based on a supplied schema cell. Can be used instead of clone as a copy constructor.
     * @param name       The name of the new instance to create.
     * @param schemaCell The schema cell to clone all values except the name from.
     * @return A builder that builds CsvSchemaCell instances.
     * @param <T> The expected value type of the cells produced by this schema cell.
     */
    public static <T> Builder<T> builder(String name, CsvSchemaCell schemaCell) {
        return new Builder<>(name, schemaCell);
    }

    public static class Builder<T> extends SchemaCell.Builder<T, CsvSchemaCell, Builder<T>> {
        private QuoteBehavior quoteBehavior = QuoteBehavior.AUTOMATIC;
        private int maxLength = -1;

        /**
         * @param quoteBehavior The quote behavior for the cell when composing. Default is {@link QuoteBehavior#AUTOMATIC}. Not used while parsing.
         * @return This builder instance.
         */
        public Builder<T> withQuoteBehavior(QuoteBehavior quoteBehavior) {
            this.quoteBehavior = quoteBehavior;
            return this;
        }

        /**
         * @param maxLength The maximum number of characters that are read or written to/from the cell. Input and output
         *                  value will be silently truncated to this length. If you want to get an error when field is to
         *                  long, use the format regexp pattern instead.
         *                  <p>
         *                  A negative value indicates that max length will not be checked.
         * @return This builder instance.
         */
        public Builder<T> withMaxLength(int maxLength) {
            this.maxLength = maxLength;
            return this;
        }

        private Builder(String name) {
            super(name);
        }

        private Builder(String name, CsvSchemaCell schemaCell) {
            super(name, schemaCell);
            this.maxLength = schemaCell.maxLength;
            this.quoteBehavior = schemaCell.quoteBehavior;
        }

        /**
         * @return A newly created CsvSchemaCell instance.
         */
        @Override
        public CsvSchemaCell build() {
            return new CsvSchemaCell(this);
        }
    }

    @Override
    public CsvSchemaCell clone() {
        return (CsvSchemaCell) super.clone();
    }

    /**
     * @return The maximum number of characters that are read or written to/from the cell. Input and output
     * value will be silently truncated to this length. If you want to get an error when field is to
     * long, use the format regexp pattern instead.
     * <p>
     * A negative value indicates that max length will not be checked.
     */
    public int getMaxLength() {
        return maxLength;
    }

    /**
     * @param maxLength The maximum number of characters that are read or written to/from the cell. Input and output
     *                  value will be silently truncated to this length. If you want to get an error when field is to
     *                  long, use the format regexp pattern instead.
     *                  <p>
     *                  A negative value indicates that max length will not be checked.
     */
    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }

    /**
     * @return True if maxLength should be considered.
     */
    public boolean isMaxLength() {
        return this.maxLength > 0;
    }

    public QuoteBehavior getQuoteBehavior() {
        return quoteBehavior;
    }

    public void setQuoteBehavior(QuoteBehavior quoteBehavior) {
        this.quoteBehavior = quoteBehavior;
    }
}
