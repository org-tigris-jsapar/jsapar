package org.jsapar.model;

import org.jsapar.schema.SchemaException;

import java.text.Format;

/**
 * {@link Cell} implementation carrying a boolean value of a cell.
 * 
 */
public class BooleanCell extends Cell  {

	/**
     * 
     */
	private static final long serialVersionUID = -6337207320287960296L;
	/**
	 * The string representation of the booleanValue of this cell.
	 */
	private Boolean booleanValue;


	public BooleanCell(String sName, Boolean value) {
		super(sName, CellType.BOOLEAN);
		this.booleanValue = value;
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
	 * @see org.jsapar.model.Cell#getStringValue(java.text.Format)
	 */
	@Override
	public String getStringValue(Format format) throws IllegalArgumentException {
	    if(this.booleanValue == null)
	        return null;
		if (format != null)
			return format.format(this.booleanValue);
		else
			return this.booleanValue.toString();
	}


    /* (non-Javadoc)
     * @see org.jsapar.model.Cell#compareValueTo(org.jsapar.model.Cell)
     */
    @Override
    public int compareValueTo(Cell right) throws SchemaException {
        if(right instanceof BooleanCell){
            Boolean bRight = ((BooleanCell)right).getBooleanValue();
            return getBooleanValue().compareTo(bRight);
        }
        else{
            throw new SchemaException("Value of cell of type " + getCellType() + " can not be compared to value of cell of type " + right.getCellType());
        }
    }
}
