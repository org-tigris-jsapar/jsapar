package org.jsapar.schema;

import org.jsapar.model.Document;
import org.jsapar.utils.StringUtils;

import java.io.Reader;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.util.*;
import java.util.stream.Stream;

/**
 * Abstract base class for all type of jsapar schemata. A schema describes how the buffer should be
 * parsed or how the lines of a {@link Document} should be written. Usually the parse and output methods are
 * called from one of the in, out or io classes.
 * 
 * @see Xml2SchemaBuilder
 * @see Schema2XmlExtractor
 * 
 */
public abstract class Schema<L extends SchemaLine<? extends SchemaCell>> implements Cloneable, Iterable<L>{

    private String lineSeparator = System.getProperty("line.separator");

    @Deprecated
    public Schema() {
    }

    /**
     * The schema lines
     */
    private LinkedHashMap<String, L> schemaLines = new LinkedHashMap<>();

    protected <S extends Schema<L>, B extends Schema.Builder<L, S, B>> Schema(Builder<L, S, B> builder) {
        this.lineSeparator = builder.lineSeparator;
        for (L schemaLine : builder.schemaLines) {
            this.addSchemaLine(schemaLine);
        }
    }


    @SuppressWarnings("unchecked")
    public abstract static class Builder<L extends SchemaLine<? extends SchemaCell>, S extends Schema<L>, B extends Schema.Builder<L, S, B>> {
        private String lineSeparator = System.getProperty("line.separator");
        private final List<L> schemaLines = new ArrayList<>();
        private Locale defaultLocale = SchemaCellFormat.defaultLocale;

        protected Builder() {
        }

        protected Builder(Schema<L> schema) {
            this.schemaLines.addAll(schema.schemaLines.values());
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


        /**
         * Successive calls to this method adds one line for each call. Does not apply default values that were set at schema builder.
         * @param schemaLine The schema line to add to this builder.
         * @return This builder instance.
         */
        public B withLine(L schemaLine){
            this.schemaLines.add(schemaLine);
            return (B)this;
        }

        /**
         * Removes all lines that have been added so far to this builder.
         * @return This builder instance.
         */
        public B withoutAnyLine(){
            this.schemaLines.clear();
            return (B)this;
        }

        /**
         * Using this method to set a default locale will have no effect unless the defaults are applied to each line
         * builder that is supposed to use this value. The default locale will not be assigned to the schema but only
         * used during building phase.
         * @param locale The default locale to use for lines of this schema.
         * @return This builder instance.
         * @see SchemaLine.Builder#applyDefaultsFrom(Builder)
         */
        public B withDefaultLocale(Locale locale){
            this.defaultLocale = locale;
            return (B)this;
        }

        protected abstract S build();

        public Locale getDefaultLocale() {
            return defaultLocale;
        }
    }
    /**
     * @param schemaLine the schemaLine to add
     */
    public void addSchemaLine(L schemaLine) {
        this.schemaLines.put(schemaLine.getLineType(), schemaLine);
    }

    /**
     * @return True if this schema does not contain any lines. False otherwise.
     */
    public boolean isEmpty() {
        return this.schemaLines.isEmpty();
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



    @SuppressWarnings("unchecked")
    public Schema<L> clone(){
        try {
            Schema<L> clone = (Schema<L>) super.clone();
            clone.schemaLines = new LinkedHashMap<>();
            for(L line: this){
                clone.addSchemaLine((L) line.clone());
            }
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return " lineSeparator='" + StringUtils.replaceJava2Escapes(this.lineSeparator) + "' schemaLines=" + this.schemaLines;
    }

    /**
     * @return list of all schema lines.
     */
    public Collection<L> getSchemaLines() {
        return this.schemaLines.values();
    }

    /**
     * @param lineType The line type to get {@link SchemaLine} for.
     * @return The {@link SchemaLine} with the supplied line type name. Optional.empty if no such schema line was
     *         found.
     */
    public Optional<L> getSchemaLine(String lineType) {
        return Optional.ofNullable(schemaLines.get(lineType));
    }

    /**
     * @return Number of schema lines of this schema.
     */
    public int size() {
        return this.schemaLines.size();
    }

    /**
     * @return A stream of all schema lines of this schema.
     */
    public Stream<L> stream() {
        return this.schemaLines.values().stream();
    }

    /**
     * @return An iterator of all schema lines of this schema.
     */
    public Iterator<L> iterator() {
        return schemaLines.values().iterator();
    }

    /**
     * Loads a schema instance from an xml that is read from the supplied reader. The xml needs to comply to the
     * JSaParSchema.xsd otherwise a {@link SchemaException} is thrown.
     *
     * @param reader
     *            The reader to read the xml from. Caller is responsible for closing the reader.
     * @return A new schema instance created based on the xml.
     * @throws SchemaException
     *             if the supplied xml does not comply to the JSaParSchema.xsd or if there is any other error while
     *             loading a schema.
     * @throws UncheckedIOException In case there was an error reading from the input.
     */
    public static Schema ofXml(Reader reader)
            throws SchemaException, UncheckedIOException {
        Xml2SchemaBuilder schemaBuilder = new Xml2SchemaBuilder();
        return schemaBuilder.build(reader);
    }

    /**
     * Exports this schema instance as xml that complies to the JSaParSchema.xsd.
     *
     * @param writer
     *            The writer where the xml is written. Caller is responsible for closing the writer.
     * @throws SchemaException
     *             if there is an error while writing the xml.
     */
    public void toXml(Writer writer) throws SchemaException {
        Schema2XmlExtractor extractor = new Schema2XmlExtractor();
        extractor.extractXml(writer, this);
    }

}
