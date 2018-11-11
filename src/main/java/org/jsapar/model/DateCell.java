package org.jsapar.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * {@link Cell} implementation carrying a date value of a cell.
 * 
 */
public class DateCell extends AbstractCell<Date> implements ComparableCell<Date> {

    public static final SimpleDateFormat ISO_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS ZZZ");

    /**
     * 
     */
    private static final long serialVersionUID = -4950587241666521775L;

    public DateCell(String sName, Date value) {
        super(sName, value, CellType.DATE);
    }

    /**
     * Creates a {@link DateCell} based on a ISO date string.
     * @param sName The name of the cell to create.
     * @param isoDate The iso date of pattern: "yyyy-MM-dd HH:mm:ss.SSS ZZZ"
     * @throws ParseException In case the date string does not follow the iso pattern
     */
    public DateCell(String sName, String isoDate) throws ParseException {
        super(sName, ISO_DATE_FORMAT.parse(isoDate), CellType.DATE);
    }

    public static Cell emptyOf(String name) {
        return new EmptyCell(name, CellType.DATE);
    }

    /**
     * Since java standard string format is quite useless, we use the iso standard format instead.
     * @return A date formatted according to iso standard by the pattern: "yyyy-MM-dd HH:mm:ss.SSS ZZZ"
     */
    @Override
    public String getStringValue() {
        return ISO_DATE_FORMAT.format(getValue());
    }
}
