package org.jsapar.schema;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.StringWriter;

import org.jsapar.JSaParException;
import org.jsapar.model.Line;
import org.jsapar.model.StringCell;
import org.junit.Test;

public class CSVSchemaLineTest  {

    boolean foundError=false;
    
    @Test
    public final void testCSVSchemaLine()  {
        CsvSchemaLine schemaLine = new CsvSchemaLine();
        assertEquals("", schemaLine.getLineType());
        assertEquals("", schemaLine.getLineTypeControlValue());
    }

    @Test
    public final void testCSVSchemaLine_String()  {
        CsvSchemaLine schemaLine = new CsvSchemaLine("LineType");
        assertEquals("LineType", schemaLine.getLineType());
        assertEquals("LineType", schemaLine.getLineTypeControlValue());
    }
    
    

    @Test
    public void testGetSchemaCell(){
        CsvSchemaLine schemaLine = new CsvSchemaLine(1);
        schemaLine.setCellSeparator(";-)");
        CsvSchemaCell cell1 = new CsvSchemaCell("First Name");
        schemaLine.addSchemaCell(cell1);
        schemaLine.addSchemaCell(new CsvSchemaCell("Last Name"));
        
        assertNull(schemaLine.getSchemaCell("Does not exist"));
        assertSame(cell1, schemaLine.getSchemaCell("First Name"));
        
    }
}
