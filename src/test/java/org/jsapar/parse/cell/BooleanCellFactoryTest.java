package org.jsapar.parse.cell;

import org.jsapar.model.BooleanCell;
import org.jsapar.text.Format;
import org.junit.Test;

import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class BooleanCellFactoryTest {
    @Test
    public void makeCell() throws Exception {
        BooleanCellFactory instance = new BooleanCellFactory();
        assertEquals(new BooleanCell("nnn", Boolean.TRUE), instance.makeCell("nnn", "true", null));
        assertEquals(new BooleanCell("nnn", Boolean.TRUE), instance.makeCell("nnn", "J", Format.ofBooleanInstance("J", "N", true)));
        assertEquals(new BooleanCell("nnn", Boolean.FALSE), instance.makeCell("nnn", "false", null));
        assertEquals(new BooleanCell("nnn", Boolean.FALSE), instance.makeCell("nnn", "N", Format.ofBooleanInstance("J", "N", true)));
    }

    @Test
    public void makeFormat() throws Exception {
        BooleanCellFactory instance = new BooleanCellFactory();
        Format f = instance.makeFormat(Locale.getDefault());
        assertNotNull(f);
        assertEquals("true", f.format(true));
        assertEquals("false", f.format(false));
        assertEquals(true, f.parse("yes"));
        assertEquals(true, f.parse("Y"));
        assertEquals(false, f.parse("0"));
        assertEquals(false, f.parse("false"));
    }

    @Test
    public void makeFormat_pattern() throws Exception {
        BooleanCellFactory instance = new BooleanCellFactory();
        Format f = instance.makeFormat(Locale.getDefault(), "J|JA|1;N|NEJ|0");
        assertNotNull(f);
        assertEquals("J", f.format(true));
        assertEquals("N", f.format(false));
        assertEquals(true, f.parse("ja"));
        assertEquals(true, f.parse("J"));
        assertEquals(false, f.parse("NEJ"));
        assertEquals(false, f.parse("n"));
    }

}