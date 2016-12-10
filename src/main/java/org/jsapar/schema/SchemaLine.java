package org.jsapar.schema;

/**
 * Abstract base class that describes the schema for a line.
 */
public abstract class SchemaLine implements Cloneable {
    private static final int    OCCURS_INFINITE      = Integer.MAX_VALUE;
    private static final String NOT_SET              = "";

    private int                 occurs               = OCCURS_INFINITE;
    private String              lineType             = NOT_SET;

    /**
     * Creates a SchemaLine that occurs infinite number of times.
     */
    public SchemaLine() {
        this.occurs = OCCURS_INFINITE;
    }

    /**
     * Creates a SchemaLine that occurs supplied number of times.
     * 
     * @param nOccurs
     *            The number of times that a line of this type occurs in the input or output text.
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
     * @param lineType The line type of this schema line.
     * @param nOccurs The number of times it should occur.
     */
    public SchemaLine(String lineType, int nOccurs){
    	this.setLineType(lineType);
    	this.setOccurs(nOccurs);
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
