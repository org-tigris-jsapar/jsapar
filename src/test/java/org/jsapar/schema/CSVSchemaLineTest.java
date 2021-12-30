package org.jsapar.schema;

import org.junit.Test;

import static org.junit.Assert.*;

public class CSVSchemaLineTest  {

    @Test
    @Deprecated
    public final void testCSVSchemaLine()  {
        CsvSchemaLine schemaLine = new CsvSchemaLine();
        assertEquals("", schemaLine.getLineType());
    }

    @Test
    public final void testCSVSchemaLine_String()  {
        CsvSchemaLine schemaLine = CsvSchemaLine.builder("LineType").build();
        assertEquals("LineType", schemaLine.getLineType());
    }
    
    

    @Test
    public void testGetSchemaCell(){
        CsvSchemaLine schemaLine = CsvSchemaLine.builder("A")
                .withCellSeparator(";-)")
                .withCells("First Name", "Last Name")
                .build();
        
        assertTrue(schemaLine.findSchemaCell("Does not exist").isEmpty());
        assertTrue(schemaLine.findSchemaCell("First Name").isPresent());
        
    }
}
