/**
 * 
 */
package org.jsapar;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;

import org.jsapar.convert.LineFilter;
import org.jsapar.convert.LineManipulator;
import org.jsapar.convert.MaxErrorsExceededException;
import org.jsapar.model.CellType;
import org.jsapar.model.Line;
import org.jsapar.parse.CellParseError;
import org.jsapar.model.StringCell;
import org.jsapar.schema.CsvSchemaCell;
import org.jsapar.schema.CsvSchemaLine;
import org.jsapar.schema.FixedWidthSchemaCell;
import org.jsapar.schema.FixedWidthSchemaLine;
import org.jsapar.schema.SchemaCellFormat;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author stejon0
 * 
 */
public class ConverterTest {

    public static final String LN = System.getProperty("line.separator");

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }


    /**
     * Test method for
     * {@link Converter#convert(java.io.Reader, org.jsapar.parse.ParseSchema, java.io.Writer, org.jsapar.schema.Schema)}
     * .
     * 
     * @throws JSaParException
     * @throws IOException
     */
    @Test
    public void testConvert() throws IOException, JSaParException {
        String toParse = "Jonas Stenberg " + LN + "Frida Bergsten ";
        ;
        org.jsapar.schema.FixedWidthSchema inputSchema = new org.jsapar.schema.FixedWidthSchema();
        FixedWidthSchemaLine inputSchemaLine = new FixedWidthSchemaLine("Person");
        inputSchemaLine.addSchemaCell(new FixedWidthSchemaCell("First name", 6));
        inputSchemaLine.addSchemaCell(new FixedWidthSchemaCell("Last name", 9));
        inputSchemaLine.setTrimFillCharacters(true);
        inputSchema.addSchemaLine(inputSchemaLine);

        org.jsapar.schema.CsvSchema outputSchema = new org.jsapar.schema.CsvSchema();
        CsvSchemaLine outputSchemaLine = new CsvSchemaLine("Person");
        outputSchemaLine.addSchemaCell(new CsvSchemaCell("First name"));
        outputSchemaLine.addSchemaCell(new CsvSchemaCell("Last name"));
        outputSchemaLine.setCellSeparator(";");
        outputSchema.addSchemaLine(outputSchemaLine);
        outputSchema.setLineSeparator("|");

        StringWriter writer = new StringWriter();
        StringReader reader = new StringReader(toParse);
        Converter converter = new Converter(inputSchema, outputSchema);
        converter.convert(reader, writer);
        reader.close();
        writer.close();
        String sResult = writer.getBuffer().toString();
        String sExpected = "Jonas;Stenberg|Frida;Bergsten|";

        Assert.assertEquals(sExpected, sResult);

    }

    /**
     * Test method for
     * {@link Converter#convert(java.io.Reader, org.jsapar.parse.ParseSchema, java.io.Writer, org.jsapar.schema.Schema)}
     * .
     * 
     * @throws JSaParException
     * @throws IOException
     */
    @Test
    public void testConvert_error() throws IOException, JSaParException {
        String toParse = "Jonas 41       " + LN + "Frida ERROR    ";
        ;
        org.jsapar.schema.FixedWidthSchema inputSchema = new org.jsapar.schema.FixedWidthSchema();
        FixedWidthSchemaLine inputSchemaLine = new FixedWidthSchemaLine("Person");
        inputSchemaLine.addSchemaCell(new FixedWidthSchemaCell("First name", 6));
        FixedWidthSchemaCell schemaCell2 = new FixedWidthSchemaCell("Shoe size", 9, new SchemaCellFormat(
                CellType.INTEGER));
        inputSchemaLine.addSchemaCell(schemaCell2);
        inputSchemaLine.setTrimFillCharacters(true);
        inputSchema.addSchemaLine(inputSchemaLine);

        org.jsapar.schema.CsvSchema outputSchema = new org.jsapar.schema.CsvSchema();
        CsvSchemaLine outputSchemaLine = new CsvSchemaLine("Person");
        outputSchemaLine.addSchemaCell(new CsvSchemaCell("First name"));
        outputSchemaLine.addSchemaCell(new CsvSchemaCell("Shoe size"));
        outputSchemaLine.setCellSeparator(";");
        outputSchema.addSchemaLine(outputSchemaLine);

        StringWriter writer = new StringWriter();
        StringReader reader = new StringReader(toParse);
        Converter converter = new Converter(inputSchema, outputSchema);
        List<CellParseError> errors = converter.convert(reader, writer);
        reader.close();
        writer.close();
        String sResult = writer.getBuffer().toString();
        String sExpected = "Jonas;41" + LN + "Frida;" + LN;

        Assert.assertEquals(1, errors.size());
        Assert.assertEquals(sExpected, sResult);

    }

    /**
     * Test method for
     * {@link Converter#convert(java.io.Reader, org.jsapar.parse.ParseSchema, java.io.Writer, org.jsapar.schema.Schema)}
     * .
     * 
     * @throws JSaParException
     * @throws IOException
     */
    @Test(expected = MaxErrorsExceededException.class)
    public void testConvert_max_error() throws IOException, JSaParException {
        String toParse = "Jonas 41       " + LN + "Frida ERROR    ";
        ;
        org.jsapar.schema.FixedWidthSchema inputSchema = new org.jsapar.schema.FixedWidthSchema();
        FixedWidthSchemaLine inputSchemaLine = new FixedWidthSchemaLine("Person");
        inputSchemaLine.addSchemaCell(new FixedWidthSchemaCell("First name", 6));
        FixedWidthSchemaCell schemaCell2 = new FixedWidthSchemaCell("Shoe size", 9, new SchemaCellFormat(
                CellType.INTEGER));
        inputSchemaLine.addSchemaCell(schemaCell2);
        inputSchemaLine.setTrimFillCharacters(true);
        inputSchema.addSchemaLine(inputSchemaLine);

        org.jsapar.schema.CsvSchema outputSchema = new org.jsapar.schema.CsvSchema();
        CsvSchemaLine outputSchemaLine = new CsvSchemaLine("Person");
        outputSchemaLine.addSchemaCell(new CsvSchemaCell("First name"));
        outputSchemaLine.addSchemaCell(new CsvSchemaCell("Shoe size"));
        outputSchemaLine.setCellSeparator(";");
        outputSchema.addSchemaLine(outputSchemaLine);

        StringWriter writer = new StringWriter();
        StringReader reader = new StringReader(toParse);
        try {
            Converter converter = new Converter(inputSchema, outputSchema);
            converter.setMaxNumberOfErrors(0);
            converter.convert(reader, writer);
        } finally {
            reader.close();
            writer.close();
        }

    }

    /**
     * Test method for
     * {@link Converter#convert(java.io.Reader, org.jsapar.parse.ParseSchema, java.io.Writer, org.jsapar.schema.Schema)}
     * .
     * 
     * @throws JSaParException
     * @throws IOException
     */
    @Test
    public void testConvert_oneLine() throws IOException, JSaParException {
        String toParse = "Jonas Stenberg ";
        ;
        org.jsapar.schema.FixedWidthSchema inputSchema = new org.jsapar.schema.FixedWidthSchema();
        FixedWidthSchemaLine inputSchemaLine = new FixedWidthSchemaLine();
        inputSchemaLine.addSchemaCell(new FixedWidthSchemaCell("First name", 6));
        inputSchemaLine.addSchemaCell(new FixedWidthSchemaCell("Last name", 9));
        inputSchemaLine.setTrimFillCharacters(true);
        inputSchema.addSchemaLine(inputSchemaLine);

        org.jsapar.schema.CsvSchema outputSchema = new org.jsapar.schema.CsvSchema();
        CsvSchemaLine outputSchemaLine = new CsvSchemaLine();
        outputSchemaLine.addSchemaCell(new CsvSchemaCell("First name"));
        outputSchemaLine.addSchemaCell(new CsvSchemaCell("Last name"));
        outputSchemaLine.setCellSeparator(";");
        outputSchema.addSchemaLine(outputSchemaLine);

        StringWriter writer = new StringWriter();
        StringReader reader = new StringReader(toParse);
        Converter converter = new Converter(inputSchema, outputSchema);
        converter.convert(reader, writer);
        String sResult = writer.getBuffer().toString();
        String sExpected = "Jonas;Stenberg" + LN;

        Assert.assertEquals(sExpected, sResult);

    }

    /**
     * Test method for
     * {@link Converter#convert(java.io.Reader, org.jsapar.parse.ParseSchema, java.io.Writer, org.jsapar.schema.Schema)}
     * .
     * 
     * @throws JSaParException
     * @throws IOException
     */
    @Test
    public void testConvert_twoKindOfLines() throws IOException, JSaParException {
        String toParse = "This file contains names" + LN + "Jonas Stenberg "
                + LN + "Frida Bergsten ";
        ;
        org.jsapar.schema.FixedWidthSchema inputSchema = new org.jsapar.schema.FixedWidthSchema();
        FixedWidthSchemaLine inputSchemaLine = new FixedWidthSchemaLine("Header");
        inputSchemaLine.setOccurs(1);
        inputSchemaLine.addSchemaCell(new FixedWidthSchemaCell("Header", 100));
        inputSchema.addSchemaLine(inputSchemaLine);

        inputSchemaLine = new FixedWidthSchemaLine("Person");
        inputSchemaLine.addSchemaCell(new FixedWidthSchemaCell("First name", 6));
        inputSchemaLine.addSchemaCell(new FixedWidthSchemaCell("Last name", 9));
        inputSchemaLine.setTrimFillCharacters(true);
        inputSchema.addSchemaLine(inputSchemaLine);

        org.jsapar.schema.CsvSchema outputSchema = new org.jsapar.schema.CsvSchema();
        CsvSchemaLine outputSchemaLine = new CsvSchemaLine("Header");
        outputSchemaLine.addSchemaCell(new CsvSchemaCell("Header"));
        outputSchema.addSchemaLine(outputSchemaLine);

        outputSchemaLine = new CsvSchemaLine("Person");
        outputSchemaLine.addSchemaCell(new CsvSchemaCell("First name"));
        outputSchemaLine.addSchemaCell(new CsvSchemaCell("Last name"));
        outputSchemaLine.setCellSeparator(";");
        outputSchema.addSchemaLine(outputSchemaLine);

        StringWriter writer = new StringWriter();
        StringReader reader = new StringReader(toParse);
        Converter converter = new Converter(inputSchema, outputSchema);
        converter.convert(reader, writer);
        String sResult = writer.getBuffer().toString();
        String sExpected = "This file contains names" + LN + "Jonas;Stenberg"
                + LN + "Frida;Bergsten" + LN;

        Assert.assertEquals(sExpected, sResult);

    }

    /**
     * Test method for
     * {@link Converter#convert(java.io.Reader, org.jsapar.parse.ParseSchema, java.io.Writer, org.jsapar.schema.Schema)}
     * .
     * 
     * @throws JSaParException
     * @throws IOException
     */
    @Test
    public void testConvert_Manipulated() throws IOException, JSaParException {
        String toParse = "Jonas Stenberg " + LN + "Frida Bergsten ";
        ;
        org.jsapar.schema.FixedWidthSchema inputSchema = new org.jsapar.schema.FixedWidthSchema();
        FixedWidthSchemaLine inputSchemaLine = new FixedWidthSchemaLine();
        inputSchemaLine.addSchemaCell(new FixedWidthSchemaCell("First name", 6));
        inputSchemaLine.addSchemaCell(new FixedWidthSchemaCell("Last name", 9));
        inputSchemaLine.setTrimFillCharacters(true);
        inputSchema.addSchemaLine(inputSchemaLine);

        org.jsapar.schema.CsvSchema outputSchema = new org.jsapar.schema.CsvSchema();
        CsvSchemaLine outputSchemaLine = new CsvSchemaLine();
        outputSchemaLine.addSchemaCell(new CsvSchemaCell("First name"));
        outputSchemaLine.addSchemaCell(new CsvSchemaCell("Last name"));
        outputSchemaLine.addSchemaCell(new CsvSchemaCell("Town"));
        outputSchemaLine.setCellSeparator(";");
        outputSchema.addSchemaLine(outputSchemaLine);

        StringWriter writer = new StringWriter();
        StringReader reader = new StringReader(toParse);
        Converter converter = new Converter(inputSchema, outputSchema);
        converter.addLineManipulator(new LineManipulator() {
            @Override
            public void manipulate(Line line) throws JSaParException {
                line.addCell(new StringCell("Town", "Stockholm"));
            }
        });
        converter.convert(reader, writer);
        String sResult = writer.getBuffer().toString();
        String sExpected = "Jonas;Stenberg;Stockholm" + LN
                + "Frida;Bergsten;Stockholm" + LN;

        Assert.assertEquals(sExpected, sResult);

    }


    @Test
    public void testConvert_twoKindOfLinesIn_OneKindOut() throws IOException, JSaParException {
        String toParse = "This file contains names" + LN + "Jonas Stenberg "
                + LN + "Frida Bergsten ";
        ;
        org.jsapar.schema.FixedWidthSchema inputSchema = new org.jsapar.schema.FixedWidthSchema();
        FixedWidthSchemaLine inputSchemaLine = new FixedWidthSchemaLine(1);
        inputSchemaLine.addSchemaCell(new FixedWidthSchemaCell("Header", 100));
        inputSchemaLine.setLineType("Header");
        inputSchema.addSchemaLine(inputSchemaLine);

        inputSchemaLine = new FixedWidthSchemaLine("Names");
        inputSchemaLine.addSchemaCell(new FixedWidthSchemaCell("First name", 6));
        inputSchemaLine.addSchemaCell(new FixedWidthSchemaCell("Last name", 9));
        inputSchemaLine.setTrimFillCharacters(true);
        inputSchema.addSchemaLine(inputSchemaLine);

        org.jsapar.schema.CsvSchema outputSchema = new org.jsapar.schema.CsvSchema();
        CsvSchemaLine outputSchemaLine;

        outputSchemaLine = new CsvSchemaLine("Names");
        outputSchemaLine.addSchemaCell(new CsvSchemaCell("First name"));
        outputSchemaLine.addSchemaCell(new CsvSchemaCell("Last name"));
        outputSchemaLine.setCellSeparator(";");
        outputSchema.addSchemaLine(outputSchemaLine);

        StringWriter writer = new StringWriter();
        StringReader reader = new StringReader(toParse);
        Converter converter = new Converter(inputSchema, outputSchema);
        converter.convert(reader, writer);
        String sResult = writer.getBuffer().toString();
        String sExpected = "Jonas;Stenberg" + LN + "Frida;Bergsten"
                + LN;

        Assert.assertEquals(sExpected, sResult);

    }


    @Test
    public void testConvert_filter() throws IOException, JSaParException {
        String toParse = "This file contains names" + LN + "Jonas Stenberg "
                + LN + "Frida Bergsten "
                + LN + "Tomas Stornos  ";
        ;
        org.jsapar.schema.FixedWidthSchema inputSchema = new org.jsapar.schema.FixedWidthSchema();
        FixedWidthSchemaLine inputSchemaLine = new FixedWidthSchemaLine(1);
        inputSchemaLine.addSchemaCell(new FixedWidthSchemaCell("Header", 100));
        inputSchemaLine.setLineType("Header");
        inputSchema.addSchemaLine(inputSchemaLine);

        inputSchemaLine = new FixedWidthSchemaLine("Names");
        inputSchemaLine.addSchemaCell(new FixedWidthSchemaCell("First name", 6));
        inputSchemaLine.addSchemaCell(new FixedWidthSchemaCell("Last name", 9));
        inputSchemaLine.setTrimFillCharacters(true);
        inputSchema.addSchemaLine(inputSchemaLine);

        org.jsapar.schema.CsvSchema outputSchema = new org.jsapar.schema.CsvSchema();
        CsvSchemaLine outputSchemaLine;

        outputSchemaLine = new CsvSchemaLine("Names");
        outputSchemaLine.addSchemaCell(new CsvSchemaCell("First name"));
        outputSchemaLine.addSchemaCell(new CsvSchemaCell("Last name"));
        outputSchemaLine.setCellSeparator(";");
        outputSchema.addSchemaLine(outputSchemaLine);

        StringWriter writer = new StringWriter();
        StringReader reader = new StringReader(toParse);
        Converter converter = new Converter(inputSchema, outputSchema);
        converter.setLineFilter(new LineFilter(){
            @Override
            public boolean shouldWrite(Line line) throws JSaParException {
                if(line.getLineType().equals("Names") && line.getCell("First name").getStringValue().equals("Tomas"))
                    return false;
                else
                    return true;
            }});

        converter.convert(reader, writer);
        String sResult = writer.getBuffer().toString();
        String sExpected = "Jonas;Stenberg" + LN + "Frida;Bergsten"
                + LN;

        Assert.assertEquals(sExpected, sResult);

    }

}
