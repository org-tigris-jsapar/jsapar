package org.jsapar.model;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.Format;
import java.text.ParseException;
import java.util.Locale;

import org.jsapar.schema.SchemaException;

/**
 * Class containging the cell decimalValue as a string representation. Each line contains a list of
 * cells.
 * 
 * @author Jonas
 * 
 */
public class BigDecimalCell extends NumberCell  {

    /**
     * 
     */
    private static final long serialVersionUID = -6337207320287960296L;

    /**
     * The string representation of the decimalValue of this cell.
     */


    public BigDecimalCell(String sName, BigDecimal value) {
        super(sName, value, CellType.DECIMAL);
    }

    public BigDecimalCell(String sName, BigInteger value) {
        super(sName, value, CellType.DECIMAL);
    }

    public BigDecimalCell(String name, String value, Format format) throws ParseException {
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
     * @return The value of this cell as a BigDecimal.
     */
    public BigDecimal getBigDecimalValue() {
        return (BigDecimal) super.getNumberValue();
    }
    
    /**
     * @return The value of this cell as a BigInteger
     */
    public BigInteger getBigIntegerValue(){
        return getBigDecimalValue().toBigInteger();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jsapar.model.Cell#setValue(java.lang.String, java.text.Format)
     */
    @Override
    public void setValue(String value, Format format) throws ParseException {
        if (format == null)
            this.setNumberValue(new BigDecimal(value));
        else
            super.setValue(value, format);

    }

    /* (non-Javadoc)
     * @see org.jsapar.model.NumberCell#compareValueTo(org.jsapar.model.Cell)
     */
    @Override
    public int compareValueTo(Cell right) throws SchemaException {
        if(right instanceof BigDecimalCell){
            BigDecimal bdRight = (((BigDecimalCell)right).getBigDecimalValue());
            return getBigDecimalValue().compareTo(bdRight);
        }
        else if(right instanceof NumberCell){
            BigDecimal bdRight = new BigDecimal(((NumberCell)right).getNumberValue().doubleValue());
            return getBigDecimalValue().compareTo(bdRight);
        }
        else{
            throw new SchemaException("Value of cell of type " + getCellType() + " can not be compared to value of cell of type " + right.getCellType());
        }
    }
}
