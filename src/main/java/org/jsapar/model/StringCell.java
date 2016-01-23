package org.jsapar.model;

import java.text.Format;
import java.text.ParseException;
import java.util.Locale;

import org.jsapar.model.Cell;
import org.jsapar.model.CellType;
import org.jsapar.schema.SchemaException;

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
     * Creates an empty cell without any name or value.
     */
    public StringCell() {
        super(CellType.STRING);

    }

    /**
     * Creates a string cell without any name with the supplied value.
     * 
     * @param sValue
     */
    public StringCell(String sValue) {
        super(CellType.STRING);
        this.stringValue = sValue;
    }

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
     * Creates a string cell without any name with the supplied value.
     * @param chValue
     */
    public StringCell(char chValue) {
        super(CellType.STRING);
        StringBuilder sb = new StringBuilder();
        sb.append(chValue);
        this.stringValue = sb.toString();
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
     * Creates a string cell with the supplied name and value. The format parameter is used to parse the supplied value.
     * @param name
     * @param value
     * @param format
     * @throws ParseException
     */
    public StringCell(String name, String value, Format format)
	    throws ParseException {
	super(name, CellType.STRING);
	setValue(value, format);
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

    /*
     * (non-Javadoc)
     * 
     * @see org.jsapar.model.Cell#setValue(java.lang.String, java.text.Format)
     */
    @Override
    public void setValue(String value, Format format) throws ParseException {
	if (format != null)
	    this.stringValue = (String) format.parseObject(value);
	else
	    this.stringValue = value;

    }



    @Override
    public void setValue(String value, Locale locale) throws ParseException {
	this.stringValue = value;
    }

    @Override
    public int compareValueTo(Cell right) throws SchemaException {
        return getStringValue().compareTo(right.getStringValue());
    }
}
