/**
 * 
 */
package org.jsapar.io;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import org.jsapar.JSaParException;
import org.jsapar.Line;
import org.jsapar.StringCell;
import org.jsapar.schema.CsvSchema;
import org.jsapar.schema.CsvSchemaCell;
import org.jsapar.schema.CsvSchemaLine;
import org.jsapar.schema.FixedWidthSchema;
import org.jsapar.schema.FixedWidthSchemaCell;
import org.jsapar.schema.FixedWidthSchemaLine;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author stejon0
 * 
 */
public class ConverterTest {

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
     * {@link org.jsapar.io.Converter#convert(java.io.Reader, org.jsapar.input.ParseSchema, java.io.Writer, org.jsapar.schema.Schema)}
     * .
     * 
     * @throws JSaParException
     * @throws IOException
     */
    @Test
    public void testConvert() throws IOException, JSaParException {
        String toParse = "Jonas Stenberg " + System.getProperty("line.separator") + "Frida Bergsten ";
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
        String sExpected = "Jonas;Stenberg" + System.getProperty("line.separator") + "Frida;Bergsten";

        Assert.assertEquals(sExpected, sResult);

    }

    /**
     * Test method for
     * {@link org.jsapar.io.Converter#convert(java.io.Reader, org.jsapar.input.ParseSchema, java.io.Writer, org.jsapar.schema.Schema)}
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
        String sExpected = "Jonas;Stenberg";

        Assert.assertEquals(sExpected, sResult);

    }

    /**
     * Test method for
     * {@link org.jsapar.io.Converter#convert(java.io.Reader, org.jsapar.input.ParseSchema, java.io.Writer, org.jsapar.schema.Schema)}
     * .
     * 
     * @throws JSaParException
     * @throws IOException
     */
    @Test
    public void testConvert_twoKindOfLines() throws IOException, JSaParException {
        String toParse = "This file contains names" + System.getProperty("line.separator") + "Jonas Stenberg "
                + System.getProperty("line.separator") + "Frida Bergsten ";
        ;
        org.jsapar.schema.FixedWidthSchema inputSchema = new org.jsapar.schema.FixedWidthSchema();
        FixedWidthSchemaLine inputSchemaLine = new FixedWidthSchemaLine(1);
        inputSchemaLine.addSchemaCell(new FixedWidthSchemaCell("Header", 100));
        inputSchema.addSchemaLine(inputSchemaLine);

        inputSchemaLine = new FixedWidthSchemaLine();
        inputSchemaLine.addSchemaCell(new FixedWidthSchemaCell("First name", 6));
        inputSchemaLine.addSchemaCell(new FixedWidthSchemaCell("Last name", 9));
        inputSchemaLine.setTrimFillCharacters(true);
        inputSchema.addSchemaLine(inputSchemaLine);

        org.jsapar.schema.CsvSchema outputSchema = new org.jsapar.schema.CsvSchema();
        CsvSchemaLine outputSchemaLine = new CsvSchemaLine(1);
        outputSchemaLine.addSchemaCell(new CsvSchemaCell("Header"));
        outputSchema.addSchemaLine(outputSchemaLine);

        outputSchemaLine = new CsvSchemaLine();
        outputSchemaLine.addSchemaCell(new CsvSchemaCell("First name"));
        outputSchemaLine.addSchemaCell(new CsvSchemaCell("Last name"));
        outputSchemaLine.setCellSeparator(";");
        outputSchema.addSchemaLine(outputSchemaLine);

        StringWriter writer = new StringWriter();
        StringReader reader = new StringReader(toParse);
        Converter converter = new Converter(inputSchema, outputSchema);
        converter.convert(reader, writer);
        String sResult = writer.getBuffer().toString();
        String sExpected = "This file contains names" + System.getProperty("line.separator") + "Jonas;Stenberg"
                + System.getProperty("line.separator") + "Frida;Bergsten";

        Assert.assertEquals(sExpected, sResult);

    }

    /**
     * Test method for
     * {@link org.jsapar.io.Converter#convert(java.io.Reader, org.jsapar.input.ParseSchema, java.io.Writer, org.jsapar.schema.Schema)}
     * .
     * 
     * @throws JSaParException
     * @throws IOException
     */
    @Test
    public void testConvert_twoKindOfInSchemas() throws IOException, JSaParException {
        String toParse = "This;file;contains;names" + System.getProperty("line.separator") + "Jonas Stenberg "
                + System.getProperty("line.separator") + "Frida Bergsten ";
        ;
        CsvSchema inSchema1 = new CsvSchema();
        CsvSchemaLine inSchema1Line1 = new CsvSchemaLine(1);
        inSchema1Line1.addSchemaCell(new CsvSchemaCell());
        inSchema1Line1.addSchemaCell(new CsvSchemaCell());
        inSchema1Line1.addSchemaCell(new CsvSchemaCell());
        inSchema1Line1.addSchemaCell(new CsvSchemaCell());
        inSchema1.addSchemaLine(inSchema1Line1);

        org.jsapar.schema.FixedWidthSchema inSchema2 = new org.jsapar.schema.FixedWidthSchema();
        FixedWidthSchemaLine inputSchemaLine = new FixedWidthSchemaLine();
        inputSchemaLine.addSchemaCell(new FixedWidthSchemaCell("First name", 6));
        inputSchemaLine.addSchemaCell(new FixedWidthSchemaCell("Last name", 9));
        inputSchemaLine.setTrimFillCharacters(true);
        inSchema2.addSchemaLine(inputSchemaLine);

        org.jsapar.schema.CsvSchema outputSchema = new org.jsapar.schema.CsvSchema();
        CsvSchemaLine outputSchemaLine = new CsvSchemaLine(1);
        outputSchemaLine.addSchemaCell(new CsvSchemaCell());
        outputSchemaLine.addSchemaCell(new CsvSchemaCell());
        outputSchemaLine.addSchemaCell(new CsvSchemaCell());
        outputSchemaLine.addSchemaCell(new CsvSchemaCell());
        outputSchemaLine.setCellSeparator("-");
        outputSchema.addSchemaLine(outputSchemaLine);

        outputSchemaLine = new CsvSchemaLine();
        outputSchemaLine.addSchemaCell(new CsvSchemaCell("First name"));
        outputSchemaLine.addSchemaCell(new CsvSchemaCell("Last name"));
        outputSchemaLine.setCellSeparator(";");
        outputSchema.addSchemaLine(outputSchemaLine);

        StringWriter writer = new StringWriter();
        StringReader reader = new StringReader(toParse);
        Converter converter = new Converter();
        converter.addInputSchema(inSchema1);
        converter.addInputSchema(inSchema2);
        converter.addOutputSchema(outputSchema);
        converter.convert(reader, writer);
        String sResult = writer.getBuffer().toString();
        String sExpected = "This-file-contains-names" + System.getProperty("line.separator") + "Jonas;Stenberg"
                + System.getProperty("line.separator") + "Frida;Bergsten";

        Assert.assertEquals(sExpected, sResult);

    }

    
    /**
     * Test method for
     * {@link org.jsapar.io.Converter#convert(java.io.Reader, org.jsapar.input.ParseSchema, java.io.Writer, org.jsapar.schema.Schema)}
     * .
     * 
     * @throws JSaParException
     * @throws IOException
     */
    @Test
    public void testConvert_twoKindOfOutSchemas() throws IOException, JSaParException {
        String toParse = "This file contains names" + System.getProperty("line.separator") + "Jonas Stenberg "
                + System.getProperty("line.separator") + "Frida Bergsten ";
        ;
        FixedWidthSchema inputSchema = new FixedWidthSchema();
        FixedWidthSchemaLine inputSchemaLine = new FixedWidthSchemaLine(1);
        inputSchemaLine.addSchemaCell(new FixedWidthSchemaCell("Header", 100));
        inputSchema.addSchemaLine(inputSchemaLine);

        inputSchemaLine = new FixedWidthSchemaLine();
        inputSchemaLine.addSchemaCell(new FixedWidthSchemaCell("First name", 6));
        inputSchemaLine.addSchemaCell(new FixedWidthSchemaCell("Last name", 9));
        inputSchemaLine.setTrimFillCharacters(true);
        inputSchema.addSchemaLine(inputSchemaLine);

        org.jsapar.schema.CsvSchema outSchema1 = new org.jsapar.schema.CsvSchema();
        CsvSchemaLine outSchema1Line1 = new CsvSchemaLine(1);
        outSchema1Line1.addSchemaCell(new CsvSchemaCell("Header"));
        outSchema1.addSchemaLine(outSchema1Line1);

        FixedWidthSchema outSchema2 = new FixedWidthSchema();
        FixedWidthSchemaLine outSchema2Line1 = new FixedWidthSchemaLine();
        outSchema2Line1.addSchemaCell(new FixedWidthSchemaCell("First name", 6));
        outSchema2Line1.addSchemaCell(new FixedWidthSchemaCell("Last name", 10));
        outSchema2.addSchemaLine(outSchema2Line1);

        StringWriter writer = new StringWriter();
        StringReader reader = new StringReader(toParse);
        Converter converter = new Converter();
        converter.addInputSchema(inputSchema);
        converter.addOutputSchema(outSchema1);
        converter.addOutputSchema(outSchema2);

        converter.convert(reader, writer);
        String sResult = writer.getBuffer().toString();
        String sExpected = "This file contains names" + System.getProperty("line.separator") + "Jonas Stenberg  "
                + System.getProperty("line.separator") + "Frida Bergsten  ";

        Assert.assertEquals(sExpected, sResult);

    }
    
    /**
     * Test method for
     * {@link org.jsapar.io.Converter#convert(java.io.Reader, org.jsapar.input.ParseSchema, java.io.Writer, org.jsapar.schema.Schema)}
     * .
     * 
     * @throws JSaParException
     * @throws IOException
     */
    @Test
    public void testConvert_Manipulated() throws IOException, JSaParException {
        String toParse = "Jonas Stenberg " + System.getProperty("line.separator") + "Frida Bergsten ";
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
        String sExpected = "Jonas;Stenberg;Stockholm" + System.getProperty("line.separator")
                + "Frida;Bergsten;Stockholm";

        Assert.assertEquals(sExpected, sResult);

    }

    
}
