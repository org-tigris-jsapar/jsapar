package org.jsapar.schema;

import org.jsapar.model.CellType;
import org.jsapar.parse.cell.CellFactory;

import java.text.Format;
import java.util.Locale;

/**
 * Describes the format of a cell, including the data type.
 */
public class SchemaCellFormat implements Cloneable {
    private final CellType cellType;
    private final java.text.Format format;
    private final String pattern;


    /**
     * Creates a new cell format object of supplied type.
     * @param cellType The type of the cell.
     */
    public SchemaCellFormat(CellType cellType) {
        this.cellType = cellType;
        this.format = null;
        this.pattern = "";
    }

    public SchemaCellFormat(CellType cellType, Format format) {
        this.cellType = cellType;
        this.format = format;
        this.pattern = "";
    }

    /**
     * Creates a new cell format object of supplied type and pattern.
     * @param cellType The type of the cell.
     * @param sPattern The pattern of the cell.
     * @throws SchemaException
     */
    public SchemaCellFormat(CellType cellType, String sPattern) throws SchemaException {
        this(cellType, sPattern, null);
    }

    /**
     * Creates a new cell format object of supplied type, pattern and locale.
     * @param cellType The type of the cell.
     * @param sPattern The pattern of the cell.
     * @param locale   The locale determines for instance how decimal separator should be formatted etc.
     * @throws SchemaException
     */
    public SchemaCellFormat(CellType cellType, String sPattern, Locale locale) throws SchemaException {
        this.cellType = cellType;
        this.pattern = sPattern;
        if (sPattern == null) {
            this.format = null;
            return;
        }
        this.format = CellFactory.getInstance(cellType).makeFormat(locale, sPattern);
    }


    /**
     * @return the cellType
     */
    public CellType getCellType() {
        return cellType;
    }

    /**
     * @return the format
     */
    public java.text.Format getFormat() {
        return format;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("CellType=");
        sb.append(this.cellType);
        if (this.format != null) {
            sb.append(", Format={");
            sb.append(this.format);
            sb.append("}");
        }
        return sb.toString();
    }

    /**
     * @return the pattern
     */
    public String getPattern() {
        return pattern;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((cellType == null) ? 0 : cellType.hashCode());
        result = prime * result + ((format == null) ? 0 : format.hashCode());
        result = prime * result + ((pattern == null) ? 0 : pattern.hashCode());
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
        if (!(obj instanceof SchemaCellFormat)) {
            return false;
        }
        SchemaCellFormat other = (SchemaCellFormat) obj;
        if (cellType == null) {
            if (other.cellType != null) {
                return false;
            }
        } else if (!cellType.equals(other.cellType)) {
            return false;
        }
        if (format == null) {
            if (other.format != null) {
                return false;
            }
        } else if (!format.equals(other.format)) {
            return false;
        }
        if (pattern == null) {
            if (other.pattern != null) {
                return false;
            }
        } else if (!pattern.equals(other.pattern)) {
            return false;
        }
        return true;
    }
    
}
