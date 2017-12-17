package org.jsapar.parse.bean;

import org.jsapar.TstPerson;
import org.jsapar.TstPostAddress;
import org.jsapar.model.Cell;
import org.jsapar.model.CellType;
import org.jsapar.model.Line;
import org.jsapar.model.LineUtils;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.*;

public class BeanBackedLineTest {

    @Test
    public void testGetCell() {
        TstPerson bean = new TstPerson("Kalle", "Anka", (short)100, 4711, new Date(), 2, 'A');
        bean.setAddress(new TstPostAddress("Kvackgatan", "Ankeborg"));
        Line line = new BeanBackedLine<>(bean, event -> {throw new AssertionError();});
        assertEquals(TstPerson.class.getName(), line.getLineType());
        Cell c = line.getCell("firstName").orElseThrow(AssertionError::new);
        assertNotNull(c);
        assertEquals("firstName", c.getName());
        assertEquals("Kalle", c.getValue());
        bean.setFirstName("Karl");
        assertEquals("Kalle", c.getValue()); // Cached value is used.
        assertEquals(CellType.STRING, c.getCellType());
        assertEquals("Anka", LineUtils.getStringCellValue(line, "lastName"));

        assertEquals("Kvackgatan", LineUtils.getStringCellValue(line, "address.street"));
    }
}