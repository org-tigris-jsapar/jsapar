package org.jsapar.schema;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.Writer;

import org.jsapar.model.Cell;
import org.jsapar.JSaParException;
import org.jsapar.model.Line;
import org.jsapar.model.StringCell;
import org.junit.Ignore;
import org.junit.Test;

public class SchemaLineTest {

    @Test
    public void testFindCell_empty() throws JSaParException {
        SchemaLineMock instance = new SchemaLineMock();
        Line line = new Line();
        SchemaCell schemaCell = new SchemaCell("First") {};
        
        Cell c = instance.doFindCell(line, schemaCell, 0);
        assertNull(c);
    }

    
    @Test
    public void testFindCell_match() throws JSaParException {
        SchemaLineMock instance = new SchemaLineMock();
        Line line = new Line();
        SchemaCell schemaCell = new SchemaCell("First") {};
        
        Cell first = new StringCell("First", "one");
        line.addCell(first);
        Cell c = instance.doFindCell(line, schemaCell, 0);
        assertEquals(first, c);

        c = instance.doFindCell(line, schemaCell, 3);
        assertEquals(first, c);
    }


    @Test
    @Ignore
    public void testFindCell_byIndex() throws JSaParException {
        SchemaLineMock instance = new SchemaLineMock();
        Line line = new Line();
        SchemaCell schemaCell = new SchemaCell("First") {};
        
        Cell first = new StringCell("First", "one");
        line.addCell(first);

        Cell second = new StringCell("test", "two");
        line.addCell(second);

        schemaCell = new SchemaCell("two") {};
        
        Cell c = instance.doFindCell(line, schemaCell, 1);
        assertEquals(second, c);
    }


    @Test
    public void testFindCell_notNamedExtra_namedOnly() throws JSaParException {
        SchemaLineMock instance = new SchemaLineMock();
        Line line = new Line();
        
        Cell first = new StringCell("First", "ett");
        line.addCell(first);

        Cell second = new StringCell("Second", "två");
        line.addCell(second);

        // Cell with no name
        Cell third = new StringCell("test3","tre");
        line.addCell(third);
        
        SchemaCell schemaCell = new SchemaCell("Second") {};
        
        Cell c = instance.doFindCell(line, schemaCell, 2);
        assertEquals(second, c);
    }    
    
    @Test
    public void testFindCell_notFound() throws JSaParException {
        SchemaLineMock instance = new SchemaLineMock();
        Line line = new Line();
        SchemaCell schemaCell = new SchemaCell("First") {};
        
        Cell first = new StringCell("First", "one");
        line.addCell(first);

        // Cell wiht no name
        Cell second = new StringCell("test2","two");
        line.addCell(second);

        schemaCell = new SchemaCell("Third") {};
        
        Cell c = instance.doFindCell(line, schemaCell, 1);
        assertNull(c);
    }    
    
    
    
    public class SchemaLineMock extends SchemaLine{

        @Override
        public SchemaCell getSchemaCell(String cellName) {
            fail("Not yet implemented");
            return null;
        }


        @Override
        public int getSchemaCellsCount() {
            fail("Not yet implemented");
            return 0;
        }

        
        public Cell doFindCell(Line line, SchemaCell schemaCell, int nSchemaCellIndex){
            return findCell(line, schemaCell, nSchemaCellIndex);
        }
        
    }

}
