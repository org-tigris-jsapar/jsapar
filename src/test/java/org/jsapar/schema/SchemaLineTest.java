package org.jsapar.schema;

import org.jsapar.model.Cell;
import org.jsapar.model.Line;
import org.jsapar.model.StringCell;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.*;

public class SchemaLineTest {

    @Test
    public void testFindCell_empty() {
        SchemaLineMock instance = new SchemaLineMock();
        Line line = new Line();
        SchemaCell schemaCell = new SchemaCell("First") {};
        
        Cell c = instance.doFindCell(line, schemaCell);
        assertNull(c);
    }

    
    @Test
    public void testFindCell_match() {
        SchemaLineMock instance = new SchemaLineMock();
        Line line = new Line();
        SchemaCell schemaCell = new SchemaCell("First") {};
        
        Cell first = new StringCell("First", "one");
        line.addCell(first);
        Cell c = instance.doFindCell(line, schemaCell);
        assertEquals(first, c);

        c = instance.doFindCell(line, schemaCell);
        assertEquals(first, c);
    }


    @Test
    @Ignore
    public void testFindCell_byIndex() {
        SchemaLineMock instance = new SchemaLineMock();
        Line line = new Line();
        SchemaCell schemaCell = new SchemaCell("First") {};
        
        Cell first = new StringCell("First", "one");
        line.addCell(first);

        Cell second = new StringCell("test", "two");
        line.addCell(second);

        schemaCell = new SchemaCell("two") {};
        
        Cell c = instance.doFindCell(line, schemaCell);
        assertEquals(second, c);
    }


    @Test
    public void testFindCell_notNamedExtra_namedOnly() {
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
        
        Cell c = instance.doFindCell(line, schemaCell);
        assertEquals(second, c);
    }    
    
    @Test
    public void testFindCell_notFound() {
        SchemaLineMock instance = new SchemaLineMock();
        Line line = new Line();
        SchemaCell schemaCell = new SchemaCell("First") {};
        
        Cell first = new StringCell("First", "one");
        line.addCell(first);

        // Cell wiht no name
        Cell second = new StringCell("test2","two");
        line.addCell(second);

        schemaCell = new SchemaCell("Third") {};
        
        Cell c = instance.doFindCell(line, schemaCell);
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

        
        public Cell doFindCell(Line line, SchemaCell schemaCell){
            return findCell(line, schemaCell);
        }
        
    }

}
