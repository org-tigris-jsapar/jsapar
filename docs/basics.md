---
layout: page
title: Basics
---
This article will describe the basics of using the library. Since schemas are a large part of the library, [basics of schemas are 
described in this separate article](basics_chema).

There are three different tasks that the JSaPar library can be used for:
1. **Parsing** - Parsing a text source and handle the result in code. 
2. **Composing** - Compose text output by feeding information in java code. 
3. **Converting**  - Convert one source into an output on a different format.

All parsers uses uses the internal data model described below as output   
and all the composers uses that data model as input. When it comes to other data forms, for instance parsing text into Java 
beans, that is instead considered converting.    
# The model
## org.jsapar.model.Document
The `Document` class is equivalent of the [DOM Document](https://en.wikipedia.org/wiki/Document_Object_Model) while parsing xml.
It is the container of all the following `Lines`. The `Document` class should only be used when handling small sized 
data since it stores all the lines in memory. When dealing with larger data inputs or outputs you should use the `Line`
directly, using events while parsing or feeding lines one-by-one while composing.

You can add lines to a document and you can iterate existing lines.
## org.jsapar.model.Line
The `Line` class represents one line in the text an contains a collection of `Cells`. You can find cells in a line by 
cell name (specified by schema) or you can iterate all cells. See [api docs](api) for more details. 

## org.jsapar.model.Cell
The `Cell` class is the base class for all type of cells. All cells has a name that is matched with the name specified by 
the schema. You can query the type of a cell and you can always get a string value of a cell but for numbers and dates the string value is 
always converted to java native string representation. When parsing, if the text input 
does not contain any value for the cell, an `EmptyCell` instance takes it's place.  

The `org.jsapar.model.LineUtils` class contains a large set of utility functions for getting and setting cell values on a 
line by native java types. 
 
# Parsing
When we talk about parsing, we mean parsing a text source. A text source can be for instance either a delimited file such as CSV or a 
fixed width file. 

In order to parse a text source we use the `org.jsapar.TextParser` class. As we saw in the the initial example earlier you create an 
instance of the `TextParser` by supplying a schema. 
```java
...
    TextParser parser = new TextParser(schema);
    DocumentBuilderLineEventListener listener = new DocumentBuilderLineEventListener();
    parser.parse(fileReader, listener);
...    
```
The schema is used for describing the text format. [See Basics of Schemas for information about schemas](basics_schema). After that we start the parsing by calling the parse method. 
The supplied line event listener is the class that receives an event for each line that is parsed.

## Events for each line
For very large files there can be a problem to build the complete `org.jsapar.model.Document` in the memory before further processing.
It may simply take up to much memory. 
All parsers in this library requires that you provide an event handler that implements org.jsapar.parse.LineEventListener while parsing.

The JSaPar library contains some convenient implementations of the org.jsapar.parse.LineEventListener interface:
<table>
    <tr><td><b>org.jsapar.parse. DocumentBuilderLineEventListener</b></td>
        <td>For smaller files you may want to handle all
        the events after the parsing is complete. In that case you may choose to use this implementation.
        That listener builds a `org.jsapar.model.Document` object containing all the parsed lines that you can iterate afterwards.</td></tr>
    <tr><td><b>org.jsapar.parse. MulticastLineEventListener</b></td>
        <td>If you need to handle the events in multiple event listener implementation, this implementation provides a
            way to register multiple line event listeners which are called one by one for each line event.</td></tr>
    <tr><td><b>org.jsapar.concurrent. ConcurrentLineEventListener</b></td>
        <td>This implementation separates the parsing thread from the consuming thread. It makes it possible for you to
            register a consumer line event listener that is called from a separate consumer thread.</td></tr>
</table>

These implementations only demonstrates what can be done in a `LineEventListener` implementation. Feel free to create
your own implementation for instance if you need to feed the result to a database or any other scenario.
## Configuration
The parser behavior can be configured in some cases; for instance what should happen if none of the schema lines can be 
used or what should happen if there are more or less cells on a line than described by the schema.
. See [api docs](api) for class `TextParseConfig` for more details.
## Error handling
### IOErrors and other serious errors
IOErrors and other serious runtime errors are thrown immediately as exceptions and should be dealt with by the caller. 
These type of errors indicate a bug or maybe a error reading the input.
 
### Format errors
Format error are handled by the library in a similar way as with line parsed events. An event is fired for every error 
and passed to a registered `ErrorEventListener`. By default, the `ExceptionErrorEventListener` is registered so if you 
don't do anything, an exception will be thrown at first error. As with the line event listener there are some provided 
alternatives, in the `org.jsapar.error` package, to use if you don't want to implement your own:

`ExceptionErrorEventListener` | Throws an exception upon the first error
`RecordingErrorEventListener` | Records all errors in a list and allows you to retrieve all errors when parsing is done.
`ThresholdRecordingErrorEventListener` | Records all errors in a list and allows you to retrieve all errors when parsing is done but if the number of errors exceeds a threshold, an exception is thrown.
`MulticastErrorEventListener` | Allows you to register multiple other error event listeners that each will get all the error events.

Each error event contains detailed information about where in the input the error occurred and what was the cause of the errors. 

You can also access all errors that have occurred while parsing a line directly on the `Line` object so if you want to deal with the
errors together with you parsing code, you can just register an error event listener that does nothing in order to avoid 
exceptions and handle errors when dealing with the lines instead.  
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
