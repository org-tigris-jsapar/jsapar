package org.jsapar.schema;

import org.jsapar.model.Cell;
import org.jsapar.model.Line;

public abstract class SchemaLine implements Cloneable {
    private static final int    OCCURS_INFINITE      = Integer.MAX_VALUE;
    private static final String NOT_SET              = "";

    private int                 occurs               = OCCURS_INFINITE;
    private String              lineType             = NOT_SET;

    public SchemaLine() {
        this.occurs = OCCURS_INFINITE;
    }

    /**
     * Creates a SchemaLine which occurs supplied number of times.
     * 
     * @param nOccurs
     *            The number of times that a line of this type occurs in the corresponding buffer.
     */
    public SchemaLine(int nOccurs) {
        this.occurs = nOccurs;
    }

    /**
     * Creates a SchemaLine with the supplied line type.
     * 
     * @param lineType
     *            The name of the type of the line.
     */
    public SchemaLine(String lineType) {
        this.setLineType(lineType);
    }
    
    /**
     * Creates a SchemaLine with the supplied line type and occurs supplied number of times.
     * @param lineType
     * @param nOccurs
     */
    public SchemaLine(String lineType, int nOccurs){
    	this.setLineType(lineType);
    	this.setOccurs(nOccurs);
    }

    /**
     * Creates a SchemaLine with the supplied line type and control value.
     * 
     * @param lineType
     *            The name of the type of the line.
     * @param lineTypeControlValue
     *            The tag that determines which type of line it is.
     */
    @Deprecated
    public SchemaLine(String lineType, String lineTypeControlValue) {
        this(lineType);
    }

    /**
     * @return The number of times this type of line occurs in the corresponding buffer.
     */
    public int getOccurs() {
        return occurs;
    }

    /**
     * @param occurs
     *            The number of times this type of line occurs in the corresponding buffer.
     */
    public void setOccurs(int occurs) {
        this.occurs = occurs;
    }

    /**
     * Setts the occurs attribute so that this type of line occurs until the end of the buffer.
     */
    public void setOccursInfinitely() {
        this.occurs = OCCURS_INFINITE;
    }

    /**
     * @return true if this line occurs to the end of the buffer, false otherwise.
     */
    public boolean isOccursInfinitely() {
        return this.occurs == OCCURS_INFINITE;
    }

    /**
     * Finds a schema cell with the specified name. This method probably performs a linear search,
     * thus the performance is poor if there are many cells on a line.
     * 
     * @param cellName
     *            The name of the schema cell to find.
     * @return The schema cell with the supplied name or null if no such cell was found.
     */
    public abstract SchemaCell getSchemaCell(String cellName);

    /**
     * Finds the cell to use for output. Each cell is identified from the schema by the name of the
     * cell. If the schema-cell has no name, the cell at the same position in the line is used under
     * the condition that it also lacks name.
     * 
     * If the schema-cell has a name the cell with the same name is used. If no such cell is found
     * and the cell at the same position lacks name, it is used instead.
     * 
     * 
     * @param line
     *            The line to find a cell within.
     * @param schemaCell
     *            The schema-cell to use.
     * @param nSchemaCellIndex
     *            The index at which the schema-cell is found.
     * @return The cell within the supplied line to use for output according to the supplied
     *         schemaCell.
     */
    protected static Cell findCell(Line line, SchemaCell schemaCell, int nSchemaCellIndex) {
        // If we should not write the cell, we don't need the cell.
        if (schemaCell.isIgnoreWrite())
            return null;

        Cell cellByIndex = null;
        if (nSchemaCellIndex < line.size())
            cellByIndex = line.getCell(nSchemaCellIndex);
        // Use optimistic matching.
        if (null != cellByIndex) {
            if (schemaCell.getName().equals(cellByIndex.getName())) {
                // We were lucky.
                return cellByIndex;
            }
        }

        // The optimistic match failed. We take the penalty.
        Cell cellByName = line.getCell(schemaCell.getName());
        if (null != cellByName) {
            return cellByName;
        } else {
            if (null == cellByIndex) {
                return null;
            } else {
                return null;
            }
        }
    }

    /**
     * @return the lineType
     */
    public String getLineType() {
        return lineType;
    }

    /**
     * @param lineType
     *            the lineType to set
     */
    public void setLineType(String lineType) {
        this.lineType = lineType;
    }


    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("SchemaLine lineType=");
        sb.append(this.lineType);
        sb.append(" occurs=");
        if (isOccursInfinitely())
            sb.append("INFINITE");
        else
            sb.append(this.occurs);
        return sb.toString();
    }


    /**
     * @return Number of cells in a line
     */
    public abstract int getSchemaCellsCount();


    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((lineType == null) ? 0 : lineType.hashCode());
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof SchemaLine)) {
            return false;
        }
        SchemaLine other = (SchemaLine) obj;
        if (lineType == null) {
            if (other.lineType != null) {
                return false;
            }
        } else if (!lineType.equals(other.lineType)) {
            return false;
        }
        return true;
    }



    @Override
    public SchemaLine clone() {
        try {
            return (SchemaLine) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError("Can never happen.", e);
        }
    }
}
