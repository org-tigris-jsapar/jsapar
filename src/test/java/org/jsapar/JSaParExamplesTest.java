package org.jsapar;

import org.jsapar.compose.Composer;
import org.jsapar.compose.bean.RecordingBeanEventListener;
import org.jsapar.error.JSaParException;
import org.jsapar.model.*;
import org.jsapar.parse.CellParseException;
import org.jsapar.parse.DocumentBuilderLineEventListener;
import org.jsapar.parse.xml.XmlParser;
import org.jsapar.schema.CsvSchemaLine;
import org.jsapar.schema.Schema;
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
        try (Reader schemaReader = new FileReader("exsamples/01_CsvSchema.xml");
             Reader fileReader = new FileReader("exsamples/01_Names.csv")) {
            Schema schema = Schema.fromXml(schemaReader);
            TextParser parser = new TextParser(schema);
            DocumentBuilderLineEventListener listener = new DocumentBuilderLineEventListener();
            parser.parse(fileReader, listener);
            Document document = listener.getDocument();

            assertEquals(3, document.size());
            assertEquals("Erik", LineUtils.getStringCellValue(document.getLine(0), "First name"));
            assertEquals("Erik", document.getLine(0).getNonEmptyCell("First name").map(Cell::getStringValue).orElse("fail"));
            assertEquals("Svensson", LineUtils.getStringCellValue(document.getLine(0), "Last name"));
            assertEquals("true", LineUtils.getStringCellValue(document.getLine(0), "Have dog"));
            assertEquals("Fredrik", LineUtils.getStringCellValue(document.getLine(1), "First name"));
            assertEquals("Larsson", LineUtils.getStringCellValue(document.getLine(1), "Last name"));
            assertEquals("false", LineUtils.getStringCellValue(document.getLine(1), "Have dog"));
            assertEquals(Boolean.FALSE, LineUtils.getBooleanCellValue(document.getLine(1),"Have dog").orElseThrow(AssertionError::new));

            assertEquals("Alfred", LineUtils.getStringCellValue(document.getLine(2), "First name"));
            assertEquals("Nilsson", LineUtils.getStringCellValue(document.getLine(2), "Last name"));
            assertEquals("true", LineUtils.getStringCellValue(document.getLine(2), "Have dog"));

            assertEquals("Person", document.getLine(0).getLineType());
            assertEquals("Person", document.getLine(1).getLineType());
        }
    }

    @Test
    public final void testExampleFixedWidth02()
            throws SchemaException, IOException, JSaParException, ParserConfigurationException, SAXException {
        try (Reader schemaReader = new FileReader("exsamples/02_FixedWidthSchema.xml");
             Reader fileReader = new FileReader("exsamples/02_Names.txt")) {
            TextParser parser = new TextParser(Schema.fromXml(schemaReader));
            DocumentBuilderLineEventListener listener = new DocumentBuilderLineEventListener();
            parser.parse(fileReader, listener);
            Document document = listener.getDocument();

            assertEquals("Erik", LineUtils.getStringCellValue(document.getLine(0), "First name"));
            assertEquals("Svensson", LineUtils.getStringCellValue(document.getLine(0), "Last name"));
            assertEquals("Fredrik", LineUtils.getStringCellValue(document.getLine(1), "First name"));
            assertEquals("Larsson", LineUtils.getStringCellValue(document.getLine(1), "Last name"));
        }
    }

    /**
     * @throws SchemaException
     * @throws IOException
     *
     */
    @Test
    public final void testExampleFlatFile03()
            throws SchemaException, IOException, JSaParException, ParserConfigurationException, SAXException {
        try (Reader schemaReader = new FileReader("exsamples/03_FlatFileSchema.xml");
             Reader fileReader = new FileReader("exsamples/03_FlatFileNames.txt")) {
            TextParser parser = new TextParser(Schema.fromXml(schemaReader));
            DocumentBuilderLineEventListener listener = new DocumentBuilderLineEventListener();
            parser.parse(fileReader, listener);
            Document document = listener.getDocument();

            assertEquals(3, document.size());
            assertEquals("Erik", LineUtils.getStringCellValue(document.getLine(0), "First name"));
            assertEquals("Erik", document.getLine(0).getNonEmptyCell("First name").map(Cell::getStringValue).orElse("fail"));
            assertEquals("Svensson", LineUtils.getStringCellValue(document.getLine(0), "Last name"));
            assertEquals("37", LineUtils.getStringCellValue(document.getLine(0), "Age"));
            assertEquals("Fredrik", LineUtils.getStringCellValue(document.getLine(1), "First name"));
            assertEquals("Larsson", LineUtils.getStringCellValue(document.getLine(1), "Last name"));
            assertEquals("17", LineUtils.getStringCellValue(document.getLine(1), "Age"));
        }
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
        assertEquals("04_Names.txt", LineUtils.getStringCellValue(headerLine, "FileName"));
        assertEquals("2007-07-07", LineUtils.getStringCellValue(headerLine, "Created date"));

        Line lineB = document.getLine(1);
        assertEquals("Person", lineB.getLineType());
        assertEquals("B", LineUtils.getStringCellValue(lineB, "Type"));
        assertEquals("Svensson", LineUtils.getStringCellValue(lineB, "Last name"));
        assertEquals("Erik", LineUtils.getStringCellValue(lineB, "First name"));
        assertEquals("Svensson", LineUtils.getStringCellValue(lineB, "Last name"));

        Line lineP = document.getLine(2);
        assertEquals("P", LineUtils.getStringCellValue(lineP, "Type"));
        assertEquals("Fredrik", LineUtils.getStringCellValue(lineP, "First name"));
        assertEquals("Larsson", LineUtils.getStringCellValue(lineP, "Last name"));

        Line footerLine = document.getLine(3);
        assertEquals("Footer", footerLine.getLineType());
        assertEquals("2", LineUtils.getStringCellValue(footerLine, "Rowcount"));
        assertEquals("F", LineUtils.getStringCellValue(footerLine, "Type"));
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
        assertEquals("Hans", LineUtils.getStringCellValue(document.getLine(0), "FirstName"));
        assertEquals("Hugge", LineUtils.getStringCellValue(document.getLine(0), "LastName"));
        assertEquals(48, LineUtils.getIntCellValue(document.getLine(0), "ShoeSize", 0));
        assertEquals("Greta", LineUtils.getStringCellValue(document.getLine(1), "FirstName"));
        assertEquals("Skog", LineUtils.getStringCellValue(document.getLine(1), "LastName"));
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

        assertEquals("06_NamesControlCell.csv", LineUtils.getStringCellValue(document.getLine(0), "FileName"));
        assertEquals("2007-07-07", LineUtils.getStringCellValue(document.getLine(0), "Created date"));
        assertEquals("Header", document.getLine(0).getLineType());
        assertEquals("Person", document.getLine(1).getLineType());
        assertEquals("Svensson", LineUtils.getStringCellValue(document.getLine(1), "Last name"));
        assertEquals("Erik", LineUtils.getStringCellValue(document.getLine(1), "First name"));
        assertEquals("Svensson", LineUtils.getStringCellValue(document.getLine(1), "Last name"));
        assertEquals("Fredrik", LineUtils.getStringCellValue(document.getLine(2), "First name"));
        assertEquals("Larsson", LineUtils.getStringCellValue(document.getLine(2), "Last name"));
        assertEquals("2", LineUtils.getStringCellValue(document.getLine(3), "Rowcount"));
    }

    @Test
    public final void testConvert01_02()
            throws IOException, JSaParException, SchemaException, ParserConfigurationException, SAXException {

        File outFile = new File("exsamples/02_Names_out.txt");
        try (Reader inSchemaReader = new FileReader("exsamples/01_CsvSchema.xml");
             Reader outSchemaReader = new FileReader("exsamples/02_FixedWidthSchema.xml");
             Reader inReader = new FileReader("exsamples/01_Names.csv");
             Writer outWriter = new FileWriter(outFile)) {
            Text2TextConverter converter = new Text2TextConverter(Schema.fromXml(inSchemaReader),
                    Schema.fromXml(outSchemaReader));
            converter.convert(inReader, outWriter);
        }

        Assert.assertTrue(outFile.isFile());
    }

    @SuppressWarnings("unchecked")
    @Test
    public final void testExampleCsvToJava07()
            throws SchemaException, IOException, JSaParException, ParseException, ParserConfigurationException,
            SAXException {
        try (Reader schemaReader = new FileReader("exsamples/07_CsvSchemaToJava.xml");
             Reader fileReader = new FileReader("exsamples/07_Names.csv")) {
            Text2BeanConverter converter = new Text2BeanConverter(Schema.fromXml(schemaReader));
            RecordingBeanEventListener<TstPerson> beanEventListener = new RecordingBeanEventListener<>();
            converter.convert(fileReader, beanEventListener);
            List<TstPerson> people = beanEventListener.getBeans();

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
            throws SchemaException, IOException, ParseException{

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


    @Test
    public final void testExampleCsv08_FirstLineAsSchemaParse()
            throws SchemaException, IOException, JSaParException, ParserConfigurationException, SAXException {
        try (Reader schemaReader = new FileReader("exsamples/08_CsvFirstLineAsSchema.xml");
                Reader fileReader = new FileReader("exsamples/08_NamesWithHeader.csv")) {
            Schema schema = Schema.fromXml(schemaReader);
            TextParser parser = new TextParser(schema);
            DocumentBuilderLineEventListener listener = new DocumentBuilderLineEventListener();
            parser.parse(fileReader, listener);
            Document document = listener.getDocument();

            assertEquals(3, document.size());
            assertEquals("Erik", LineUtils.getStringCellValue(document.getLine(0), "First name"));
            assertEquals("Erik", document.getLine(0).getNonEmptyCell("First name").map(Cell::getStringValue).orElse("fail"));
            assertEquals("Svensson", LineUtils.getStringCellValue(document.getLine(0), "Last name"));
            assertEquals(true, LineUtils.getBooleanCellValue(document.getLine(0), "Have dog", false));
            assertEquals("Fredrik", LineUtils.getStringCellValue(document.getLine(1), "First name"));
            assertEquals("Larsson", LineUtils.getStringCellValue(document.getLine(1), "Last name"));
            assertEquals("false", LineUtils.getStringCellValue(document.getLine(1), "Have dog"));
            assertEquals(false, LineUtils.getBooleanCellValue(document.getLine(1),"Have dog").orElseThrow(AssertionError::new));

            assertEquals("Alfred", LineUtils.getStringCellValue(document.getLine(2), "First name"));
            assertEquals("Nilsson", LineUtils.getStringCellValue(document.getLine(2), "Last name"));
            assertEquals(false, LineUtils.getBooleanCellValue(document.getLine(2), "Have dog").orElseThrow(AssertionError::new));

            assertEquals("Person", document.getLine(0).getLineType());
            assertEquals("Person", document.getLine(1).getLineType());
        }
    }


    @Test
    public final void testExampleCsv08_FirstLineAsSchemaCompose()
            throws SchemaException, IOException, JSaParException, ParserConfigurationException, SAXException {
        try (Reader schemaReader = new FileReader("exsamples/08_CsvFirstLineAsSchema.xml");
                Writer writer = new StringWriter())
        {
            Schema schema = Schema.fromXml(schemaReader);
            Composer composer = new TextComposer(schema, writer);

            Line line1 = new Line("Person");
            line1.putCellValue("First name", "Erik", StringCell::new);
            line1.putCellValue("Middle name", "Jan", StringCell::new);
            line1.putCellValue("Last name", "Svensson", StringCell::new);
            line1.putCellValue("Have dog", true, BooleanCell::new);

            Line line2 = new Line("Person");
            line2.putCellValue("First name", "Sven", StringCell::new);
            line2.putCellValue("Middle name", "Göran", StringCell::new);
            line2.putCellValue("Last name", "Nilsson", StringCell::new);
            line2.putCellValue("Have dog", false, BooleanCell::new);

            composer.composeLine(line1);
            composer.composeLine(line2);
            String[] lines = writer.toString().split("\n");
            assertEquals(3, lines.length);
            assertEquals("Middle name;Have dog;First name", lines[0]);
            assertEquals("Jan;yes;Erik", lines[1]);
            assertEquals("Göran;no;Sven", lines[2]);
        }
    }


}
