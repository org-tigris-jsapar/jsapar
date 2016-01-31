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
        
        Cell c = instance.doFindCell(line, schemaCell, 0, false);
        assertNull(c);
    }

    
    @Test
    public void testFindCell_match() throws JSaParException {
        SchemaLineMock instance = new SchemaLineMock();
        Line line = new Line();
        SchemaCell schemaCell = new SchemaCell("First") {};
        
        Cell first = new StringCell("First", "one");
        line.addCell(first);
        Cell c = instance.doFindCell(line, schemaCell, 0, false);
        assertEquals(first, c);

        c = instance.doFindCell(line, schemaCell, 3, false);
        assertEquals(first, c);
    }


    @Ignore
    @Test
    public void testFindCell_byIndex() throws JSaParException {
        SchemaLineMock instance = new SchemaLineMock();
        Line line = new Line();
        SchemaCell schemaCell = new SchemaCell("First") {};
        
        Cell first = new StringCell("First", "one");
        line.addCell(first);

        Cell second = new StringCell("test", "two");
        line.addCell(second);

        schemaCell = new SchemaCell("two") {};
        
        Cell c = instance.doFindCell(line, schemaCell, 1, false);
        assertEquals(second, c);
    }

    @Ignore
    @Test
    public void testFindCell_notNamed() throws JSaParException {
        SchemaLineMock instance = new SchemaLineMock();
        Line line = new Line();
        SchemaCell schemaCell = new SchemaCell("First") {};
        
        Cell first = new StringCell("test1","one");
        line.addCell(first);

        // Cell wiht no name
        Cell second = new StringCell("test2","two");
        line.addCell(second);

        schemaCell = new SchemaCell("Third") {};
        
        Cell c = instance.doFindCell(line, schemaCell, 0, false);
        assertEquals(first, c);
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
        
        Cell c = instance.doFindCell(line, schemaCell, 2, true);
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
        
        Cell c = instance.doFindCell(line, schemaCell, 1, true);
        assertNull(c);
    }    
    
    
    
    public class SchemaLineMock extends SchemaLine{

        @Override
        public SchemaCell getSchemaCell(String cellName) {
            fail("Not yet implemented");
            return null;
        }

        @Override
        public void output(Line line, Writer writer) throws IOException, JSaParException {
            fail("Not yet implemented");
            
        }

        @Override
        public int getSchemaCellsCount() {
            fail("Not yet implemented");
            return 0;
        }

        @Override
        public SchemaCell getSchemaCellAt(int index) {
            fail("Not yet implemented");
            return null;
        }
        
        public Cell doFindCell(Line line, SchemaCell schemaCell, int nSchemaCellIndex, boolean namedCellsOnly){
            return findCell(line, schemaCell, nSchemaCellIndex, namedCellsOnly);
        }
        
    }

}
