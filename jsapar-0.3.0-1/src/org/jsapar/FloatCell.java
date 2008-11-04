/** 
 * Copyright: Jonas Stenberg
 */
package org.jsapar;

import java.text.Format;
import java.text.ParseException;
import java.util.Locale;

/**
 * @author Jonas Stenberg
 * 
 */
public class FloatCell extends NumberCell implements Comparable<FloatCell> {

	/**
     * 
     */
	private static final long serialVersionUID = 2102712515168714171L;

	/**
     */
	public FloatCell() {
		super(CellType.FLOAT);
	}

	/**
	 * @param value
	 */
	public FloatCell(Float value) {
		super(value, CellType.FLOAT);
	}

	/**
	 * @param value
	 */
	public FloatCell(Double value) {
		super(value, CellType.FLOAT);
	}

	/**
	 * @param name
	 * @param value
	 */
	public FloatCell(String name, Float value) {
		super(name, value, CellType.FLOAT);
	}

	/**
	 * @param name
	 * @param value
	 */
	public FloatCell(String name, Double value) {
		super(name, value, CellType.FLOAT);
	}

	/**
	 * @param name
	 * @param value
	 * @param format
	 * @param locale 
	 * @throws ParseException
	 */
	public FloatCell(String name, String value, Format format)
			throws ParseException {
		super(name, value, format, CellType.FLOAT);
	}

	public FloatCell(String name, String value, Locale locale) throws ParseException {
		super(name, value, locale, CellType.FLOAT);
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(FloatCell right) {
		return Double.compare(this.getNumberValue().doubleValue(), right
				.getNumberValue().doubleValue());
	}
}
