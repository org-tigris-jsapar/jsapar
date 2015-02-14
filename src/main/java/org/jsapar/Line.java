package org.jsapar;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.Iterator;

/**
 * A line is one row of the input buffer. Each line contains a list of cells. Cells can be retrieved
 * either by index O(1) or by name O(n). Note that the class is not synchronized internally. If
 * multiple threads access the same instance, external synchronization is required.
 * 
 * @author Jonas Stenberg
 * 
 */
public class Line implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 6026541900371948402L;
    public static final String EMPTY = "";

    private java.util.ArrayList<Cell> cellsByIndex = null;

    private java.util.HashMap<String, Cell> cellsByName = null;

    /**
     * Line type.
     */
    private String lineType = EMPTY;

    /**
     * Creates an empty line without any cells.
     */
    public Line() {
        this.cellsByIndex = new java.util.ArrayList<Cell>();
        this.cellsByName = new java.util.HashMap<String, Cell>();
    }

    /**
     * Creates an empty line without any cells but with an initial capacity.
     * 
     * @param nInitialCapacity
     *            The initial capacity. Used only to reserve space. If capacity is exceeded, the
     *            capacity grows automatically.
     */
    public Line(int nInitialCapacity) {
        this.cellsByIndex = new java.util.ArrayList<Cell>(nInitialCapacity);
        this.cellsByName = new java.util.HashMap<String, Cell>(nInitialCapacity);

    }

    /**
     * Creates an empty line of a specified type, without any cells but with an initial capacity.
     * 
     * @param sLineType
     *            The type of the line.
     */
    public Line(String sLineType) {
        this();
        lineType = sLineType;
    }

    /**
     * Creates an empty line of a specified type, without any cells but with an initial capacity.
     * 
     * @param sLineType
     *            The type of the line.
     * @param nInitialCapacity
     *            The initial capacity. Used only to reserve space. If capacity is exceeded, the
     *            capacity grows automatically.
     */
    public Line(String sLineType, int nInitialCapacity) {
        this(nInitialCapacity);
        lineType = sLineType;
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
    @SuppressWarnings("unchecked")
    public java.util.List<Cell> getCells() {
        return (java.util.List<Cell>) cellsByIndex.clone();
    }

    /**
     * Returns an iterator that will iterate all the cells of this line.
     * 
     * @return An iterator that will iterate all the cells of this line.
     */
    public Iterator<Cell> getCellIterator() {
        return cellsByIndex.iterator();
    }

    /**
     * Adds a cell to the cellsByName list.
     * 
     * @param cell
     *            The cell to add
     * @throws JSaParException
     *             If a cell with the same name already exists.
     */
    private void addCellByName(Cell cell) throws JSaParException {
        Cell oldCell = cellsByName.get(cell.getName());
        if (oldCell != null)
            throw new JSaParException("A cell with the name '" + cell.getName()
                    + "' already exists. Failed to add cell.");
        this.cellsByName.put(cell.getName(), cell);
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
        if (cell.getName() != null){
            addCellByName(cell);
        }
        this.cellsByIndex.add(cell);
    }

    /**
     * Removes cell with the given name.
     * 
     * @param sName
     *            The name of the cell to remove.
     * @return The removed cell
     */
    public Cell removeCell(String sName) {
        Cell foundCell = this.cellsByName.remove(sName);
        if (foundCell != null) {
            Iterator<Cell> i = this.cellsByIndex.iterator();
            while (i.hasNext()) {
                if (sName.equals(i.next().getName())) {
                    i.remove();
                    break;
                }
            }
        }
        return foundCell;

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
        Cell removed = null;
        if (this.cellsByIndex.size() > index && this.cellsByIndex.get(index) != null) {
            removed = this.cellsByIndex.remove(index);
            if (removed.getName() != null)
                this.cellsByName.remove(removed.getName());
        }
        return removed;
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
        if(cell.getValue() == null)
            return removeCell(cell.getName());
        
        Cell foundCell = null;
        if (cell.getName() != null) {
            foundCell = this.cellsByName.put(cell.getName(), cell);
            if (foundCell != null) {
                Iterator<Cell> i = this.cellsByIndex.iterator();
                while (i.hasNext()) {
                    if (cell.getName().equals(i.next().getName()))
                        i.remove();
                }
            }
        }
        this.cellsByIndex.add(cell);
        return foundCell;
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
        if (cell.getName() != null) {
            Cell oldCell = this.cellsByName.put(cell.getName(), cell);
            if (oldCell != null)
                throw new JSaParException("A cell with the name '" + cell.getName()
                        + "' already exists. Failed to add cell.");

        }
        this.cellsByIndex.add(index, cell);
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
        Cell removed = null;
        if (this.cellsByIndex.size() > index) {
            removed = this.cellsByIndex.set(index, cell);
            if (removed.getName() != null) {
                this.cellsByName.remove(removed.getName());
            }
        }
        if (cell.getName() != null) {
            Cell second = this.cellsByName.put(cell.getName(), cell);
            if (second != null && second != removed) {
                Iterator<Cell> i = this.cellsByIndex.iterator();
                while (i.hasNext()) {
                    Cell current = i.next();
                    if (cell.getName().equals(current.getName()) && cell != current) {
                        i.remove();
                        break;
                    }
                }
            }
        }
        return removed;
    }

    /**
     * Gets a cell at specified index. First cell has index 0.
     * 
     * @param index
     * @return The cell
     */
    public Cell getCell(int index) {
        return this.cellsByIndex.get(index);
    }

    /**
     * Gets a cell with specified name. Name is specified by the schema. Record cell values can be reached by using the dot notation.<br/>
     * E.g. A cell named "Second" belonging to a record cell named "Record" can be fetched by using the name  "Record.Second" 
     * 
     * @param name
     * @return The cell or null if there is no cell with specified name.
     */
    public Cell getCell(String name) {
        Cell found = this.cellsByName.get(name);
        if(found != null)
            return found;
        
        int dotIndex = name.indexOf('.');
        if(dotIndex < 1)
            return null;
        
        found = this.cellsByName.get(name.substring(0, dotIndex));
        if(found == null || !(found instanceof RecordCell))
            return null;
        RecordCell record = (RecordCell) found;
        return record.getCell(name.substring(dotIndex+1));
    }

    /**
     * Gets the number of cells that this line contains.
     * 
     * @return the number of cells that this line contains.
     */
    public int getNumberOfCells() {
        return this.cellsByIndex.size();
    }

    /**
     * Returns the type of this line. The line type attribute is primarily used when parsing lines
     * of different types, distinguished by a control cell.
     * 
     * @return the lineType or an empty string if no line type has been set.
     */
    public String getLineType() {
        return lineType;
    }

    /**
     * Sets the type of this line. The line type attribute is primarily used when parsing lines of
     * different types, distinguished by a control cell.
     * 
     * @param lineType
     *            the lineType to set. Can not be null. Use empty string if there is no better
     *            value.
     */
    public void setLineType(String lineType) {
        if (lineType == null)
            throw new IllegalArgumentException("Line.lineType can not be set to null value.");
        this.lineType = lineType;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (this.lineType != null && this.lineType.length() > 0) {
            sb.append("Line type=");
            sb.append(this.lineType);
            sb.append(", ");
        }
        sb.append("Cells: ");
        sb.append(this.cellsByIndex);
        return sb.toString();
    }

    /**
     * @param cellName
     * @return True if there is a cell with the specified name, false otherwise.
     */
    boolean isCell(String cellName) {
        return this.getCell(cellName) != null ? true : false;
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
        if(value==null)
            removeCell(cellName);
        else
            this.replaceCell(new StringCell(cellName, value));
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
        if(value==null)
            removeCell(cellName);
        else
            this.setCellValue(cellName, value.toString());
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
        this.replaceCell(new IntegerCell(cellName, value));
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
        this.replaceCell(new IntegerCell(cellName, value));
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
        this.replaceCell(new FloatCell(cellName, value));
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
        this.replaceCell(new BooleanCell(cellName, value));
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
        this.replaceCell(new CharacterCell(cellName, value));
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
        if(value==null)
            removeCell(cellName);
        else
            this.replaceCell(new DateCell(cellName, value));
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
        if(value==null)
            removeCell(cellName);
        else
            this.replaceCell(new BigDecimalCell(cellName, value));
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
        if(value==null)
            removeCell(cellName);
        else
            this.replaceCell(new BigDecimalCell(cellName, value));
    }
    
    /**
     * Utility function that gets the string cell value of the specified cell.
     * 
     * @param cellName
     * @return The value of the specified cell or null if there is no such cell.
     */
    public String getStringCellValue(String cellName) {
        Cell cell = this.getCell(cellName);
        return (cell != null) ? cell.getStringValue() : null;
    }
    
    /**
     * Utility function that gets the string cell value of the specified cell.
     * 
     * @param cellName
     * @param defaultValue
     * @return The value of the specified cell. Returns the default value if there is no cell with supplied name or if the cell is empty.
     */
    public String getCellValue(String cellName, String defaultValue) {
        Cell cell = getCell(cellName);
        if(cell == null || cell.isEmpty() || cell.getStringValue()==null || cell.getStringValue().isEmpty())
            return defaultValue;
        return cell.getStringValue();
    }

    /**
     * @param cellName
     * @return The cell with the specified name.
     * @throws JSaParException
     *             if the cell does not exist.
     */
    private Cell getExistingCell(String cellName) throws JSaParException {
        Cell cell = this.getCell(cellName);
        if (cell == null)
            throw new JSaParException("There is no cell with the name '" + cellName + "' in this line");
        return cell;
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
        Cell cell = getExistingCell(cellName);
        if (cell instanceof NumberCell) {
            NumberCell numberCell = (NumberCell) cell;
            return numberCell.getNumberValue().intValue();
        }
        if(cell.isEmpty())
            throw new JSaParException("The cell ["+cell+"] does not have a value and thus cannot be parsed into an integer value.");

        try {
            return Integer.parseInt(cell.getStringValue());
        } catch (NumberFormatException e) {
            throw new JSaParNumberFormatException("Error while trying to convert cell ["+cell+"] to an integer value." , e);
        }
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
        Cell cell = getCell(cellName);
        if(cell == null || cell.isEmpty())
            return defaultValue;
        if (cell instanceof NumberCell) {
            NumberCell numberCell = (NumberCell) cell;
            return numberCell.getNumberValue().intValue();
        }

        try {
            return Integer.parseInt(cell.getStringValue());
        } catch (NumberFormatException e) {
            throw new JSaParNumberFormatException("Error while trying to convert cell ["+cell+"] to an integer value." , e);
        }
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
        Cell cell = getExistingCell(cellName);
        if (cell instanceof CharacterCell) {
            CharacterCell chCell = (CharacterCell) cell;
            return chCell.getCharacterValue();
        }

        String s = cell.getStringValue();
        if (s.isEmpty())
            throw new NumberFormatException("Could not convert string cell [" + cell
                    + "] to a character since string is empty.");
        return s.charAt(0);
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
        Cell cell = getCell(cellName);
        if (cell == null || cell.isEmpty())
            return defaultValue;
        if (cell instanceof CharacterCell) {
            CharacterCell chCell = (CharacterCell) cell;
            return chCell.getCharacterValue();
        }

        String s = cell.getStringValue();
        if (s.isEmpty())
            throw new NumberFormatException("Could not convert string cell [" + cell
                    + "] to a character since string is empty.");
        return s.charAt(0);
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
        Cell cell = getExistingCell(cellName);
        if (cell instanceof BooleanCell) {
            BooleanCell booleanCell = (BooleanCell) cell;
            return booleanCell.getBooleanValue();
        }

        return Boolean.valueOf(cell.getStringValue());
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
        Cell cell = getCell(cellName);
        if(cell == null || cell.isEmpty())
            return defaultValue;
        if (cell instanceof BooleanCell) {
            BooleanCell booleanCell = (BooleanCell) cell;
            return booleanCell.getBooleanValue();
        }

        return Boolean.valueOf(cell.getStringValue());
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
        Cell cell = getExistingCell(cellName);
        if (cell instanceof NumberCell) {
            NumberCell numberCell = (NumberCell) cell;
            return numberCell.getNumberValue().longValue();
        }

        try {
            return Long.parseLong(cell.getStringValue());
        } catch (NumberFormatException e) {
            throw new JSaParNumberFormatException("Error while trying to convert cell ["+cell+"] to a long integer value." , e);
        }
        
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
        Cell cell = getCell(cellName);
        if(cell == null || cell.isEmpty())
            return defaultValue;
        if (cell instanceof NumberCell) {
            NumberCell numberCell = (NumberCell) cell;
            return numberCell.getNumberValue().longValue();
        }

        try {
            return Long.parseLong(cell.getStringValue());
        } catch (NumberFormatException e) {
            throw new JSaParNumberFormatException("Error while trying to convert cell ["+cell+"] to a long integer value." , e);
        }
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
        Cell cell = getExistingCell(cellName);
        if (cell instanceof DateCell) {
            DateCell dateCell = (DateCell) cell;
            return dateCell.getDateValue();
        }

        throw new JSaParException("The cell " + cell + " is not of type DateCell.");
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
        Cell cell = getCell(cellName);
        if(cell == null || cell.isEmpty())
            return defaultValue;
        
        if (cell instanceof DateCell) {
            DateCell dateCell = (DateCell) cell;
            return dateCell.getDateValue();
        }
        else if (cell instanceof StringCell){
            if(cell.getStringValue().isEmpty())
                return defaultValue;
        }

        throw new JSaParException("The cell " + cell + " is not of type DateCell.");
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
        Cell cell = getExistingCell(cellName);
        String s = cell.getStringValue();
        
        try {
            return (E) Enum.valueOf(enumClass, s);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Error while trying to convert cell ["+cell+"] to an enum value of type "+enumClass.getSimpleName()+"." , e);
        }
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
    @SuppressWarnings("unchecked")
    public <E extends Enum<E>> E getCellValue(String cellName, E defaultValue) throws IllegalArgumentException {
        Cell cell = getCell(cellName);
        if(cell == null || cell.isEmpty())
            return defaultValue;

        String s = cell.getStringValue();
        try {
            return (E) Enum.valueOf(defaultValue.getClass(), s);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Error while trying to convert cell ["+cell+"] to an enum value of type "+defaultValue.getClass().getSimpleName()+"." , e);
        }
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
        Cell cell = getExistingCell(cellName);
        if (cell instanceof NumberCell) {
            NumberCell numberCell = (NumberCell) cell;
            return numberCell.getNumberValue().doubleValue();
        }
        if(cell.isEmpty())
            throw new JSaParException("The cell ["+cell+"] does not have a value and thus cannot be parsed into a floating point value.");

        try {
            return Double.parseDouble(cell.getStringValue());
        } catch (NumberFormatException e) {
            throw new JSaParNumberFormatException("Error while trying to convert cell ["+cell+"] to a floating point value." , e);
        }
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
        Cell cell = getCell(cellName);
        if(cell == null || cell.isEmpty())
            return defaultValue;
        
        if (cell instanceof NumberCell) {
            NumberCell numberCell = (NumberCell) cell;
            return numberCell.getNumberValue().doubleValue();
        }
        
        try {
            return Double.parseDouble(cell.getStringValue());
        } catch (NumberFormatException e) {
            throw new JSaParNumberFormatException("Error while trying to convert cell ["+cell+"] to a floating point value." , e);
        }
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
        Cell cell = getExistingCell(cellName);
        if (cell instanceof BigDecimalCell) {
            BigDecimalCell numberCell = (BigDecimalCell) cell;
            return numberCell.getBigDecimalValue();
        }
        
        if(cell.isEmpty())
            throw new JSaParException("The cell ["+cell+"] does not have a value and thus cannot be parsed into a decimal value.");


        try {
            return new BigDecimal(cell.getStringValue());
        } catch (NumberFormatException e) {
            throw new JSaParNumberFormatException("Error while trying to convert cell ["+cell+"] to a decimal value." , e);
        }
        
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
        Cell cell = getCell(cellName);
        if (cell == null || cell.isEmpty())
            return defaultValue;

        if (cell instanceof BigDecimalCell) {
            BigDecimalCell numberCell = (BigDecimalCell) cell;
            return numberCell.getBigDecimalValue();
        }

        try {
            return new BigDecimal(cell.getStringValue());
        } catch (NumberFormatException e) {
            throw new JSaParNumberFormatException("Error while trying to convert cell ["+cell+"] to a decimal value." , e);
        }
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
        Cell cell = getExistingCell(cellName);
        if (cell instanceof BigDecimalCell) {
            BigDecimalCell numberCell = (BigDecimalCell) cell;
            return numberCell.getBigIntegerValue();
        }
        if(cell.isEmpty())
            throw new JSaParException("The cell ["+cell+"] does not have a value and thus cannot be parsed into an integer value.");

        try {
            return new BigInteger(cell.getStringValue());
        } catch (NumberFormatException e) {
            throw new JSaParNumberFormatException("Error while trying to convert cell ["+cell+"] to an integer value." , e);
        }
        
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
        Cell cell = getCell(cellName);
        if(cell == null || cell.isEmpty())
            return defaultValue;
        
        if (cell instanceof BigDecimalCell) {
            BigDecimalCell numberCell = (BigDecimalCell) cell;
            return numberCell.getBigIntegerValue();
        }

        try {
            return new BigInteger(cell.getStringValue());
        } catch (NumberFormatException e) {
            throw new JSaParNumberFormatException("Error while trying to convert cell ["+cell+"] to an integer value." , e);
        }
    }    
    /**
     * @param cellName
     * @return true if the cell with the specified name contains a value.
     */
    public boolean isCellSet(String cellName) {
        Cell cell = getCell(cellName);
        if (cell == null)
            return false;

        if (cell.isEmpty())
            return false;

        return true;
    }

    /**
     * @param cellName
     * @param type
     * @return true if the cell with the specified name contains a value of the specified type.
     */
    public boolean isCellSet(String cellName, CellType type) {
        Cell cell = getCell(cellName);
        if (cell == null)
            return false;

        if (cell.isEmpty())
            return false;

        return cell.getCellType().equals(type);
    }

}
