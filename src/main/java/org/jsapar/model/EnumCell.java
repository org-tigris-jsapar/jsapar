package org.jsapar.model;

public final class EnumCell<E extends Enum<E>> extends AbstractCell<E> implements ComparableCell<E>{

    /**
     * Creates a cell with a name and value.
     *
     * @param name     The name of the cell
     * @param value    The value of the cell
     */
    public EnumCell(String name, E value) {
        super(name, value, CellType.ENUM);
    }

    /**
     * @param name The name of the empty cell.
     * @return A new Empty cell of supplied name.
     */
    @SuppressWarnings("rawtypes")
    public static Cell<Enum> emptyOf(String name) {
        return new EmptyCell<>(name, CellType.ENUM);
    }

    @Override
    public String getStringValue() {
        return getValue().name();
    }

    @Override
    public Cell<E> cloneWithName(String newName) {
        return new EnumCell<>(newName, getValue());
    }
}
