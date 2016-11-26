package org.jsapar.model;

import java.text.Format;
import java.util.Date;

/**
 * {@link Cell} implementation carrying a date value of a cell.
 * 
 */
public class DateCell extends Cell implements Comparable<DateCell> {

    /**
     * 
     */
    private static final long serialVersionUID = -4950587241666521775L;
    /**
     * The string representation of the dateValue of this cell.
     */
    private Date dateValue;

    public DateCell(String sName, Date value) {
        super(sName, CellType.DATE);
        this.dateValue = value;
    }

    /**
     * @return the dateValue
     */
    @Override
    public Object getValue() {
        return this.dateValue;
    }

    /**
     * @param value
     *            the decimal value to set
     */
    public void setDateValue(Date value) {
        this.dateValue = value;
    }

    /**
     * @return The string dateValue of this cell.
     */
    public Date getDateValue() {
        return this.dateValue;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jsapar.model.Cell#getStringValue(java.text.Format)
     */
    @Override
    public String getStringValue(Format format) throws IllegalArgumentException {
        if(this.dateValue == null)
            return null;
        if (format != null)
            return format.format(this.dateValue);
        else
            return this.dateValue.toString();
    }

    @Override
    public int compareTo(DateCell right) {
        return this.getDateValue().compareTo(right.getDateValue());
    }


    /* (non-Javadoc)
     * @see java.lang.Object#clone()
     */
    @Override
    public DateCell clone() {
        DateCell clone = (DateCell)super.clone();
        clone.dateValue = (Date)this.dateValue.clone();
        return clone;
    }

    /* (non-Javadoc)
     * @see org.jsapar.model.Cell#compareValueTo(org.jsapar.model.Cell)
     */
    @Override
    public int compareValueTo(Cell right) {
        if(right instanceof DateCell){
            Date dateRight = ((DateCell)right).getDateValue();
            return getDateValue().compareTo(dateRight);
        }
        else{
            throw new IllegalArgumentException("Value of cell of type " + getCellType() + " can not be compared to value of cell of type " + right.getCellType());
        }
    }

    
}
