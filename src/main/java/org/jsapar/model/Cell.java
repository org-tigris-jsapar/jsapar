package org.jsapar.model;

import java.io.Serializable;
import java.text.Format;

/**
 * Base class which represents a parsable item on a line in the original document. A cell has a
 * name and a value. A cell can be only exist as one of the sub-classes of this class. The type of
 * the value denotes which sub-class to use.
 * 
 */
public abstract class Cell<T> implements Serializable {

    private final T value;

    private static final long serialVersionUID = -3609313087173019221L;

    /**
     * The name of the cell. Can be null if there is no name.
     */
    private final String name;

    /**
     * Denotes the type of the cell.
     *
     */
    private final CellType          cellType;

    /**
     * Creates a cell with a name.
     * 
     * @param name        The name of the cell
     * @param cellType    The type of the cell.
     */
    public Cell(String name, T value, CellType cellType) {
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
    public String getName() {
        return this.name;
    }

    /**
     * Gets a string representation of the value formatted by the supplied format.
     * 
     * @param format
     *            A formatter for the specified type or null if default formatting is sufficient.
     * @return The formatted value.
     * @throws IllegalArgumentException - if the Format cannot format the given object
     */
    public String getStringValue(Format format) throws IllegalArgumentException {
        if (format != null)
            return format.format(this.value);
        else
            return this.value.toString();
    }

    /**
     * Gets a string representation of the value formatted as String.valueOf(...)
     * 
     * @return The value.
     */
    public String getStringValue() {
        return String.valueOf(getValue());
    }


    /**
     * @return The value of the cell.
     */
    public T getValue() {
        return value;
    }

    /**
     * @return A string representation of the cell, including the name of the cell, suitable for
     *         debugging. Use the method getValue() to get the real value of the cell.
     */
    @Override
    public String toString() {
        if (this.name != null)
            return this.name + "=" + getStringValue();
        else
            return getStringValue();
    }

    /**
     * @return the cellType
     */
    public CellType getCellType() {
        return cellType;
    }


    /**
     * Compares value of this cell with the value of the supplied cell. 
     * 
     * @param right The cell to compare to.
     * @return a negative integer, zero, or a positive integer as this cell's value is less than, equal to, or greater than the specified cell's value. 
     * @throws IllegalArgumentException if the value of provided cell cannot be compared to the value of this cell.
     */
    public abstract int compareValueTo(Cell<T> right);
    
    /**
     * @return true if the cell is not set to any value, false otherwise.
     */
    public boolean isEmpty(){
        return false;
    }

}
