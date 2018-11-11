package org.jsapar.model;

/**
 * Abstract base class for all type of cells that can be represented as a {@link Number}.
 * 
 */
public abstract class NumberCell extends AbstractCell<Number> {

    /**
     * 
     */
    private static final long serialVersionUID = -2103478512589522630L;


    /**
     * @param name The name of the cell
     * @param value The value
     * @param cellType The type of the cell, from the sub-class.
     */
    NumberCell(String name, Number value, CellType cellType) {
        super(name, value, cellType);
    }


    /* (non-Javadoc)
     * @see org.jsapar.model.Cell#compareValueTo(org.jsapar.model.Cell)
     */
    @Override
    public int compareValueTo(Cell<Number> right)  {
        if(right instanceof BigDecimalCell){
            return -right.compareValueTo(this);
        }
        assert right instanceof NumberCell : "Value of cell of type " + getCellType() + " can not be compared to value of cell of type " + right.getCellType();
        return Double.compare(getValue().doubleValue(), right.getValue().doubleValue());
    }

}
