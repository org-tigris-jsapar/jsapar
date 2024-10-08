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
     * @param name  The name of the cell
     * @param value The value
     */
    public FloatCell(String name, Number value) {
        super(name, value, CellType.FLOAT);
    }



    @Override
    public int compareValueTo(Cell<Number> right) {
        if(!(right instanceof FloatCell))
            return Double.compare(getValue().doubleValue(), right.getValue().doubleValue());
        return super.compareValueTo(right);
    }

    @Override
    public Cell<Number> cloneWithName(String newName) {
        return new FloatCell(newName, getValue());
    }

    /**
     * @param name The name of the empty cell.
     * @return A new Empty cell of supplied name.
     */
    public static Cell<Double> emptyOf(String name) {
        return new EmptyCell<>(name, CellType.FLOAT);
    }

}
