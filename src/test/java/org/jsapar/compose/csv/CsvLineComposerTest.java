package org.jsapar.compose.csv;

import org.jsapar.error.JSaParException;
import org.jsapar.model.CellType;
import org.jsapar.model.EmptyCell;
import org.jsapar.model.Line;
import org.jsapar.model.StringCell;
import org.jsapar.schema.CsvSchemaLine;
import org.jsapar.schema.QuoteSyntax;
import org.junit.Test;

import java.io.StringWriter;

import static org.junit.Assert.assertEquals;

public class CsvLineComposerTest {

    @Test
    public void testOutput() throws JSaParException {

        CsvSchemaLine schemaLine = CsvSchemaLine.builder("A")
                .withCellSeparator(";-)")
                .withCells("First Name", "Last Name")
                .build();

        Line line = new Line("A");
        line.addCell(new StringCell("First Name", "Jonas"));
        line.addCell(new StringCell("Last Name", "Stenberg"));
        StringWriter writer = new StringWriter();

        CsvLineComposer lineComposer = new CsvLineComposer(writer, schemaLine, "\n", QuoteSyntax.FIRST_LAST);
        lineComposer.compose(line);

        assertEquals("Jonas;-)Stenberg", writer.toString());

    }

    @Test
    public void testOutput_ignoreWrite() throws JSaParException {
        CsvSchemaLine schemaLine = CsvSchemaLine.builder("A")
                .withCellSeparator(";-)")
                .withCell("First Name", c->c.withIgnoreWrite(true))
                .withCell( "Last Name")
                .build();

        Line line = new Line("A");
        line.addCell(new StringCell("First Name", "Jonas"));
        line.addCell(new StringCell("Last Name", "Stenberg"));
        StringWriter writer = new StringWriter();

        CsvLineComposer lineComposer = new CsvLineComposer(writer, schemaLine, "\n", QuoteSyntax.FIRST_LAST);
        lineComposer.compose(line);

        assertEquals(";-)Stenberg", writer.toString());

    }

    @Test
    public void testOutput_2byte_unicode() throws JSaParException {
        CsvSchemaLine schemaLine = CsvSchemaLine.builder("A")
                .withCellSeparator("\uFFD0")
                .withCells("First Name", "Last Name")
                .build();

        Line line = new Line("A");
        line.addCell(new StringCell("First Name", "Jonas"));
        line.addCell(new StringCell("Last Name", "Stenberg"));
        StringWriter writer = new StringWriter();

        CsvLineComposer lineComposer = new CsvLineComposer(writer, schemaLine, "\n", QuoteSyntax.FIRST_LAST);
        lineComposer.compose(line);

        assertEquals("Jonas\uFFD0Stenberg", writer.toString());
    }


    @Test
    public void testOutput_not_found_in_line() throws JSaParException {
        CsvSchemaLine schemaLine = CsvSchemaLine.builder("A")
                .withCellSeparator(";-)")
                .withCells("First Name", "Last Name", "Shoe size")
                .build();

        Line line = new Line("A");
        line.addCell(new StringCell("First Name", "Jonas"));
        line.addCell(new StringCell("Last Name", "Stenberg"));
        StringWriter writer = new StringWriter();

        CsvLineComposer lineComposer = new CsvLineComposer(writer, schemaLine, "\n", QuoteSyntax.FIRST_LAST);
        lineComposer.compose(line);

        assertEquals("Jonas;-)Stenberg;-)", writer.toString());

    }

    @Test
    public void testOutput_null_value() throws JSaParException {
        CsvSchemaLine schemaLine = CsvSchemaLine.builder("A")
                .withCellSeparator(";-)")
                .withCells("First Name", "Last Name", "Shoe size")
                .build();

        Line line = new Line("");
        line.addCell(new StringCell("First Name", "Jonas"));
        line.addCell(new StringCell("Last Name", "Stenberg"));
        line.addCell(StringCell.emptyOf("Shoe size"));
        StringWriter writer = new StringWriter();

        CsvLineComposer lineComposer = new CsvLineComposer(writer, schemaLine, "\n", QuoteSyntax.FIRST_LAST);
        lineComposer.compose(line);

        assertEquals("Jonas;-)Stenberg;-)", writer.toString());

    }

    @Test
    public void testOutput_reorder() throws JSaParException {
        CsvSchemaLine schemaLine = CsvSchemaLine.builder("A")
                .withCellSeparator(";")
                .withCells("First Name", "Last Name")
                .build();

        Line line = new Line("");
        line.addCell(new StringCell("Last Name", "Stenberg"));
        line.addCell(new StringCell("First Name", "Jonas"));
        StringWriter writer = new StringWriter();

        CsvLineComposer lineComposer = new CsvLineComposer(writer, schemaLine, "\n", QuoteSyntax.FIRST_LAST);
        lineComposer.compose(line);

        assertEquals("Jonas;Stenberg", writer.toString());

    }

    @Test
    public void testOutput_default() throws JSaParException {
        CsvSchemaLine schemaLine = CsvSchemaLine.builder("A")
                .withCellSeparator(";")
                .withCell("First Name")
                .withCell("Last Name", c->c.withDefaultValue("Svensson"))
                .build();

        Line line = new Line("");
        line.addCell(new StringCell("First Name", "Jonas"));
        StringWriter writer = new StringWriter();

        CsvLineComposer lineComposer = new CsvLineComposer(writer, schemaLine, "\n", QuoteSyntax.FIRST_LAST);
        lineComposer.compose(line);

        assertEquals("Jonas;Svensson", writer.toString());

    }




}