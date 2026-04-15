package org.jsapar.model;

/**
 * {@link Cell} implementation carrying a character value of a cell.
 *
 */
public final class CharacterCell extends AbstractCell<Character> implements ComparableCell<Character> {

    public CharacterCell(String sName, Character value) {
        super(sName, value, CellType.CHARACTER);
    }

    /**
     * @param name The name of the empty cell.
     * @return A new Empty cell of supplied name.
     */
    public static Cell<Character> emptyOf(String name) {
        return new EmptyCell<>(name, CellType.CHARACTER);
    }

    @Override
    public Cell<Character> cloneWithName(String newName) {
        return new CharacterCell(newName, getValue());
    }
}
