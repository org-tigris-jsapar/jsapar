---
layout: page
title: Basics
---
<h1>Basics</h1>
This article will describe the basics of using the library. Since schemas are a large part of the library, [basics of schemas](basics_schema)  are described in this separate article.

* TOC
{:toc}

There are three different tasks that the JSaPar library can be used for:
1. **Parsing** - Parsing a text source into the internal data model and handle the result in code. 
2. **Composing** - Compose text output by feeding data of the internal data model in java code. 
3. **Converting**  - Convert one source into an output on a different format.

All parsers produce output represented by the internal data model described below and all the composers accepts that data model as input.
When it comes to other data forms, for instance parsing text into Java beans, that is instead considered converting.    
# The model
## org.jsapar.model.Cell
A `Cell` represents one table data field.

The `Cell` class is the base class for all type of cells where subclasses handles different primary types.
All cells have a name that is matched with the name specified by the schema. You can query the type of a cell and you can always get a string value of a cell but for numbers and dates the string value is
always converted to java native string representation. When parsing, if the text input
does not contain any value for the cell, an `EmptyCell` instance takes its place.
## org.jsapar.model.Line
The `Line` class represents one line in the text and contains a collection of `Cells`. You can find cells in a line by
cell name (specified by schema) or you can iterate all cells. See [api docs](api) for more details.

The `org.jsapar.model.LineUtils` class contains a large set of utility functions for getting and setting cell values on a
line by native java types.
## org.jsapar.model.Document
The `Document` class is equivalent of the [DOM Document](https://en.wikipedia.org/wiki/Document_Object_Model) while parsing XML.
It is the container of all the following `Lines`. The `Document` class should only be used when handling small sized 
data since it stores all the lines in memory. When dealing with larger data inputs or outputs you should use the `Line`
directly, using consumer while parsing or feeding lines one-by-one while composing.

You can add lines to a document, and you can iterate existing lines.

# Parsing
When we talk about parsing below, we mean parsing a text source into the internal data model described above. A text source can be for instance either a delimited file such as CSV or a 
fixed width file but the library is not limited to handling files as text source.

In order to parse a text source we use the `org.jsapar.TextParser` class. As we saw in the initial example earlier you create an
instance of the `TextParser` by supplying a schema. 
```java
...
    TextParser parser = new TextParser(schema);
    DocumentBuilderLineConsumer documentBuilder = new DocumentBuilderLineConsumer();
    parser.parseForEach(fileReader, documentBuilder);
    Document document = documentBuilder.getDocument()
...    
```
The schema is used for describing the text format. [See Basics of Schemas](basics_schema) for information about schemas. After that we start the parsing by calling the parse method.
The supplied line consumer is the class that gets called for each line that is parsed.
## Consumer gets called for each line
For very large data sources there can be a problem to build the complete `org.jsapar.model.Document` in the memory before further processing.
It may simply take up too much memory. There are other situations where you may prefer to handle one line at a time instead of getting all
lines in a `Document` when parsing is complete.

All parsers in this library requires that you provide a `java.util.function.Consumer<Line>` while parsing.

The JSaPar library contains some convenient implementations of the `java.util.function.Consumer<Line>` interface:
<table>
    <tr><td><b>org.jsapar.parse. DocumentBuilderLineConsumer</b></td>
        <td>For smaller files you may want to handle all
        the lines after the parsing is complete. In that case you may choose to use this implementation.
        This consumer acts as an [aggregator](http://www.enterpriseintegrationpatterns.com/patterns/messaging/Aggregator.html) and builds a `org.jsapar.model.Document` object containing all the parsed lines that you can iterate afterwards.</td></tr>
    <tr><td><b>org.jsapar.parse. ByLineTypeLineConsumer</b></td>
        <td>Allows you to register different line consumers for different line types. Use MulticastConsumer if you want to have multiple consumers for each line type.</td></tr>
    <tr><td><b>org.jsapar.parse. MulticastConsumer</b></td>
        <td>If you need to handle the lines in multiple consumer implementations, this implementation provides a
            way to register multiple consumers which are called one by one for each line.</td></tr>
    <tr><td><b>org.jsapar.concurrent. ConcurrentConsumer</b></td>
        <td>This implementation separates the parsing thread from the consuming thread. It makes it possible for you to
            register a consumer that is called from a separate consumer thread.</td></tr>
</table>

These implementations only demonstrates what can be done in a `java.util.function.Consumer` implementation. Feel free to create
your own implementation for instance if you need to feed the result to a database or any other scenario.
## Parsing into stream
The consequence of using the `parseForEach()` method that we saw in previous section is that the whole input source (file) 
will be scanned whether you need it or not. In cases when you only need to find a defined number of lines or search
for a particular line, that can be very inefficient. Using the `stream()` method will pull characters from the reader
when needed as lines are consumed from the stream. In the example below, only the first 10 lines of the input source
will be read, even though the input source might be huge.
```java
...
     TextParser parser = new TextParser(schema, config);
     try(Reader reader = new StringReader(text)){
        List<Line> result=parser.stream(reader).limit(10).collect(Collectors.toList());
     }
...    
```
Another advantage of the `stream()` method is that you might already be familiar with the `java.util.stream.Stream` interface
and how to map, aggregate, and so on the result. There are plenty of tutorials online for how to work with Java streams.

You can still use the pre-defined collectors described above if you terminate the stream with a `forEach()` at the end.
```java
...
    parser.stream(reader).limit(10).forEach(myAwsomeCollector);
...    
```
## Configuration
The parser behavior can be configured in some cases, for instance:
 * What should happen if none of the schema lines can be used?
 * What should happen if there are more or less cells on a line than described by the schema.
See [api docs](api) for class `TextParseConfig` for more details.
## Error handling
### IOErrors and other serious errors
IOErrors and other serious runtime errors are thrown immediately as exceptions and should be dealt with by the caller. 
These type of errors indicate a bug or maybe an error reading the input.
 
### Format errors
Format error are handled by the library in a similar way as with line consumers. An error consumer is called for every error 
. By default, the `ExceptionErrorConsumer` is registered so if you 
don't do anything, an exception will be thrown at first error. As with the line consumer there are some provided 
alternatives, in the `org.jsapar.error` package, to use if you don't want to implement your own:

|Consumer Class name|Description|
|---|---|
|`ExceptionErrorConsumer` | Throws an exception upon the first error|
|`ThresholdCollectingErrorConsumer` | Records all errors in a list and allows you to retrieve all errors when parsing is done but if the number of errors exceeds a threshold, an exception is thrown.|

You may also use the more generic consumers in the `org.jsapar.parse` package: 

`CollectingConsumer` | Records all errors in a list and allows you to retrieve all errors when parsing is done.
`MulticastConsumer` | Allows you to register multiple other error consumers that each will get called when an error occurs.

|   |
|---|
|   |

Each error contains detailed information about where in the input the error occurred and what was the cause of the errors. 

You can also access all errors that have occurred while parsing a line directly on the `Line` object so if you want to deal with the
errors together with you parsing code, you can just register an error event listener that does nothing in order to avoid 
exceptions and handle errors when dealing with the lines instead.  
# Composing
When we talk about composing below, we mean composing a text output out of the internal data model described above. 
If you want to compose text output based on your Java bean objects, you should jump the chapter **Converting java objects to text** below.

In order to compose a text output we use the `org.jsapar.TextComposer` class. As in the introduction example we create a `TextComposer` 
by supplying a schema and a writer. The schema is used to format the output. 

```java
...
    TextComposer composer = new TextComposer(schema, writer);
    composer.composeLine(new Line("Person")
            .addCell(new StringCell("First name", "Fredrik"))
            .addCell(new StringCell("Last name", "Larsson"))
            .addCell(new BooleanCell("Has dog", false)));
...
```
Then we feed the composer with lines. There are some options when it comes to feeding data to the composer. You may:

* Feed lines one-by-one by calling `composeLine()` method for each line
* Feed an entire `Document` instance  
* Provide a `java.util.stream.Stream<Line>` with lines
* Provide a `java.util.Iterator<Line>` with lines

All the formatting information is described by the schema. [See Basics of Schemas for information about schemas.](basics_schema) 

See [API docs](api) for class `TextComposer` for more details.
## Error handling 
IOErrors and other serious runtime errors are thrown immediately as exceptions and should be dealt with by the caller. 
These type of errors indicate a bug or maybe an error writing to the output.
# Converting
Converting is when you have one data source and want to produce a different output. The internal data model is still used internally
as an intermediate data format but input and output are of different type. All the converters uses the event 
mechanism under the hood, thus it reads, converts and writes one line at a time. This means it is very lean regarding memory usage.
## Converting text to text
If you are only interested in converting a file of one format into another, you can use the `org.jsapar.Text2TextConverter` 
where you specify the input and the output schema for the conversion.

*You can find a version of [this example in the jsapar-examples project](https://github.com/org-tigris-jsapar/jsapar-examples/tree/master/src/main/java/org/jsapar/examples/basics/b1)*

There is not much code that is needed for converting from one text format to another:  
```java
try (Reader inSchemaReader = new FileReader(inSchemaXmlFile);
     Reader outSchemaReader = new FileReader(outSchemaXmlFile);
     Reader inReader = new FileReader(inFile);
     Writer outWriter = new FileWriter(outFile)) {
    
    Schema inputSchema = Schema.ofXml(inSchemaReader);
    Schema outputSchema = Schema.ofXml(outSchemaReader);
    
    Text2TextConverter converter = new Text2TextConverter(inputSchema, outputSchema);
    converter.convert(inReader, outWriter);
}
```
When converting it is important that the line types and cell names matches between the input and the output schemas. It works like this:
1. The converter first parses one line using the input schema
1. The line type of the parsed line is used to match a line with the same type in the output schema. Lines that are not matched are omitted.
1. For each cell in the parsed line, the converter uses the name of the cell to find a cell in the output schema to use. 
Cells that are not matched are omitted. Cells in the output schema that was not provided in the data source are left empty.
 
All line types and cell names are case-sensitive so be thorough.  
  
Almost all the job lies in defining the schemas. You can even run the text to text converter directly from the command line without coding. [See below](#Running text to text conversion from command line). 
## Converting text to Java beans
You can use the `org.jsapar.Text2BeanConverter` in order to build java objects directly for each line in the data source.
For this to work, the line type of each line in the input schema needs to contain the full name of the java bean that 
you want to create for that line and the cell name for each cell needs to match against the bean property that you want 
to assign a value to. Use dot notation to separate sub properties. See example below.

The [advanced](advanced) section will describe how to handle the case that you want to re-use an existing schema that does
 not conform to these rules or if you want to provide your own bean factory implementation. 

The following of the [java bean requirements](https://en.wikipedia.org/wiki/JavaBeans) apply for the bean class:
* There has to be a constructor with no arguments.
* There have to be both getter and setter methods for all bean properties.

As with the parser the `Text2BeanConverter` calls a consumer for each bean that has been parsed. You need to provide an implementation
of the `java.util.function.BiConsumer<T, Line>` or `java.util.function.Consumer<T>` which will be called for each bean that is parsed. 

You may use the implementation: `org.jsapar.parse.CollectingConsumer` that saves all beans that was 
created in an internal list to be retrieved later. Not to be used for large data sets since it will store all beans in memory.   

### Example
*You can find a version of [this example in the jsapar-examples project](https://github.com/org-tigris-jsapar/jsapar-examples/tree/master/src/main/java/org/jsapar/examples/basics/b2)*

This example will describe how to convert CSV text into instances of a class `com.example.Employee` defined as:
```java
public class Employee {
    private String         name;
    private int            employeeNumber;
    private Address        address;
    private LocalDate      birthDate;

    public Employee() {}
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getEmployeeNumber()  { return employeeNumber; }
    public void setEmployeeNumber(int employeeNumber) { this.employeeNumber = employeeNumber; }
    public Address getAddress()  { return address; }
    public void setAddress(Address address) { this.address = address; }
    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate localDate) {this.birthDate = localDate;}
}
```

...having a property of class `com.example.Address` like this:
```java
public class Address {
    private String         street;

    public Address() {}
    public String getStreet() { return street; }
    public void setStreet(String street) { this.street = street; }
}
```
If the input looks like this:
```csv
Famous Duck,17,1931-02-19,Duckcity 13
Mats Mouse,42,1930-09-15,Holestreet 3
```
The schema to use could look like this:
```xml
<?xml version="1.0" encoding="UTF-8"?>
<schema xmlns="http://jsapar.tigris.org/JSaParSchema/2.0">
  <csvschema lineseparator="\n">
    <line occurs="*" linetype="com.example.Employee" cellseparator=",">
      <cell name="name" />
      <cell name="employeeNumber" ><format type="integer"/></cell>
      <cell name="birthDate" > <format type="local_date" pattern="yyyy-MM-dd"/></cell>
      <cell name="address.street" />
    </line>
  </csvschema>
</schema>
```
As you can see. We need to add a `.` between address and street as an indication that this is a property of a property. 
There is no limit to the number of levels you can have. 

The java code needed for this to work:
```java
    Schema parseSchema = Schema.ofXml(inputSchemaReader);
    Text2BeanConverter<Employee> converter = new Text2BeanConverter<>(parseSchema);
    converter.convertForEach(fileReader, employee->{
        // Handle each emplyee here...
    });
    
```
When converting into beans there is also the alternative of using the `Text2BeanConverter.stream()` method. This method
pulls characters from the input reader when needed for parsing as beans are pulled from the parser.
```java
    converter.stream(fileReader).forEach(employee->{
        // Handle each emplyee here...
    });
    
```
## Converting java objects to text
*You can find a full version of [this example in the jsapar-examples project](https://github.com/org-tigris-jsapar/jsapar-examples/tree/master/src/main/java/org/jsapar/examples/basics/b3)*

Use the class `org.jsapar.Bean2TextConverter` in order to convert java objects into an output text according to a schema. Basically
it works the other way around compared to converting from text to java beans as described above. The same rules apply to both
how the schema is created and to the java beans. You feed the converter with java beans and the schema will handle the 
formatting of the text output.

The example code for converting employees to text as in previous example would be like this:
```java
Collection<Employee> employees = new ArrayList<>();
// Fill the list with employees

Bean2TextConverter<Employee> converter = new Bean2TextConverter<>(Schema.ofXml(schemaXmlReader), writer);
employees.forEach(employee ->converter.convert(employee));
```  

## Manipulating or transforming lines while converting
Not everything can be converted one-to-one by simple mapping of the format. Sometimes you need to add, remove, split or filter data for each line while converting.

There are two options for manipulating data during transformation:
1. Transformer. More flexible but also more complex. Allows enriching, filtering and splitting lines.
2. LineManipulator. Simpler but provides no means of splitting into several lines.

You cannot combine transformer and line manipulators for the same instance of a converter. Adding a transformer totally 
disables all line manipulators.

### Transformer 
The transformer can be used for  
 * [Content Enricher](http://www.enterpriseintegrationpatterns.com/patterns/messaging/DataEnricher.html) 
 * [Content Filter](http://www.enterpriseintegrationpatterns.com/patterns/messaging/ContentFilter.html)
 * [Splitter](https://www.enterpriseintegrationpatterns.com/patterns/messaging/Sequencer.html)

A transformer is an implementation of `java.util.function.Function<Line, List<Line>>` which is called for each line. The `apply()` method takes a Line as parameter 
and should return a list of lines. Only the lines returned by this function will be forwarded to the composer. Please note
that if you want to split a line, you will need to clone the input line before altering it, otherwise you will forward several
references to the same instance.

By returning an empty list in the `apply()` method, you can indicate that the line should be omitted from the output.

For example, if you may need to convert a cost contained in a cell between currencies:
```java
    Text2TextConverter converter = new Text2TextConverter(Schema.ofXml(inSchemaReader),
            Schema.ofXml(outSchemaReader));

    converter.setTransformer(line -> {
        LineUtils.setDecimalCellValue(line, "costInSEK", convertGBPtoSEK(LineUtils.getDecimalCellValue(line, "costInGBP"));
        return Collections.singeltonList(line);
    });
        
    converter.convert(inReader, outWriter);
```

### LineManipulator
The `org.jsapar.convert.LineManipulator` interface can be used for 
 * [Content Enricher](http://www.enterpriseintegrationpatterns.com/patterns/messaging/DataEnricher.html) 
 * [Content Filter](http://www.enterpriseintegrationpatterns.com/patterns/messaging/ContentFilter.html)
 
You can add line manipulators
to all converters and thus tap into the stream of lines and manipulate them before they are processed further. By returning
false from the `manipulate()` method, you can indicate that the line should be omitted from the output.

It is a bit simpler to use than the [transformer](#Transformer) described above but does on the other hand not provide any
means of splitting into several lines.

For example, if you may need to convert a cost contained in a cell between currencies. Then you can add a line manipulator to the converter like this:
 
```java
    Text2TextConverter converter = new Text2TextConverter(Schema.ofXml(inSchemaReader),
            Schema.ofXml(outSchemaReader));

    converter.addLineManipulator(line -> {
        LineUtils.setDecimalCellValue(line, "costInSEK", convertGBPtoSEK(LineUtils.getDecimalCellValue(line, "costInGBP"));
        return true;
    });
        
    converter.convert(inReader, outWriter);
```
You can add multiple line manipulators to a converter, and they will be called in the same order as they were added. 
Returning false from a manipulator indicates that the line should be omitted completely from the output.

You can both add and remove cells in a line manipulator.

*See [this example in the jsapar-examples project](https://github.com/org-tigris-jsapar/jsapar-examples/tree/master/src/main/java/org/jsapar/examples/basics/b4)*
## Asynchronous conversion
The `org.jsapar.concurrent.ConcurrentText2TextConverter` is an asynchronous version of the `Text2TextConverter`.
Internally it starts a separate thread that handles the composing part and thus can utilize resources more efficiently.

Tests have shown though that unless the data source is really large, the gain of concurrency is less
than the overhead of starting a new thread and synchronizing threads. As a rule of thumb while working with normal
files on disc, don't use this concurrent version unless your input normally exceeds at least 1MB.
## Running text to text conversion from command line
The class `org.jsapar.ConverterMain` has a main method that is also registered as the default main method for the jar file.
This means that you can run the converter without any coding at all.

If you run it without any arguments you will get a help text with further instructions:
```bash
$ java -jar jsapar-2.1.0.jar 
IllegalArgumentException: Too few arguments

Usage:
 1. Read all properties from a file:
jsapar.jar <property file name> 

 2. Convert from one text file to another using different input and output
    schemas:
jsapar.jar -in.schema <input schema path> 
           -out.schema <output schema path>
           -in.file <input file name> 
           [-out.file <output file name>]
           [-in.file.encoding  <input file encoding (or system default is used)>]
           [-out.file.encoding <output file encoding (or system default is used)>]

 3. Transform a text file into xml, html or any other format using XSLT:
jsapar.jar -in.schema <input schema path> 
           -xslt.file <xslt file path>
           -in.file <input file name> 
           [-out.file <output file name>]
           [-in.file.encoding  <input file encoding (or system default is used)>]
           [-out.file.encoding <output file encoding (or system default is used)>]
           [-xslt.encoding     <xslt file encoding (or system default is used)>]
           [-xslt.method       <xslt method to use. (xml is default)
                                Probably one of xml, html or text>]
```

