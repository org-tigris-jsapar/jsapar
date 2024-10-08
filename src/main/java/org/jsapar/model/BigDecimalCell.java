package org.jsapar.model;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * {@link Cell} implementation carrying a decimal value of a cell. Decimal value can be assigned or retrieved either as
 * {@link BigDecimal} or as {@link BigInteger}
 * 
 *
 */
public final class BigDecimalCell extends NumberCell  {

    /**
     * 
     */
    private static final long serialVersionUID = -6337207320287960296L;

    /**
     * Creates a new decimal cell.
     * @param sName    The name of the cell.
     * @param value The value of the cell.
     */
    public BigDecimalCell(String sName, BigDecimal value) {
        super(sName, value, CellType.DECIMAL);
    }

    /**
     * Creates a new decimal cell.
     * @param sName    The name of the cell.
     * @param value The value of the cell.
     */
    public BigDecimalCell(String sName, BigInteger value) {
        super(sName, new BigDecimal(value), CellType.DECIMAL);
    }


     /**
     * @return The value of this cell as a BigDecimal.
     */
    public BigDecimal getBigDecimalValue() {
        return (BigDecimal) super.getValue();
    }

    /**
     * @return The value of this cell as a BigInteger
     */
    public BigInteger getBigIntegerValue(){
        return getBigDecimalValue().toBigInteger();
    }


    /* (non-Javadoc)
     * @see org.jsapar.model.NumberCell#compareValueTo(org.jsapar.model.Cell)
     */
    @Override
    public int compareValueTo(Cell<Number> right) {
        if(right instanceof BigDecimalCell){
            BigDecimal bdRight = (((BigDecimalCell)right).getBigDecimalValue());
            return getBigDecimalValue().compareTo(bdRight);
        }
        assert right instanceof NumberCell : "Value of cell of type " + getCellType() + " can not be compared to value of cell of type " + right.getCellType();
        BigDecimal bdRight = BigDecimal.valueOf(right.getValue().doubleValue());
        return getBigDecimalValue().compareTo(bdRight);
    }

    @Override
    public Cell<Number> cloneWithName(String newName) {
        return new BigDecimalCell(newName, getBigDecimalValue());
    }

    /**
     * @param name The name of the empty cell.
     * @return A new Empty cell of supplied name.
     */
    public static EmptyCell<BigDecimal> emptyOf(String name) {
        return new EmptyCell<>(name, CellType.DECIMAL);
    }

}
