package org.jsapar.model;

import org.jsapar.schema.SchemaException;

import java.io.Serializable;

/**
 * Abstract class which represents a parsable item on a line in the original document. A cell has a
 * name and a value. A cell can be only exist as one of the sub-classes of this class. The type of
 * the value denotes which sub-class to use.
 * 
 * @author Jonas
 * 
 */
public abstract class Cell implements Serializable, Cloneable {

    /**
     * Denotes the type of the cell.
     * 
     */

    /**
     * 
     */
    private static final long serialVersionUID = -3609313087173019221L;

    /**
     * The name of the cell. Can be null if there is no name.
     */
    private final String name;

    private final CellType          cellType;

    /**
     * Creates a cell with a name.
     * 
     * @param name        The name of the cell
     * @param cellType    The type of the cell.
     */
    public Cell(String name, CellType cellType) {
        this.name = name;
        this.cellType = cellType;
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
     * @throws IllegalArgumentException
     */
    public abstract String getStringValue(java.text.Format format) throws IllegalArgumentException;

    /**
     * Gets a string representation of the value formatted as String.valueOf(...)
     * 
     * @return The value.
     */
    public String getStringValue() {
        return String.valueOf(getValue());
    }


    /**
     * @return The value of the cell as an object.
     */
    public abstract Object getValue();

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
     * Gets the value formatted according to its xml base type.
     * 
     * @return a string containing the value in xml format.
     */
    public String getXmlValue() {
        return getStringValue();
    }


    
    /* (non-Javadoc)
     * @see java.lang.Object#clone()
     */
    @Override
    public Cell clone() {
        try {
            return (Cell) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e);
        }
    }

    /**
     * Compares value of this cell with the value of the supplied cell. 
     * 
     * @param right The cell to compare to.
     * @return a negative integer, zero, or a positive integer as this cell's value is less than, equal to, or greater than the specified cell's value. 
     * @throws SchemaException
     */
    public abstract int compareValueTo(Cell right) throws SchemaException;
    
    /**
     * @return true if the cell is not set to any value, false otherwise.
     */
    public boolean isEmpty(){
        return false;
    }

}
