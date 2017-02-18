package org.jsapar.model;

import java.text.Format;

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
    private final Number numberValue;

    /**
     * @param name The name of the cell
     * @param value The value
     * @param cellType The type of the cell, from the sub-class.
     */
    public NumberCell(String name, Number value, CellType cellType) {
        super(name, cellType);
        assert value != null : "Cell value cannot be null, use EmptyCell for empty values.";
        this.numberValue = value;
    }


    /**
     * @return the numberValue
     */
    @Override
    public Object getValue() {
        return this.numberValue;
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
        if (format != null)
            return format.format(this.numberValue);
        else
            return this.numberValue.toString();
    }


    /* (non-Javadoc)
     * @see org.jsapar.model.Cell#compareValueTo(org.jsapar.model.Cell)
     */
    @Override
    public int compareValueTo(Cell right)  {
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
            throw new IllegalArgumentException("Value of cell of type " + getCellType() + " can not be compared to value of cell of type " + right.getCellType());
        }
    }

}
