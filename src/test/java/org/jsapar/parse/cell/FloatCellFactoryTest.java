package org.jsapar.parse.cell;

import org.jsapar.model.FloatCell;
import org.jsapar.text.Format;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Locale;

public class FloatCellFactoryTest {
    final FloatCellFactory cellFactory = new FloatCellFactory();

    @Test
    public void testSetValueStringLocale() throws ParseException {
        FloatCell cell = (FloatCell) cellFactory.makeCell("test", "3.141,59", cellFactory.makeFormat(Locale.GERMANY));

        Assert.assertEquals(3141.59, cell.getValue().doubleValue(), 0.001);
    }

    @Test
    public void testSetValueStringFormat() throws ParseException {
        Format<BigDecimal> format = Format.ofDecimalInstance("#,###.##", Locale.GERMANY);
        FloatCell cell = (FloatCell) cellFactory.makeCell("test", "3.141,59", format);

        Assert.assertEquals(3141.59, cell.getValue().doubleValue(), 0.001);

        cell = (FloatCell) cellFactory.makeCell("test", "3141,59", format);
        Assert.assertEquals(3141.59, cell.getValue().doubleValue(), 0.001);

    }

}