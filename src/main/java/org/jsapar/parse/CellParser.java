package org.jsapar.parse;

import org.jsapar.error.ErrorEvent;
import org.jsapar.error.ErrorEventListener;
import org.jsapar.model.Cell;
import org.jsapar.model.EmptyCell;
import org.jsapar.schema.SchemaCell;

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
                return cellSchema.getDefaultCell().makeCopy(cellSchema.getName());
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
            Cell cell = cellSchema.makeCell(sValue);
            validateRange(cellSchema, cell);
            return cell;
        } catch (java.text.ParseException e) {
            errorEventListener.errorEvent(new ErrorEvent(this,
                    new CellParseException(cellSchema.getName(), sValue, cellSchema.getCellFormat(), e.getMessage())));
            return null;
        }

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
