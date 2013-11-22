/**
 * 
 */
package org.jsapar.schema;

import junit.framework.Assert;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author stejon0
 *
 */
public class FixedWidthCellPositionsTest {

    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    /**
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

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
     * Test method for {@link org.jsapar.schema.FixedWidthCellPositions#increment(org.jsapar.schema.FixedWidthSchemaCell)}.
     */
    @Test
    public void testIncrement() {
        FixedWidthSchemaCell cell = new FixedWidthSchemaCell("test", 17);
        FixedWidthCellPositions pos = new FixedWidthCellPositions();
        pos.increment(cell);
        Assert.assertEquals(1, pos.getFirst());
        Assert.assertEquals(17, pos.getLast());

        pos.increment(cell);
        Assert.assertEquals(18, pos.getFirst());
        Assert.assertEquals(34, pos.getLast());
    }

}
