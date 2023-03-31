package org.jsapar.model;

import java.io.Serializable;

/**
 * Base interface which represents a parsable item on a line in the original document. A cell has a
 * name, a value and a type. The type of the value denotes which subclass to use.
 */
public interface Cell<T> extends Serializable, Comparable<Cell<T>> {

    /**
     * Gets the name of the cell.
     *
     * @return the name
     */
    String getName();

    /**
     * Gets a string representation of the value normally formatted as value.toString()
     *
     * @return The value.
     */
    default String getStringValue() {
        return String.valueOf(getValue());
    }

    /**
     * @return The value of the cell. If the cell is empty as checked with {@link #isEmpty()}, then the return value is
     * unknown and may be null.
     */
    T getValue();

    /**
     * @return the cellType
     */
    CellType getCellType();

    /**
     * Compares value of this cell with the value of the supplied cell.
     *
     * @param right The cell to compare to.
     * @return a negative integer, zero, or a positive integer as this cell's value is less than, equal to, or greater than the specified cell's value.
     * @throws IllegalArgumentException if the value of provided cell cannot be compared to the value of this cell.
     */
    int compareValueTo(Cell<T> right);

    /**
     * @return true if the cell is not set to any value, false otherwise.
     */
    default boolean isEmpty() {
        return false;
    }

    /**
     * This implementation  orders cells by
     * <ol>
     * <li> The name</li>
     * <li> The type (order in {@link CellType} enum)</li>
     * <li> The value as by {@link #compareValueTo(Cell)}</li>
     *</ol>
     * @param right The value to compare against.
     * @return a negative integer, zero, or a positive integer as this object is less than, equal to, or greater than the specified object.
     */
    @Override
    default int compareTo(Cell<T> right) {
        assert right!=null : "Cannot compare a null value.";
        int rc = this.getName().compareTo(right.getName());
        if (rc != 0)
            return rc;
        rc = this.getCellType().compareTo(right.getCellType());
        if (rc != 0)
            return rc;
        return this.compareValueTo(right);
    }
}
