package org.jsapar;

import org.jsapar.error.JSaParException;
import org.jsapar.error.MaxErrorsExceededException;
import org.jsapar.error.RecordingErrorEventListener;
import org.jsapar.error.ThresholdRecordingErrorEventListener;
import org.jsapar.model.CellType;
import org.jsapar.model.StringCell;
import org.jsapar.text.TextParseConfig;
import org.jsapar.schema.*;
import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

public class Text2TextConverterTest {

    public static final String LN = System.getProperty("line.separator");

    @Test
    public void testGetSetConfig(){
        TextParseConfig config = new TextParseConfig();
        Text2TextConverter converter = new Text2TextConverter(makeFixedWidthPersonSchema(), makeCsvPersonSchema(), config);
        assertSame(config, converter.getParseConfig());
        TextParseConfig newConfig = new TextParseConfig();
        converter.setParseConfig(newConfig);
        assertSame(newConfig, converter.getParseConfig());
    }

    @Test
    public void testGetSchema(){
        FixedWidthSchema parseSchema = makeFixedWidthPersonSchema();
        CsvSchema composeSchema = makeCsvPersonSchema();
        Text2TextConverter converter = new Text2TextConverter(parseSchema, composeSchema);
        assertNotNull(converter.getParseConfig());
        assertSame(parseSchema, converter.getParseSchema());
        assertSame(composeSchema, converter.getComposeSchema());
    }

    @Test
    public void testConvert() throws IOException, JSaParException {
        String toParse = "Jonas Stenberg " + LN + "Frida Bergsten ";

        FixedWidthSchema inputSchema = makeFixedWidthPersonSchema();
        CsvSchema outputSchema = makeCsvPersonSchema();

        StringWriter writer = new StringWriter();
        StringReader reader = new StringReader(toParse);
        Text2TextConverter converter = new Text2TextConverter(inputSchema, outputSchema);
        converter.convert(reader, writer);
        reader.close();
        writer.close();
        String sResult = writer.getBuffer().toString();
        String sExpected = "Jonas;Stenberg|Frida;Bergsten";

        assertEquals(sExpected, sResult);

    }

    private CsvSchema makeCsvPersonSchema() {
        CsvSchema outputSchema = new CsvSchema();
        outputSchema.addSchemaLine(makeCsvPersonSchemaLine());
        outputSchema.setLineSeparator("|");
        return outputSchema;
    }

    private CsvSchemaLine makeCsvPersonSchemaLine() {
        return CsvSchemaLine.builder("Person")
                    .withCells("First name","Last name")
                    .withCellSeparator(";")
                    .build();
    }


    @Test
    public void testConvert_error() throws IOException, JSaParException {
        String toParse = "Jonas 41       " + LN + "Frida ERROR    ";
        FixedWidthSchema inputSchema = new FixedWidthSchema();
        FixedWidthSchemaLine inputSchemaLine = new FixedWidthSchemaLine("Person");
        inputSchemaLine.addSchemaCell(FixedWidthSchemaCell.builder("First name", 6).build());
        FixedWidthSchemaCell schemaCell2 = FixedWidthSchemaCell.builder("Shoe size", 9)
                .withCellType(CellType.INTEGER)
                .withAlignment(FixedWidthSchemaCell.Alignment.LEFT)
                .build();
        inputSchemaLine.addSchemaCell(schemaCell2);
        inputSchema.addSchemaLine(inputSchemaLine);

        CsvSchema outputSchema = new CsvSchema();
        CsvSchemaLine outputSchemaLine = new CsvSchemaLine("Person");
        outputSchemaLine.addSchemaCell( CsvSchemaCell.builder("First name").build());
        outputSchemaLine.addSchemaCell( CsvSchemaCell.builder("Shoe size").build());
        outputSchemaLine.setCellSeparator(";");
        outputSchema.addSchemaLine(outputSchemaLine);

        try (StringWriter writer = new StringWriter(); StringReader reader = new StringReader(toParse)) {
            Text2TextConverter converter = new Text2TextConverter(inputSchema, outputSchema);
            RecordingErrorEventListener errorEventListener = new RecordingErrorEventListener();
            converter.setErrorEventListener(errorEventListener);
            converter.convert(reader, writer);
            String sResult = writer.getBuffer().toString();
            String sExpected = "Jonas;41" + LN + "Frida;";

            assertEquals(1, errorEventListener.getErrors().size());
            assertEquals(sExpected, sResult);
        }

    }

    @Test(expected = MaxErrorsExceededException.class)
    public void testConvert_max_error() throws IOException, JSaParException {
        String toParse = "Jonas 41       " + LN + "Frida ERROR    ";
        FixedWidthSchema inputSchema = new FixedWidthSchema();
        FixedWidthSchemaLine inputSchemaLine = new FixedWidthSchemaLine("Person");
        inputSchemaLine.addSchemaCell( FixedWidthSchemaCell.builder("First name", 6).build());
        FixedWidthSchemaCell schemaCell2 =  FixedWidthSchemaCell.builder("Shoe size", 9).withCellType(CellType.INTEGER).build();
        inputSchemaLine.addSchemaCell(schemaCell2);
        inputSchema.addSchemaLine(inputSchemaLine);

        CsvSchema outputSchema = new CsvSchema();
        CsvSchemaLine outputSchemaLine = new CsvSchemaLine("Person");
        outputSchemaLine.addSchemaCell(new CsvSchemaCell("First name"));
        outputSchemaLine.addSchemaCell(new CsvSchemaCell("Shoe size"));
        outputSchemaLine.setCellSeparator(";");
        outputSchema.addSchemaLine(outputSchemaLine);

        try (StringWriter writer = new StringWriter(); StringReader reader = new StringReader(toParse)) {
            Text2TextConverter converter = new Text2TextConverter(inputSchema, outputSchema);
            RecordingErrorEventListener errorEventListener = new ThresholdRecordingErrorEventListener(0);
            converter.setErrorEventListener(errorEventListener);
            converter.convert(reader, writer);
        }

    }

    @Test
    public void testConvert_oneLine() throws IOException, JSaParException {
        String toParse = "Jonas Stenberg ";

        FixedWidthSchema inputSchema =makeFixedWidthPersonSchema();

        CsvSchema outputSchema = makeCsvPersonSchema();

        StringWriter writer = new StringWriter();
        StringReader reader = new StringReader(toParse);
        Text2TextConverter converter = new Text2TextConverter(inputSchema, outputSchema);
        converter.convert(reader, writer);
        String sResult = writer.getBuffer().toString();
        String sExpected = "Jonas;Stenberg";

        assertEquals(sExpected, sResult);

    }

    @Test
    public void testConvert_twoKindOfLines() throws IOException, JSaParException {
        String toParse = "This file contains names" + LN + "Jonas Stenberg "
                + LN + "Frida Bergsten ";
        FixedWidthSchema inputSchema = new FixedWidthSchema();
        FixedWidthSchemaLine inputSchemaLine = new FixedWidthSchemaLine("Header");
        inputSchemaLine.setOccurs(1);
        inputSchemaLine.addSchemaCell(new FixedWidthSchemaCell("Header", 100));
        inputSchema.addSchemaLine(inputSchemaLine);

        inputSchema.addSchemaLine( makeFixedWidthPersonSchemaLine());

        CsvSchema outputSchema = new CsvSchema();
        CsvSchemaLine outputSchemaLine = CsvSchemaLine.builder("Header")
                .withCell("Header")
                .build();
        outputSchema.addSchemaLine(outputSchemaLine);

        outputSchema.addSchemaLine(makeCsvPersonSchemaLine());

        StringWriter writer = new StringWriter();
        StringReader reader = new StringReader(toParse);
        Text2TextConverter converter = new Text2TextConverter(inputSchema, outputSchema);
        converter.convert(reader, writer);
        String sResult = writer.getBuffer().toString();
        String sExpected = "This file contains names" + LN + "Jonas;Stenberg"
                + LN + "Frida;Bergsten";

        assertEquals(sExpected, sResult);

    }

    @Test
    public void testConvert_Manipulated() throws IOException, JSaParException {
        String toParse = "Jonas Stenberg " + LN + "Frida Bergsten ";
        FixedWidthSchema inputSchema = makeFixedWidthPersonSchema();

        CsvSchema outputSchema = new CsvSchema();
        CsvSchemaLine outputSchemaLine = CsvSchemaLine.builder("Person")
                .withCells("First name","Last name", "Town")
                .withCellSeparator(";")
                .build();
        outputSchema.addSchemaLine(outputSchemaLine);

        StringWriter writer = new StringWriter();
        StringReader reader = new StringReader(toParse);
        Text2TextConverter converter = new Text2TextConverter(inputSchema, outputSchema);
        converter.addLineManipulator(line -> {
            line.addCell(new StringCell("Town", "Stockholm"));
            return true;
        });
        converter.convert(reader, writer);
        String sResult = writer.getBuffer().toString();
        String sExpected = "Jonas;Stenberg;Stockholm" + LN
                + "Frida;Bergsten;Stockholm";

        assertEquals(sExpected, sResult);

    }

    public FixedWidthSchema makeFixedWidthPersonSchema() {
        FixedWidthSchema inputSchema = new FixedWidthSchema();
        inputSchema.addSchemaLine(makeFixedWidthPersonSchemaLine());
        return inputSchema;
    }

    public FixedWidthSchemaLine makeFixedWidthPersonSchemaLine() {
        return FixedWidthSchemaLine.builder("Person")
                .withCell("First name", 6)
                .withCell("Last name", 9)
                .build();
    }


    @Test
    public void testConvert_twoKindOfLinesIn_OneKindOut() throws IOException, JSaParException {
        String toParse = "This file contains names" + LN + "Jonas Stenberg "
                + LN + "Frida Bergsten ";
        FixedWidthSchema inputSchema = new FixedWidthSchema();
        FixedWidthSchemaLine inputSchemaLine = FixedWidthSchemaLine.builder("Header")
                .withOccurs(1)
                .withCell("Header", 100)
                .build();
        inputSchema.addSchemaLine(inputSchemaLine);

        inputSchema.addSchemaLine(makeFixedWidthPersonSchemaLine());

        CsvSchema outputSchema = makeCsvPersonSchema();

        StringWriter writer = new StringWriter();
        StringReader reader = new StringReader(toParse);
        Text2TextConverter converter = new Text2TextConverter(inputSchema, outputSchema);
        converter.convert(reader, writer);
        String sResult = writer.getBuffer().toString();
        String sExpected = "Jonas;Stenberg|Frida;Bergsten";

        assertEquals(sExpected, sResult);

    }


    @Test
    public void testConvert_filter() throws IOException, JSaParException {
        String toParse = "This file contains names" + LN + "Jonas Stenberg "
                + LN + "Frida Bergsten "
                + LN + "Tomas Stornos  ";
        FixedWidthSchema inputSchema = new FixedWidthSchema();
        FixedWidthSchemaLine inputSchemaLine = FixedWidthSchemaLine.builder("Header")
                .withOccurs(1)
                .withCell("Header", 100)
                .build();
        inputSchema.addSchemaLine(inputSchemaLine);
        inputSchema.addSchemaLine(makeFixedWidthPersonSchemaLine());

        CsvSchema outputSchema = new CsvSchema();
        outputSchema.addSchemaLine(makeCsvPersonSchemaLine());

        StringWriter writer = new StringWriter();
        StringReader reader = new StringReader(toParse);
        Text2TextConverter converter = new Text2TextConverter(inputSchema, outputSchema);
        converter.addLineManipulator(line -> !line.getLineType().equals("Person") || !line.getCell("First name").orElseThrow(() -> new AssertionError("Should be set")).getStringValue().equals("Tomas"));
        converter.convert(reader, writer);
        String sResult = writer.getBuffer().toString();
        String sExpected = "Jonas;Stenberg" + LN + "Frida;Bergsten";

        assertEquals(sExpected, sResult);

    }

}
