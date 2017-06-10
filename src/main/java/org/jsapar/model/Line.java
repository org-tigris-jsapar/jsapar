package org.jsapar.model;

import org.jsapar.parse.CellParseException;

import java.io.Serializable;
import java.util.*;

/**
 * A line is one row of the input buffer. Each line contains a list of cells. Cells within the line can be retrieved
 * by name. When building a line by parsing an input and using a {@link org.jsapar.schema.Schema}, the name of a cell
 * will be the same as the name of the corresponding {@link org.jsapar.schema.SchemaCell} and the name of a line
 * will be the same as the name or the corresponding {@link org.jsapar.schema.SchemaLine}.
 * <p>
 * Note that the class is not synchronized internally. If
 * multiple threads access the same instance, external synchronization is required.
 * <p>
 * In order to make it easier to retrieve and alter cell values within a {@link org.jsapar.model.Line}, you may use the {@link org.jsapar.model.LineUtils} class.
 * @see LineUtils
 * @see Cell
 * @see Document
 */
public class Line implements Serializable, Cloneable, Iterable<Cell> {

    /**
     *
     */
    private static final long   serialVersionUID = 6026541900371948402L;
    public static final  String EMPTY            = "";

    private Map<String, Cell> cells = new LinkedHashMap<>();
    private Map<String, CellParseException> cellErrors = new LinkedHashMap<>();

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
     * @param initialCapacity The initial capacity. Used only to reserve space. If capacity is exceeded, the
     *                        capacity grows automatically.
     */
    public Line(int initialCapacity) {
        cells = new LinkedHashMap<>(initialCapacity);
    }

    /**
     * Creates an empty line of a specified type, without any cells but with an initial capacity.
     *
     * @param sLineType The type of the line.
     */
    public Line(String sLineType) {
        this();
        lineType = sLineType;
    }

    /**
     * Creates an empty line of a specified type, without any cells but with an initial capacity.
     *
     * @param sLineType       The type of the line.
     * @param initialCapacity The initial capacity. Used only to reserve space. If capacity is exceeded, the
     *                        capacity grows automatically.
     */
    public Line(String sLineType, int initialCapacity) {
        this(initialCapacity);
        lineType = sLineType;
    }

    /**
     * Returns a clone of the internal collection that contains all the cells.
     * For better performance while iterating multiple lines, it is better to call the
     * {@link #iterator()} method.
     *
     * @return A shallow clone of the internal collection that contains all the cells of this line.
     * Altering the returned collection will not alter the original collection of the this
     * Line but altering one of its cells will alter the cell within this line.
     * @see #iterator()
     */
    public List<Cell> getCells() {
        return new ArrayList<>(cells.values());
    }

    /**
     * Returns an iterator that will iterate all the cells of this line.
     *
     * @return An iterator that will iterate all the cells of this line.
     */
    @Override
    public Iterator<Cell> iterator() {
        return cells.values().iterator();
    }

    /**
     * Adds a cell to the end of the line. Requires that there is not already a cell with the same name within this
     * line. Use method {@link #putCell(Cell)} instead if you want the new cell to replace any existing cell with the same name.
     *
     * @param cell The cell to add
     * @throws IllegalStateException if cell with the same name already exist. Use method replaceCell() instead if you
     *                               want existing cells with the same name to be replaced instead.
     */
    public void addCell(Cell cell) {
        Cell oldCell = cells.get(cell.getName());
        if (oldCell != null)
            throw new IllegalStateException(
                    "A cell with the name '" + cell.getName() + "' already exists. Failed to add cell.");
        this.cells.put(cell.getName(), cell);
    }

    /**
     * Removes cell with the given name.
     *
     * @param sName The name of the cell to remove.
     * @return Optional that contains the removed cell if found
     */
    public Optional<Cell> removeCell(String sName) {
        return Optional.ofNullable(this.cells.remove(sName));
    }

    /**
     * Adds a cell to the line end of the line, replacing any existing cell with the same name.
     *
     * @param cell The cell to add
     * @return Optional containing the replaced cell if there was one within the line with the same name.
     * @see #addCell(Cell)
     */
    public Optional<Cell> putCell(Cell cell) {
        return Optional.ofNullable(this.cells.put(cell.getName(), cell));
    }

    /**
     * Gets a cell with specified name. Name is specified by the schema.
     *
     * @param name The name of the cell to get
     * @return Optional cell that is set if there is a cell with specified name.
     */
    public Optional<Cell> getCell(String name) {
        return Optional.ofNullable(this.cells.get(name));
    }

    /**
     * Gets a non empty cell with specified name. Name is specified by the schema.
     *
     * @param name The name of the cell to get
     * @return Optional cell that is set if there is a cell with specified name and that cell is not empty.
     */
    public Optional<Cell> getNonEmptyCell(String name) {
        return getCell(name).filter(c->!c.isEmpty());
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
     * @param lineType the lineType to set. Can not be null. Use empty string if there is no better
     *                 value.
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
        if (this.getLineNumber() > 0) {
            sb.append(" Line number=");
            sb.append(this.lineNumber);
        }
        sb.append(" Cells: ");
        sb.append(this.cells.values());
        return sb.toString();
    }

    /**
     * Checks if there is a cell with the specified name.
     *
     * @param cellName The name of the cell to check.
     * @return True if there is a cell with the specified name, false otherwise.
     */
    boolean isCell(String cellName) {
        return this.getCell(cellName).isPresent();
    }

    public long getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(long lineNumber) {
        this.lineNumber = lineNumber;
    }

    @Override
    public Line clone() {
        Line clone;
        try {
            clone = (Line) super.clone();
        } catch (CloneNotSupportedException e) {
            // This should never happen.
            throw new AssertionError(e);
        }

        clone.cells = new LinkedHashMap<>(this.cells.size());
        // No need to make a deep copy since cells are all final.
        clone.cells.putAll(this.cells);

        return clone;
    }

    /**
     * @param error The cell error to add.
     */
    public void addCellError(CellParseException error) {
        this.cellErrors.put(error.getCellName(), error);
    }

    /**
     * @return True if the line has errors on any of the cells.
     */
    public boolean hasCellErrors(){
        return !this.cellErrors.isEmpty();
    }

    /**
     * @param cellName The name of the cell to get error for.
     * @return If there is an error for the given cell name, that error is returned.The error with the given cell name.
     */
    public Optional<CellParseException> getCellError(String cellName){
        return Optional.ofNullable(cellErrors.get(cellName));
    }

    /**
     * All cell errors of this line.
     * @return All cell errors of this line.
     */
    public Collection<CellParseException> getCellErrors(){
        return cellErrors.values();
    }

}
