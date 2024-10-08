package org.jsapar.model;

import org.junit.Before;
import org.junit.Test;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

/**
 * @author Jonas Stenberg
 *
 */
public class DateCellTest {
    Date now;
    Date aDate;

    @Before
    public void setUp() {
        now = new Date();

        java.util.Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Sweden"));
        calendar.set(2007, Calendar.SEPTEMBER, 1, 12, 5);
        calendar.set(Calendar.SECOND, 30);
        calendar.set(Calendar.MILLISECOND, 555);
        aDate = calendar.getTime();
    }

    /**
     * Test method for
     * {@link DateCell#DateCell(java.lang.String, java.util.Date)}
     * .
     */
    @Test
    public final void testDateCell() {
        DateCell cell = new DateCell("Name", now);
        assertEquals("Name", cell.getName());
        assertEquals(now, cell.getValue());
    }

    @Test
    public final void testDateCellIso() throws ParseException {
        DateCell cell = new DateCell("Name", "2018-11-11 12:53:45.123 +0200");
        assertEquals("Name", cell.getName());
        assertEquals(DateCell.ISO_DATE_FORMAT.parse("2018-11-11 12:53:45.123 +0200"), cell.getValue());
    }
    @Test
    public void testGetSetDateValue() {
        Date date = new Date();
        DateCell cell = new DateCell("Name", date);
        assertEquals(date, cell.getValue());

    }

    @Test
    public void testCloneWithName() {
        DateCell cell = new DateCell("Name", aDate);
        Cell<Date> clone = cell.cloneWithName("NewName");
        assertEquals("NewName", clone.getName());
        assertEquals(aDate, clone.getValue());
        assertNotSame(cell.getValue(), clone.getValue());
    }
}
