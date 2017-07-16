package org.jsapar.model;

/**
 * {@link Cell} implementation carrying a character value of a cell.
 *
 */
public class CharacterCell extends ComparableCell<Character> {

    /**
     * 
     */
    private static final long serialVersionUID = 8442587766024601673L;

    public CharacterCell(String sName, Character value) {
        super(sName, value, CellType.CHARACTER);
    }

}
