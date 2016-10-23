package org.jsapar.model;

import org.jsapar.schema.SchemaException;
import org.jsapar.utils.StringUtils;

import java.text.*;
import java.util.Locale;

/**
 * Abstract base class for all type of cells that can be represented as a {@link Number}.
 * 
 */
public abstract class NumberCell extends Cell {

    /**
     * 
     */
    private static final long serialVersionUID = -2103478512589522630L;

    /**
     * The {@link Number} value of this cell.
     */
    private Number numberValue;

    public NumberCell(String sName, CellType cellType) {
        super(sName, cellType);
    }

    public NumberCell(String sName, Number value, CellType cellType) {
        super(sName, cellType);
        this.numberValue = value;
    }

    public NumberCell(String name, String value, Format format, CellType cellType) throws ParseException {
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
     *            the decimal value to set
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
     * @see org.jsapar.model.Cell#getStringValue(java.text.Format)
     */
    @Override
    public String getStringValue(Format format) throws IllegalArgumentException {
        if(this.numberValue == null)
            return null;
        if (format != null)
            return format.format(this.numberValue);
        else
            return this.numberValue.toString();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jsapar.model.Cell#setValue(java.lang.String, java.text.Format)
     */
    @Override
    public void setValue(String value, Format format) throws ParseException {
        ParsePosition pos = new ParsePosition(0);
        value = adjustValueForSpaces(value, format);
        this.numberValue = (Number) format.parseObject(value, pos);

        if (pos.getIndex() < value.length())
            // It is not acceptable to parse only a part of the string. That can happen for instance if there is a space
            // in an integer value.
            throw new java.text.ParseException("Invalid characters found while parsing number.", pos.getIndex());
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jsapar.model.Cell#setValue(java.lang.String, java.text.Format)
     */
    @Override
    public void setValue(String value, Locale locale) throws ParseException {
        Format format = NumberFormat.getInstance(locale);
        setValue(value, format);
    }

    private static String adjustValueForSpaces(String sValue, Format format) {
        if (format != null && format instanceof DecimalFormat) {
            // This is necessary because some locales (e.g. swedish)
            // have non breakable space as thousands grouping character. Naturally
            // we want to remove all space characters including the non breakable.
            DecimalFormat decFormat = (DecimalFormat) format;
            char groupingSeparator = decFormat.getDecimalFormatSymbols().getGroupingSeparator();
            if (Character.isSpaceChar(groupingSeparator)) {
                sValue = StringUtils.removeAllSpaces(sValue);
            }
        }
        return sValue;
    }


    /* (non-Javadoc)
     * @see org.jsapar.model.Cell#compareValueTo(org.jsapar.model.Cell)
     */
    @Override
    public int compareValueTo(Cell right) throws SchemaException {
        if(right instanceof BigDecimalCell){
            return -right.compareValueTo(this);
        }
        if(right instanceof NumberCell){
            Number nRight = ((NumberCell)right).getNumberValue();
            double dLeft = getNumberValue().doubleValue();
            double dRight = nRight.doubleValue();
            if(dLeft < dRight)
                return -1;
            else if(dLeft > dRight)
                return 1;
            else
                return 0;
        }
        else{
            throw new SchemaException("Value of cell of type " + getCellType() + " can not be compared to value of cell of type " + right.getCellType());
        }
    }

}
