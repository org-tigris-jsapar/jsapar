package org.jsapar;

import java.text.Format;
import java.text.ParseException;
import java.util.Locale;

/**
 * Class containging the cell booleanValue as a string representation. Each line
 * contains a list of cells.
 * 
 * @author Jonas
 * 
 */
public class BooleanCell extends Cell implements Comparable<BooleanCell> {

	/**
     * 
     */
	private static final long serialVersionUID = -6337207320287960296L;
	/**
	 * The string representation of the booleanValue of this cell.
	 */
	private Boolean booleanValue;

	public BooleanCell() {
		super(CellType.BOOLEAN);
	}

	public BooleanCell(Boolean value) {
		super(CellType.BOOLEAN);
		this.booleanValue = value;
	}

	public BooleanCell(String sName, Boolean value) {
		super(sName, CellType.BOOLEAN);
		this.booleanValue = value;
	}

	public BooleanCell(String name, String value, Format format)
			throws ParseException {
		super(name, CellType.BOOLEAN);
		setValue(value, format);
	}

	public BooleanCell(String name, String value, Locale locale) throws ParseException {
		super(name, CellType.BOOLEAN);
		setValue(value, locale);
	}

	/**
	 * @return the booleanValue
	 */
	@Override
	public Object getValue() {
		return this.booleanValue;
	}

	/**
	 * @param value
	 *            the decimal value to set
	 */
	public void setBooleanValue(Boolean value) {
		this.booleanValue = value;
	}

	/**
	 * @return The string booleanValue of this cell.
	 */
	public Boolean getBooleanValue() {
		return this.booleanValue;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jsapar.Cell#getStringValue(java.text.Format)
	 */
	@Override
	public String getStringValue(Format format) throws IllegalArgumentException {
		if (format != null)
			return format.format(this.booleanValue);
		else
			return this.booleanValue.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jsapar.Cell#setValue(java.lang.String, java.text.Format)
	 * 
	 * Inspired by JDom.
	 */
	@Override
	public void setValue(String value, Format format) throws ParseException {
		this.booleanValue = (Boolean) format.parseObject(value);
	}

	@Override
	public int compareTo(BooleanCell right) {
		return this.getBooleanValue().compareTo(right.getBooleanValue());
	}

	@Override
	public void setValue(String value, Locale locale) throws ParseException {
		String valueTrim = value.trim();
		if ((valueTrim.equalsIgnoreCase("true"))
				|| (valueTrim.equalsIgnoreCase("on"))
				|| (valueTrim.equalsIgnoreCase("1"))
				|| (valueTrim.equalsIgnoreCase("yes"))) {
			this.booleanValue = new Boolean(true);
		} else if ((valueTrim.equalsIgnoreCase("false"))
				|| (valueTrim.equalsIgnoreCase("off"))
				|| (valueTrim.equalsIgnoreCase("0"))
				|| (valueTrim.equalsIgnoreCase("no"))) {
			this.booleanValue = new Boolean(false);
		} else {
			throw new ParseException("Failed to parse boolean value from: "
					+ value, 0);
		}
	}
}
