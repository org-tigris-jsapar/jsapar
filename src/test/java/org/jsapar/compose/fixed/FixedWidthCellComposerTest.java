package org.jsapar.compose.fixed;

import org.jsapar.error.JSaParException;
import org.jsapar.model.Cell;
import org.jsapar.model.StringCell;
import org.jsapar.schema.FixedWidthSchemaCell;
import org.junit.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import static org.junit.Assert.assertEquals;

public class FixedWidthCellComposerTest {


    @Test
    public final void testOutput_Center() throws IOException, JSaParException {
        FixedWidthSchemaCell schemaCell = FixedWidthSchemaCell.builder("First name", 11)
                .withAlignment(FixedWidthSchemaCell.Alignment.CENTER)
                .build();

        Writer writer = new StringWriter();
        Cell<String> cell = new StringCell("First name","Jonas");
        FixedWidthCellComposer composer = new FixedWidthCellComposer(schemaCell);
        composer.compose(writer, cell);

        assertEquals("   Jonas   ", writer.toString());
    }

    @Test
    public final void testOutput_Center_overflow_even() throws IOException, JSaParException {
        FixedWidthSchemaCell schemaCell = FixedWidthSchemaCell.builder("First name", 7)
                .withAlignment(FixedWidthSchemaCell.Alignment.CENTER)
                .build();

        Writer writer = new StringWriter();
        Cell<String> cell = new StringCell("First name","000Jonas000");
        FixedWidthCellComposer composer = new FixedWidthCellComposer(schemaCell);
        composer.compose(writer, cell);

        assertEquals("0Jonas0", writer.toString());
    }

    /*
     * Overflow is an odd number
     */
    @Test
    public final void testOutput_Center_overflow_odd() throws IOException, JSaParException {
        FixedWidthSchemaCell schemaCell = FixedWidthSchemaCell.builder("First name", 6)
                .withAlignment(FixedWidthSchemaCell.Alignment.CENTER)
                .build();

        Writer writer = new StringWriter();
        Cell<String> cell = new StringCell("First name","000Jonas000");
        FixedWidthCellComposer composer = new FixedWidthCellComposer(schemaCell);
        composer.compose(writer, cell);

        assertEquals("0Jonas", writer.toString());
    }

    @Test
    public final void testOutput_Left() throws IOException, JSaParException {
        FixedWidthSchemaCell schemaCell = FixedWidthSchemaCell.builder("First name", 11)
                .withAlignment(FixedWidthSchemaCell.Alignment.LEFT)
                .build();

        Writer writer = new StringWriter();
        Cell<String> cell = new StringCell("First name","Jonas");
        FixedWidthCellComposer composer = new FixedWidthCellComposer(schemaCell);
        composer.compose(writer, cell);

        assertEquals("Jonas      ", writer.toString());
    }

    @Test
    public final void testOutput_Exact() throws IOException, JSaParException {
        FixedWidthSchemaCell schemaCell = FixedWidthSchemaCell.builder("First name", 5)
                .build();

        Writer writer = new StringWriter();
        Cell<String> cell = new StringCell("First name","Jonas");
        FixedWidthCellComposer composer = new FixedWidthCellComposer(schemaCell);
        composer.compose(writer, cell);

        assertEquals("Jonas", writer.toString());
    }

    @Test
    public final void testOutput_Rigth() throws IOException, JSaParException {
        FixedWidthSchemaCell schemaCell = FixedWidthSchemaCell.builder("First name", 11)
                .withPadCharacter('*')
                .withAlignment(FixedWidthSchemaCell.Alignment.RIGHT)
                .build();

        Writer writer = new StringWriter();
        Cell<String> cell = new StringCell("First name","Jonas");
        FixedWidthCellComposer composer = new FixedWidthCellComposer(schemaCell);
        composer.compose(writer, cell);

        assertEquals("******Jonas", writer.toString());
    }

    @Test
    public final void testOutput_Rigth_overflow() throws IOException, JSaParException {
        FixedWidthSchemaCell schemaCell = FixedWidthSchemaCell.builder("First name", 6)
                .withAlignment(FixedWidthSchemaCell.Alignment.RIGHT)
                .withPadCharacter('*')
                .build();

        Writer writer = new StringWriter();
        Cell<String> cell = new StringCell("First name","0000Jonas");
        FixedWidthCellComposer composer = new FixedWidthCellComposer(schemaCell);
        composer.compose(writer, cell);

        assertEquals("0Jonas", writer.toString());
    }

    @Test
    public final void testOutput_Default() throws IOException, JSaParException {
        FixedWidthSchemaCell schemaCell = FixedWidthSchemaCell.builder("Size", 11)
                .withDefaultValue("10")
                .build();

        Writer writer = new StringWriter();
        FixedWidthCellComposer composer = new FixedWidthCellComposer(schemaCell);
        composer.compose(writer, composer.makeEmptyCell());

        assertEquals("10         ", writer.toString());
    }


}