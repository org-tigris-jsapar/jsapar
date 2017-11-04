package org.jsapar.parse.cell;

import org.jsapar.model.Cell;
import org.jsapar.model.DateCell;
import org.junit.Test;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import static org.junit.Assert.assertEquals;

/**
 * Created by stejon0 on 2016-10-23.
 */
public class DateCellFactoryTest {

    DateCellFactory cellFactory = new DateCellFactory();


    @Test
    public final void testDateCellStringStringFormat() throws ParseException {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        DateCell cell = (DateCell) cellFactory.makeCell("Name", "2007-10-01 14:13", format);
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
        DateCell cell = (DateCell) cellFactory.makeCell("Name", "2007-10-01 14:13", format);

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
        DateCell cell = (DateCell) cellFactory.makeCell("Name", "2007-10-01 14:13", format);

        assertEquals("Mon Oct 01 14:13:00 CEST 2007", cell.getStringValue());
    }

}