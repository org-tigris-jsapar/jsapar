package org.jsapar.schema;

import java.text.DecimalFormat;
import java.util.Locale;

import org.jsapar.BigDecimalCell;
import org.jsapar.BooleanCell;
import org.jsapar.Cell;
import org.jsapar.DateCell;
import org.jsapar.EmptyCell;
import org.jsapar.FloatCell;
import org.jsapar.IntegerCell;
import org.jsapar.StringCell;
import org.jsapar.Cell.CellType;
import org.jsapar.input.CellParseError;
import org.jsapar.input.ParseException;
import org.jsapar.utils.StringUtils;

public abstract class SchemaCell implements Cloneable {

    private final static SchemaCellFormat CELL_FORMAT_PROTOTYPE = new SchemaCellFormat(CellType.STRING);

    private String name;
    private SchemaCellFormat cellFormat = CELL_FORMAT_PROTOTYPE;
    private boolean ignoreRead = false;
    private boolean mandatory = false;
    private Cell minValue = null;
    private Cell maxValue = null;
    private Locale locale = Locale.getDefault();

    public SchemaCell() {
    }

    public SchemaCell(String sName) {
        this.name = sName;
    }

    /**
     * Indicates if this cell should be ignored after reading it from the buffer. If ignoreRead is
     * true the cell will not be stored to the current Line object.
     * 
     * @return the ignoreRead
     */
    public boolean isIgnoreRead() {
        return ignoreRead;
    }

    /**
     * @param ignoreRead
     *            Indicates if this cell should be ignored after reading it from the buffer. If
     *            ignoreRead is true the cell will not be stored to the current Line object.
     */
    public void setIgnoreRead(boolean ignoreRead) {
        this.ignoreRead = ignoreRead;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the cellFormat
     */
    public SchemaCellFormat getCellFormat() {
        return cellFormat;
    }

    /**
     * @param cellFormat
     *            the cellFormat to set
     */
    public void setCellFormat(SchemaCellFormat cellFormat) {
        this.cellFormat = cellFormat;
    }

    /**
     * Creates a cell with a parsed value according to the schema specification for this cell.
     * 
     * @param sValue
     * @return A new cell of a type according to the schema specified. Returns null if there is no
     *         value.
     * @throws SchemaException
     * @throws ParseException
     */
    public Cell makeCell(String sValue) throws ParseException {

        // If the cell is mandatory, don't accept empty string.
        if (sValue.length() <= 0) {
            if (isMandatory()) {
                throw new ParseException(new CellParseError(getName(), "", getCellFormat(),
                        "Mandatory cell requires a value."));
            } else {
                return new EmptyCell(getName(), this.cellFormat.getCellType());
            }
        }

        try {
            CellType cellType = this.cellFormat.getCellType();
            Cell cell;
            if (getCellFormat().getFormat() != null)
                cell = SchemaCell.makeCell(cellType, getName(), sValue, this.getCellFormat().getFormat());
            else
                cell = SchemaCell.makeCell(cellType, getName(), sValue, getLocale());
            validateRange(cell);
            return cell;
        } catch (SchemaException e) {
            throw new ParseException(new CellParseError(getName(), sValue, getCellFormat(), e.getMessage()), e);
        } catch (java.text.ParseException e) {
            throw new ParseException(new CellParseError(getName(), sValue, getCellFormat(), e.getMessage()), e);
        }

    }

    /**
     * @param cellType
     * @param sName
     * @param sValue
     * @param format
     * @return A cell object that has been parsed from the supplied sValue parameter according to
     *         the supplied format.
     * @throws ParseE
     *             , java.lang.Comparable, java.lang.Comparable, java.lang.Comparablexception
     * @throws java.text.ParseException
     * @throws SchemaException
     */
    public static Cell makeCell(CellType cellType, String sName, String sValue, java.text.Format format)
            throws java.text.ParseException, SchemaException {
        switch (cellType) {
        case STRING:
            return new StringCell(sName, sValue, format);
        case DATE:
            return new DateCell(sName, sValue, format);
        case DECIMAL:
            if (format != null && format instanceof DecimalFormat) {
                // This is necessary because some locales (e.g. swedish)
                // have non breakable space as grouping character. Naturally
                // we want to remove all space characters including the
                // non breakable.
                DecimalFormat decFormat = (DecimalFormat) format;
                char groupingSeparator = decFormat.getDecimalFormatSymbols().getGroupingSeparator();
                if (Character.isSpaceChar(groupingSeparator)) {
                    sValue = StringUtils.removeAllSpaces(sValue);
                }
            }
            return new BigDecimalCell(sName, sValue, format);
        case BOOLEAN:
            return new BooleanCell(sName, sValue, format);
        case INTEGER:
            return new IntegerCell(sName, sValue, format);
        case FLOAT:
            return new FloatCell(sName, sValue, format);
        case CUSTOM:
        default:
            throw new SchemaException("Cell type not implemented: " + cellType);

        }
    }

    /**
     * @param cellType
     * @param sName
     * @param sValue
     * @param locale
     * @return A cell object that has been parsed from the supplied sValue parameter according to
     *         the default format for supplied type and locale.
     * @throws ParseE
     *             , java.lang.Comparable, java.lang.Comparable, java.lang.Comparablexception
     * @throws java.text.ParseException
     * @throws SchemaException
     */
    public static Cell makeCell(CellType cellType, String sName, String sValue, Locale locale)
            throws java.text.ParseException, SchemaException {
        switch (cellType) {
        case STRING:
            return new StringCell(sName, sValue);
        case DATE:
            return new DateCell(sName, sValue, locale);
        case DECIMAL:
            return new BigDecimalCell(sName, sValue, locale);
        case BOOLEAN:
            return new BooleanCell(sName, sValue, locale);
        case INTEGER:
            return new IntegerCell(sName, sValue, locale);
        case FLOAT:
            return new FloatCell(sName, sValue, locale);
        case CUSTOM:
        default:
            throw new SchemaException("Cell type not implemented: " + cellType);

        }
    }

    /**
     * Indicates if the corresponding cell is mandatory, that is an error will be reported if it
     * does not exist while parsing.
     * 
     * @return true if the cell is mandatory, false otherwise.
     */
    public boolean isMandatory() {
        return mandatory;
    }

    /**
     * @param mandatory
     *            Indicates if the corresponding cell is mandatory, that is an error will be
     *            reported if it does not exist while parsing.
     */
    public void setMandatory(boolean mandatory) {
        this.mandatory = mandatory;
    }

    public SchemaCell clone() throws CloneNotSupportedException {
        return (SchemaCell) super.clone();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("SchemaCell name='");
        sb.append(this.name);
        sb.append("'");
        if (this.cellFormat != null) {
            sb.append(" cellFormat=");
            sb.append(this.cellFormat);
        }

        if (this.ignoreRead)
            sb.append(" IGNOREREAD");
        if (this.mandatory)
            sb.append(" MANDATORY");
        return sb.toString();
    }

    /**
     * @return the maxValue
     */
    public Cell getMaxValue() {
        return maxValue;
    }

    /**
     * @param maxValue
     *            the maxValue to set
     */
    public void setMaxValue(Cell maxValue) {
        this.maxValue = maxValue;
    }

    /**
     * @return the minValue
     */
    public Cell getMinValue() {
        return minValue;
    }

    /**
     * @param minValue
     *            the minValue to set
     */
    public void setMinValue(Cell minValue) {
        this.minValue = minValue;
    }

    /**
     * @return the locale
     */
    public Locale getLocale() {
        return locale;
    }

    /**
     * @param locale
     *            the locale to set
     */
    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    /**
     * Validates that the cell value is within the valid range. Throws a SchemaException if value is
     * not within borders.
     * 
     * @param cell
     * @throws SchemaException
     */
    protected void validateRange(Cell cell) throws SchemaException {
        /*
         * if (this.minValue != null && cell.compareTo(this.minValue) < 0) throw new
         * SchemaException("The value is below minimum range limit."); else if (this.maxValue !=
         * null && cell.compareTo(this.maxValue) > 0) throw new
         * SchemaException("The value is above maximum range limit.");
         */
    }

    /**
     * @param value
     * @throws SchemaException
     * @throws java.text.ParseException
     */
    public void setMinValue(String value) throws SchemaException, java.text.ParseException {
        Locale locale = new Locale("US_en");
        Cell cell = SchemaCell.makeCell(this.getCellFormat().getCellType(), "Min", value, locale);
        this.minValue = cell;
    }

    /**
     * @param value
     * @throws SchemaException
     * @throws java.text.ParseException
     */
    public void setMaxValue(String value) throws SchemaException, java.text.ParseException {
        Locale locale = new Locale("US_en");
        Cell cell = SchemaCell.makeCell(this.getCellFormat().getCellType(), "Max", value, locale);
        this.maxValue = cell;
    }
}
