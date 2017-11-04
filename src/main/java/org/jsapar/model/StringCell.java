package org.jsapar.model;

/**
 * {@link Cell} implementation carrying a string value of a cell.
 *
 */
public class StringCell extends ComparableCell<String> {

    private static final long serialVersionUID = -2776042954053921679L;


    /**
     * Creates a string cell with the supplied name and value.
     * 
     * @param name The name of the cell
     * @param value The value. Cannot be null. Use {@link EmptyCell} for empty values.
     */
    public StringCell(String name, String value) {
        super(name, value, CellType.STRING);
    }


    /**
     * Creates a string cell with the supplied name and value.
     *
     * @param name The name of the cell
     * @param value The value
     */
    public StringCell(String name, char value) {
        super(name, String.valueOf(value), CellType.STRING);
    }

}