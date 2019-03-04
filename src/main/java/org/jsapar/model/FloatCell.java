package org.jsapar.model;

/**
 * Float cell contains a double precision float number. Single precision float
 * values are converted into double precision values.
 * 
 */
public final class FloatCell extends NumberCell {

    private static final long serialVersionUID = 2102712515168714171L;

    /**
     * Creates a float number cell with supplied name. Converts the float value
     * into a double precision float value.
     * 
     * @param name The name of the cell
     * @param value The value
     */
    public FloatCell(String name, Float value) {
	super(name, value, CellType.FLOAT);
    }

    /**
     * Creates a float number cell with supplied name.
     * 
     * @param name The name of the cell
     * @param value The value
     */
    public FloatCell(String name, Double value) {
	super(name, value, CellType.FLOAT);
    }


    @Override
    public int compareValueTo(Cell<Number> right) {
        if(right instanceof FloatCell)
            return Double.compare(getValue().doubleValue(), right.getValue().doubleValue());
        return super.compareValueTo(right);
    }
}
