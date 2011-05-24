package org.jsapar;

import java.text.ParseException;
import java.util.Locale;

import org.jsapar.schema.SchemaException;

/**
 * Abstract class which represents a parsable item on a line in the original document. A cell has a
 * name and a value. A cell can be only exist as one of the sub-classes of this class. The type of
 * the value denotes which sub-class to use.
 * 
 * @author Jonas
 * 
 */
public abstract class Cell implements java.io.Serializable, Cloneable {

    /**
     * Denotes the type of the cell.
     * 
     */
    public enum CellType {
        STRING, DATE, INTEGER, BOOLEAN, FLOAT, DECIMAL, CHARACTER, CUSTOM
    }

    /**
     * 
     */
    private static final long serialVersionUID = -3609313087173019221L;

    /**
     * The name of the cell. Can be null if there is no name.
     */
    private String            name;

    private CellType          cellType         = CellType.STRING;

    /**
     * Creates an empty cell.
     */
    public Cell(CellType cellType) {
        this.cellType = cellType;
    }

    /**
     * Creates a cell with a name.
     * 
     * @param sName
     */
    public Cell(String sName, CellType cellType) {
        setName(sName);
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
     * Sets the name of the cell. Changing the name of a cell that is part of a line will compromise
     * the key.
     * 
     * @param name
     *            the name to set
     */
    private void setName(String name) {
        this.name = name;
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
     * Sets the value of the cell by a string, parsed by the default format for the specified
     * locale.
     * 
     * @param value
     *            The string representation of the value.
     * @param locale
     *            The locale to use while parsing string value.
     * @throws ParseException
     */
    public abstract void setValue(String value, Locale locale) throws ParseException;

    /**
     * Sets the value of the cell by a string, parsed by the format supplied.
     * 
     * @param value
     *            The string representation of the value.
     * @param format
     *            The format to be used when parsing. If null, use system default.
     * @throws ParseException
     */
    public abstract void setValue(String value, java.text.Format format) throws ParseException;

    /**
     * Sets the value of the cell by a string, parsed by the format supplied.
     * 
     * @param value
     *            The string representation of the value.
     * @throws ParseException
     */
    public void setValue(String value) throws ParseException {
        setValue(value, Locale.getDefault());
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

    /**
     * Creates a copy of this cell but with the supplied name.
     * 
     * @param sNewName
     * @return A copy of this cell but with the new name.
     * @throws JSaParException
     */
    public Cell makeCopy(String sNewName) {
        Cell clone = this.clone();
        clone.setName(sNewName);
        return clone;
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
     * @param right
     * @return a negative integer, zero, or a positive integer as this cell's value is less than, equal to, or greater than the specified cell's value. 
     * @throws SchemaException
     */
    public abstract int compareValueTo(Cell right) throws SchemaException;
}
