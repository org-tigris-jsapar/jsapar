package org.jsapar.model;

import java.io.Serializable;

/**
 * Base interface which represents a parsable item on a line in the original document. A cell has a
 * name, a value and a type. The type of the value denotes which sub-class to use.
 * 
 */
public interface Cell<T> extends Serializable{


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
    default String getStringValue(){
        return String.valueOf(getValue());
    }


    /**
     * @return The value of the cell.
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
    default boolean isEmpty(){
        return false;
    }


}
