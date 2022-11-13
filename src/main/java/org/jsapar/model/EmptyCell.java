package org.jsapar.model;

/**
 * This class is a special case where an empty value has been parsed from a source. It can be used as a type independent
 * placeholder of an empty value.
 */
public final class EmptyCell<T> extends AbstractCell<T> {
    /**
     *
     */
    private static final long   serialVersionUID = 36481831017227154L;
    private final static String STRING_VALUE     = "";

    /**
     * @param name The name of the empty cell to create.
     * @param cellType The type of the cell if one is present. If null, the type will be {@link CellType#STRING}
     */
    public EmptyCell(String name, CellType cellType) {
        super(name, null, ((cellType == null) ? CellType.STRING : cellType));
    }

    @Override
    public String getStringValue() {
        return STRING_VALUE;
    }

    @Override
    public int compareValueTo(Cell<T> right) {
        return right.isEmpty() ? 0 : -1;
    }

    /**
     * @return true. Always.
     */
    @Override
    public boolean isEmpty() {
        return true;
    }

}
