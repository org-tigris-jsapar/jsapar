package org.jsapar;

import org.jsapar.compose.bean.RecordingBeanEventListener;
import org.jsapar.error.JSaParException;
import org.jsapar.model.Document;
import org.jsapar.model.Line;
import org.jsapar.model.LineUtils;
import org.jsapar.model.StringCell;
import org.jsapar.parse.CellParseException;
import org.jsapar.parse.DocumentBuilderLineEventListener;
import org.jsapar.parse.xml.XmlParser;
import org.jsapar.schema.SchemaException;
import org.jsapar.schema.Xml2SchemaBuilder;
import org.junit.Assert;
import org.junit.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;

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
    public final void testExampleCsv01()
            throws SchemaException, IOException, JSaParException, ParserConfigurationException, SAXException {
        Reader schemaReader = new FileReader("exsamples/01_CsvSchema.xml");
        Xml2SchemaBuilder xmlBuilder = new Xml2SchemaBuilder();
        Reader fileReader = new FileReader("exsamples/01_Names.csv");
        TextParser parser = new TextParser(xmlBuilder.build(schemaReader));
        DocumentBuilderLineEventListener listener = new DocumentBuilderLineEventListener();
        parser.parse(fileReader, listener);
        Document document = listener.getDocument();
        fileReader.close();

        assertEquals("Erik", LineUtils.getStringCellValue(document.getLine(0), "First name").orElse("fail"));
        assertEquals("Svensson", LineUtils.getStringCellValue(document.getLine(0), "Last name").orElse("fail"));
        assertEquals("true", LineUtils.getStringCellValue(document.getLine(0), "Have dog").orElse("fail"));
        assertEquals("Fredrik", LineUtils.getStringCellValue(document.getLine(1), "First name").orElse("fail"));
        assertEquals("Larsson", LineUtils.getStringCellValue(document.getLine(1), "Last name").orElse("fail"));
        assertEquals("false", LineUtils.getStringCellValue(document.getLine(1), "Have dog").orElse("fail"));
        assertEquals(Boolean.FALSE, LineUtils.getBooleanCellValue(document.getLine(1),"Have dog"));

        assertEquals("Alfred", LineUtils.getStringCellValue(document.getLine(2), "First name").orElse("fail"));
        assertEquals("Nilsson", LineUtils.getStringCellValue(document.getLine(2), "Last name").orElse("fail"));
        assertEquals("true", LineUtils.getStringCellValue(document.getLine(2), "Have dog").orElse("fail"));

        assertEquals("Person", document.getLine(0).getLineType());
        assertEquals("Person", document.getLine(1).getLineType());
    }

    @Test
    public final void testExampleFixedWidth02()
            throws SchemaException, IOException, JSaParException, ParserConfigurationException, SAXException {
        Reader schemaReader = new FileReader("exsamples/02_FixedWidthSchema.xml");
        Xml2SchemaBuilder schemaBuilder = new Xml2SchemaBuilder();
        Reader fileReader = new FileReader("exsamples/02_Names.txt");
        TextParser parser = new TextParser(schemaBuilder.build(schemaReader));
        DocumentBuilderLineEventListener listener = new DocumentBuilderLineEventListener();
        parser.parse(fileReader, listener);
        Document document = listener.getDocument();
        fileReader.close();

        assertEquals("Erik", LineUtils.getStringCellValue(document.getLine(0), "First name").orElse("fail"));
        assertEquals("Svensson", LineUtils.getStringCellValue(document.getLine(0), "Last name").orElse("fail"));
        assertEquals("Fredrik", LineUtils.getStringCellValue(document.getLine(1), "First name").orElse("fail"));
        assertEquals("Larsson", LineUtils.getStringCellValue(document.getLine(1), "Last name").orElse("fail"));
    }

    /**
     * @throws SchemaException
     * @throws IOException
     *
     */
    @Test
    public final void testExampleFlatFile03()
            throws SchemaException, IOException, JSaParException, ParserConfigurationException, SAXException {
        Reader schemaReader = new FileReader("exsamples/03_FlatFileSchema.xml");
        Xml2SchemaBuilder schemaBuilder = new Xml2SchemaBuilder();
        Reader fileReader = new FileReader("exsamples/03_FlatFileNames.txt");
        TextParser parser = new TextParser(schemaBuilder.build(schemaReader));
        DocumentBuilderLineEventListener listener = new DocumentBuilderLineEventListener();
        parser.parse(fileReader, listener);
        Document document = listener.getDocument();
        fileReader.close();

        assertEquals(3, document.size());
        assertEquals("Erik", LineUtils.getStringCellValue(document.getLine(0), "First name").orElse("fail"));
        assertEquals("Svensson", LineUtils.getStringCellValue(document.getLine(0), "Last name").orElse("fail"));
        assertEquals("37", LineUtils.getStringCellValue(document.getLine(0), "Age").orElse("fail"));
        assertEquals("Fredrik", LineUtils.getStringCellValue(document.getLine(1), "First name").orElse("fail"));
        assertEquals("Larsson", LineUtils.getStringCellValue(document.getLine(1), "Last name").orElse("fail"));
        assertEquals("17", LineUtils.getStringCellValue(document.getLine(1), "Age").orElse("fail"));
    }

    /**
     * @throws SchemaException
     * @throws IOException
     *
     */
    @Test
    public final void testExampleFixedWidth04_parse()
            throws SchemaException, IOException, JSaParException, ParserConfigurationException, SAXException {
        Reader schemaReader = new FileReader("exsamples/04_FixedWidthSchemaControlCell.xml");
        Xml2SchemaBuilder schemaBuilder = new Xml2SchemaBuilder();
        Reader fileReader = new FileReader("exsamples/04_Names.txt");
        TextParser parser = new TextParser(schemaBuilder.build(schemaReader));
        DocumentBuilderLineEventListener listener = new DocumentBuilderLineEventListener();
        parser.parse(fileReader, listener);
        Document document = listener.getDocument();
        fileReader.close();

        Line headerLine = document.getLine(0);
        assertEquals("Header", headerLine.getLineType());
        assertEquals("04_Names.txt", LineUtils.getStringCellValue(headerLine, "FileName").orElse("fail"));
        assertEquals("2007-07-07", LineUtils.getStringCellValue(headerLine, "Created date").orElse("fail"));

        Line lineB = document.getLine(1);
        assertEquals("Person", lineB.getLineType());
        assertEquals("B", LineUtils.getStringCellValue(lineB, "Type").orElse("fail"));
        assertEquals("Svensson", LineUtils.getStringCellValue(lineB, "Last name").orElse("fail"));
        assertEquals("Erik", LineUtils.getStringCellValue(lineB, "First name").orElse("fail"));
        assertEquals("Svensson", LineUtils.getStringCellValue(lineB, "Last name").orElse("fail"));

        Line lineP = document.getLine(2);
        assertEquals("P", LineUtils.getStringCellValue(lineP, "Type").orElse("fail"));
        assertEquals("Fredrik", LineUtils.getStringCellValue(lineP, "First name").orElse("fail"));
        assertEquals("Larsson", LineUtils.getStringCellValue(lineP, "Last name").orElse("fail"));

        Line footerLine = document.getLine(3);
        assertEquals("Footer", footerLine.getLineType());
        assertEquals("2", LineUtils.getStringCellValue(footerLine, "Rowcount").orElse("fail"));
        assertEquals("F", LineUtils.getStringCellValue(footerLine, "Type").orElse("fail"));
    }

    /**
     * @throws SchemaException
     * @throws IOException
     *
     */
    @Test
    public final void testExampleFixedWidth04_compose()
            throws SchemaException, IOException, JSaParException, ParserConfigurationException, SAXException {
        Reader schemaReader = new FileReader("exsamples/04_FixedWidthSchemaControlCell.xml");
        Xml2SchemaBuilder schemaBuilder = new Xml2SchemaBuilder();

        Document document = new Document();
        document.addLine(new Line("Header")
                .addCell(new StringCell("Type", "H"))
                .addCell(new StringCell("FileName", "04_Names.txt"))
                .addCell(new StringCell("Created date", "2017-07-07")));

        document.addLine(new Line("Person")
                .addCell(new StringCell("Type", "B"))
                .addCell(new StringCell("Last name", "Nilsson"))
                .addCell(new StringCell("First name", "Åsa-Nisse"))
                .addCell(new StringCell("Middle name", "Knut"))
                .addCell(new StringCell("Some other", "stuff that will not get written"))
        );

        document.addLine(new Line("Pet")
                .addCell(new StringCell("Type", "E"))
                .addCell(new StringCell("Name", "Kalle Anka"))
        );

        document.addLine(new Line("Person")
                .addCell(new StringCell("Type", "P"))
                .addCell(new StringCell("Last name", "Karlsson"))
                .addCell(new StringCell("First name", "Arne"))
                .addCell(new StringCell("Middle name", "91:an"))
        );

        document.addLine(new Line("Footer")
                .addCell(new StringCell("Type", "F"))
                .addCell(new StringCell("Rowcount", "2"))
        );

        try(Writer w = new StringWriter()) {
            TextComposer composer = new TextComposer(schemaBuilder.build(schemaReader), w);
            composer.compose(document);
            System.out.println(w.toString());
            String[] strings=w.toString().split("\r\n");
            assertEquals(4, strings.length);
            assertEquals("H04_Names.txt   2017-07-07", strings[0]);
            assertEquals("BÅsa-NissKnut     Nilsson ", strings[1]);
            assertEquals("PArne    91:an    Karlsson", strings[2]);
        }

    }

    @Test
    public final void testExampleXml05() throws SchemaException, IOException, JSaParException {
        java.util.List<CellParseException> parseErrors = new java.util.LinkedList<CellParseException>();
        Reader fileReader = new FileReader("exsamples/05_Names.xml");
        XmlParser parser = new XmlParser();
        DocumentBuilderLineEventListener listener = new DocumentBuilderLineEventListener();
        parser.parse(fileReader, listener);
        Document document = listener.getDocument();
        fileReader.close();

        // System.out.println("Errors: " + parseErrors.toString());

        assertEquals(2, document.size());
        assertEquals("Hans", LineUtils.getStringCellValue(document.getLine(0), "FirstName").orElse("fail"));
        assertEquals("Hugge", LineUtils.getStringCellValue(document.getLine(0), "LastName").orElse("fail"));
        assertEquals(48, LineUtils.getIntCellValue(document.getLine(0), "ShoeSize", 0));
        assertEquals("Greta", LineUtils.getStringCellValue(document.getLine(1), "FirstName").orElse("fail"));
        assertEquals("Skog", LineUtils.getStringCellValue(document.getLine(1), "LastName").orElse("fail"));
        assertEquals(31, LineUtils.getIntCellValue(document.getLine(1), "ShoeSize", 0));
    }

    @Test
    public final void testExampleCsvControlCell06()
            throws SchemaException, IOException, JSaParException, ParserConfigurationException, SAXException {
        Reader schemaReader = new FileReader("exsamples/06_CsvSchemaControlCell.xml");
        Xml2SchemaBuilder schemaBuilder = new Xml2SchemaBuilder();
        Reader fileReader = new FileReader("exsamples/06_NamesControlCell.csv");
        TextParser parser = new TextParser(schemaBuilder.build(schemaReader));
        DocumentBuilderLineEventListener listener = new DocumentBuilderLineEventListener();
        parser.parse(fileReader, listener);
        Document document = listener.getDocument();
        fileReader.close();

        assertEquals("06_NamesControlCell.csv", LineUtils.getStringCellValue(document.getLine(0), "FileName").orElse("fail"));
        assertEquals("2007-07-07", LineUtils.getStringCellValue(document.getLine(0), "Created date").orElse("fail"));
        assertEquals("Header", document.getLine(0).getLineType());
        assertEquals("Person", document.getLine(1).getLineType());
        assertEquals("Svensson", LineUtils.getStringCellValue(document.getLine(1), "Last name").orElse("fail"));
        assertEquals("Erik", LineUtils.getStringCellValue(document.getLine(1), "First name").orElse("fail"));
        assertEquals("Svensson", LineUtils.getStringCellValue(document.getLine(1), "Last name").orElse("fail"));
        assertEquals("Fredrik", LineUtils.getStringCellValue(document.getLine(2), "First name").orElse("fail"));
        assertEquals("Larsson", LineUtils.getStringCellValue(document.getLine(2), "Last name").orElse("fail"));
        assertEquals("2", LineUtils.getStringCellValue(document.getLine(3), "Rowcount").orElse("fail"));
    }

    @Test
    public final void testConvert01_02()
            throws IOException, JSaParException, SchemaException, ParserConfigurationException, SAXException {
        Reader inSchemaReader = new FileReader("exsamples/01_CsvSchema.xml");
        Reader outSchemaReader = new FileReader("exsamples/02_FixedWidthSchema.xml");
        Xml2SchemaBuilder xmlBuilder = new Xml2SchemaBuilder();
        File outFile = new File("exsamples/02_Names_out.txt");
        try (Reader inReader = new FileReader("exsamples/01_Names.csv"); Writer outWriter = new FileWriter(outFile)) {

            Text2TextConverter converter = new Text2TextConverter(xmlBuilder.build(inSchemaReader),
                    xmlBuilder.build(outSchemaReader));
            converter.convert(inReader, outWriter);
        }

        Assert.assertTrue(outFile.isFile());
    }

    @SuppressWarnings("unchecked")
    @Test
    public final void testExampleCsvToJava07()
            throws SchemaException, IOException, JSaParException, ParseException, ParserConfigurationException,
            SAXException {
        Reader schemaReader = new FileReader("exsamples/07_CsvSchemaToJava.xml");
        Xml2SchemaBuilder xmlBuilder = new Xml2SchemaBuilder();
        try(Reader fileReader = new FileReader("exsamples/07_Names.csv")) {
            Text2BeanConverter converter = new Text2BeanConverter(xmlBuilder.build(schemaReader));
            RecordingBeanEventListener<TstPerson> beanEventListener = new RecordingBeanEventListener<>();
            converter.convert(fileReader, beanEventListener);
            List<TstPerson> people = beanEventListener.getBeans();
            fileReader.close();

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
    }
    
    @Test
    public final void testExampleJavaToCsv07()
            throws SchemaException, IOException, ParseException, ParserConfigurationException, SAXException {

        List<TstPerson> people = new LinkedList<TstPerson>();
        TstPerson testPerson1 = new TstPerson("Nils", "Holgersson", (short)4, 4711, dateFormat.parse("1902-08-07 12:43:22"), 9, 'A');
        testPerson1.setAddress(new TstPostAddress("Track", "Village"));
        people.add(testPerson1);

        TstPerson testPerson2 = new TstPerson("Jonathan", "Lionheart", (short)37, 17, dateFormat.parse("1955-03-17 12:33:12"), 123456, 'C');
        testPerson2.setAddress(new TstPostAddress("Path", "City"));
        people.add(testPerson2);
        
        
        Reader schemaReader = new FileReader("exsamples/07_CsvSchemaToJava.xml");
        Xml2SchemaBuilder xmlBuilder = new Xml2SchemaBuilder();
        StringWriter writer = new StringWriter();
        Bean2TextConverter<TstPerson> converter = new Bean2TextConverter<>(xmlBuilder.build(schemaReader));
        converter.convert(people, writer);

        String result=writer.toString();
        String[] resultLines = result.split("\r\n");
//        System.out.println(result);
        assertEquals("Nils;;Holgersson;4;4711;1902-08-07 12:43;A;Track;Village", resultLines[0]);
        assertEquals("Jonathan;;Lionheart;37;17;1955-03-17 12:33;C;Path;City", resultLines[1]);
    }
    
}
