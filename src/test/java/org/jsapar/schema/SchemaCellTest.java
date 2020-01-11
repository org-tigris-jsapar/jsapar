package org.jsapar.schema;

import org.jsapar.model.BigDecimalCell;
import org.jsapar.model.CellType;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class SchemaCellTest {


    @Test
    public void testSchemaCellString() {
        SchemaCell schemaCell = StringSchemaCell.builder("test").build();
        assertNotNull(schemaCell);
        assertEquals("test", schemaCell.getName());
    }


    /**
     * Test method for {@link org.jsapar.schema.SchemaCell#clone()}.
     * 
     */
    @Test
    public void testClone() {
        SchemaCell schemaCell = StringSchemaCell.builder("test").build();
        SchemaCell clone = schemaCell.clone();
        assertNotNull(clone);
        assertEquals(schemaCell.getName(), clone.getName());
    }

    /**
     * Test method for {@link org.jsapar.schema.SchemaCell#toString()}.
     */
    @Test
    public void testToString() {
        SchemaCell schemaCell = StringSchemaCell.builder("test").build();
        String s = schemaCell.toString();
        assertNotNull(s);
    }

    @Test
    public void withMinValue() {
        SchemaCell schemaCell = StringSchemaCell.builder("test")
                .withLocale("sv","SE")
                .withType(CellType.DECIMAL)
                .withPattern("#,###.0")
                .withMinValue("123,34")
                .build();
        assertEquals(new BigDecimal("123.34"), ((BigDecimalCell)schemaCell.getMinValue()).getValue());
    }

}
