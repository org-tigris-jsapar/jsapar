package org.jsapar;

import java.math.BigDecimal;
import java.text.Format;
import java.text.ParseException;
import java.util.Locale;

/**
 * Class containging the cell decimalValue as a string representation. Each line
 * contains a list of cells.
 * 
 * @author Jonas
 * 
 */
public class BigDecimalCell extends NumberCell implements
		java.lang.Comparable<BigDecimalCell> {

	/**
     * 
     */
	private static final long serialVersionUID = -6337207320287960296L;

	/**
	 * The string representation of the decimalValue of this cell.
	 */

	public BigDecimalCell() {
		super(CellType.DECIMAL);
	}

	public BigDecimalCell(BigDecimal value) {
		super(value, CellType.DECIMAL);
	}

	public BigDecimalCell(String sName, BigDecimal value) {
		super(sName, value, CellType.DECIMAL);
	}

	public BigDecimalCell(String name, String value, Format format)
			throws ParseException {
		super(name, value, format, CellType.DECIMAL);
	}

	public BigDecimalCell(String name, String value, Locale locale) throws ParseException {
		super(name, value, locale, CellType.DECIMAL);
	}

	/**
	 * @param value
	 *            the BigDecimal value to set
	 */
	public void setBigDecimalValue(BigDecimal value) {
		super.setNumberValue(value);
	}

	/**
	 * @return The string decimalValue of this cell.
	 */
	public BigDecimal getBigDecimalValue() {
		return (BigDecimal) super.getNumberValue();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jsapar.Cell#setValue(java.lang.String, java.text.Format)
	 */
	@Override
	public void setValue(String value, Format format) throws ParseException {
		if (format == null)
			this.setNumberValue(new BigDecimal(value));
		else
			super.setValue(value, format);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(BigDecimalCell right) {
		return getBigDecimalValue().compareTo(right.getBigDecimalValue());
	}
}
