package org.jsapar.parse;

import org.jsapar.parse.LineEventListener;
import org.jsapar.model.Cell;
import org.jsapar.model.CellType;
import org.jsapar.model.EmptyCell;
import org.jsapar.parse.CellParseError;
import org.jsapar.parse.LineErrorEvent;
import org.jsapar.parse.ParseException;
import org.jsapar.schema.SchemaCell;
import org.jsapar.schema.SchemaException;

public class CellParser {

    private static final String EMPTY_STRING = "";

    public CellParser() {
    }

    
    
    /**
     * Creates a cell with a parsed value according to the schema specification for this cell. This
     * method does not throw exception of mandatory cell does not exist. Instead it reports an error
     * event and continues.
     * 
     * @param sValue
     * @param listener
     * @param nLineNumber
     * @return A new cell of a type according to the schema specified. Returns null if there is no
     *         value.
     * @throws ParseException
     */
    public Cell parse(SchemaCell cellSchema, String sValue, LineEventListener listener, long nLineNumber) throws ParseException {
        if (sValue.isEmpty()) {
            checkIfMandatory(cellSchema, listener, nLineNumber);

            if (cellSchema.getDefaultCell() != null) {
                return cellSchema.getDefaultCell().makeCopy(cellSchema.getName());
            } else {
                return new EmptyCell(cellSchema.getName(), cellSchema.getCellFormat().getCellType());
            }
        }
        return parse(cellSchema, sValue);
    }
    
    /**
     * Creates a cell with a parsed value according to the schema specification for this cell. Does
     * not check if cell is mandatory!!
     * 
     * @param sValue
     * @return A new cell of a type according to the schema specified. Returns null if there is no
     *         value.
     * @throws SchemaException
     * @throws ParseException
     */
    public Cell parse(SchemaCell cellSchema, String sValue) throws ParseException {

        // If the cell is empty, check if default value exists.
        if (sValue.length() <= 0 || (cellSchema.getEmptyPattern() != null && cellSchema.getEmptyPattern().matcher(sValue).matches())) {
            if (cellSchema.getDefaultCell() != null) {
                return cellSchema.getDefaultCell().makeCopy(cellSchema.getName());
            } else {
                return new EmptyCell(cellSchema.getName(), cellSchema.getCellFormat().getCellType());
            }
        }

        try {
            CellType cellType = cellSchema.getCellFormat().getCellType();
            Cell cell;
            if (cellSchema.getCellFormat().getFormat() != null)
                cell = SchemaCell.makeCell(cellType, cellSchema.getName(), sValue, cellSchema.getCellFormat().getFormat());
            else
                cell = SchemaCell.makeCell(cellType, cellSchema.getName(), sValue, cellSchema.getLocale());
            validateRange(cellSchema, cell);
            return cell;
        } catch (SchemaException e) {
            throw new ParseException(new CellParseError(cellSchema.getName(), sValue, cellSchema.getCellFormat(), e.getMessage()), e);
        } catch (java.text.ParseException e) {
            throw new ParseException(new CellParseError(cellSchema.getName(), sValue, cellSchema.getCellFormat(), e.getMessage()), e);
        }

    }    
    
    /**
     * Validates that the cell value is within the valid range. Throws a SchemaException if value is
     * not within borders.
     * 
     * @param cell
     * @throws SchemaException
     * @throws ParseException
     * @throws SchemaException
     */
    protected void validateRange(SchemaCell cellSchema, Cell cell) throws SchemaException {

        if (cellSchema.getMinValue() != null && cell.compareValueTo(cellSchema.getMinValue()) < 0)
            throw new SchemaException("The value is below minimum range limit.");
        else if (cellSchema.getMaxValue() != null && cell.compareValueTo(cellSchema.getMaxValue()) > 0)
            throw new SchemaException("The value is above maximum range limit.");

    }

    /**
     * Checks if cell is mandatory and in that case fires an error event.
     * 
     * @param listener
     * @param nLineNumber
     * @throws ParseException
     */
    protected void checkIfMandatory(SchemaCell cellSchema, LineEventListener listener, long nLineNumber) throws ParseException {
        if (cellSchema.isMandatory()) {
            CellParseError e = new CellParseError(nLineNumber, cellSchema.getName(), EMPTY_STRING, cellSchema.getCellFormat(),
                    "Mandatory cell requires a value.");
            listener.lineErrorEvent(new LineErrorEvent(this, e));
        }
    }    
}
