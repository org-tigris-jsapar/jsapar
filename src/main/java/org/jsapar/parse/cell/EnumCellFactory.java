package org.jsapar.parse.cell;

import org.jsapar.model.Cell;
import org.jsapar.model.EnumCell;
import org.jsapar.schema.SchemaCell;
import org.jsapar.schema.SchemaException;
import org.jsapar.text.format.EnumFormat;
import org.jsapar.text.Format;

import java.text.ParseException;
import java.util.Locale;

/**
 * Parses string values into {@link Cell} objects
 */
public class EnumCellFactory<E extends Enum<E>> implements CellFactory<E> {

    @Override
    public Cell<E> makeCell(String name, String value, Format<E> format) throws ParseException {
        E enumValue = format.parse(value);
        return new EnumCell<>(name, enumValue);
    }

    @Override
    public Format<E> makeFormat(Locale locale) {
        return null;
    }

    /**
     * Create a {@link Format} instance for the enum cell type given the locale and a specified pattern.
     * @param locale  For enum cell type, the locale is insignificant.
     * @param pattern A pattern to use for the format object.
     *                The pattern have to contain the class name of the Enum to create a format for.
     * Example: pattern="org.jsapar.model.CellType" will create a format that can parse and compose enum values for the
     *                enum class org.jsapar.model.CellType. Full class name, including package names is required.
     * @return A format object to use for the enum type cells.
     */
    @SuppressWarnings("unchecked")
    @Override
    public Format<E> makeFormat(Locale locale, String pattern) {
        try {
            Class<E> enumClass = (Class<E>) Class.forName(pattern,false, Thread.currentThread().getContextClassLoader());
            return EnumFormat.builder(enumClass).build();
        } catch (ClassNotFoundException e) {
            throw new SchemaException("There is no Enum class with class name: " + pattern);
        }
    }

    @Override
    public int actualCacheMaxSize(SchemaCell schemaCell, int configuredCacheMaxSize) {
        Format<?> format = schemaCell.getFormat();
        if(format instanceof EnumFormat){
            @SuppressWarnings("unchecked")
            EnumFormat<E> enumFormat = (EnumFormat<E>) format;
            return enumFormat.numberOfTextValues();
        }
        return configuredCacheMaxSize;
    }
}
