---
layout: page
title: Why JSaPar
---
<h1>Why JSaPar</h1>
There are many libraries for Java that read and write CSV files, and quite a few that handle fixed width files. This 
article describes what makes JSaPar different, when it is a good choice and, just as important, when you are better 
off with something else.

* TOC
{:toc}

# In short
Choose JSaPar when **the file format is the difficult part**:
* The file has several types of lines, for instance a header, different kinds of detail lines and a footer.
* The file is fixed width, or you need to handle both fixed width and delimited files with the same code.
* The format should be possible to change, or new formats added, without changing the code.
* You need to both read and write the same format.
* You cannot, or do not want to, add any other dependencies to your project. 

Choose something else when the file is a plain CSV file with one type of line and all you want is to get it in or out of 
your Java objects as fast or with as little code as possible. See [when not to use JSaPar](#when-not-to-use-jsapar) 
below.

# What makes JSaPar different
## One file, several types of lines
Many file formats that are used for exchanging data between systems, such as files to and from banks, postal operators 
and public authorities, do not look like a table. They contain different types of lines where the first characters 
tell what type of line it is:
```text
H20261114ACME    
B00017Famous Duck         19310219
B00042Mats Mouse          19300915
A00042Holestreet 3        Mousetown   
T0000003
```
With JSaPar you describe each type of line in the schema and state the condition that tells them apart. Each parsed 
line then gets a line type that you can use in your code, and when composing, each line is formatted according to its 
type:
```xml
<?xml version="1.0" encoding="UTF-8"?>
<schema xmlns="http://jsapar.tigris.org/JSaParSchema/2.0">
  <fixedwidthschema lineseparator="\n">
    <line linetype="Header" occurs="1">
      <cell name="Type" length="1" default="H"><linecondition><match pattern="H"/></linecondition></cell>
      <cell name="Created" length="8"><format type="local_date" pattern="yyyyMMdd"/></cell>
      <cell name="Sender" length="8"/>
    </line>
    <line linetype="Person">
      <cell name="Type" length="1" default="B"><linecondition><match pattern="B"/></linecondition></cell>
      <cell name="Id" length="5" alignment="right" padcharacter="0"><format type="integer"/></cell>
      <cell name="Name" length="20"/>
      <cell name="Birth date" length="8"><format type="local_date" pattern="yyyyMMdd"/></cell>
    </line>
    <line linetype="Address">
      <cell name="Type" length="1" default="A"><linecondition><match pattern="A"/></linecondition></cell>
      <cell name="Person id" length="5" alignment="right" padcharacter="0"><format type="integer"/></cell>
      <cell name="Street" length="20"/>
      <cell name="City" length="12"/>
    </line>
    <line linetype="Trailer">
      <cell name="Type" length="1" default="T"><linecondition><match pattern="T"/></linecondition></cell>
      <cell name="Line count" length="7" alignment="right" padcharacter="0"><format type="integer"/></cell>
    </line>
  </fixedwidthschema>
</schema>
```
```java
TextParser parser = new TextParser(schema);
parser.parseForEach(reader, line -> {
    switch (line.getLineType()) {
        case "Person" -> handlePerson(line);
        case "Address" -> handleAddress(line);
        default -> { }
    }
});
```
The same works for delimited files. See [line condition](basics_schema#line-condition) for details.

## The format is not part of your code
The schema can be loaded from a xml document, so it can be stored as a file, as a resource or in a database. 
This means that:
* A changed position, a new column or a different date format is a change of configuration and not a new release 
of your software.
* The same code can handle many different formats, for instance one for each customer or supplier, by just selecting 
a different schema.
* Someone who is not a Java developer can read, review and change the format.

If you prefer to keep everything in the code, the same schema can be [built in Java](basics_schema#the-schema) instead.

## Fixed width and delimited files are handled in the same way
Whether the file is fixed width or delimited is a property of the schema. Your code works with lines and cells, or with
your own [Java beans](bean_mapping), and does not need to know what the file looks like. Converting a fixed width file into CSV, or 
the other way around, is a matter of using two different schemas with the 
[`Text2TextConverter`](basics#converting-text-to-text), which can also be done 
[from the command line](basics#running-text-to-text-conversion-from-command-line) without writing any code.

Fixed width specifics, such as alignment, pad character, implied decimals and files where there are no line separators 
at all, are supported.

## Parsing and composing use the same schema
The same schema is used for both reading and writing. What you parse with a schema can be composed again with the 
same schema, including type conversions, quoting and padding. You do not need to describe the format twice.

## Type conversion, validation and error handling
* Cells are converted to and from numbers, booleans, dates, the `java.time` types and enums according to the pattern 
and locale in the schema.
* A cell can be mandatory, have a default value, be restricted to a range or need to match a regular expression.
* Errors can either stop the parsing at the first error, or be collected so that you can report all errors in a file at once.
Each error tells the line number, the name of the cell and what was expected.

## No dependencies and low memory usage
* JSaPar has no dependencies to other libraries. Apart from `java.base` it only requires the `java.xml` module, 
and it is a named Java module of its own.
* Parsing and composing is done one line at a time, so the size of the file does not affect the amount of memory 
needed. Lines can be consumed with a `Consumer` or pulled lazily from a `java.util.stream.Stream`.

# Performance
JSaPar is not built to win benchmarks, but it should never be the bottleneck in your application. As an indication, 
these are the results of the [JMH benchmarks in the source code](https://github.com/org-tigris-jsapar/jsapar/tree/master/src/jmh/java/org/jsapar/benchmark) 
for version 2.5 with Java 25 on a laptop (Intel Core Ultra 7 155U), parsing text that is already in memory into 
`Line` objects in one thread:

|Content|CSV|Fixed width|
|---|---|---|
|8 string cells per line|2.8 million lines/s|2.4 million lines/s|
|8 cells per line with integers, decimals, dates and booleans|2.0 million lines/s|1.9 million lines/s|
|32 cells per line with integers, decimals, dates and booleans|0.45 million lines/s| |

The figures will be different on your hardware and with your data. You can run the benchmarks yourself with 
`mvn -Pbench test-compile exec:exec`.

We have not made any comparison with other libraries. Libraries that are specialized in CSV and that leave the type 
conversion to you can be expected to be faster at splitting plain CSV lines into strings. 

# When not to use JSaPar
* **A plain CSV file should become a list of objects with a minimum of code.** If there is only one type of line, the 
column names are in the first line and you already have a class with the same property names, then libraries with 
data binding such as Jackson CSV or OpenCSV require less set up.
* **Your classes are `record` classes or immutable.** The bean mapping of JSaPar requires a constructor without 
arguments and setter methods. You can still parse into `Line` objects and create the objects yourself, but libraries 
such as Jackson CSV can create records directly.
* **Raw speed on simple CSV is all that matters.** Use a library that is specialized in that, such as FastCSV.
* **The lines form a hierarchy that should become nested objects.** JSaPar gives you the lines in order, one by one, 
with their line types. Collecting for instance all address lines into the person they belong to is up to your code. 
BeanIO can describe such groups of lines in its mapping.
* **You do not know the format beforehand.** JSaPar does not detect separators, quote characters or types automatically. 
The exception is that the names and the order of the columns can be read from 
[the first line of a CSV file](basics_schema#the-first-line-describes-cell-layout).
* **The file is not a text file.** Excel documents, binary formats with packed decimals and such are not supported.
* **Your project is built with Java 20 or older.** The current version requires Java 21. Older versions of JSaPar are 
still available, see [dependencies](index#dependencies).

# Compared to other libraries
The table below is a rough guide to how JSaPar relates to some other well known libraries. It was written in November 2026 
and to the best of our knowledge. The other libraries evolve, and they have many features that are not mentioned here, so 
please check their documentation before you decide. Corrections are welcome as 
[issues on Github](https://github.com/org-tigris-jsapar/jsapar/issues).

|Library|Delimited|Fixed width|Several line types in one file|Format described outside the code|Type conversion|Bean mapping|
|---|---|---|---|---|---|---|
|**JSaPar**|Yes|Yes|Yes|Yes, xml|Yes|Yes|
|[BeanIO](https://github.com/beanio/beanio)|Yes|Yes|Yes, also nested groups|Yes, xml|Yes|Yes|
|[univocity-parsers](https://github.com/uniVocity/univocity-parsers)|Yes|Yes|Partly|No|Yes|Yes|
|[Jackson CSV](https://github.com/FasterXML/jackson-dataformats-text/tree/master/csv)|Yes|No|No|No|Yes|Yes, also records|
|[OpenCSV](https://opencsv.sourceforge.net/)|Yes|No|No|No|Yes|Yes|
|[Apache Commons CSV](https://commons.apache.org/proper/commons-csv/)|Yes|No|No|No|No|No|
|[FastCSV](https://fastcsv.org/)|Yes|No|No|No|No|No|
|[Fixedformat4j](https://github.com/jeyben/fixedformat4j)|No|Yes|No|No|Yes|Yes|

Some notes about the alternatives:
* **BeanIO** is the library that is most similar to JSaPar. It also uses a mapping that can be loaded from xml and it 
handles several types of lines. It is centered around mapping to beans and can build nested objects out of groups of 
lines, which JSaPar cannot. JSaPar on the other hand has a model of lines and cells that can be used without any 
bean classes at all, can convert directly between two formats and has no dependencies. The original BeanIO project has not been released 
since 2014 but it lives on as a fork with new maven coordinates.
* **univocity-parsers** handles both delimited and fixed width files, is known for its speed and has a lot of 
features. The latest release is from January 2021.
* **Jackson CSV** is a natural choice if you already use Jackson, since the same annotations and data binding as 
for JSON can be used.
* **OpenCSV** maps CSV columns to bean properties by annotations, either by the name in the header line or by position.
* **Apache Commons CSV** and **FastCSV** give you each line as a record of strings and leave the rest to you. 
That is often all you need for simple CSV files.
* **Fixedformat4j** maps fixed width lines to beans with annotations.
* If you are using **Spring Batch**, its own flat file reader and writer are well integrated with the rest of the 
framework. JSaPar can be used within a custom reader or writer if you need what the schema offers.

There is also a long list of [links to other related projects](links).

# Next step
Continue with the [introduction](introduction) to see how to parse and compose your first file.
