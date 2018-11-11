package org.jsapar.model;

import java.util.Objects;

/**
 * Base class which represents a parsable item on a line in the original document. A cell has a
 * name and a value. A cell can be only exist as one of the sub-classes of this class. The type of
 * the value denotes which sub-class to use.
 * 
 */
public abstract class AbstractCell<T> implements Cell<T> {

    private final T value;

    /**
     * The name of the cell. Can be null if there is no name.
     */
    private final String name;

    /**
     * Denotes the type of the cell.
     */
    private final CellType cellType;

    /**
     * Since all members are final, we can cache the hash code when needed.
     */
    private volatile int hashCode = Integer.MIN_VALUE;

    /**
     * Creates a cell with a name.
     *
     * @param name        The name of the cell
     * @param value       The value to set for this cell.
     * @param cellType    The type of the cell.
     */
    AbstractCell(String name, T value, CellType cellType) {
        assert name != null : "Cell name cannot be null.";
        assert cellType != null : "Cell type cannot be null.";
        assert value != null : "Cell value cannot be null, use EmptyCell for empty values.";
        this.name = name;
        this.cellType = cellType;
        this.value = value;
    }

    /**
     * Gets the name of the cell.
     * 
     * @return the name
     */
    @Override
    public String getName() {
        return this.name;
    }

    /**
     * @return The value of the cell.
     */
    @Override
    public T getValue() {
        return value;
    }

    /**
     * @return A string representation of the cell, including the name of the cell, suitable for
     * debugging. Use the method {@link #getValue()} to get the real value of the cell or use {@link #getStringValue()}
     * to get a string representation of the real value.
     */
    @Override
    public String toString() {
        return this.name + "=" + getStringValue();
    }

    /**
     * @return the cellType
     */
    @Override
    public CellType getCellType() {
        return cellType;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AbstractCell<?> cell = (AbstractCell<?>) o;
        return Objects.equals(value, cell.value) &&
                Objects.equals(name, cell.name) &&
                cellType == cell.cellType;
    }

    @Override
    public int hashCode() {
        // Since all members are final, we can cache the hash code.
        if(this.hashCode == Integer.MIN_VALUE){
            this.hashCode = Objects.hash(value, name, cellType);
        }
        return this.hashCode;
    }

}
