package org.jsapar;

import java.text.Format;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.Locale;


/**
 * Class containging the cell numberValue as a string representation. Each line
 * contains a list of cells.
 * 
 * @author Jonas
 * 
 */
public abstract class NumberCell extends Cell {

    /**
     * 
     */
    private static final long serialVersionUID = -2103478512589522630L;
    /**
     * The string representation of the numberValue of this cell.
     */
    private Number numberValue;

    public NumberCell(CellType cellType) {
	super(cellType);

    }

    public NumberCell(Number value, CellType cellType) {
	super(cellType);
	this.numberValue = value;
    }

    public NumberCell(String sName, Number value, CellType cellType) {
	super(sName, cellType);
	this.numberValue = value;
    }

    public NumberCell(String name, String value, Format format, CellType cellType)
	    throws ParseException {
	super(name, cellType);
	setValue(value, format);
    }

    public NumberCell(String name, String value, Locale locale, CellType cellType) throws ParseException {
    	super(name, cellType);
    	setValue(value, locale);
	}

	/**
     * @return the numberValue
     */
    @Override
    public Object getValue() {
	return this.numberValue;
    }

    /**
     * @param value
     *                the decimal value to set
     */
    public void setNumberValue(Number value) {
	this.numberValue = value;
    }

    /**
     * @return The string numberValue of this cell.
     */
    public Number getNumberValue() {
	return this.numberValue;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jsapar.Cell#getStringValue(java.text.Format)
     */
    @Override
    public String getStringValue(Format format) throws IllegalArgumentException {
	if (format != null)
	    return format.format(this.numberValue);
	else
	    return this.numberValue.toString();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jsapar.Cell#setValue(java.lang.String, java.text.Format)
     */
    @Override
    public void setValue(String value, Format format) throws ParseException {
	ParsePosition pos=new ParsePosition(0);
	this.numberValue = (Number) format.parseObject(value, pos);
	
	if(pos.getIndex() < value.length())
		// It is not acceptable to parse only a part of the string.
		throw new java.text.ParseException("Invalid characters found while parsing number.", pos.getIndex());
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.jsapar.Cell#setValue(java.lang.String, java.text.Format)
     */
    @Override
    public void setValue(String value, Locale locale) throws ParseException {
	    Format format = NumberFormat.getInstance(locale);
	    setValue(value, format);
    }

}
