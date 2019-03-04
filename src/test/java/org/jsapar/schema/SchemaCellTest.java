package org.jsapar.schema;

import org.jsapar.model.BigDecimalCell;
import org.jsapar.model.CellType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author stejon0
 * 
 */
public class SchemaCellTest {


    /**
     * Test method for {@link org.jsapar.schema.SchemaCell#SchemaCell(java.lang.String)} .
     */
    @Test
    public void testSchemaCellString() {
        TestSchemaCell schemaCell = new TestSchemaCell("test");
        assertNotNull(schemaCell);
        assertEquals("test", schemaCell.getName());
    }


    /**
     * Test method for {@link org.jsapar.schema.SchemaCell#clone()}.
     * 
     */
    @Test
    public void testClone() {
        TestSchemaCell schemaCell = new TestSchemaCell("test");
        TestSchemaCell clone = (TestSchemaCell) schemaCell.clone();
        assertNotNull(clone);
        assertEquals(schemaCell.getName(), clone.getName());
    }

    /**
     * Test method for {@link org.jsapar.schema.SchemaCell#toString()}.
     */
    @Test
    public void testToString() {
        TestSchemaCell schemaCell = new TestSchemaCell("test");
        String s = schemaCell.toString();
        assertNotNull(s);
    }

    @Test
    public void setMinValue() throws Exception {
        TestSchemaCell schemaCell = new TestSchemaCell("test");
        schemaCell.setLocale(new Locale("sv", "SE"));
        schemaCell.setCellFormat(CellType.DECIMAL, "#,###.0");
        schemaCell.setMinValue("123,34");
        assertEquals(new BigDecimal("123.34"), ((BigDecimalCell)schemaCell.getMinValue()).getValue());
    }

    /**
     * To be able to have a specific SchemaCell to test.
     * 
     * @author stejon0
     * 
     */
    private class TestSchemaCell extends SchemaCell {
        TestSchemaCell(String name) {
            super(name);
        }

    }

}
