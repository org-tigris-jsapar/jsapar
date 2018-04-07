---
layout: default
title: JSaPar Introduction
--- 
[Home](index) | [Introduction](introduction) | [Baics of Schemas](schemaintroduction)

# Java Schema Parser
The <a href="api/index.html">javadoc</a> contains more comprehensive documentation regarding the classes mentioned below. <br/><br/>
The JSaPar is a java library that provides a parser for flat and CSV (Comma Separated Values) files.
The concept is that a schema denotes the way a file should be parsed or composed. The schema instance to be used can be built by specifying a xml-document or it can be constructed programmatically by using java code.
The parser is event driven, meaning that you need to provide an event handler while parsing. For convenience there are some
event handlers provided or you may implement your own. For instance, the org.jsapar.parse.DocumentBuilderLineEventHandler
builds a  a org.jsapar.model.Document object that contains a list of org.jsapar.model.Line objects which contains a list
of org.jsapar.model.Cell objects.

Supported file formats:
* <b>Fixed width </b><i>- Also refered to as flat file. Each cell is described only by its positions within the line. </i>
* <b>CSV </b><i>- (Comma Separated Values) Each cell is limited by a separator character (or characters).</i>

## Simple example of parsing CSV file
Let us say that we have a CSV file that we need to parse. In this example the file contains lines that all have the same type. They each contain four cells (columns). Here is an example of the content of such a file.

```csv
Erik;Vidfare;Svensson;yes
Fredrik;Allvarlig;Larsson;no
"Alfred";"Stark";Nilsson;yes
```
The first column contains the first name. The second column contains a middle name (that we are not interested in parsing). The fourth column contains a boolean value that can have one of the values "yes" or "no" where yes is considered as boolean true.

In order to parse this type of files you first need to define a schema of the file. The easiest way to do this is to use the xml format. Here is a simple example of a schema file that can be used to parse the file above:
```xml
<?xml version="1.0" encoding="UTF-8"?>
<schema xmlns="http://jsapar.tigris.org/JSaParSchema/2.0">
  <csvschema lineseparator="\n">
    <line occurs="*" linetype="Person" cellseparator=";" quotechar="&quot;">
      <cell name="First name" />
      <cell name="Middle name" ignoreread="true"/>
      <cell name="Last name" />
      <cell name="Have dog"><format type="boolean" pattern="yes;no"/></cell>
    </line>
  </csvschema>
</schema>
```

The code that you need to write in order to use the JSaPar library to parse files of this type is this:
```java
try (Reader schemaReader = new FileReader("examples/01_CsvSchema.xml");
    Reader fileReader = new FileReader("examples/01_Names.csv")) {
    Schema schema = Schema.ofXml(schemaReader);
    TextParser parser = new TextParser(schema);
    DocumentBuilderLineEventListener listener = new DocumentBuilderLineEventListener();
    parser.parse(fileReader, listener);
    Document document = listener.getDocument();
    Line firstLine = document.iterator().next();
    assert "Erik".equals( LineUtils.getStringCellValue(firstLine, "First name")) );
}
```
In this example we
1. Load the Schema from a file by using a FileReader for the schema file.
1. Then we use that schema to create a TextParser.
1. We then create a DocumentBuilderLineEventListener that is a pre defined event listener that collects all
the events for each line and then builds a Document object that can be fetched when done parsing.
1. The resulting document
instance contains a list of Line objects where each Line represent a line in the input file. Now, depending on what we want to do
with the parsed result, we may for example use the LineUtils class that contains a number of convenient methods to get cell
values of different types from a Line.

The example above is a small simple example. For larger files you probably want to implement a different event listener
that handles each line immediately as it is parsed. This way you will never load the whole content of the input file in the memory.
If you rather work with your own Java class directly instead of getting Line objects, you probably want to look at the Text2BeanConverter class.

## Simple example of composing a CSV file
The code to use the JSaPar library to compose a file, using the schema above could look like this:
```java
try (Reader schemaReader = new FileReader("examples/01_CsvSchema.xml");
     Writer writer = new FileWriter("out.csv")) {
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
            .addCell(new BooleanCell("Have dog", false)));
}
```
In this example we
1. Load the Schema from a file by using a FileReader for the schema file.
1. Then we use that schema to create a TextComposer.
1. Then we feed the composer with newly created Line objects. As you can see, the cell values can be set in some 
different ways. The [Java doc](api) provides more details about your different options. You may for instance feed lines 
to the composer by using a java `Stream<Line>` or an `Iterator<Line>`. 

The advantage of this schema approach is that if you parse or compose a large number of similar files you can adapt the 
schema file if the file format changes instead of making changes within your code.

# Parsing
## Events for each line
For very large files there can be a problem to build the complete org.jsapar.model.Document in the memory before further processing.
It may simply take up to much memory. 
All parsers in this library requires that you provide an event handler that implements org.jsapar.parse.LineEventListener while parsing.

The JSaPar library contains some convenient implementations of the org.jsapar.parse.LineEventListener interface:
<table>
    <tr><td><b>org.jsapar.parse. DocumentBuilderLineEventListener</b></td>
        <td>For smaller files you may want to handle all
        the events after the parsing is complete. In that case you may choose to use this implementation.
        That listener builds a org.jsapar.model.Document object containing all the parsed lines that you can iterate afterwards.</td></tr>
    <tr><td><b>org.jsapar.parse. MulticastLineEventListener</b></td>
        <td>If you need to handle the events in multiple event listener implementation, this implementation provides a
            way to register multiple line event listeners which are called one by one for each line event.</td></tr>
    <tr><td><b>org.jsapar.concurrent. ConcurrentLineEventListener</b></td>
        <td>This implementation separates the parsing thread from the consuming thread. It makes it possible for you to
            register a consumer line event listener that is called from a separate consumer thread.</td></tr>
</table>

## Error handling 
# Composing
## Error handling 
# Converting
## Text to text
If you are only interesting in converting a file of one format into another, you can use the org.jsapar.Text2TextConverter where you specify the input and the output schema for the conversion.
The converter uses the event mechanism under the hood, thus it reads, converts and writes one line at a time.
This means it is very lean regarding memory usage.
## Text to Java beans
Use the org.jsapar.Text2BeanConverter in order to build java objects for each line in a file (or input).
Note that in order to be able to use this feature, the schema have to be carefully written. 
For instance, the line type (name) of the line within the schema have to contain the complete class name of the java class to build for each line. 
## Java objects to text
Use the class Bean2TextConverter in order to convert java objects an output text file according to a schema.
## Text to markup (XML or HTML)
Use the class Text2XmlConverter in order to produce a xml output. You can register a XSLT together with this converter and in
that way you convert the text to any other text output format such as HTML.
## Using XML as input
It is possible to parse an xml document that conforms to the XMLDocumentFormat.xsd (http://jsapar.tigris.org/XMLDocumentFormat/1.0).
Use the class org.jsapar.XmlParser in order to parse an xml file and produce line parsed events.
## Manipulating lines while converting

# Further Examples
The files for the examples below are provided in the <code>examples</code> folder of the project. The JUnit test <code>org.jsapar.JSaParExamplesTest.java</code>
contains a more comprehensive set of examples of how to use the package.
##Example of converting a <b>Fixed width file</b> into a <b>CSV file</b> according to two xml-schemas
```java
try(Reader inSchemaReader = new FileReader("samples/01_CsvSchema.xml");
    Reader outSchemaReader = new FileReader("samples/02_FixedWidthSchema.xml")) {
    Xml2SchemaBuilder xmlBuilder = new Xml2SchemaBuilder();
    File outFile = new File("samples/02_Names_out.txt");
    try(Reader inReader = new FileReader("samples/01_Names.csv");
        Writer outWriter = new FileWriter(outFile)) {
        Converter converter = new Converter(xmlBuilder.build(inSchemaReader),
                                            xmlBuilder.build(outSchemaReader));
        converter.convert(inReader, outWriter);
    }
    Assert.assertTrue(outFile.isFile());
}
```
##Example of converting a <b>CSV file</b> into a list of <b>Java objects</b> according to an xml-schema
```java
Reader schemaReader = new FileReader("samples/07_CsvSchemaToJava.xml");
Xml2SchemaBuilder xmlBuilder = new Xml2SchemaBuilder();
Reader fileReader = new FileReader("samples/07_Names.csv");
Parser parser = new Parser(xmlBuilder.build(schemaReader));
List<CellParseError> parseErrors = new LinkedList<>()
List<TestPerson> people = parser.buildJava(fileReader, parseErrors);
fileReader.close();
```
If you want to run this example, you will need the class org.jsapar.TstPerson within your classpath. 
The class is not included in the jar file or in the binary package but it can be found in the source package. 
As an alternative you can create your own TstPerson class and modify the schema 07_CsvSchemaToJava.xml to use that class instead. 
The class should contain a default constructor plus getters and setters for all the attributes used in the schema.
