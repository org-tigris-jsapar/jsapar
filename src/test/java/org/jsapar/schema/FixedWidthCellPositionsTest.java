package org.jsapar.schema;

import org.junit.*;

import static org.junit.Assert.assertEquals;

public class FixedWidthCellPositionsTest {

    /**
     * Test method for {@link org.jsapar.schema.FixedWidthCellPositions#increment(org.jsapar.schema.FixedWidthSchemaCell)}.
     */
    @Test
    public void testIncrement() {
        FixedWidthSchemaCell cell = new FixedWidthSchemaCell("test", 17);
        FixedWidthCellPositions pos = new FixedWidthCellPositions();
        pos.increment(cell);
        assertEquals(1, pos.getFirst());
        assertEquals(17, pos.getLast());

        pos.increment(cell);
        assertEquals(18, pos.getFirst());
        assertEquals(34, pos.getLast());
    }

}
