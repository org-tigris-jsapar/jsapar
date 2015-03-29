package org.jsapar;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;

import org.jsapar.input.CellParseError;
import org.jsapar.input.JavaBuilder;
import org.jsapar.input.ParseSchema;
import org.jsapar.input.Parser;
import org.jsapar.input.XmlDocumentParser;
import org.jsapar.io.Converter;
import org.jsapar.output.Outputter;
import org.jsapar.schema.SchemaException;
import org.jsapar.schema.Xml2SchemaBuilder;
import org.junit.Assert;
import org.junit.Test;

/**
 * These tests are not unit tests!!<br>
 * The tests in this class uses the sample files provided in the folder resources/samples. The tests
 * below show how JSaPar can be used to parse files.
 * 
 * @author stejon0
 * 
 */
public class JSaParExamplesTest {
    private static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Test
    public final void testExampleCsv01() throws SchemaException, IOException, JSaParException {
        Reader schemaReader = new FileReader("samples/01_CsvSchema.xml");
        Xml2SchemaBuilder xmlBuilder = new Xml2SchemaBuilder();
        Reader fileReader = new FileReader("samples/01_Names.csv");
        Parser parser = new Parser(xmlBuilder.build(schemaReader));
        Document document = parser.build(fileReader);
        fileReader.close();

        assertEquals("Erik", document.getLine(0).getCell(0).getStringValue());
        assertEquals("Svensson", document.getLine(0).getCell(1).getStringValue());
        assertEquals("true", document.getLine(0).getCell(2).getStringValue());
        assertEquals("Fredrik", document.getLine(1).getCell(0).getStringValue());
        assertEquals("Larsson", document.getLine(1).getCell(1).getStringValue());
        assertEquals("false", document.getLine(1).getCell(2).getStringValue());
        assertEquals(Boolean.FALSE, ((BooleanCell)document.getLine(1).getCell(2)).getBooleanValue());

        assertEquals("Alfred", document.getLine(2).getCell("First name").getStringValue());
        assertEquals("Nilsson", document.getLine(2).getCell("Last name").getStringValue());
        assertEquals("true", document.getLine(2).getCell("Have dog").getStringValue());

        assertEquals("Person", document.getLine(0).getLineType());
        assertEquals("Person", document.getLine(1).getLineType());
    }

    @Test
    public final void testExampleFixedWidth02() throws SchemaException, IOException, JSaParException {
        Reader schemaReader = new FileReader("samples/02_FixedWidthSchema.xml");
        Xml2SchemaBuilder builder = new Xml2SchemaBuilder();
        Reader fileReader = new FileReader("samples/02_Names.txt");
        Parser parser = new Parser(builder.build(schemaReader));
        Document document = parser.build(fileReader);
        fileReader.close();

        assertEquals("Erik    ", document.getLine(0).getCell(0).getStringValue());
        assertEquals("Svensson ", document.getLine(0).getCell(1).getStringValue());
        assertEquals("Fredrik ", document.getLine(1).getCell(0).getStringValue());
        assertEquals("Larsson  ", document.getLine(1).getCell(1).getStringValue());
    }

    /**
     * @throws SchemaException
     * @throws IOException
     * @throws JSaParException
     */
    @Test
    public final void testExampleFlatFile03() throws SchemaException, IOException, JSaParException {
        Reader schemaReader = new FileReader("samples/03_FlatFileSchema.xml");
        Xml2SchemaBuilder builder = new Xml2SchemaBuilder();
        Reader fileReader = new FileReader("samples/03_FlatFileNames.txt");
        Parser parser = new Parser(builder.build(schemaReader));
        Document document = parser.build(fileReader);
        fileReader.close();

        assertEquals(3, document.getNumberOfLines());
        assertEquals("Erik", document.getLine(0).getCell(0).getStringValue());
        assertEquals("Svensson", document.getLine(0).getCell(1).getStringValue());
        assertEquals("37", document.getLine(0).getCell("Age").getStringValue());
        assertEquals("Fredrik", document.getLine(1).getCell(0).getStringValue());
        assertEquals("Larsson", document.getLine(1).getCell(1).getStringValue());
        assertEquals("17", document.getLine(1).getCell("Age").getStringValue());
    }

    /**
     * @throws SchemaException
     * @throws IOException
     * @throws JSaParException
     */
    @Test
    public final void testExampleFixedWidth04() throws SchemaException, IOException, JSaParException {
        Reader schemaReader = new FileReader("samples/04_FixedWidthSchemaControlCell.xml");
        Xml2SchemaBuilder builder = new Xml2SchemaBuilder();
        Reader fileReader = new FileReader("samples/04_Names.txt");
        Parser parser = new Parser(builder.build(schemaReader));
        Document document = parser.build(fileReader);
        fileReader.close();

        assertEquals("04_Names.txt", document.getLine(0).getCell("FileName").getStringValue());
        assertEquals("2007-07-07", document.getLine(0).getCell("Created date").getStringValue());
        assertEquals("Header", document.getLine(0).getLineType());
        assertEquals("Svensson", document.getLine(1).getCell(1).getStringValue());
        assertEquals("Erik", document.getLine(1).getCell(0).getStringValue());
        assertEquals("Svensson", document.getLine(1).getCell(1).getStringValue());
        assertEquals("Fredrik", document.getLine(2).getCell(0).getStringValue());
        assertEquals("Larsson", document.getLine(2).getCell(1).getStringValue());
        assertEquals("2", document.getLine(3).getCell(0).getStringValue());
    }

    @Test
    public final void testExampleXml05() throws SchemaException, IOException, JSaParException {
        java.util.List<CellParseError> parseErrors = new java.util.LinkedList<CellParseError>();
        ParseSchema schema = new XmlDocumentParser();
        Reader fileReader = new FileReader("samples/05_Names.xml");
        Parser parser = new Parser(schema);
        Document document = parser.build(fileReader, parseErrors);
        fileReader.close();

        // System.out.println("Errors: " + parseErrors.toString());

        assertEquals(2, document.getNumberOfLines());
        assertEquals("Hans", document.getLine(0).getCell("FirstName").getStringValue());
        assertEquals("Hugge", document.getLine(0).getCell("LastName").getStringValue());
        assertEquals(48, ((IntegerCell) document.getLine(0).getCell("ShoeSize")).getNumberValue().intValue());
        assertEquals("Greta", document.getLine(1).getCell("FirstName").getStringValue());
        assertEquals("Skog", document.getLine(1).getCell("LastName").getStringValue());
        assertEquals(31, ((IntegerCell) document.getLine(1).getCell("ShoeSize")).getNumberValue().intValue());
    }

    @Test
    public final void testExampleCsvControlCell06() throws SchemaException, IOException, JSaParException {
        Reader schemaReader = new FileReader("samples/06_CsvSchemaControlCell.xml");
        Xml2SchemaBuilder builder = new Xml2SchemaBuilder();
        Reader fileReader = new FileReader("samples/06_NamesControlCell.csv");
        Parser parser = new Parser(builder.build(schemaReader));
        Document document = parser.build(fileReader);
        fileReader.close();

        assertEquals("06_NamesControlCell.csv", document.getLine(0).getCell("FileName").getStringValue());
        assertEquals("2007-07-07", document.getLine(0).getCell("Created date").getStringValue());
        assertEquals("Header", document.getLine(0).getLineType());
        assertEquals("Person", document.getLine(1).getLineType());
        assertEquals("Svensson", document.getLine(1).getCell(1).getStringValue());
        assertEquals("Erik", document.getLine(1).getCell(0).getStringValue());
        assertEquals("Svensson", document.getLine(1).getCell(1).getStringValue());
        assertEquals("Fredrik", document.getLine(2).getCell(0).getStringValue());
        assertEquals("Larsson", document.getLine(2).getCell(1).getStringValue());
        assertEquals("2", document.getLine(3).getCell(0).getStringValue());
    }

    @Test
    public final void testConvert01_02() throws IOException, JSaParException {
        File outFile = new File("samples/02_FixedWidthSchema.xml");
        Reader inSchemaReader = new FileReader("samples/01_CsvSchema.xml");
        Reader outSchemaReader = new FileReader(outFile);
        Xml2SchemaBuilder xmlBuilder = new Xml2SchemaBuilder();
        Reader inReader = new FileReader("samples/01_Names.csv");
        Writer outWriter = new FileWriter("samples/02_Names_out.txt");
        Converter converter = new Converter(xmlBuilder.build(inSchemaReader), xmlBuilder.build(outSchemaReader));
        converter.convert(inReader, outWriter);
        inReader.close();
        outWriter.close();

        Assert.assertTrue(outFile.isFile());
    }

    @SuppressWarnings("unchecked")
    @Test
    public final void testExampleCsvToJava07() throws SchemaException, IOException, JSaParException, ParseException {
        Reader schemaReader = new FileReader("samples/07_CsvSchemaToJava.xml");
        Xml2SchemaBuilder xmlBuilder = new Xml2SchemaBuilder();
        Reader fileReader = new FileReader("samples/07_Names.csv");
        Parser parser = new Parser(xmlBuilder.build(schemaReader));
        List<CellParseError> parseErrors = new LinkedList<CellParseError>();
        List<TstPerson> people = parser.buildJava(fileReader, parseErrors);
        fileReader.close();

        assertEquals("The errors" + parseErrors, 0, parseErrors.size());
        assertEquals(2, people.size());
        assertEquals("Erik", people.get(0).getFirstName());
        assertEquals("Svensson", people.get(0).getLastName());
        assertEquals(45, people.get(0).getShoeSize());
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        assertEquals(df.parse("1901-01-13 13:45"), people.get(0).getBirthTime());
        assertEquals('A', people.get(0).getDoor());
        assertEquals("Stigen", people.get(0).getAddress().getStreet());
        assertEquals("Staden", people.get(0).getAddress().getTown());
        
        assertEquals("Fredrik", people.get(1).getFirstName());
        assertEquals("Larsson", people.get(1).getLastName());
        assertEquals(17, people.get(1).getLuckyNumber());
        assertEquals('C', people.get(1).getDoor());
        assertEquals("Road", people.get(1).getAddress().getStreet());
        assertEquals("Town", people.get(1).getAddress().getTown());
    }
    
    @Test
    public final void testExampleJavaToCsv07() throws SchemaException, IOException, JSaParException, ParseException {

        List<TstPerson> people = new LinkedList<TstPerson>();
        TstPerson testPerson1 = new TstPerson("Nils", "Holgersson", (short)4, 4711, dateFormat.parse("1902-08-07 12:43:22"), 9, 'A');
        testPerson1.setAddress(new TstPostAddress("Track", "Village"));
        people.add(testPerson1);

        TstPerson testPerson2 = new TstPerson("Jonathan", "Lionheart", (short)37, 17, dateFormat.parse("1955-03-17 12:33:12"), 123456, 'C');
        testPerson2.setAddress(new TstPostAddress("Path", "City"));
        people.add(testPerson2);
        
        
        Reader schemaReader = new FileReader("samples/07_CsvSchemaToJava.xml");
        Xml2SchemaBuilder xmlBuilder = new Xml2SchemaBuilder();
        Outputter outputter = new Outputter(xmlBuilder.build(schemaReader));

        JavaBuilder javaBuilder=new JavaBuilder();
        Document doc = javaBuilder.build(people);
        System.out.println("The document:" + doc);
        
        StringWriter writer = new StringWriter();
        outputter.output(doc, writer);
        
        String result=writer.toString();
        String[] resultLines = result.split("\r\n");
//        System.out.println(result);
        assertEquals("Nils;;Holgersson;4;4711;1902-08-07 12:43;A;Track;Village", resultLines[0]);
        assertEquals("Jonathan;;Lionheart;37;17;1955-03-17 12:33;C;Path;City", resultLines[1]);
    }
    
}
