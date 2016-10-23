package org.jsapar.model;

import org.jsapar.schema.SchemaException;

import java.text.Format;

/**
 * Class containging the cell stringValue as a string representation. Each line
 * contains a list of cells.
 * 
 * @author Jonas
 * 
 */
public class StringCell extends Cell {

    /**
     * 
     */
    private static final long serialVersionUID = -2776042954053921679L;

    /**
     * The string representation of the stringValue of this cell.
     */
    private String stringValue;


    /**
     * Creates a string cell with the supplied name and value.
     * 
     * @param sName
     * @param sValue
     */
    public StringCell(String sName, String sValue) {
	super(sName, CellType.STRING);
	this.stringValue = sValue;
    }


    /**
     * Creates a string cell with the supplied name and value.
     * 
     * @param sName
     * @param chValue
     */
    public StringCell(String sName, char chValue) {
        super(sName, CellType.STRING);
        StringBuilder sb = new StringBuilder();
        sb.append(chValue);
        this.stringValue = sb.toString();
    }


    /**
     * @return the stringValue as an Object.
     */
    @Override
    public Object getValue() {
	return this.stringValue;
    }

    /**
     * @param value
     *            the stringValue to set
     */
    public void setStringValue(String value) {
	this.stringValue = value;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jsapar.model.Cell#getStringValue()
     */
    @Override
    public String getStringValue() {
	return this.stringValue;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jsapar.model.Cell#getStringValue(java.text.Format)
     */
    @Override
    public String getStringValue(Format format) throws IllegalArgumentException {
        if (this.stringValue == null)
            return null;
        if (format != null) {
            return format.format(this.stringValue);
        } else
            return this.stringValue;
    }


    @Override
    public int compareValueTo(Cell right) throws SchemaException {
        return getStringValue().compareTo(right.getStringValue());
    }
}
