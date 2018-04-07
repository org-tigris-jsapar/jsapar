---
layout: page
title: Basics
---

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
