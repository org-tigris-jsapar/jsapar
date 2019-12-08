package org.jsapar;

import org.jsapar.bean.BeanMap;
import org.jsapar.compose.Composer;
import org.jsapar.error.JSaParException;
import org.jsapar.error.ValidationAction;
import org.jsapar.model.*;
import org.jsapar.parse.CollectingConsumer;
import org.jsapar.parse.DocumentBuilderLineConsumer;
import org.jsapar.parse.xml.XmlParser;
import org.jsapar.schema.Schema;
import org.jsapar.schema.SchemaException;
import org.jsapar.schema.Xml2SchemaBuilder;
import org.junit.Assert;
import org.junit.Test;

import java.io.*;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * These tests are not unit tests!!<br>
 * The tests in this class uses the sample files provided in the folder resources/samples. The tests
 * below show how JSaPar can be used to parse files.
 * 
 */
public class JSaParExamplesTest {
    private static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Test
    public final void testExampleCsv01_parse()
            throws IOException, JSaParException {
        try (Reader schemaReader = new FileReader("examples/01_CsvSchema.xml");
             Reader fileReader = new FileReader("examples/01_Names.csv")) {
            Schema schema = Schema.ofXml(schemaReader);
            TextParser parser = new TextParser(schema);
            DocumentBuilderLineConsumer listener = new DocumentBuilderLineConsumer();
            parser.parseForEach(fileReader, listener);
            Document document = listener.getDocument();

            assertEquals(3, document.size());
            Line firstLine = document.iterator().next();
            assertEquals("Erik", LineUtils.getStringCellValue(firstLine, "First name"));
            assertEquals("Erik", firstLine.getNonEmptyCell("First name").map(Cell::getStringValue).orElse("fail"));
            assertEquals("Svensson", LineUtils.getStringCellValue(firstLine, "Last name"));
            assertEquals("true", LineUtils.getStringCellValue(firstLine, "Has dog"));
            assertEquals("Fredrik", LineUtils.getStringCellValue(document.getLine(1), "First name"));
            assertEquals("Larsson", LineUtils.getStringCellValue(document.getLine(1), "Last name"));
            assertEquals("false", LineUtils.getStringCellValue(document.getLine(1), "Has dog"));
            assertEquals(Boolean.FALSE, LineUtils.getBooleanCellValue(document.getLine(1),"Has dog").orElseThrow(AssertionError::new));

            assertEquals("Alfred", LineUtils.getStringCellValue(document.getLine(2), "First name"));
            assertEquals("Nilsson", LineUtils.getStringCellValue(document.getLine(2), "Last name"));
            assertEquals("true", LineUtils.getStringCellValue(document.getLine(2), "Has dog"));

            assertEquals("Person", firstLine.getLineType());
            assertEquals("Person", document.getLine(1).getLineType());
        }
    }

    @Test
    public final void testExampleCsv01_compose()
            throws SchemaException, IOException{
        try (Reader schemaReader = new FileReader("examples/01_CsvSchema.xml");
             StringWriter writer = new StringWriter()) {
            Schema schema = Schema.ofXml(schemaReader);
            TextComposer composer = new TextComposer(schema, writer);
            Line line1 = new Line("Person")
                    .addCell(new StringCell("First name", "Erik"))
                    .addCell(new StringCell("Middle name", "Vidfare"));
            LineUtils.setStringCellValue(line1, "Last name", "Svensson");
            composer.composeLine(line1);

            composer.composeLine(new Line("Person")
                    .addCell(new StringCell("First name", "Fredrik"))
                    .addCell(new StringCell("Last name", "Larsson"))
                    .addCell(new BooleanCell("Has dog", false)));

            composer.composeEmptyLine();

            String[] lines = writer.toString().split("\n", -1);
            assertEquals(3, lines.length);
            assertEquals("Erik;Vidfare;Svensson;", lines[0]);
            assertEquals("Fredrik;;Larsson;no", lines[1]);
            assertEquals("", lines[2]);
        }
    }

    @Test
    public final void testExampleFixedWidth02()
            throws IOException, JSaParException {
        try (Reader schemaReader = new FileReader("examples/02_FixedWidthSchema.xml");
             Reader fileReader = new FileReader("examples/02_Names.txt")) {
            TextParser parser = new TextParser(Schema.ofXml(schemaReader));
            parser.getParseConfig().setOnLineInsufficient(ValidationAction.ERROR);
            DocumentBuilderLineConsumer documentBuilder = new DocumentBuilderLineConsumer();
            parser.parseForEach(fileReader, documentBuilder);
            Document document = documentBuilder.getDocument();

            assertEquals("Erik", LineUtils.getStringCellValue(document.getLine(0), "First name"));
            assertEquals("Svensson", LineUtils.getStringCellValue(document.getLine(0), "Last name"));
            assertEquals(new BigDecimal("68.45"), LineUtils.getDecimalCellValue(document.getLine(0), "weight").orElse(null));
            assertEquals("Fredrik", LineUtils.getStringCellValue(document.getLine(1), "First name"));
            assertEquals("Larsson", LineUtils.getStringCellValue(document.getLine(1), "Last name"));
            assertEquals(new BigDecimal("92.75"), LineUtils.getDecimalCellValue(document.getLine(1), "weight").orElse(null));
        }
    }

    @Test
    public final void testExampleFlatFile03()
            throws IOException, JSaParException {
        try (Reader schemaReader = new FileReader("examples/03_FlatFileSchema.xml");
             Reader fileReader = new FileReader("examples/03_FlatFileNames.txt")) {
            TextParser parser = new TextParser(Schema.ofXml(schemaReader));
            DocumentBuilderLineConsumer listener = new DocumentBuilderLineConsumer();
            parser.parseForEach(fileReader, listener);
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

    @Test
    public final void testExampleFixedWidth04_parse()
            throws IOException, JSaParException {
        Reader schemaReader = new FileReader("examples/04_FixedWidthSchemaControlCell.xml");
        Xml2SchemaBuilder schemaBuilder = new Xml2SchemaBuilder();
        Reader fileReader = new FileReader("examples/04_Names.txt");
        TextParser parser = new TextParser(schemaBuilder.build(schemaReader));
        DocumentBuilderLineConsumer listener = new DocumentBuilderLineConsumer();
        parser.parseForEach(fileReader, listener);
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

    @Test
    public final void testExampleFixedWidth04_compose()
            throws IOException, JSaParException {
        Reader schemaReader = new FileReader("examples/04_FixedWidthSchemaControlCell.xml");
        Xml2SchemaBuilder schemaBuilder = new Xml2SchemaBuilder();

        Document document = new Document();
        document.addLine(new Line("Header")
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
                .addCell(new StringCell("Name", "Agust Anka"))
        );

        document.addLine(new Line("Person")
                .addCell(new StringCell("Last name", "Karlsson"))
                .addCell(new StringCell("First name", "Arne"))
                .addCell(new StringCell("Middle name", "91:an"))
        );

        document.addLine(new Line("Footer")
                .addCell(new StringCell("Rowcount", "2"))
        );

        try(Writer w = new StringWriter()) {
            TextComposer composer = new TextComposer(schemaBuilder.build(schemaReader), w);
            composer.compose(document);
            System.out.println(w.toString());
            String[] strings=w.toString().split("\n");
            assertEquals(4, strings.length);
            assertEquals("H04_Names.txt   2017-07-07", strings[0]);
            assertEquals("BÅsa-NissKnut     Nilsson ", strings[1]);
            assertEquals("PArne    91:an    Karlsson", strings[2]);
        }

    }

    @Test
    public final void testExampleXml05() throws IOException, JSaParException {
        Reader fileReader = new FileReader("examples/05_Names.xml");
        XmlParser parser = new XmlParser();
        DocumentBuilderLineConsumer documentBuilder = new DocumentBuilderLineConsumer();
        parser.parseForEach(fileReader, documentBuilder);
        Document document = documentBuilder.getDocument();
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
            throws IOException, JSaParException {
        Reader schemaReader = new FileReader("examples/06_CsvSchemaControlCell.xml");
        Xml2SchemaBuilder schemaBuilder = new Xml2SchemaBuilder();
        Reader fileReader = new FileReader("examples/06_NamesControlCell.csv");
        TextParser parser = new TextParser(schemaBuilder.build(schemaReader));
        DocumentBuilderLineConsumer documentBuilder = new DocumentBuilderLineConsumer();
        parser.parseForEach(fileReader, documentBuilder);
        Document document = documentBuilder.getDocument();
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
        assertEquals("Freddie the \";-) fantastic\"", LineUtils.getStringCellValue(document.getLine(2), "Middle name"));
        assertEquals("2", LineUtils.getStringCellValue(document.getLine(3), "Rowcount"));
    }

    @Test
    public final void testConvert01_02()
            throws IOException, JSaParException {

        File outFile = new File("examples/02_Names_out.txt");
        outFile.delete();
        try (Reader inSchemaReader = new FileReader("examples/01_CsvSchema.xml");
             Reader outSchemaReader = new FileReader("examples/02_FixedWidthSchema.xml");
             Reader inReader = new FileReader("examples/01_Names.csv");
             Writer outWriter = new FileWriter(outFile)) {
            Text2TextConverter converter = new Text2TextConverter(Schema.ofXml(inSchemaReader),
                    Schema.ofXml(outSchemaReader));
            converter.convert(inReader, outWriter);
        }

        Assert.assertTrue(outFile.isFile());
    }

    @Test
    public final void testConvert01_02_usingMain()
            throws JSaParException {

        final String outFileName = "examples/02_Names_out.txt";
        File outFile = new File(outFileName);
        outFile.delete();
        ConverterMain
                .main(new String[] { "-in.file", "examples/01_Names.csv", "-out.file", outFileName, "-in.schema",
                        "examples/01_CsvSchema.xml", "-out.schema", "examples/02_FixedWidthSchema.xml" });

        Assert.assertTrue(outFile.isFile());
    }

    @Test
    public final void testExampleCsvToJava07()
            throws IOException, JSaParException, ParseException {
        try (Reader schemaReader = new FileReader("examples/07_CsvSchemaToJava.xml");
             Reader fileReader = new FileReader("examples/07_Names.csv")) {
            Text2BeanConverter<TstPerson> converter = new Text2BeanConverter<>(Schema.ofXml(schemaReader));
            CollectingConsumer<TstPerson> beanConsumer = new CollectingConsumer<>();
            converter.convertForEach(fileReader, beanConsumer);
            List<TstPerson> people = beanConsumer.getCollected();

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
    public final void testExampleBeanCollectionToCsv07()
            throws SchemaException, IOException, ParseException {

        List<TstPerson> people = new LinkedList<>();
        TstPerson testPerson1 = new TstPerson("Nils", "Holgersson", (short)4, 4711, dateFormat.parse("1902-08-07 12:43:22"), 9, 'A');
        testPerson1.setAddress(new TstPostAddress("Track", "Village"));
        people.add(testPerson1);

        TstPerson testPerson2 = new TstPerson("Jonathan", "Lionheart", (short)37, 17, dateFormat.parse("1955-03-17 12:33:12"), 123456, 'C');
        testPerson2.setAddress(new TstPostAddress("Path", "City"));
        people.add(testPerson2);
        
        
        Reader schemaReader = new FileReader("examples/07_CsvSchemaToJava.xml");
        Xml2SchemaBuilder xmlBuilder = new Xml2SchemaBuilder();
        StringWriter writer = new StringWriter();
        BeanCollection2TextConverter<TstPerson> converter = new BeanCollection2TextConverter<>(xmlBuilder.build(schemaReader));
        converter.convert(people, writer);

        String result=writer.toString();
        String[] resultLines = result.split("\n");
//        System.out.println(result);
        assertEquals("Nils;;Holgersson;4;4711;1902-08-07 12:43;A;Track;Village", resultLines[0]);
        assertEquals("Jonathan;;Lionheart;37;17;1955-03-17 12:33;C;Path;City", resultLines[1]);
    }

    @Test
    public final void testExampleBeanToCsv07()
            throws SchemaException, IOException, ParseException {

        TstPerson testPerson1 = new TstPerson("Nils", "Holgersson", (short)4, 4711, dateFormat.parse("1902-08-07 12:43:22"), 9, 'A');
        testPerson1.setAddress(new TstPostAddress("Track", "Village"));

        TstPerson testPerson2 = new TstPerson("Jonathan", "Lionheart", (short)37, 17, dateFormat.parse("1955-03-17 12:33:12"), 123456, 'C');
        testPerson2.setAddress(new TstPostAddress("Path", "City"));

        Reader schemaReader = new FileReader("examples/07_CsvSchemaToJava.xml");
        Xml2SchemaBuilder xmlBuilder = new Xml2SchemaBuilder();
        StringWriter writer = new StringWriter();
        Bean2TextConverter<TstPerson> converter = new Bean2TextConverter<>(xmlBuilder.build(schemaReader), writer);
        converter.convert(testPerson1);
        converter.convert(testPerson2);

        String result=writer.toString();
        String[] resultLines = result.split("\n");
        //        System.out.println(result);
        assertEquals("Nils;;Holgersson;4;4711;1902-08-07 12:43;A;Track;Village", resultLines[0]);
        assertEquals("Jonathan;;Lionheart;37;17;1955-03-17 12:33;C;Path;City", resultLines[1]);
    }

    /**
     * Example that displays how to use an existing schema that does not use bean property names and still use it to
     * compose a csv based on a list of beans by using a bean map that maps between the bean property names and the schema
     * names.
     */
    @Test
    public final void testExampleBeanToCsv06_beanMap()
            throws SchemaException, IOException, ParseException, ClassNotFoundException {

        List<TstPerson> people = new LinkedList<>();
        TstPerson testPerson1 = new TstPerson("Nils", "Holgersson", (short) 4, 4711, dateFormat.parse("1902-08-07 12:43:22"), 9, 'A');
        testPerson1.setGender(TstGender.M);
        testPerson1.setAddress(new TstPostAddress("Track", "Village"));
        people.add(testPerson1);

        TstPerson testPerson2 = new TstPerson("Jonathan", "Lionheart", (short) 37, 17, dateFormat.parse("1955-03-17 12:33:12"), 123456, 'C');
        testPerson2.setAddress(new TstPostAddress("Path", "City"));
        testPerson2.setGender(TstGender.M);
        people.add(testPerson2);

        BeanCollection2TextConverter<TstPerson> converter;
        try (Reader schemaReader = new FileReader("examples/06_CsvSchemaControlCell.xml");
             Reader beanMapReader = new FileReader("examples/06_BeanMap.xml")) {
            converter = new BeanCollection2TextConverter<>(Schema.ofXml(schemaReader), BeanMap.ofXml(beanMapReader));
        }
        StringWriter writer = new StringWriter();
        converter.convert(people, writer);

        String result = writer.toString();
        String[] resultLines = result.split("\n");
//        System.out.println(result);
        assertEquals("B;\"Nils\";;Holgersson;M", resultLines[0]);
        assertEquals("B;\"Jonathan\";;Lionheart;M", resultLines[1]);
    }

    @Test
    public final void testExampleCsvToBean06_beanMap()
            throws IOException, JSaParException, ClassNotFoundException {
        try (Reader schemaReader = new FileReader("examples/06_CsvSchemaControlCell.xml");
                Reader fileReader = new FileReader("examples/06_NamesControlCell.csv");
                Reader beanMapReader = new FileReader("examples/06_BeanMap.xml")) {
            Text2BeanConverter<TstPerson> converter = new Text2BeanConverter<>(Schema.ofXml(schemaReader), BeanMap.ofXml(beanMapReader));
            converter.getComposeConfig().setOnUndefinedLineType(ValidationAction.OMIT_LINE);
            CollectingConsumer<TstPerson> beanConsumer = new CollectingConsumer<>();
            converter.convertForEach(fileReader, beanConsumer);
            List<TstPerson> people = beanConsumer.getCollected();

            assertEquals(2, people.size());
            assertEquals("Erik", people.get(0).getFirstName());
            assertEquals("Svensson", people.get(0).getLastName());

            assertEquals("Fredrik", people.get(1).getFirstName());
            assertEquals("Larsson", people.get(1).getLastName());
        }
    }

    /**
     * Example that displays how to use an existing schema that does not use bean property names and still use it to
     * compose a csv based on a list of beans by using a bean map that maps between the bean property names and the schema
     * names.
     */
    @Test
    public final void testExampleBeanToCsv06_annotated()
            throws SchemaException, IOException, ParseException {

        List<TstPersonAnnotated> people = new LinkedList<>();
        TstPersonAnnotated testPerson1 = new TstPersonAnnotated("Nils", "Holgersson", (short) 4, 4711, dateFormat.parse("1902-08-07 12:43:22"), 9, 'A');
        testPerson1.setGender(TstGender.M);
        testPerson1.setAddress(new TstPostAddress("Track", "Village"));
        people.add(testPerson1);

        TstPersonAnnotated testPerson2 = new TstPersonAnnotated("Jonathan", "Lionheart", (short) 37, 17, dateFormat.parse("1955-03-17 12:33:12"), 123456, 'C');
        testPerson2.setAddress(new TstPostAddress("Path", "City"));
        testPerson2.setGender(TstGender.M);
        people.add(testPerson2);

        BeanCollection2TextConverter<TstPersonAnnotated> converter;
        try (Reader schemaReader = new FileReader("examples/06_CsvSchemaControlCell.xml")) {
            converter = new BeanCollection2TextConverter<>(Schema.ofXml(schemaReader), BeanMap.ofClasses(Collections.singletonList(TstPersonAnnotated.class)));
        }
        StringWriter writer = new StringWriter();
        converter.convert(people, writer);

        String result = writer.toString();
        String[] resultLines = result.split("\n");
//        System.out.println(result);
        assertEquals("B;\"Nils\";;Holgersson;M", resultLines[0]);
        assertEquals("B;\"Jonathan\";;Lionheart;M", resultLines[1]);
    }

    @Test
    public final void testExampleCsvToBean06_annotated()
            throws IOException, JSaParException {
        try (Reader schemaReader = new FileReader("examples/06_CsvSchemaControlCell.xml");
             Reader fileReader = new FileReader("examples/06_NamesControlCell.csv")) {
            Text2BeanConverter<TstPersonAnnotated> converter = new Text2BeanConverter<>(Schema.ofXml(schemaReader), BeanMap.ofClass(TstPersonAnnotated.class));
            converter.getComposeConfig().setOnUndefinedLineType(ValidationAction.OMIT_LINE);
            CollectingConsumer<TstPersonAnnotated> beanConsumer = new CollectingConsumer<>();
            converter.convertForEach(fileReader, beanConsumer);
            List<TstPersonAnnotated> people = beanConsumer.getCollected();

            assertEquals(2, people.size());
            assertEquals("Erik", people.get(0).getFirstName());
            assertEquals("Svensson", people.get(0).getLastName());

            assertEquals("Fredrik", people.get(1).getFirstName());
            assertEquals("Larsson", people.get(1).getLastName());
        }
    }

    @Test
    public final void testExampleCsvToBean06_beanMapOverride()
            throws IOException, JSaParException {
        try (Reader schemaReader = new FileReader("examples/06_CsvSchemaControlCell.xml");
                Reader fileReader = new FileReader("examples/06_NamesControlCell.csv")) {
            final BeanMap overrideBeanMap = BeanMap.ofClass(TstPersonAnnotated.class);
            final Schema parseSchema = Schema.ofXml(schemaReader);
            BeanMap beanMap = BeanMap.ofSchema(parseSchema, overrideBeanMap);
            Text2BeanConverter<TstPersonAnnotated> converter = new Text2BeanConverter<>(parseSchema, beanMap);
            converter.getComposeConfig().setOnUndefinedLineType(ValidationAction.OMIT_LINE);
            CollectingConsumer<TstPersonAnnotated> beanConsumer = new CollectingConsumer<>();
            converter.convertForEach(fileReader, beanConsumer);
            List<TstPersonAnnotated> people = beanConsumer.getCollected();

            assertEquals(2, people.size());
            assertEquals("Erik", people.get(0).getFirstName());
            assertEquals("Svensson", people.get(0).getLastName());
            assertEquals(TstGender.M, people.get(0).getGender());

            assertEquals("Fredrik", people.get(1).getFirstName());
            assertEquals("Larsson", people.get(1).getLastName());
        }
    }

    @Test
    public final void testExampleCsvToBean06_beanMapOverrideEnum()
            throws IOException, JSaParException, ClassNotFoundException {
        try (Reader schemaReader = new FileReader("examples/06_CsvSchemaControlCellEnum.xml");
             Reader fileReader = new FileReader("examples/06_NamesControlCellEnum.csv");
             Reader beanMapReader = new FileReader("examples/06_BeanMapOverride.xml")) {
            final BeanMap overrideBeanMap = BeanMap.ofXml(beanMapReader);
            final Schema parseSchema = Schema.ofXml(schemaReader);
            BeanMap beanMap = BeanMap.ofSchema(parseSchema, overrideBeanMap);
            Text2BeanConverter<TstPerson> converter = new Text2BeanConverter<>(parseSchema, beanMap);
            converter.getComposeConfig().setOnUndefinedLineType(ValidationAction.OMIT_LINE);
            CollectingConsumer<TstPerson> beanConsumer = new CollectingConsumer<>();
            converter.convertForEach(fileReader, beanConsumer);
            List<TstPerson> people = beanConsumer.getCollected();

            assertEquals(3, people.size());
            assertEquals(TstGender.M, people.get(0).getGender());
            assertEquals(TstGender.M, people.get(1).getGender());
            assertEquals(TstGender.F, people.get(2).getGender());
        }
    }


    @Test
    public final void testExampleCsv08_FirstLineAsSchemaParse()
            throws IOException, JSaParException {
        try (Reader schemaReader = new FileReader("examples/08_CsvFirstLineAsSchema.xml");
                Reader fileReader = new FileReader("examples/08_NamesWithHeader.csv")) {
            Schema schema = Schema.ofXml(schemaReader);
            TextParser parser = new TextParser(schema);
            DocumentBuilderLineConsumer documentBuilder = new DocumentBuilderLineConsumer();
            parser.parseForEach(fileReader, documentBuilder);
            Document document = documentBuilder.getDocument();

            assertEquals(3, document.size());
            assertEquals("Erik", LineUtils.getStringCellValue(document.getLine(0), "First name"));
            assertEquals("Erik", document.getLine(0).getNonEmptyCell("First name").map(Cell::getStringValue).orElse("fail"));
            assertEquals("Svensson", LineUtils.getStringCellValue(document.getLine(0), "Last name"));
            assertTrue(LineUtils.getBooleanCellValue(document.getLine(0), "Has dog", false));
            assertEquals("Fredrik", LineUtils.getStringCellValue(document.getLine(1), "First name"));
            assertEquals("Larsson", LineUtils.getStringCellValue(document.getLine(1), "Last name"));
            assertEquals("false", LineUtils.getStringCellValue(document.getLine(1), "Has dog"));
            assertFalse(LineUtils.getBooleanCellValue(document.getLine(1),"Has dog").orElseThrow(AssertionError::new));

            assertEquals("Alfred", LineUtils.getStringCellValue(document.getLine(2), "First name"));
            assertEquals("Nilsson", LineUtils.getStringCellValue(document.getLine(2), "Last name"));
            assertFalse(LineUtils.getBooleanCellValue(document.getLine(2), "Has dog").orElseThrow(AssertionError::new));

            assertEquals("Person", document.getLine(0).getLineType());
            assertEquals("Person", document.getLine(1).getLineType());
        }
    }


    @Test
    public final void testExampleCsv08_FirstLineAsSchemaCompose()
            throws IOException, JSaParException {
        try (Reader schemaReader = new FileReader("examples/08_CsvFirstLineAsSchema.xml");
                Writer writer = new StringWriter())
        {
            Schema schema = Schema.ofXml(schemaReader);
            Composer composer = new TextComposer(schema, writer);

            Line line1 = new Line("Person");
            line1.putCellValue("First name", "Erik", StringCell::new);
            line1.putCellValue("Middle name", "Jan", StringCell::new);
            line1.putCellValue("Last name", "Svensson", StringCell::new);
            line1.putCellValue("Has dog", true, BooleanCell::new);

            Line line2 = new Line("Person");
            line2.putCellValue("First name", "Sven", StringCell::new);
            line2.putCellValue("Middle name", "Göran", StringCell::new);
            line2.putCellValue("Last name", "Nilsson", StringCell::new);
            line2.putCellValue("Has dog", false, BooleanCell::new);

            composer.composeLine(line1);
            composer.composeLine(line2);
            String[] lines = writer.toString().split("\n");
            assertEquals(3, lines.length);
            assertEquals("Middle name;Has dog;First name", lines[0]);
            assertEquals("Jan;yes;Erik", lines[1]);
            assertEquals("Göran;no;Sven", lines[2]);
        }
    }


}
