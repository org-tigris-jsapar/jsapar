package org.jsapar.model;

public final class EnumCell<E extends Enum> extends AbstractCell<E> {

    /**
     * Creates a cell with a name and value.
     *
     * @param name     The name of the cell
     * @param value    The value of the cell
     */
    public EnumCell(String name, E value) {
        super(name, value, CellType.ENUM);
    }

    @SuppressWarnings("unchecked")
    @Override
    public int compareValueTo(Cell<E> right) {
        return this.getValue().compareTo(right.getValue());
    }

    /**
     * @param name The name of the empty cell.
     * @return A new Empty cell of supplied name.
     */
    public static Cell emptyOf(String name) {
        return new EmptyCell(name, CellType.ENUM);
    }

    @Override
    public String getStringValue() {
        return getValue().name();
    }
}
