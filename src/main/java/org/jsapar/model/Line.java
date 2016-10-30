package org.jsapar.model;

import java.io.Serializable;
import java.util.*;

/**
 * A line is one row of the input buffer. Each line contains a list of cells. Cells can be retrieved
 * either by index O(1) or by name O(n). Note that the class is not synchronized internally. If
 * multiple threads access the same instance, external synchronization is required.
 * 
 * @author Jonas Stenberg
 * 
 */
public class Line implements Serializable, Cloneable{

    /**
     * 
     */
    private static final long serialVersionUID = 6026541900371948402L;
    public static final String EMPTY = "";

    private Map<String, Cell> cells = new LinkedHashMap<>();

    /**
     * Line type.
     */
    private String lineType = EMPTY;

    /**
     * Assigned when parsing to the line number of the input source. Used primarily for logging and tracking. Has no
     * significance when composing. First line has lineNumber=1. Equals 0 if not assigned.
     */
    private long lineNumber = 0L;

    /**
     * Creates an empty line without any cells.
     */
    public Line() {
        cells = new LinkedHashMap<>();
    }

    /**
     * Creates an empty line without any cells but with an initial capacity.
     * 
     * @param initialCapacity
     *            The initial capacity. Used only to reserve space. If capacity is exceeded, the
     *            capacity grows automatically.
     */
    public Line(int initialCapacity) {
        cells = new LinkedHashMap<>(initialCapacity);
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
     * @param initialCapacity
     *            The initial capacity. Used only to reserve space. If capacity is exceeded, the
     *            capacity grows automatically.
     */
    public Line(String sLineType, int initialCapacity) {
        this(initialCapacity);
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
    public List<Cell> getCells() {
        return new ArrayList<>(cells.values()) ;
    }

    /**
     * Returns an iterator that will iterate all the cells of this line.
     * 
     * @return An iterator that will iterate all the cells of this line.
     */
    public Iterator<Cell> getCellIterator() {
        return cells.values().iterator();
    }

    /**
     * Adds a cell to the cells list.
     * 
     * @param cell
     *            The cell to add
     * @throws IllegalStateException
     *             If a cell with the same name already exists.
     */
    private void addCellByName(Cell cell)  {
        Cell oldCell = cells.get(cell.getName());
        if (oldCell != null)
            throw new IllegalStateException("A cell with the name '" + cell.getName()
                    + "' already exists. Failed to add cell.");
        this.cells.put(cell.getName(), cell);
    }

    /**
     * Adds a cell to the end of the line. Requires that there is not already a cell with the same name within this
     * line. Use method replaceCell() instead if you don't care if a cell with the same name already exist.
     *
     * @param cell
     *            The cell to add
     * @throws IllegalStateException
     *             if cell with the same name already exist. Use method replaceCell() instead if you don't care if a
     *             cell with the same name already exist.
     */
    public void addCell(Cell cell)  {
        if (cell.getName() != null)
            addCellByName(cell);
    }

    /**
     * Removes cell with the given name.
     * 
     * @param sName
     *            The name of the cell to remove.
     * @return The removed cell
     */
    public Cell removeCell(String sName) {
        return this.cells.remove(sName);
    }


    /**
     * Adds a cell to the line end of the line, replacing any existing cell with the same name.
     * 
     * @param cell
     *            The cell to add
     * @return The replaced cell or null if there were no cell within the line with the same name.
     */
    public Cell replaceCell(Cell cell) {
        return this.cells.put(cell.getName(), cell);
    }


    /**
     * Gets a cell with specified name. Name is specified by the schema.
     * 
     * @param name The name of the cell to get
     * @return The cell or null if there is no cell with specified name.
     */
    public Cell getCell(String name) {
        return this.cells.get(name);
    }

    /**
     * Gets the number of cells that this line contains.
     * 
     * @return the number of cells that this line contains.
     */
    public int size() {
        return this.cells.size();
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
            sb.append("Line type=[");
            sb.append(this.lineType);
            sb.append("]");
        }
        if(this.getLineNumber() > 0){
            sb.append(" Line number=");
            sb.append(this.lineNumber);
        }
        sb.append(" Cells: ");
        sb.append(this.cells.values());
        return sb.toString();
    }

    /**
     * Checks if there is a cell with the specified name.
     * @param cellName The name of the cell to check.
     * @return True if there is a cell with the specified name, false otherwise.
     */
    boolean isCell(String cellName) {
        return this.getCell(cellName) != null;
    }


    /**
     * Checks if there is a cell with the specified name and if it is not empty.
     * @param cellName The name of the cell to check.
     * @return true if the cell with the specified name exists and that it contains a value.
     */
    @SuppressWarnings("SimplifiableIfStatement")
    public boolean isCellSet(String cellName) {
        Cell cell = getCell(cellName);
        if (cell == null)
            return false;

        return !cell.isEmpty();
    }

    /**
     * Checks if there is a cell with the specified name and type and that is not empty.
     * @param cellName The name of the cell to check.
     * @param type The type to check.
     * @return true if the cell with the specified name contains a value of the specified type.
     */
    @SuppressWarnings("SimplifiableIfStatement")
    public boolean isCellSet(String cellName, CellType type) {
        Cell cell = getCell(cellName);
        if (cell == null)
            return false;

        if (cell.isEmpty())
            return false;

        return cell.getCellType().equals(type);
    }

    public long getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(long lineNumber) {
        this.lineNumber = lineNumber;
    }

    @Override
    public Line clone()  {
        Line clone;
        try {
            clone = (Line) super.clone();
        } catch (CloneNotSupportedException e) {
            // This should never happen.
            throw new AssertionError(e);
        }

        clone.cells = new LinkedHashMap<>(this.cells.size());

        for (Cell cell : this.cells.values()) {
            Cell cellClone = cell.clone();
            clone.cells.put(cellClone.getName(), cellClone);
        }

        return clone;
    }
}
