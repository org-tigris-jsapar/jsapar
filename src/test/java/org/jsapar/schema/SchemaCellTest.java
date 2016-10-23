/**
 * 
 */
package org.jsapar.schema;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author stejon0
 * 
 */
public class SchemaCellTest {

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }


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
     * @throws CloneNotSupportedException
     */
    @Test
    public void testClone() throws CloneNotSupportedException {
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

    /**
     * To be able to have a specific SchemaCell to test.
     * 
     * @author stejon0
     * 
     */
    private class TestSchemaCell extends SchemaCell {


        public TestSchemaCell(String name) {
            super(name);
        }

    }

}
