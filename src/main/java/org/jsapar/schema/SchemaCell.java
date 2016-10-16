package org.jsapar.schema;

import org.jsapar.model.Cell;
import org.jsapar.model.CellType;
import org.jsapar.model.EmptyCell;
import org.jsapar.parse.ParseException;

import java.util.Locale;
import java.util.regex.Pattern;

public abstract class SchemaCell implements Cloneable {

    protected static final String         EMPTY_STRING          = "";

    private final static SchemaCellFormat CELL_FORMAT_PROTOTYPE = new SchemaCellFormat(CellType.STRING);

    private final String                  name;
    private SchemaCellFormat              cellFormat            = CELL_FORMAT_PROTOTYPE;
    private boolean                       ignoreRead            = false;
    private boolean                       ignoreWrite = false;
    private boolean                       mandatory             = false;
    private Cell                          minValue              = null;
    private Cell                          maxValue              = null;
    private Cell                          defaultCell           = null;
    private String                        defaultValue          = null;
    private Locale                        locale                = Locale.getDefault();
    private Pattern                       emptyPattern          = null;
    private CellValueCondition            lineCondition         = null;


    public SchemaCell(String sName) {
        this(sName, CELL_FORMAT_PROTOTYPE);
    }

    public SchemaCell(String sName, SchemaCellFormat cellFormat) {
        if(sName == null || sName.isEmpty())
            throw new IllegalArgumentException("SchemaCell.name cannot be null or empty.");
        this.cellFormat = cellFormat;
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
     * @return true if the cell should be ignored while writing.
     */
    public boolean isIgnoreWrite() {
        return ignoreWrite;
    }

    /**
     * @param ignoreWrite If true, this cell will be blank while writing. 
     */
    public void setIgnoreWrite(boolean ignoreWrite) {
        this.ignoreWrite = ignoreWrite;
    }
    
    /**
     * @return the name
     */
    public String getName() {
        return name;
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
     * Creates a cell with a parsed value according to the schema specification for this cell. Does
     * not check if cell is mandatory!!
     * 
     * @param sValue
     * @return A new cell of a type according to the schema specified. Returns null if there is no
     *         value.
     * @throws SchemaException If there is an error in the schema
     * @throws java.text.ParseException If the value cannot be parsed according to the format of this cell schema.
     */
    public Cell makeCell(String sValue) throws java.text.ParseException{

        // If the cell is empty, check if default value exists.
        if (sValue.length() <= 0 || (emptyPattern != null && emptyPattern.matcher(sValue).matches())) {
            if (defaultCell != null) {
                return defaultCell.makeCopy(this.name);
            } else {
                return new EmptyCell(this.name, this.cellFormat.getCellType());
            }
        }

        CellType cellType = this.cellFormat.getCellType();
        Cell cell;
        if (getCellFormat().getFormat() != null)
            cell = SchemaCell.makeCell(cellType, this.name, sValue, this.getCellFormat().getFormat());
        else
            cell = SchemaCell.makeCell(cellType, this.name, sValue, getLocale());
        return cell;

    }

    /**
     * If you have created a custom type you need to override this method and handle the custom type.
     * @param cellType
     * @param sName
     * @param sValue
     * @param format
     * @return A cell object that has been parsed from the supplied sValue parameter according to
     *         the supplied format.
     * @throws java.lang.Comparable, java.lang.Comparable, java.lang.Comparablexception
     * @throws java.text.ParseException
     * @throws SchemaException
     */
    public static Cell makeCell(CellType cellType, String sName, String sValue, java.text.Format format)
            throws java.text.ParseException {
        return cellType.makeCell(sName, sValue, format);
    }

    /**
     * @param cellType
     * @param sName
     * @param sValue
     * @param locale
     * @return A cell object that has been parsed from the supplied sValue parameter according to
     *         the default format for supplied type and locale.
     * @throws java.lang.Comparable, java.lang.Comparable, java.lang.Comparablexception
     * @throws java.text.ParseException
     * @throws SchemaException
     */
    public static Cell makeCell(CellType cellType, String sName, String sValue, Locale locale)
            throws java.text.ParseException, SchemaException {
        return cellType.makeCell(sName, sValue, locale);
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

    public SchemaCell clone() {
        try {
            return (SchemaCell) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e);
        }
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
        if (this.defaultValue != null) {
            sb.append(" defaultValue=");
            sb.append(this.defaultValue);
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
     * Validates that the default value is within the valid range. Throws a SchemaException if value is
     * not within borders.
     * 
     * @throws SchemaException
     */
    protected void validateDefaultValueRange() throws SchemaException {
        if (this.minValue != null && defaultCell.compareValueTo(this.minValue) < 0)
            throw new SchemaException("The value is below minimum range limit.");
        else if (this.maxValue != null && defaultCell.compareValueTo(this.maxValue) > 0)
            throw new SchemaException("The value is above maximum range limit.");

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

    /**
     * @return The default cell. The value of the default cell will be used if input/output is
     *         missing.
     */
    public Cell getDefaultCell() {
        return defaultCell;
    }

    /**
     * @param defaultCell
     *            The default cell. The value of the default cell will be used if input/output is
     *            missing. The name of the cell has no importance, it will not be used.
     */
    public void setDefaultCell(Cell defaultCell) {
        this.defaultCell = defaultCell.makeCopy(this.name);
        this.defaultValue = getDefaultCell().getStringValue(getCellFormat().getFormat());
    }

    /**
     * Sets the default value as a string. The default value have to be parsable according to the
     * schema format. As long as it is parsable, it will be used exactly as is even though it might
     * not look the same as if it was formatted from a value.
     * 
     * @param sDefaultValue
     *            The default value formatted according to this schema. Will be used if input/output
     *            is missing for this cell.
     * @throws java.text.ParseException When the supplied value cannot be parsed according to this cell schema.
     */
    public void setDefaultValue(String sDefaultValue) throws java.text.ParseException {
        this.defaultCell = makeCell(sDefaultValue);
        validateDefaultValueRange();

        this.defaultValue = sDefaultValue;
    }

    /**
     * @return The default value formatted according to this schema. Will be used if input/output is
     *         missing.
     */
    public String getDefaultValue() {
        return defaultValue;
    }

    /**
     * @return The default value if it is not null or empty string otherwise.
     */
    private String getDefaultValueOrEmpty() {
        return defaultValue == null ? EMPTY_STRING : defaultValue;
    }



    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof SchemaCell))
            return false;

        SchemaCell that = (SchemaCell) o;

        return name.equals(that.name);

    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    /**
     * @return true if there is a default value, false otherwise.
     */
    public boolean isDefaultValue() {
        return this.defaultCell != null;
    }

    /**
     * @return the emptyPattern
     */
    public Pattern getEmptyPattern() {
        return emptyPattern;
    }

    /**
     * The empty pattern can be used to ignore cells that contains a text that should be regared as empty. For instance
     * the cell might contain the text NULL to indicate that there is no value even though this is a date or a numeric field.
     * 
     * @param emptyPattern the regexp pattern that will be matched against to determine if this cell is empty
     */
    public void setEmptyPattern(Pattern emptyPattern) {
        this.emptyPattern = emptyPattern;
    }

    /**
     * The empty pattern can be used to ignore cells that contains a text that should be regared as empty. For instance
     * the cell might contain the text NULL to indicate that there is no value even though this is a date or a numeric field.
     * 
     * @param emptyPattern
     *            the regexp pattern string that will be matched against to determine if this cell is empty
     */
    public void setEmptyPattern(String emptyPattern) {
        if(emptyPattern != null && !emptyPattern.isEmpty())
            this.emptyPattern = Pattern.compile(emptyPattern);
    }

    public CellValueCondition getLineCondition() {
        return lineCondition;
    }

    public void setLineCondition(CellValueCondition lineCondition) {
        this.lineCondition = lineCondition;
    }

    public boolean hasLineCondition(){
        return this.lineCondition != null;
    }
}
