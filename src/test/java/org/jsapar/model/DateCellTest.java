/** 
 * Copyright: Jonas Stenberg
 */
package org.jsapar.model;

import org.junit.Before;
import org.junit.Test;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import static org.junit.Assert.assertEquals;

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


}
