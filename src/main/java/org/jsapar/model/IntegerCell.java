/** 
 * Copyright: Jonas Stenberg
 */
package org.jsapar.model;

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
