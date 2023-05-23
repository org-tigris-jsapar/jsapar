package org.jsapar.parse.cell;

import org.jsapar.error.JSaParException;
import org.jsapar.model.Cell;
import org.jsapar.model.CellType;
import org.jsapar.model.EmptyCell;
import org.jsapar.parse.CellParseException;
import org.jsapar.schema.SchemaCell;
import org.jsapar.schema.SchemaException;
import org.jsapar.text.Format;
import org.jsapar.utils.cache.Cache;

import java.text.ParseException;
import java.util.Locale;
import java.util.function.Consumer;

/**
 * Internal class for parsing text on cell level.
 */
public class CellParser<S extends SchemaCell> {

    private final S schemaCell;
    private final Cell<?> defaultCell;
    private final EmptyCell<?> emptyCell;
    private final CellFactory<?> cellFactory;
    private Format<?> format;
    private final Cache<String, Cell<?>> cellCache ;
    private static final String EMPTY_STRING = "";


    /**
     * Creates cell parser according to supplied schema and with a maximum cache size.
     * @param schemaCell The schema to use.
     * @param maxCacheSize The maximum number of cells to keep in cache while parsing. The value 0 will disable cache.
     */
    protected CellParser(S schemaCell, int maxCacheSize) {
        this.schemaCell = schemaCell;

        CellType cellType = schemaCell.getCellFormat().getCellType();
        cellFactory = CellFactory.getInstance(cellType);
        assert cellFactory != null;
        cellCache = Cache.ofMaxSize(cellFactory.actualCacheMaxSize(schemaCell, maxCacheSize));
        format = schemaCell.getFormat();
        if(format == null)
            format  = cellFactory.makeFormat(schemaCell.getLocale());

        try {
            this.defaultCell = schemaCell.isDefaultValue() ? makeCell(schemaCell.getDefaultValue()) : null;
        }catch (ParseException e){
            throw new SchemaException("Failed to set default value of cell " + schemaCell.getName() + " to value [" + schemaCell.getDefaultValue() + "]", e);
        }
        this.emptyCell = schemaCell.makeEmptyCell();
    }

    /**
     * Creates a cell with a parsed value according to the schema specification for this cell. This
     * method does not throw exception if mandatory cell does not exist. Instead, it reports an error
     * event and continues and may then return null.
     *
     * @param sValue             The value of the cell
     * @param errorEventListener Error event listener to deliver errors to.
     * @return A new cell of a type according to the schema specified. Returns null if there was en error while parsing.
     */
    public Cell<?> parse(String sValue, Consumer<JSaParException> errorEventListener) {
        if (sValue.isEmpty()) {
            checkIfMandatory(errorEventListener);

            if (isDefaultValue()) {
                return defaultCell;
            } else {
                return emptyCell;
            }
        }
        return doParse(sValue, errorEventListener);
    }

    public boolean isDefaultValue() {
        return this.defaultCell != null;
    }

    /**
     * Creates a cell with a parsed value according to the schema specification for this cell. Does
     * not check if cell is mandatory!! Reports a cell error event if an error occurs.
     *
     * @param sValue             The value of the cell
     * @param errorEventListener Error event listener to deliver errors to.
     * @return A new cell of a type according to the schema specified. Returns null if an error occurs.
     */
    private Cell<?> doParse(String sValue, Consumer<JSaParException> errorEventListener) {
        try {
            Cell<?> cell = makeCell(sValue);
            validateRange(cell);
            return cell;
        } catch (java.text.ParseException e) {
            errorEventListener.accept(
                    new CellParseException(schemaCell.getName(), sValue, schemaCell.getCellFormat(), e));
            return null;
        }
    }

    /**
     * Creates a cell with a parsed value according to the schema specification for this cell. Does
     * not check if cell is mandatory!!
     *
     * @param sValue The value to assign to the new cell
     * @return A new cell of a type according to the schema specified. Returns null if there is no
     *         value.
     * @throws ParseException If the value cannot be parsed according to the format of this cell schema.
     */
    Cell<?> makeCell(String sValue) throws ParseException {

        // If the cell is empty, check if default value exists.
        if (sValue.isEmpty() || (schemaCell.hasEmptyCondition() && schemaCell.getEmptyCondition().test(sValue))) {
            if (schemaCell.isDefaultValue()) {
                return defaultCell;
            } else {
                return emptyCell;
            }
        }
        Cell<?> cell = cellCache.get(sValue);
        if(cell == null) {
            cell = cellFactory.makeCell(schemaCell.getName(), sValue, format);
            cellCache.put(sValue, cell);
        }
        return cell;

    }


    /**
     * Creates a new cell
     * @param cellType Type of the cell
     * @param sName Name of the cell
     * @param sValue Value of the cell
     * @param locale   The locale to use to create default format
     * @return A cell object that has been parsed from the supplied sValue parameter according to
     *         the default format for supplied type and locale.
     * @throws ParseException If the value cannot be parsed according to the format of this cell schema.
     */
    public static Cell<?> makeCell(CellType cellType, String sName, String sValue, Locale locale)
            throws java.text.ParseException {
        CellFactory cellFactory = CellFactory.getInstance(cellType);

        Format<?> format = cellFactory.makeFormat(locale);
        return cellFactory.makeCell(sName, sValue, format);
    }
    /**
     * Validates that the cell value is within the valid range. Throws a SchemaException if value is
     * not within borders.
     *
     * @param cell       The cell to validate
     * @throws ParseException If the value cannot be parsed according to the format of this cell schema.
     */
    @SuppressWarnings("unchecked")
    private void validateRange(Cell<?> cell) throws ParseException {
        if (schemaCell.getMinValue() != null && cell.compareValueTo(schemaCell.getMinValue()) < 0) {
            throw new ParseException("The value is below minimum range limit ("+schemaCell.getMinValue().getStringValue()+").", 0);
        } else if (schemaCell.getMaxValue() != null && cell.compareValueTo(schemaCell.getMaxValue()) > 0)
            throw new ParseException("The value is above maximum range limit ("+schemaCell.getMaxValue().getStringValue()+").", 0);
    }

    /**
     * Checks if cell is mandatory and in that case fires an error event.
     *
     * @param errorEventListener The error event listener to deliver errors to.
     */
    public void checkIfMandatory(Consumer<JSaParException> errorEventListener) {
        if (schemaCell.isMandatory()) {
            CellParseException e = new CellParseException(schemaCell.getName(), EMPTY_STRING,
                    schemaCell.getCellFormat(), "Mandatory cell requires a value.");
            errorEventListener.accept(e);
        }
    }

    public S getSchemaCell() {
        return schemaCell;
    }

    public Cell<?> makeDefaultCell() {
        return defaultCell;
    }

    /**
     * Creates cell parser according to supplied schema and with cache disabled.
     * @param schemaCell The schema to use.
     * @return A {@link CellParser} instance to use for parsing supplied schemaCell.
     * @param <S> The type of the schema cell.
     */
    public static <S extends SchemaCell> CellParser<S> ofSchemaCell(S schemaCell) {
        return new CellParser<>(schemaCell, 0);
    }

    /**
     * Creates cell parser according to supplied schema and with a maximum cache size.
     * @param schemaCell The schema to use.
     * @param maxCacheSize The maximum number of cells to keep in cache while parsing. The value 0 will disable cache.
     * @return A {@link CellParser} instance to use for parsing supplied schemaCell.
     * @param <S> The type of the schema cell.
     */
    public static <S extends SchemaCell> CellParser<S> ofSchemaCell(S schemaCell, int maxCacheSize) {
        return new CellParser<>(schemaCell, maxCacheSize);
    }

}
