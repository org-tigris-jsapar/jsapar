package org.jsapar.parse.cell;

import org.jsapar.model.Cell;
import org.jsapar.model.CellType;
import org.jsapar.schema.SchemaCell;
import org.jsapar.text.Format;

import java.text.ParseException;
import java.util.Locale;

/**
 * Interface for cell creation. Override this factory for each type of cell.
 */
public interface CellFactory<T> {

    /**
     * Get an instance of a cell factory depending on the cell type.
     * @param cellType The cell type to create cell factory for.
     * @return A cell factory of correct type.
     */
    static  CellFactory<?> getInstance(CellType cellType){
        return cellType.getCellFactory();
    }

    /**
     * Parse and create a cell with the given name based on the value and the provided format.
     * @param name The name to give the newly created cell.
     * @param value The value to parse.
     * @param format The format object to use while parsing.
     * @return A new cell.
     * @throws ParseException If parsing could not be done with the given format object.
     */
    Cell<? extends T> makeCell(String name, String value, Format<T> format) throws ParseException;

    /**
     * Create a default format object for the current cell type given the locale.
     * @param locale The locale to use for the format object.
     * @return A format object for the current cell type.
     */
    Format<? extends T> makeFormat(Locale locale);

    /**
     * Create a {@link Format} instance for the current cell type given the locale and a specified pattern.
     * @param locale The locale to use for the format object.
     * @param pattern A pattern to use for the format object. If null or empty, default format will be returned.
     * @return A format object for the current cell type.
     */
    Format<? extends T> makeFormat(Locale locale, String pattern);

    /**
     * For some types of cells, the actual cache max size can be different from configured, for instance boolean can
     * cell factories can return 2 since there will most likely be only two different values.
     * @param schemaCell             The schema cell to use.
     * @param configuredCacheMaxSize The cache max size in configuration.
     * @return The actual cache max size.
     */
    default int actualCacheMaxSize(SchemaCell schemaCell, int configuredCacheMaxSize){
        return configuredCacheMaxSize;
    }
}
