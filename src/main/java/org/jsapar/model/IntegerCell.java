/** 
 * Copyright: Jonas Stenberg
 */
package org.jsapar.model;

import java.text.Format;
import java.text.ParseException;
import java.util.Locale;


/**
 * @author Jonas Stenberg
 *
 */
public class IntegerCell extends NumberCell implements Comparable<IntegerCell>{

    /**
     * 
     */
    private static final long serialVersionUID = -6131249480571994885L;

    /**
     * @param name
     * @param value
     */
    public IntegerCell(String name, Integer value) {
	super(name, value, CellType.INTEGER);
    }

    /**
     * @param name
     * @param value
     */
    public IntegerCell(String name, Long value) {
	super(name, value, CellType.INTEGER);
    }

    /**
     * @param name
     * @param value
     */
    public IntegerCell(String name, Short value) {
        super(name, value, CellType.INTEGER);
    }

    /**
     * @param name
     * @param value
     */
    public IntegerCell(String name, Byte value) {
        super(name, value, CellType.INTEGER);
    }

    /**
     * @param name
     * @param value
     * @param format
     * @throws ParseException
     */
    public IntegerCell(String name, String value, Format format)
	    throws ParseException {
	super(name, value, format, CellType.INTEGER);
    }

    public IntegerCell(String name, String value, Locale locale) throws ParseException {
    	super(name, value, locale, CellType.INTEGER);
	}


    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(IntegerCell right){
    	Long leftValue = this.getNumberValue().longValue();
    	Long rightValue = right.getNumberValue().longValue();
    	return leftValue.compareTo(rightValue);
    }
}
