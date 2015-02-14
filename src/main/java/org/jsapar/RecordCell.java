package org.jsapar;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.Format;
import java.text.ParseException;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;

import org.jsapar.schema.SchemaException;

public class RecordCell extends Cell {
    
    private static final long serialVersionUID = 3634500228798987091L;
    private String unparsedValue;
    private Line cells = new Line();

    public RecordCell() {
        super(CellType.RECORD);
    }

    public RecordCell(String sName) {
        super(sName, CellType.RECORD);
    }

    @Override
    public String getStringValue(Format format) throws IllegalArgumentException {
        return unparsedValue;
    }
    
    /* (non-Javadoc)
     * @see org.jsapar.Cell#getStringValue()
     */
    @Override
    public String getStringValue() {
        return unparsedValue;
    }
    
    @Override
    public void setValue(String value, Locale locale) throws ParseException {
        unparsedValue = value;
    }

    @Override
    public void setValue(String value, Format format) throws ParseException {
        unparsedValue = value;
    }

    @Override
    public Object getValue() {
        return cells.getCells();
    }

    @Override
    public int compareValueTo(Cell right) throws SchemaException {
        return 0;
    }
    
    
    
    /**
     * For better performance while iterating multiple lines, it is better to call the
     * {@link #getCellIterator()} method.
     * 
     * @return A clone of the internal collection that contains all the cells of this documents.
     *         Altering the returned collection will not alter the original collection of the this
     *         Line.
     * @see #getCellIterator()
     */
    public java.util.List<Cell> getCells() {
        return cells.getCells();
    }

    /**
     * Returns an iterator that will iterate all the cells of this line.
     * 
     * @return An iterator that will iterate all the cells of this line.
     */
    public Iterator<Cell> getCellIterator() {
        return cells.getCellIterator();
    }



    /**
     * Adds a cell to the end of the line. Requires that there is not already a cell with the same name within this
     * line. Use method replaceCell() instead if you don't care if a cell with the same name already exist.
     * 
     * @param cell
     *            The cell to add
     * @throws JSaParException
     *             if cell with the same name already exist. Use method replaceCell() instead if you don't care if a
     *             cell with the same name already exist.
     */
    public void addCell(Cell cell) throws JSaParException {
        cells.addCell(cell);
    }

    /**
     * Removes cell with the given name.
     * 
     * @param sName
     *            The name of the cell to remove.
     * @return The removed cell
     */
    public Cell removeCell(String sName) {
        return cells.removeCell(sName);
    }

    /**
     * Removes cell with specified index. Cells to the right of the removed cell will be moved one
     * step to the left.
     * 
     * @param index
     *            The index of the cell to remove.
     * @return The removed cell or null if the index was out of bounds.
     */
    public Cell removeCell(int index) {
        return cells.removeCell(index);
    }

    /**
     * Adds a cell to the line end of the line, replacing any existing cell with the same name. If the value of the
     * supplied cell is null, any existing cell is removed but no new cell will be added.
     * 
     * @param cell
     *            The cell to add
     * @return The replaced cell or null if there were no cell within the line with the same name.
     */
    public Cell replaceCell(Cell cell) {
        return cells.replaceCell(cell);
    }

    /**
     * Adds a cell at specified index of a line. First cell has index 0. Existing cells to the right
     * of the new cell will have incremented indexes.
     * 
     * @param cell
     *            The cell to add
     * @param index
     *            The index the cell will have in the line.
     * @throws JSaParException 
     */
    public void addCell(Cell cell, int index) throws JSaParException {
        cells.addCell(cell, index);
    }

    /**
     * Replaces a cell at specified index of a line. First cell has index 0.<br>
     * Note that if the line contains another cell with the name same name as the supplied cell,
     * both that cell and the cell at the specified index will be removed. This can lead to 
     * unexpected behavior since this also affects the index of all cells with higher index than the 
     * removed cell.
     * 
     * @param cell
     *            The cell to add
     * @param index
     *            The index the cell will have in the line.
     * @return The replaced cell (at the index) or null if there were no cell within the line at
     *         that index.
     */
    public Cell replaceCell(Cell cell, int index) {
        return cells.replaceCell(cell, index);
    }

    /**
     * Gets a cell at specified index. First cell has index 0.
     * 
     * @param index
     * @return The cell
     */
    public Cell getCell(int index) {
        return cells.getCell(index);
    }

    /**
     * Gets a cell with specified name. Name is specified by the schema.
     * 
     * @param name
     * @return The cell or null if there is no cell with specified name.
     */
    public Cell getCell(String name) {
        return cells.getCell(name);
    }

    /**
     * Gets the number of cells that this line contains.
     * 
     * @return the number of cells that this line contains.
     */
    public int getNumberOfCells() {
        return cells.getNumberOfCells();
    }
    
    
    /**
     * @param cellName
     * @return True if there is a cell with the specified name, false otherwise.
     */
    boolean isCell(String cellName) {
        return cells.isCell(cellName);
    }

    /**
     * Utility function that adds a cell with the specified name and value to the end of the line or
     * replaces an existing cell if there already is one with the same name.
     * 
     * @param cellName
     *            The name of the cell to add/replace.
     * @param value
     *            The string value to set. If null, existing value will be removed but no new value will be set. 
     */
    public void setCellValue(String cellName, String value) {
        cells.setCellValue(cellName, value);
    }

    /**
     * Utility function that adds a cell with the specified name and value to the end of the line or
     * replaces an existing cell if there already is one with the same name.
     * 
     * @param cellName
     *            The name of the cell to add/replace.
     * @param value
     *            The string value to set. If null, existing value will be removed but no new value will be set.
     */
    public <E extends Enum<E>> void setCellValue(String cellName, E value) {
        cells.setCellValue(cellName, value);
    }
    
    /**
     * Utility function that adds a cell with the specified name and value to the end of the line or
     * replaces an existing cell if there already is one with the same name.
     * 
     * @param cellName
     *            The name of the cell to add/replace.
     * @param value
     *            The integer value to set.
     */
    public void setCellValue(String cellName, int value) {
        cells.setCellValue(cellName, value);
    }

    /**
     * Utility function that adds a cell with the specified name and value to the end of the line or
     * replaces an existing cell if there already is one with the same name.
     * 
     * @param cellName
     *            The name of the cell to add/replace.
     * @param value
     *            The long integer value to set.
     */
    public void setCellValue(String cellName, long value) {
        cells.setCellValue(cellName, value);
    }

    /**
     * Utility function that adds a cell with the specified name and value to the end of the line or
     * replaces an existing cell if there already is one with the same name.
     * 
     * @param cellName
     *            The name of the cell to add/replace.
     * @param value
     *            The double value to set.
     */
    public void setCellValue(String cellName, double value) {
        cells.setCellValue(cellName, value);
    }

    /**
     * Utility function that adds a cell with the specified name and value to the end of the line or replaces an
     * existing cell if there already is one with the same name.
     * 
     * @param cellName
     *            The name of the cell to add/replace.
     * @param value
     *            The boolean value to set.
     */
    public void setCellValue(String cellName, boolean value) {
        cells.setCellValue(cellName, value);
    }

    /**
     * Utility function that adds a cell with the specified name and value to the end of the line or replaces an
     * existing cell if there already is one with the same name.
     * 
     * @param cellName
     *            The name of the cell to add/replace.
     * @param value
     *            The character value to set.
     */
    public void setCellValue(String cellName, char value) {
        cells.setCellValue(cellName, value);
    }
    
    /**
     * Utility function that adds a cell with the specified name and value to the end of the line or
     * replaces an existing cell if there already is one with the same name.
     * 
     * @param cellName
     *            The name of the cell to add/replace.
     * @param value
     *            The date value to set. If null, existing value will be removed but no new value will be set.
     */
    public void setCellValue(String cellName, Date value) {
        cells.setCellValue(cellName, value);
    }
    
    /**
     * Utility function that adds a cell with the specified name and value to the end of the line or
     * replaces an existing cell if there already is one with the same name.
     * 
     * @param cellName
     *            The name of the cell to add/replace.
     * @param value
     *            The value to set. If null, existing value will be removed but no new value will be set.
     */
    public void setCellValue(String cellName, BigDecimal value) {
        cells.setCellValue(cellName, value);
    }

    /**
     * Utility function that adds a cell with the specified name and value to the end of the line or
     * replaces an existing cell if there already is one with the same name.
     * 
     * @param cellName
     *            The name of the cell to add/replace.
     * @param value
     *            The value to set. If null, existing value will be removed but no new value will be set.
     */
    public void setCellValue(String cellName, BigInteger value) {
        cells.setCellValue(cellName, value);
    }
    
    /**
     * Utility function that gets the string cell value of the specified cell.
     * 
     * @param cellName
     * @return The value of the specified cell or null if there is no such cell.
     */
    public String getStringCellValue(String cellName) {
        return cells.getStringCellValue(cellName);
    }
    
    /**
     * Utility function that gets the string cell value of the specified cell.
     * 
     * @param cellName
     * @param defaultValue
     * @return The value of the specified cell. Returns the default value if there is no cell with supplied name or if the cell is empty.
     */
    public String getCellValue(String cellName, String defaultValue) {
        return cells.getCellValue(cellName, defaultValue);
    }


    /**
     * Utility function that gets the integer cell value of the specified cell. If the specified
     * cell does not exist, a JSaparException is thrown. Tries to parse an integer value if cell is
     * not of type NumberCell. Throws a NumberFormatException if the value is not a parsable
     * integer.
     * 
     * @param cellName
     * @return The integer value of the cell with the specified name.
     * @throws JSaParException
     *             , NumberFormatException
     */
    public int getIntCellValue(String cellName) throws JSaParException, NumberFormatException {
        return cells.getIntCellValue(cellName);
    }

    /**
     * Utility function that gets the integer cell value of the specified cell. If the specified
     * cell does not exist, the defaultValue is returned. Tries to parse an integer value if cell is
     * not of type NumberCell. Throws a NumberFormatException if the value is not a parsable
     * integer.
     * @param cellName
     * @param defaultValue
     * @return The integer value of the cell with the specified name.
     * @throws JSaParException 
     */
    public int getCellValue(String cellName, int defaultValue) throws NumberFormatException {
        return cells.getCellValue(cellName, defaultValue);
    }
    
    
    /**
     * Utility function that gets the char cell value of the specified cell. If the specified cell does not exist, a
     * JSaparException is thrown. Tries to parse a character value if cell is not of type CharacterCell. Throws a
     * NumberFormatException if the value is not a parsable character.
     * 
     * @param cellName
     * @return The char value of the cell with the specified name.
     * @throws JSaParException
     *             , NumberFormatException
     */
    public char getCharCellValue(String cellName) throws JSaParException, NumberFormatException {
        return cells.getCharCellValue(cellName);
    }

    /**
     * Utility function that gets the character cell value of the specified cell. If the specified cell does not exist,
     * the defaultValue is returned. Tries to parse a character value if cell is not of type CharacterCell. Throws a
     * NumberFormatException if the value is not a parsable character.
     * 
     * @param cellName
     * @param defaultValue
     * @return The char value of the cell with the specified name.
     * @throws NumberFormatException
     */
    public char getCellValue(String cellName, char defaultValue) throws NumberFormatException {
        return cells.getCellValue(cellName, defaultValue);
    }

    /**
     * Utility function that gets the boolean cell value of the specified cell. If the specified
     * cell does not exist, a JSaparException is thrown. Tries to parse a boolean value if cell is
     * not of type BooleanCell. 
     * @param cellName
     * @return The boolean value of the cell with the supplied name.
     * @throws JSaParException
     */
    public boolean getBooleanCellValue(String cellName) throws JSaParException  {
        return cells.getBooleanCellValue(cellName);
    }
    
    /**
     * Utility function that gets the boolean cell value of the specified cell. If the specified
     * cell does not exist, the defaultValue is returned. Tries to parse a boolean value if cell is
     * not of type BooleanCell. 
     * @param cellName
     * @param defaultValue
     * @return The boolean value of the cell with the supplied name.
     */
    public boolean getCellValue(String cellName, boolean defaultValue)  {
        return cells.getCellValue(cellName, defaultValue);
    }
    
    /**
     * Utility function that gets the long integer cell value of the specified cell. If the specified
     * cell does not exist, a JSaparException is thrown. Tries to parse a long integer value if cell is
     * not of type NumberCell. Throws a NumberFormatException if the value is not a parsable
     * integer.
     * 
     * @param cellName
     * @return The long integer value of the cell with the specified name.
     * @throws JSaParException
     *             , NumberFormatException
     */
    public long getLongCellValue(String cellName) throws JSaParException, NumberFormatException {
        return cells.getLongCellValue(cellName);
    }

    /**
     * Utility function that gets the long integer cell value of the specified cell. If the specified
     * cell does not exist, the supplied default value is returned. Tries to parse a long integer value if cell is
     * not of type NumberCell. Throws a NumberFormatException if the value is not a parsable
     * integer.
     * @param cellName
     * @param defaultValue
     * @return The long integer value of the cell with the specified name.
     * @throws NumberFormatException
     */
    public long getCellValue(String cellName, long defaultValue) throws NumberFormatException {
        return cells.getCellValue(cellName, defaultValue);
    }
    
    /**
     * Utility function that gets the date cell value of the specified cell. If the specified
     * cell does not exist or if it is not a DateCell, a JSaparException is thrown. 
     * 
     * @param cellName
     * @return The date value of the specified cell.
     * @throws JSaParException if the cell is not of type DateCell.
     */
    public Date getDateCellValue(String cellName) throws JSaParException {
        return cells.getDateCellValue(cellName);
    }
    
    /**
     * Utility function that gets the date cell value of the specified cell. If the specified
     * cell does not exist, the supplied default value is returned. If if it is not a DateCell, a JSaparException is thrown. 
     * @param cellName
     * @param defaultValue
     * @return The date cell value if the cell exist and is of type DateCell. Returns the defaultValue if the cell does not exist. 
     * @throws JSaParException If if it is not a DateCell.
     */
    public Date getCellValue(String cellName, Date defaultValue) throws JSaParException {
        return cells.getCellValue(cellName, defaultValue);
    }

    /**
     * Utility function that gets the enum cell value of the specified cell. If the specified cell does not exist, the
     * supplied default value is returned. 
     * 
     * @param cellName
     * @param enumClass The class of the enum to convert the value into.
     * @return The enum cell value if the cell.
     * @throws JSaParException
     *             If if specified cell does not exist.
     * @throws IllegalArgumentException
     *             If the enum type of the defaultValue does not have an enum constant with the name equal to the value
     *             of the specified cell.
     */
    public <E extends Enum<E>> E getEnumCellValue(String cellName, Class<E> enumClass) throws JSaParException, IllegalArgumentException {
        return cells.getEnumCellValue(cellName, enumClass);
    }
    
    /**
     * Utility function that gets the Enum cell value of the specified cell. If the specified cell does not exist, the
     * supplied default value is returned. 
     * 
     * @param cellName
     * @param defaultValue 
     * @return The enum cell value if the cell exist and can be converted to an enum. Returns the defaultValue if the cell does
     *         not exist.
     * @throws IllegalArgumentException
     *             If the enum type of the defaultValue does not have an enum constant with the name equal to the value
     *             of the specified cell.
     */
    public <E extends Enum<E>> E getCellValue(String cellName, E defaultValue) throws IllegalArgumentException {
        return cells.getCellValue(cellName, defaultValue);
    }
    
    
    /**
     * Utility function that gets the double cell value of the specified cell. If the specified cell
     * does not exist, a JSaparException is thrown. Tries to parse a double value if cell is not of
     * type FloatCell. Throws a NumberFormatException if the value is not a parsable double.
     * 
     * @param cellName
     * @return The double value of the cell with the specified name.
     * @throws JSaParException
     *             , NumberFormatException
     */
    public double getDoubleCellValue(String cellName) throws JSaParException, NumberFormatException {
        return cells.getDoubleCellValue(cellName);
    }
    
    /**
     * Utility function that gets the double cell value of the specified cell. If the specified cell does not exist, the
     * supplied defaultValue is returned. Tries to parse a double value if cell is not of type FloatCell. Throws a
     * NumberFormatException if the value is not a parsable double.
     * 
     * @param cellName
     * @param defaultValue
     * @return The double value of the cell with the specified name.
     * @throws NumberFormatException
     * @throws JSaParException 
     */
    public double getCellValue(String cellName, double defaultValue) throws NumberFormatException {
        return cells.getCellValue(cellName, defaultValue);
    }

    /**
     * Utility function that gets the BigDecimal cell value of the specified cell. If the specified cell does not exist,
     * a JSaparException is thrown. Tries to parse a BigDecimal value if cell is not of type BigDecimalCell. Throws a
     * NumberFormatException if the value is not a parsable BigDecimal.
     * 
     * @param cellName
     * @return The double value of the cell with the specified name.
     * @throws JSaParException
     *             , NumberFormatException
     */
    public BigDecimal getDecimalCellValue(String cellName) throws JSaParException, NumberFormatException {
        return cells.getDecimalCellValue(cellName);
    }

    /**
     * Utility function that gets the BigDecimal cell value of the specified cell. If the specified cell does not exist,
     * a JSaParException is thrown. Tries to parse a BigDecimal value if cell is not of type BigDecimalCell. Throws a
     * NumberFormatException if the value is not a parsable BigDecimal.
     * 
     * @param cellName
     * @return The double value of the cell with the specified name.
     * @throws JSaParException
     *             , NumberFormatException
     */
    public BigDecimal getCellValue(String cellName, BigDecimal defaultValue) throws NumberFormatException  {
        return cells.getCellValue(cellName, defaultValue);
    }

    /**
     * Utility function that gets the BigInteger cell value of the specified cell. If the specified cell does not exist,
     * the supplied defaultValue is returned. Tries to parse a BigInteger value if cell is not of type DecimalCell. Throws a
     * NumberFormatException if the value is not a parsable BigDecimal.
     * 
     * @param cellName
     * @return The double value of the cell with the specified name.
     * @throws JSaParException
     *             , NumberFormatException
     */
    public BigInteger getBigIntegerCellValue(String cellName) throws JSaParException, NumberFormatException {
        return cells.getBigIntegerCellValue(cellName);
    }    
    /**
     * Utility function that gets the BigInteger cell value of the specified cell. If the specified cell does not exist,
     * the supplied defaultValue is returned. Tries to parse a BigInteger value if cell is not of type DecimalCell. Throws a
     * NumberFormatException if the value is not a parsable BigDecimal.
     * 
     * @param cellName
     * @return The double value of the cell with the specified name.
     * @throws JSaParException
     *             , NumberFormatException
     */
    public BigInteger getCellValue(String cellName, BigInteger defaultValue) throws NumberFormatException {
        return cells.getCellValue(cellName, defaultValue);
    }    
    /**
     * @param cellName
     * @return true if the cell with the specified name contains a value.
     */
    public boolean isCellSet(String cellName) {
        return cells.isCellSet(cellName);
    }

    /**
     * @param cellName
     * @param type
     * @return true if the cell with the specified name contains a value of the specified type.
     */
    public boolean isCellSet(String cellName, CellType type) {
        return isCellSet(cellName, type);
    }

    

}