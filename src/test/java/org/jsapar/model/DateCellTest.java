/** 
 * Copyright: Jonas Stenberg
 */
package org.jsapar.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.jsapar.model.Cell;
import org.jsapar.model.DateCell;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Jonas Stenberg
 * 
 */
public class DateCellTest {
    DateCell now;
    DateCell aDate;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
	now = new DateCell("Now", new java.util.Date());

	java.util.Calendar calendar = Calendar.getInstance(TimeZone
		.getTimeZone("Sweden"));
	calendar.set(2007, 9, 01, 12, 5);
	calendar.set(Calendar.SECOND, 30);
	calendar.set(Calendar.MILLISECOND, 555);
	aDate = new DateCell("A date", calendar.getTime());
    }

    /**
     * Test method for {@link DateCell#getXmlValue()}.
     */
    @Test
    public final void testGetXmlValue() {
	assertEquals("2007-10-01T14:05:30.555+02:00", aDate.getXmlValue());
    }


    /**
     * Test method for
     * {@link DateCell#DateCell(java.lang.String, java.util.Date)}
     * .
     */
    @Test
    public final void testDateCellStringDate() {
	Date date = new Date();
	DateCell cell = new DateCell("Name", date);
	assertEquals("Name", cell.getName());
	assertEquals(date, cell.getValue());
    }

    /**
     * Test method for
     * {@link DateCell#DateCell(java.lang.String, java.lang.String, java.text.Format)}
     * .
     * 
     * @throws ParseException
     */
    @Test
    public final void testDateCellStringStringFormat() throws ParseException {
	DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	DateCell cell = new DateCell("Name", "2007-10-01 14:13", format);
	assertEquals("Name", cell.getName());
	assertEquals(format.parse("2007-10-01 14:13"), cell.getValue());
    }

    /**
     * Test method for
     * {@link DateCell#getStringValue(java.text.Format)}.
     * 
     * @throws ParseException
     */
    @Test
    public final void testGetStringValueFormat() throws ParseException {
	DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	DateCell cell = new DateCell("Name", "2007-10-01 14:13", format);

	assertEquals("2007-10-01 14:13", cell.getStringValue(format));
    }

    /**
     * Test method for {@link Cell#getStringValue()}.
     * 
     * @throws ParseException
     */
    @Test
    public final void testGetStringValue() throws ParseException {
	DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	DateCell cell = new DateCell("Name", "2007-10-01 14:13", format);

	assertEquals("Mon Oct 01 14:13:00 CEST 2007", cell.getStringValue());
    }

}
