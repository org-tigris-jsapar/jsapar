package org.jsapar.schema;

import org.jsapar.model.Line;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * Abstract base class that describes the schema for a line. For instance if you want to ignore a header line you
 * can add a SchemaLine instance to your schema with occurs==1 and ignoreRead==true;
 *
 * @see Schema
 * @see SchemaCell
 */
public abstract class SchemaLine<C extends SchemaCell> implements Cloneable, Iterable<C> {
    /**
     * Constant to be used in occurs attribute and that indicates that lines can occur infinite number of times.
     */
    private static final int OCCURS_INFINITE = Integer.MAX_VALUE;
    private static final String NOT_SET = "";

    /**
     * The schema cells.
     */
    private Map<String, C> schemaCells = new LinkedHashMap<>();

    /**
     * The number of times this type of line occurs in the corresponding input or output.
     *
     * @see #isOccursInfinitely()
     * @see #setOccursInfinitely()
     */
    private int occurs = OCCURS_INFINITE;

    /**
     * The type of the line. This line type will be part of each of the parsed  {@link Line} instances that was created
     * by using this instance.
     * <p>
     * When composing, the line type of the {@link Line} supplied to the composer will be used by the composer to determine
     * which {@link SchemaLine} instance to use for composing.
     */
    private String lineType = NOT_SET;

    /**
     * If set to true, this type of line will be read from the input but then ignored, thus it will not produce any line
     * parsed event.
     */
    private boolean ignoreRead = false;

    /**
     * If set to true, this type of line will not be written to the output.
     */
    private boolean ignoreWrite = false;

    /**
     * Creates a SchemaLine that occurs infinite number of times.
     * <p>
     * Deprecated since 2.2. Use builder instead.
     */
    @Deprecated
    public SchemaLine() {
    }

    /**
     * Creates a SchemaLine that occurs supplied number of times.
     *
     * <p>
     * Deprecated since 2.2. Use builder instead.
     * @param nOccurs The number of times that a line of this type occurs in the input or output text.
     */
    @Deprecated
    public SchemaLine(int nOccurs) {
        this.occurs = nOccurs;
    }

    /**
     * Creates a SchemaLine with the supplied line type and that occurs infinite number of times.
     *
     * <p>
     * Deprecated since 2.2. Use builder instead.
     * @param lineType The name of the type of the line.
     */
    @Deprecated
    public SchemaLine(String lineType) {
        this.setLineType(lineType);
    }

    /**
     * Creates a SchemaLine with the supplied line type and occurs supplied number of times.
     *
     * <p>
     * Deprecated since 2.2. Use builder instead.
     * @param lineType The line type of this schema line.
     * @param nOccurs  The number of times it should occur.
     */
    @Deprecated
    public SchemaLine(String lineType, int nOccurs) {
        this.setLineType(lineType);
        this.setOccurs(nOccurs);
    }

    <L extends SchemaLine<C>, B extends Builder<C, L, B>> SchemaLine(Builder<C, L, B> builder) {
        builder.schemaCells.forEach(c-> this.schemaCells.put(c.getName(), c));
        this.occurs = builder.occurs;
        this.lineType = builder.lineType;
        this.ignoreRead = builder.ignoreRead;
        this.ignoreWrite = builder.ignoreWrite;
    }

    @SuppressWarnings("unchecked")
    public abstract static class Builder<C extends SchemaCell, L extends SchemaLine<C>, B extends Builder<C, L, B>> {
        private final String lineType;
        private List<C> schemaCells = new ArrayList<>();
        private int occurs = OCCURS_INFINITE;
        private boolean ignoreRead;
        private boolean ignoreWrite;

        public Builder(String lineType) {
            this.lineType = lineType;
        }

        public B withOccurs(int nOccurs) {
            this.occurs = nOccurs;
            return (B) this;
        }

        /**
         * Adds a supplied schema cell to this builder. Can be called multiple times to add more cells.
         * @param schemaCell The schema cell to add.
         * @return This builder instance.
         */
        public B withCell(C schemaCell) {
            this.schemaCells.add(schemaCell);
            return (B) this;
        }

        /**
         * Adds a supplied schema cells to this builder in the order provided. Can be called multiple times to add more cells.
         * @param schemaCells The schema cells to add.
         * @return This builder instance.
         */
        public B withCells(C ... schemaCells) {
            for(C cell : schemaCells){
                withCell(cell);
            }
            return (B) this;
        }

        public B withIgnoreRead(boolean ignoreRead) {
            this.ignoreRead = ignoreRead;
            return (B) this;
        }

        public B withIgnoreWrite(boolean ignoreWrite) {
            this.ignoreWrite = ignoreWrite;
            return (B) this;
        }

        public void withOccursInfinitely() {
            this.occurs = OCCURS_INFINITE;
        }

        public abstract L build();

    }

    /**
     * @return The number of times this type of line occurs in the corresponding input or output.
     */
    public int getOccurs() {
        return occurs;
    }

    /**
     * @see #isOccursInfinitely()
     * @see #setOccursInfinitely()
     * @param occurs The number of times this type of line occurs in the corresponding input or output.
     */
    public void setOccurs(int occurs) {
        this.occurs = occurs;
    }

    /**
     * Sets the occurs attribute so that this type of line occurs until the end of the buffer.
     */
    public void setOccursInfinitely() {
        this.occurs = OCCURS_INFINITE;
    }

    /**
     * @return true if this line occurs to the end of the buffer, false otherwise.
     */
    public boolean isOccursInfinitely() {
        return this.occurs == OCCURS_INFINITE;
    }

    /**
     * Finds a schema cell with the specified name.
     *
     * @param cellName The name of the schema cell to find.
     * @return The schema cell with the supplied name or null if no such cell was found.
     */
    public C getSchemaCell(String cellName){
        return schemaCells.get(cellName);
    }


    /**
     * @return The type of the line. This line type will be part of each of the parsed  {@link Line} instances that was created
     * by using this instance.
     * <p>
     * When composing, the line type of the {@link Line} supplied to the composer will be used by the composer to determine
     * which {@link SchemaLine} instance to use for composing.
     */
    public String getLineType() {
        return lineType;
    }

    /**
     * @param lineType The type of the line. This line type will be part of each of the parsed  {@link Line} instances that was created
     *       by using this instance.
     *       <p>
     *       When composing, the line type of the {@link Line} supplied to the composer will be used by the composer to determine
     *       which {@link SchemaLine} instance to use for composing.
     */
    public void setLineType(String lineType) {
        this.lineType = lineType;
    }

    /**
     * @return If true, this type of line will be read from the input but then ignored, thus it will not produce any line
     *       parsed event.
     */
    public boolean isIgnoreRead() {
        return ignoreRead;
    }

    /**
     * @param ignoreRead If set to true, this type of line will be read from the input but then ignored, thus it will not produce any line
     *       parsed event.
     */
    public void setIgnoreRead(boolean ignoreRead) {
        this.ignoreRead = ignoreRead;
    }

    /**
     * @return If true, this type of line will not be written to the output.
     */
    public boolean isIgnoreWrite() {
        return ignoreWrite;
    }

    /**
     * @param ignoreWrite If set to true, this type of line will not be written to the output.
     */
    public void setIgnoreWrite(boolean ignoreWrite) {
        this.ignoreWrite = ignoreWrite;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("SchemaLine lineType=");
        sb.append(this.lineType);
        sb.append(" occurs=");
        if (isOccursInfinitely())
            sb.append("INFINITE");
        else
            sb.append(this.occurs);
        sb.append(" schemaCells=");
        sb.append(this.schemaCells);
        return sb.toString();
    }


    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof SchemaLine))
            return false;
        SchemaLine that = (SchemaLine) o;
        return Objects.equals(lineType, that.lineType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getLineType());
    }

    @Override
    public SchemaLine<C> clone() {
        try {
            SchemaLine<C> clone = (SchemaLine<C>) super.clone();
            clone.schemaCells = new LinkedHashMap<>();

            for (C cell : this.schemaCells.values()) {
                clone.addSchemaCell((C) cell.clone());
            }

            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError("Can never happen.", e);
        }
    }

    /**
     * @return A collection of all schema cells in the correct order.
     */
    public Collection<C> getSchemaCells(){
        return this.schemaCells.values();
    }


    /**
     * Adds a schema cell to this row.
     *
     * @param cell The cell to add
     */
    public void addSchemaCell(C cell) {
        this.schemaCells.put(cell.getName(), cell);
    }

    /**
     * @return Number of cells in a line
     */
    public int size() {
        return this.schemaCells.size();
    }

    public Stream<C> stream() {
        return schemaCells.values().stream();
    }

    public Iterator<C> iterator() {
        return schemaCells.values().iterator();
    }

    public void forEach(Consumer<? super C> consumer) {
        schemaCells.values().forEach(consumer);
    }

    /**
     * Removes all schema cells from this line.
     */
    public void clear(){
        schemaCells.clear();
    }
}
