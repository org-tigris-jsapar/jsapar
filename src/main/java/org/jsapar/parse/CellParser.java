package org.jsapar.parse;

import org.jsapar.error.ErrorEvent;
import org.jsapar.error.ErrorEventListener;
import org.jsapar.model.*;
import org.jsapar.schema.SchemaCell;
import org.jsapar.schema.SchemaException;
import org.jsapar.utils.StringUtils;

import java.text.DecimalFormat;
import java.text.Format;
import java.util.Locale;

public class CellParser {

    private static final String EMPTY_STRING = "";

    public CellParser() {
    }

    /**
     * Creates a cell with a parsed value according to the schema specification for this cell. This
     * method does not throw exception of mandatory cell does not exist. Instead it reports an error
     * event and continues.
     *
     * @param cellSchema         The cell schema to use
     * @param sValue             The value of the cell
     * @param errorEventListener Error event listener to deliver errors to.
     * @return A new cell of a type according to the schema specified. Returns null if there is no
     * value.
     */
    public Cell parse(SchemaCell cellSchema, String sValue, ErrorEventListener errorEventListener) {
        if (sValue.isEmpty()) {
            checkIfMandatory(cellSchema, errorEventListener);

            if (cellSchema.getDefaultCell() != null) {
                return cellSchema.getDefaultCell().clone();
            } else {
                return new EmptyCell(cellSchema.getName(), cellSchema.getCellFormat().getCellType());
            }
        }
        return doParse(cellSchema, sValue, errorEventListener);
    }

    /**
     * Creates a cell with a parsed value according to the schema specification for this cell. Does
     * not check if cell is mandatory!! Reports a cell error event if an error occurs.
     *
     * @param cellSchema         The cell schema to use
     * @param sValue             The value of the cell
     * @param errorEventListener Error event listener to deliver errors to.
     * @return A new cell of a type according to the schema specified. Returns null if an error occurs.
     */
    private Cell doParse(SchemaCell cellSchema, String sValue, ErrorEventListener errorEventListener) {

        try {
            Cell cell = makeCell(cellSchema, sValue);
            validateRange(cellSchema, cell);
            return cell;
        } catch (java.text.ParseException e) {
            errorEventListener.errorEvent(new ErrorEvent(this,
                    new CellParseException(cellSchema.getName(), sValue, cellSchema.getCellFormat(), e.getMessage())));
            return null;
        }

    }

    /**
     * Creates a cell with a parsed value according to the schema specification for this cell. Does
     * not check if cell is mandatory!!
     *
     * @param schemaCell    The cell schema to use.
     * @param sValue The value to assign to the new cell
     * @return A new cell of a type according to the schema specified. Returns null if there is no
     *         value.
     * @throws SchemaException If there is an error in the schema
     * @throws java.text.ParseException If the value cannot be parsed according to the format of this cell schema.
     */
    public Cell makeCell(SchemaCell schemaCell, String sValue) throws java.text.ParseException{

        String name = schemaCell.getName();
        // If the cell is empty, check if default value exists.
        if (sValue.length() <= 0 || (schemaCell.getEmptyPattern() != null && schemaCell.getEmptyPattern().matcher(sValue).matches())) {
            if (schemaCell.isDefaultValue()) {
                return schemaCell.getDefaultCell().clone();
            } else {
                return new EmptyCell(name, schemaCell.getCellFormat().getCellType());
            }
        }

        CellType cellType = schemaCell.getCellFormat().getCellType();
        Cell cell;
        if (schemaCell.getCellFormat().getFormat() != null)
            cell = makeCell(cellType, name, sValue, schemaCell.getCellFormat().getFormat());
        else
            cell = makeCell(cellType, name, sValue, schemaCell.getLocale());
        return cell;

    }


    /**
     * Creates a new cell
     * @param cellType Type of the cell
     * @param sName Name of the cell
     * @param sValue Value of the cell
     * @param format Text format to use
     * @return A cell object that has been parsed from the supplied sValue parameter according to
     *         the supplied format.
     * @throws java.text.ParseException If there is a formatting error
     */
    public static Cell makeCell(CellType cellType, String sName, String sValue, java.text.Format format)
            throws java.text.ParseException {

        switch (cellType) {

        case STRING:
            return new StringCell(sName, sValue, format);
        case DATE:
            return new DateCell(sName, sValue, format);
        case INTEGER:
            sValue = adjustValueForSpaces(sValue, format, false);
            return new IntegerCell(sName, sValue, format);
        case BOOLEAN:
            return new BooleanCell(sName, sValue, format);
        case FLOAT:
            sValue = adjustValueForSpaces(sValue, format, false);
            return new FloatCell(sName, sValue, format);
        case DECIMAL:
            sValue = adjustValueForSpaces(sValue, format, true);
            return new BigDecimalCell(sName, sValue, format);
        case CHARACTER:
            return new CharacterCell(sName, sValue, format);
        case CUSTOM:
            throw new UnsupportedOperationException("Custom Cell type not yet supported.");
        }
        throw new UnsupportedOperationException("Unknown Cell type.");
    }

    private static String adjustValueForSpaces(String sValue, Format format, boolean parseBigDecimal) {
        if (format != null && format instanceof DecimalFormat) {
            // This is necessary because some locales (e.g. swedish)
            // have non breakable space as thousands grouping character. Naturally
            // we want to remove all space characters including the non breakable.
            DecimalFormat decFormat = (DecimalFormat) format;
            decFormat.setParseBigDecimal(parseBigDecimal);
            char groupingSeparator = decFormat.getDecimalFormatSymbols().getGroupingSeparator();
            if (Character.isSpaceChar(groupingSeparator)) {
                sValue = StringUtils.removeAllSpaces(sValue);
            }
        }
        return sValue;
    }

    /**
     * Creates a new cell
     * @param cellType Type of the cell
     * @param sName Name of the cell
     * @param sValue Value of the cell
     * @param locale   The locale to use to create default format
     * @return A cell object that has been parsed from the supplied sValue parameter according to
     *         the default format for supplied type and locale.
     * @throws java.text.ParseException
     * @throws SchemaException
     */
    public static Cell makeCell(CellType cellType, String sName, String sValue, Locale locale)
            throws java.text.ParseException, SchemaException {
        return cellType.makeCell(sName, sValue, locale);
    }
    /**
     * Validates that the cell value is within the valid range. Throws a SchemaException if value is
     * not within borders.
     *
     * @param cellSchema The cell schema to use
     * @param cell       The cell to validate
     * @throws java.text.ParseException
     */
    protected void validateRange(SchemaCell cellSchema, Cell cell) throws java.text.ParseException {

        if (cellSchema.getMinValue() != null && cell.compareValueTo(cellSchema.getMinValue()) < 0) {
            throw new java.text.ParseException("The value is below minimum range limit.", 0);
        } else if (cellSchema.getMaxValue() != null && cell.compareValueTo(cellSchema.getMaxValue()) > 0)
            throw new java.text.ParseException("The value is above maximum range limit.", 0);
    }

    /**
     * Checks if cell is mandatory and in that case fires an error event.
     *
     * @param cellSchema         The cell schema to use
     * @param errorEventListener The error event listener to deliver errors to.
     */
    protected void checkIfMandatory(SchemaCell cellSchema, ErrorEventListener errorEventListener) {
        if (cellSchema.isMandatory()) {
            CellParseException e = new CellParseException(cellSchema.getName(), EMPTY_STRING,
                    cellSchema.getCellFormat(), "Mandatory cell requires a value.");
            errorEventListener.errorEvent(new ErrorEvent(this, e));
        }
    }
}
