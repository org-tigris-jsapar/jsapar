---
layout: page
title: JSaPar Introduction
--- 
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
The page [Basics of Schemas](basics_schema) describes schemas in more details. 

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

That is all you need to parse a CSV file.

The example above is a small simple example. For larger files you probably want to implement a different event listener
that handles each line immediately as it is parsed. That way you will never load the whole content of the input file in the memory.
If you rather work with your own Java class directly instead of getting Line objects, you probably want to look at the Text2BeanConverter class.

## Simple example of composing a CSV file
The code to use the JSaPar library to compose a file, using the same schema as when parsing above, could look like this:
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

## Simple example of parsing and composing fixed width file
As long as only the file format differs, only the schema needs to change. That is the whole idea of working with schemas.
This means that if you want to parse or compose fixed with file you can use exactly the same code as with the CSV example 
above. The only thing that needs to be changed is the schema.

If you have a fixed with file that looks like this:
```text
Erik    Vidfare   Svensson Y
Fredrik Allvarlig Larsson  N
Alfred  Stark     Nilsson  Y
```
...you can use a schema like this to parse (or compose) it:
```xml
<?xml version="1.0" encoding="UTF-8"?>
<schema xmlns="http://jsapar.tigris.org/JSaParSchema/2.0">
<fixedwidthschema lineseparator="\n">
    <line linetype="Person" occurs="*" >
      <cell name="First name" length="8"/>
      <cell name="Middle name" length="10" ignoreread="true"/>
      <cell name="Last name" length="9"/>
      <cell name="Have dog" length="1"><format type="boolean" pattern="Y;N"/></cell>
    </line>
  </fixedwidthschema>
</schema>
``` 
The noticeable difference from working with CSV files above is that you need to specify a length for the cell. The length 
describes how many character each cell/column has.

Here you can see the advantage of keeping the format of the file separated from your Java code. If the file format 
changes you do not need to alter your code.

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
