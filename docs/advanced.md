---
layout: page
title: Advanced
---
<h1>Advanced</h1>
* TOC
{:toc}
*This page is under construction*

Please see the [API javadocs](api) for more advanced documentation.

How to map lines and cells to your own Java classes is described in the article about [bean mapping](bean_mapping).

The schema XSD itself provides
a lot of documentation about the details of each allowed element and attribute within the schema xml. The latest version
of the schema is located here: 

[JSaParSchema.xsd](https://raw.githubusercontent.com/org-tigris-jsapar/jsapar/master/src/main/resources/xml/schema/JSaParSchema.xsd)

If you want to download the XSD as a file, you will probably need to right-click on the link above and choose *"Save link as..."* depending on your browser.

# Advanced parsing
## Using XML as input
It is possible to parse a xml document that conforms to the 
[XMLDocumentFormat.xsd](https://raw.githubusercontent.com/org-tigris-jsapar/jsapar/master/src/main/resources/xml/schema/XMLDocumentFormat.xsd) 
(name space `http://jsapar.tigris.org/XMLDocumentFormat/2.0`). This is the same format that is produced by the `Text2XmlConverter`.

Use the class `org.jsapar.Xml2TextConverter` in order to convert such xml into text according to a schema, or the class 
`org.jsapar.parse.xml.XmlParser` in order to parse the xml and get each line supplied to a line consumer.

For security reasons, a xml document that contains a `DOCTYPE` declaration is rejected.

# Master and detail lines
*New since version 2.5*

Some files group lines together: a master line followed by the detail lines that belong to it, for instance an order 
followed by its order lines. A master line can have detail lines of several types and detail lines can in turn be 
master lines of other detail lines. Example
[09_Orders.csv](https://github.com/org-tigris-jsapar/jsapar/blob/master/examples/09_Orders.csv):
```csv
O;1001;2026-11-02;ACME
L;SKU-1;2
D;10
H;Created by web shop
L;SKU-7;1
O;1002;2026-11-03;Globex
L;SKU-3;5
```
Here each order (`O`) has order lines (`L`) and history log lines (`H`), and an order line can have a discount line (`D`).

## The schema
Each detail line type names its master line type with the attribute `parent`. Which line type a line has is still 
decided as usual, typically by a [line condition](basics_schema#line-condition). From 
[09_CsvSchemaMasterDetail.xml](https://github.com/org-tigris-jsapar/jsapar/blob/master/examples/09_CsvSchemaMasterDetail.xml):
```xml
<csvschema lineseparator="\n" cellseparator=";">
  <line linetype="Order">
    <cell name="type" default="O"><linecondition><match pattern="O"/></linecondition></cell>
    <cell name="orderId"><format type="integer"/></cell>
    <cell name="date"><format type="local_date" pattern="yyyy-MM-dd"/></cell>
    <cell name="customer"/>
  </line>
  <line linetype="OrderLine" parent="Order">
    <cell name="type" default="L"><linecondition><match pattern="L"/></linecondition></cell>
    <cell name="sku"/>
    <cell name="quantity"><format type="integer"/></cell>
  </line>
  <line linetype="Discount" parent="OrderLine">
    <cell name="type" default="D"><linecondition><match pattern="D"/></linecondition></cell>
    <cell name="percent"><format type="integer"/></cell>
  </line>
  <line linetype="HistoryLog" parent="Order">
    <cell name="type" default="H"><linecondition><match pattern="H"/></linecondition></cell>
    <cell name="text"/>
  </line>
</csvschema>
```
When building the schema in code, use `withParent()`:
```java
CsvSchemaLine.builder("OrderLine")
        .withParent("Order")
        ...
```
The parent line type has to exist in the schema and a line type cannot be its own parent, directly or through other 
line types. Note that the `occurs` attribute counts lines in the whole file, not per master line.

## Parsing
When the schema has detail lines, the parser passes on only the top level lines, here the `Order` lines. The 
detail lines are available from their master line with `Line.getDetails()`, in the order they were read regardless of 
line type, or with `Line.getDetails(lineType)` for one line type:
```java
TextParser parser = new TextParser(schema);
parser.parseForEach(reader, order -> {
    for (Line orderLine : order.getDetails("OrderLine")) {
        List<Line> discounts = orderLine.getDetails("Discount");
        ...
    }
});
```
This works the same way when parsing into a stream and when converting. A master line is passed on when the next top 
level line is read or when the input ends, so the last one is not lost. The whole group, a master line together with 
all its detail lines, is kept in memory until it is complete.

The lines are read as a tree written from the top down: each detail line belongs to the closest preceding line of its 
parent line type that is still open, and a line closes all open lines below its own level. In the sequence `O`, `L`,
`H`, `D` above, the `H` line closes the `L` line, so the `D` line would have no master line to belong to.

A detail line without master line to belong to, for instance when the input starts with a detail line, is never 
passed on. What else happens is controlled by `TextParseConfig.setOnOrphanDetailLine()`:

|Value|Behavior|
|---|---|
|`ERROR`|Default. The line is reported to the error consumer and parsing continues.|
|`EXCEPTION`|An exception is thrown and parsing stops.|
|`NONE` or `OMIT_LINE`|The line is silently skipped.|

## Mapping to beans
Use the annotation `@JSaParDetail` on a collection or map attribute of the class of the master line:
```java
@JSaParLine(lineType = "Order")
public class Order {
    @JSaParCell(name = "orderId")
    private long orderId;
    @JSaParDetail(lineType = "OrderLine", keyCell = "sku")
    private Map<String, OrderLine> lines;
    @JSaParDetail(lineType = "HistoryLog")
    private List<HistoryLog> history;
    // getters and setters
}

@JSaParLine(lineType = "OrderLine")
public class OrderLine {
    @JSaParCell(name = "sku")
    private String sku;
    @JSaParDetail(lineType = "Discount")
    private List<Discount> discounts;
    // getters and setters
}
```
```java
Text2BeanConverter<Order> converter = new Text2BeanConverter<>(schema, Order.class);
converter.convertForEach(reader, order -> ...);
```
The classes of the detail lines need to be annotated with `@JSaParLine` too. The element classes of attributes with 
`@JSaParDetail` are added to the bean map automatically, so it is enough to supply the class of the top level line.

* The attribute can be a `List`, `Set`, any other `Collection` or a `Map`. 
* For a `Map`, `keyCell` names the cell of the detail line whose value is used as key. The key is the typed value of 
  the cell, so for an integer cell the key type is `Long`. A detail line without value in the key cell, or with a key 
  that already exists in the map, is reported as an error and the first entry is kept.
* If the attribute is null when the first detail bean is added, a new `ArrayList`, `LinkedHashSet` or `LinkedHashMap` 
  is created and assigned with the setter. If the attribute type is a concrete class, such as `TreeMap`, an instance 
  of that class is created instead.
* Detail lines of a type that is not mapped are ignored.

The same mapping can be made with the [bean map builder](bean_mapping#bean-map-builder):
```java
BeanMap beanMap = BeanMap.builder()
        .withLine("Order", Order.class, b -> b
                .withCell("orderId")
                .withDetail("OrderLine", "lines", "sku")
                .withDetail("HistoryLog", "history"))
        .withLine("OrderLine", OrderLine.class, b -> b
                .withCell("sku")
                .withDetail("Discount", "discounts"))
        ...
        .build();
```
or with [bean map xml](bean_mapping#bean-map-xml):
```xml
<bean name="com.example.Order" linetype="Order">
    <property name="orderId" cellname="orderId"/>
    <detail linetype="OrderLine" name="lines" keycell="sku"/>
    <detail linetype="HistoryLog" name="history"/>
</bean>
```

## Composing
When composing text, the detail lines of a line are written after the line, recursively and in the order they were 
added. Converting text with master and detail lines to text therefore keeps the order of the input. If a line is not 
written, for instance because its line type has `ignorewrite`, neither are its detail lines. When converting to xml with 
`Text2XmlConverter`, detail lines are written as `line` elements directly after their master line.

When converting beans to text, the beans in the attributes annotated with `@JSaParDetail` are written as detail lines 
after the line of the master bean, one attribute after the other in the order they are declared, and for a map in the 
iteration order of its values. Beans do not keep the order between detail lines of different types, so in the example
above an order is written with all its order lines first and then its history log lines.

# Advanced converting
## Text to markup (XML or HTML)
Use the class `org.jsapar.Text2XmlConverter` in order to produce a xml output. You can register an XSLT together with this converter and in
that way you convert the text to any other text output format such as HTML.
