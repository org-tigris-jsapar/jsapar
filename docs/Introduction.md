---
layout: default
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

# The schema
The schema is what describes the format of the input or output. The same schema can be used for both parsing and composing.
Usually the easiest way to work with a schema is to use the
xml format. The example below describes a simple schema for a CSV file taken from the first example above.
```xml
<?xml version="1.0" encoding="UTF-8"?>
<schema xmlns="http://jsapar.tigris.org/JSaParSchema/2.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
   xsi:schemaLocation="http://jsapar.tigris.org/JSaParSchema/2.0 http://jsapar.tigris.org/JSaParSchema/2.0/JSaParSchema.xsd">
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
## The schema of the schema
In the schema above, I have added the xsi:schemaLocation which helps intelligent xml editors to find the 
<a href="https://en.wikipedia.org/wiki/XML_Schema_(W3C)">XSD</a> that is used for JSaPar schemas. The XSD itself provides
a lot of documentation about the details of each allowed element and attribute within the schema xml. A published version
of the schema is located at. 

[http://jsapar.tigris.org/JSaParSchema/2.0/JSaParSchema.xsd](http://jsapar.tigris.org/JSaParSchema/2.0/JSaParSchema.xsd)

If you want to download the XSD as a file, you will probably need to right click on the link above and choose *"Save link as..."* depending on your browser.

## The Schema xml
After the leading root `<schema>` element you need to define what type of input or output you have. There are two choices:

1. `<csvschema>`
1. `<fixedwidthschema>`

Depending on the choice here, the rest of the schema will be different. 

### The line separator
On this level you may also specify what type of line separator your input or output have. You can use any character 
sequence as line separator but for convenience the following escaped characters will also work within the xml:

* `\n` - LF (line feed) or hex 0A. 
* `\r` - CR (carrige return) or hex 0D.
* `\t` - TAB (horizontal tab) or hex 09.
* `\f` - FF (form feed) or hex 0C.

For Unix systems the normal line separator is `"\n"` and for Windows systems the normal line separator is `"\r\n"`. Omitting
the `lineseparator` attribute will result in that the system default is used. Be aware though that if you rely on system 
default, the schema will behave differently if you move it to a different platform. It is therefore recommended to always 
specify the line separator explicitly.  

For fixed width files you may also specify an 
empty string if lines are determined only by the length of the line which can be the case for Mainframe computers (COBOL).

When parsing, if you have specified one of either `"\n"` or `"\r\n"` as line separator, then the parser will consider both of them to be valid
line separators but when composing, only the specified line separator will be used.

## The Schema xml for CSV 
### Line
The `<line>` element describes a type of line that can occur in your input or output. For instance, you may have a 
different header line that have a different set of columns than the rest of the file. The `occurs` attribute describes 
how many lines to expect of a certain type. By setting `occurs="*"` you indicate that the line may occur infinite number 
of times.

The attribute `linetype` sets the name of the type of line described by the line element. When parsing, the line type is
present in all parsed Line objects and can be used to determine how to treat the Line. When composing, you need to set the 
lineType of all Line objects that you provide to the Composer in order to make it produce lines of a specific type.

On this level you need to specify the `cellseparator` attribute which should describe how cells/columns are separated 
within the input/output. You can use any character sequence 
and you can use the same escaped characters as with the line separator described above. Please note that if the cell 
separator may occur within a value of a cell, you will need to quote the cell. See chapter about Quoted values below.

The attributes `ignoreread` and `ignorewrite` can be used to indicate that the line should be ignored while parsing or 
composing.

#### Line condition
Sometimes the type of line is not determined by position but instead by the value of one of the cells. If you for instance have the following file to parse:
```csv
H;06_NamesControlCell.csv;2007-07-07
B;Erik;Vidfare;Svensson
B;Fredrik;;Larsson
F;2
```
In this file, the value of the first column determines how to parse the rest of the line. `H` means header, `B` means body and `F` means footer.

In order to parse file above, you can use a schema that looks like this:
```xml
<?xml version="1.0" encoding="UTF-8"?>
<schema xmlns="http://jsapar.tigris.org/JSaParSchema/2.0">
  <csvschema lineseparator="\n">
    <line linetype="Header" cellseparator=";">
      <cell name="Type" default="H"><linecondition><match pattern="H"/></linecondition></cell>
      <cell name="FileName"/>
      <cell name="Created date"/>
    </line>
    <line linetype="Person" cellseparator=";" quotechar="&quot;">
      <cell name="Type" default="B" ><linecondition><match pattern="B"/></linecondition></cell>
      <cell name="First name"/>
      <cell name="Middle name" ignoreread="true"/>
      <cell name="Last name" />
    </line>
    <line linetype="Footer" cellseparator=";">
      <cell name="Type" default="F"><linecondition><match pattern="F"/></linecondition></cell>
      <cell name="Rowcount"/>
    </line>
  </csvschema>
</schema>
```
When parsing a file with a schema like this, it is important that you check the line type of the returned Line instance.

You may add a line condition on any cell within your schema. If you add more than one line condition on the same line, 
all of them need to comply in order for the line type to be used.
 
You may combine the a line condition with the `occurs` attribute. In this case, the `occurs` value indicates the maximum number
of times that a line type is used when parsing.

When composing, you set the value of the line condition cell as with any other cell so the line condition as no effect 
when composing. By assigning a default value for the line condition cell as we do above, we make sure that we do not need 
to assign any value to that cell while composing. 
### Cell
The `<cell>` element describes the format of a particular cell or column. Each cell needs to have a name. By default the 
cell type is string so if you do not want the library to do any type conversion, the minimal configuration for a cell is:

```xml
<cell name="TheName"/>
```
With the attribute `mandatory="true"`, you can specify that an error is generated if a cell does not have any value. See 
chapter about error handling below.
 
The attribute `default` can be used to assign a default value that will be used if the cell does not contain any value.

As with lines, you can use `ignoreread` and `ignorewrite` on cell level to skip reading while parsing or to skip writing 
a cell value while composing. If `ignorewrite=true`, an empty cell will be written as if it contained an empty string. 
#### Cell formats
If you want the library to do type conversion while parsing or composing, you need to specify the format of a cell. For 
example, by adding the format:
```xml
<cell name="Birthdate"><format type="date" pattern="YYYY-mm-DD"/></cell>
```
The parser will convert string date values into DateCell containing a java.util.Date with the parsed date.

The following types are supported:
* string
* character
* decimal
* integer
* float
* boolean
* date
* local_date
* local_time
* local_date_time
* zoned_date_time

The `pattern` attribute behaves differently depending on the type: 
* If the type is string then the pattern should contain a regular expression to which the value is validated against. See [java.util.regex.Pattern](https://docs.oracle.com/javase/8/docs/api/java/util/regex/Pattern.html) This only works while parsing.
* If the type is any of the numerical types, then the pattern should be described according to the [java.text.DecimalFormat](https://docs.oracle.com/javase/8/docs/api/java/text/DecimalFormat.html). See chapter Internationalization below to be able to handle locale specific formatting of numerical values.
* If the type is date, then the pattern should be described according to [java.text.SimpleDateFormat](https://docs.oracle.com/javase/8/docs/api/java/text/SimpleDateFormat.html).
* If the type is boolean, the pattern should contain the true and false values separated with a ; character. Example: `pattern="Y;N"` will imply that Y represents true and N to represents false. Comparison while parsing is not case sensitive. Multiple true or false values can be specified, separated with the | character but the first value is always the one used while composing. Example: `pattern="Y|YES;N|NO"`

If the `pattern` attribute is omitted, the default system pattern is used.
### Quoted values
### The first line describes cell layout
It is quite common in CSV files to have one header row that contains the name of the columns within the file. For instance, the file might look like this:
```csv
First name;Middle name;Last name;Have dog
Erik;Vidfare;Svensson;yes
Fredrik;Allvarlig;Larsson;no
"Alfred";"Stark";Nilsson;yes
```
This type of format is supported by the library out-of-the-box. All you need to do is to is to set the attribute 
`firstlineasschema="true"` on the `<line>` element. Then the order of the cell while parsing is no longer denoted by the 
order of the `<cell>` elements in the schema. Instead the order is fetched from the first header row. It is important though that 
the name of the cells within the schema matches the names in the header. The advantage of using such a format is that the
producer of the CSV file can choose to re-arrange, add or remove columns without impacts on neither the code nor the schema.   

Here is a schema that could be used to parse the file above:
```xml
<?xml version="1.0" encoding="UTF-8"?>
<schema xmlns="http://jsapar.tigris.org/JSaParSchema/2.0">
  <csvschema lineseparator="\n">
    <line occurs="*" linetype="Person" cellseparator=";" quotechar="&quot;" firstlineasschema="true">
      <cell name="Middle name" ignoreread="true"/>
      <cell name="Have dog"><format type="boolean" pattern="yes;no"/></cell>
    </line>
  </csvschema>
</schema>
```   
As you can see in this example, not all cells are described in the schema. Only those cells, where additional information 
is needed from the schema, needs to be present. By default a string cell is otherwise expected. For instance the values of the 
`First name` column will still be parsed as if they are string value cells. This means that we could have omitted all the `<cell>` elements
from the schema above but then the parser would have parsed also the `Middle name` cells which we have no interest in 
and the `Have dog` cell would have
been parsed as string values `"yes"` and `"no"` instead of true boolean values. 

It is important though that if you provide `<cell>` elements for such a schema, the cell names need to match exactly what 
is specified in the header line. Matching is case sensitive.

You may for instance provide default values for missing columns or specify that a cell is mandatory by adding a `<cell>` element for that column.   

When composing, if `firstlineasschema="true"` then the output will be produced according to the cell layout of the schema
and with an additional header line with the name of the cells as specified by the schema. So in this case it is important that all the cells are present
and in the correct position.

## The Schema xml for fixed width files
### Line
### Cell

## Line types
Within the schema, you specify a number of line types. When parsing, the type of the line is either denoted by it's position
within the input (governed by the `occurs` attribute) or by a number of conditional cells. For one type of line you can for instance specify that the first cell
has a specific constant value. When composing, the line type is assigned when you create the Line objects. When
converting from one format to another, the line type names of the input and the output schema needs to match.

## Internationalization 

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
It is possilbe to parse an xml document that conforms to the XMLDocumentFormat.xsd (http://jsapar.tigris.org/XMLDocumentFormat/1.0).
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
