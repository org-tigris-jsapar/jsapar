/**
 *
 */
package org.jsapar.model;

import org.jsapar.schema.SchemaException;

import java.text.Format;

/**
 * This class is a special case where an empty value has been parsed from a source. It can be used as a type independent
 * placeholder of an empty value.
 */
public class EmptyCell extends Cell implements Comparable<EmptyCell> {
    /**
     *
     */
    private static final long   serialVersionUID = 36481831017227154L;
    private final static String STRING_VALUE     = "";

    /**
     * @param name The name of the empty cell to create.
     * @param cellType The type of the cell if one is present. If null, the type will be CellType.STRING
     */
    public EmptyCell(String name, CellType cellType) {
        super(name, ((cellType == null) ? CellType.STRING : cellType));
    }

    /* (non-Javadoc)
     * @see org.jsapar.model.Cell#getStringValue(java.text.Format)
     */
    @Override
    public String getStringValue(Format format) {
        return STRING_VALUE;
    }

    /* (non-Javadoc)
     * @see org.jsapar.model.Cell#getStringValue()
     */
    @Override
    public String getStringValue() {
        return STRING_VALUE;
    }

    /* (non-Javadoc)
     * @see org.jsapar.model.Cell#getValue()
     */
    @Override
    public Object getValue() {
        return null;
    }

    @Override
    public int compareTo(EmptyCell right) {
        return 0;
    }

    @Override
    public int compareValueTo(Cell right) throws SchemaException {
        // Everything is equal to empty cell :-S.
        return 0;
    }

    /**
     * @return true. Always.
     */
    @Override
    public boolean isEmpty() {
        return true;
    }

}
