package org.jsapar;

import org.jsapar.error.*;
import org.jsapar.model.CellType;
import org.jsapar.model.StringCell;
import org.jsapar.parse.CollectingConsumer;
import org.jsapar.text.TextParseConfig;
import org.jsapar.schema.*;
import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Collections;

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
        return CsvSchema.builder()
                .withLine(makeCsvPersonSchemaLine())
                .withLineSeparator("|")
                .build();
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
        FixedWidthSchema inputSchema = makeFixedWidthPersonShoeSchema();

        CsvSchema outputSchema = CsvSchema.builder()
                .withLine(CsvSchemaLine.builder("Person")
                        .withCells("First name", "Shoe size")
                        .withCellSeparator(";")
                        .build())
                .build();

        try (StringWriter writer = new StringWriter(); StringReader reader = new StringReader(toParse)) {
            Text2TextConverter converter = new Text2TextConverter(inputSchema, outputSchema);
            CollectingConsumer<JSaParException> errorEventListener = new CollectingConsumer<>();
            converter.setErrorConsumer(errorEventListener);
            converter.convert(reader, writer);
            String sResult = writer.getBuffer().toString();
            String sExpected = "Jonas;41" + LN + "Frida;";

            assertEquals(1, errorEventListener.getCollected().size());
            assertEquals(sExpected, sResult);
        }

    }

    public FixedWidthSchema makeFixedWidthPersonShoeSchema() {
        return FixedWidthSchema.builder()
                    .withLine(FixedWidthSchemaLine.builder("Person")
                            .withCell("First name", 6)
                            .withCell(FixedWidthSchemaCell.builder("Shoe size", 9)
                                    .withType(CellType.INTEGER)
                                    .withAlignment(FixedWidthSchemaCell.Alignment.LEFT)
                                    .build())
                            .build())
                    .build();
    }

    @Test(expected = MaxErrorsExceededException.class)
    public void testConvert_max_error() throws IOException, JSaParException {
        String toParse = "Jonas 41       " + LN + "Frida ERROR    ";
        FixedWidthSchema inputSchema = makeFixedWidthPersonShoeSchema();

        CsvSchema outputSchema = CsvSchema.builder()
                .withLine( CsvSchemaLine.builder("Person")
                        .withCells("First name","Shoe size")
                        .withCellSeparator(";")
                        .build())
                .build();

        try (StringWriter writer = new StringWriter(); StringReader reader = new StringReader(toParse)) {
            Text2TextConverter converter = new Text2TextConverter(inputSchema, outputSchema);
            ThresholdCollectingErrorConsumer errorEventListener = new ThresholdCollectingErrorConsumer(0);
            converter.setErrorConsumer(errorEventListener);
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

        FixedWidthSchema inputSchema = makeFixedWidthHeaderPersonSchema();

        CsvSchema outputSchema = CsvSchema.builder()
                .withLine(CsvSchemaLine.builder("Header")
                        .withCell("Header")
                        .build())
                .withLine(makeCsvPersonSchemaLine())
                .build();

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

        CsvSchema outputSchema = CsvSchema.builder()
                .withLine(CsvSchemaLine.builder("Person")
                        .withCells("First name", "Last name", "Town")
                        .withCellSeparator(";")
                        .build())
                .build();

        StringWriter writer = new StringWriter();
        StringReader reader = new StringReader(toParse);
        Text2TextConverter converter = new Text2TextConverter(inputSchema, outputSchema);
        converter.setTransformer(line -> {
            line.addCell(new StringCell("Town", "Stockholm"));
            return Collections.singletonList(line);
        });
        converter.convert(reader, writer);
        String sResult = writer.getBuffer().toString();
        String sExpected = "Jonas;Stenberg;Stockholm" + LN
                + "Frida;Bergsten;Stockholm";

        assertEquals(sExpected, sResult);

    }

    public FixedWidthSchema makeFixedWidthPersonSchema() {
        return FixedWidthSchema.builder()
                .withLine(makeFixedWidthPersonSchemaLine())
                .build();
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
        FixedWidthSchema inputSchema = makeFixedWidthHeaderPersonSchema();

        CsvSchema outputSchema = makeCsvPersonSchema();

        StringWriter writer = new StringWriter();
        StringReader reader = new StringReader(toParse);
        Text2TextConverter converter = new Text2TextConverter(inputSchema, outputSchema);
        converter.convert(reader, writer);
        String sResult = writer.getBuffer().toString();
        String sExpected = "Jonas;Stenberg|Frida;Bergsten";

        assertEquals(sExpected, sResult);

    }

    public FixedWidthSchema makeFixedWidthHeaderPersonSchema() {
        return FixedWidthSchema.builder()
                .withLine(FixedWidthSchemaLine.builder("Header")
                        .withOccurs(1)
                        .withCell("Header", 100)
                        .build())
                .withLine(makeFixedWidthPersonSchemaLine())
                .build();
    }


    @Test
    public void testConvert_filter() throws IOException, JSaParException {
        String toParse = "This file contains names" + LN + "Jonas Stenberg "
                + LN + "Frida Bergsten "
                + LN + "Tomas Stornos  ";
        FixedWidthSchema inputSchema = makeFixedWidthHeaderPersonSchema();

        CsvSchema outputSchema = makeCsvPersonSchema();

        StringWriter writer = new StringWriter();
        StringReader reader = new StringReader(toParse);
        Text2TextConverter converter = new Text2TextConverter(inputSchema, outputSchema);
        converter.addLineManipulator(line -> !line.getLineType().equals("Person") || !line.getCell("First name").orElseThrow(() -> new AssertionError("Should be set")).getStringValue().equals("Tomas"));
        converter.convert(reader, writer);
        String sResult = writer.getBuffer().toString();
        String sExpected = "Jonas;Stenberg|Frida;Bergsten";

        assertEquals(sExpected, sResult);

    }

}
