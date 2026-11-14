package org.jsapar.parse.cell;

import org.jsapar.model.Cell;
import org.jsapar.model.CharacterCell;
import org.jsapar.schema.SchemaCell;
import org.jsapar.text.Format;

import java.text.ParseException;
import java.util.Locale;

/**
 * Parses character values into {@link Cell} objects
 */
public class CharacterCellFactory implements CellFactory<Character> {

    public CharacterCellFactory() {}

    @Override
    public Cell<Character> makeCell(String name, String value, Format<Character> format) throws ParseException {
        final Character characterValue = format.parse(value);
        return new CharacterCell(name, characterValue);
    }

    @Override
    public Format<Character> makeFormat(Locale locale) {
        return null;
    }

    @Override
    public Format<Character> makeFormat(Locale locale, String pattern) {
        return Format.ofCharacterInstance();
    }

    /**
     * @param schemaCell The schema cell to use.
     * @return 0 since parsing is cheap and a cache hit only saves creating a small cell object.
     */
    @Override
    public int defaultCacheMaxSize(SchemaCell schemaCell) {
        return 0;
    }
}
