package org.jsapar.parse;

import org.jsapar.model.Cell;
import org.jsapar.model.CellType;
import org.jsapar.model.EmptyCell;
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
     * @return A new cell of a type according to the schema specified. Returns null if there is no
     *         value.
     * @throws ParseException
     */
    public Cell parse(SchemaCell cellSchema, String sValue, ErrorEventListener listener){
        if (sValue.isEmpty()) {
            checkIfMandatory(cellSchema, listener);

            if (cellSchema.getDefaultCell() != null) {
                return cellSchema.getDefaultCell().makeCopy(cellSchema.getName());
            } else {
                return new EmptyCell(cellSchema.getName(), cellSchema.getCellFormat().getCellType());
            }
        }
        return doParse(cellSchema, sValue, listener);
    }
    
    /**
     * Creates a cell with a parsed value according to the schema specification for this cell. Does
     * not check if cell is mandatory!! Reports a cell error event if an error occurs.
     * 
     * @param sValue
     * @return A new cell of a type according to the schema specified. Returns null if an error occurs.
     * @throws SchemaException
     * @throws ParseException
     */
    private Cell doParse(SchemaCell cellSchema, String sValue, ErrorEventListener listener) {


        try {
            Cell cell = cellSchema.makeCell(sValue);
            validateRange(cellSchema, cell);
            return cell;
        } catch (java.text.ParseException e) {
            listener.cellErrorEvent(new CellErrorEvent(this, new CellParseError(cellSchema.getName(), sValue, cellSchema.getCellFormat(), e.getMessage())));
            return null;
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
    protected void validateRange(SchemaCell cellSchema, Cell cell)
            throws java.text.ParseException {

        if (cellSchema.getMinValue() != null && cell.compareValueTo(cellSchema.getMinValue()) < 0) {
            throw new java.text.ParseException("The value is below minimum range limit.", 0);
        }
        else if (cellSchema.getMaxValue() != null && cell.compareValueTo(cellSchema.getMaxValue()) > 0)
            throw new java.text.ParseException("The value is above maximum range limit.", 0);
    }

    /**
     * Checks if cell is mandatory and in that case fires an error event.
     * 
     * @param listener
     * @throws ParseException
     */
    protected void checkIfMandatory(SchemaCell cellSchema, ErrorEventListener listener)  {
        if (cellSchema.isMandatory()) {
            CellParseError e = new CellParseError(cellSchema.getName(), EMPTY_STRING, cellSchema.getCellFormat(),
                    "Mandatory cell requires a value.");
            listener.cellErrorEvent(new CellErrorEvent(this, e));
        }
    }    
}
